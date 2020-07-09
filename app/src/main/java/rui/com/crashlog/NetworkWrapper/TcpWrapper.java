package rui.com.crashlog.NetworkWrapper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class TcpWrapper
{
    private static final String TAG = "TcpWrapper";

    private Executors mExecutors = null;
    private ExecutorService mExecutorService = null;
    private ThreadPoolExecutor mThreadPool = null; //线程池

    private Socket mSocket = null;
    private ServerSocket mServerSocket = null;
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;

    private String mDesIp;                          //目标Ip
    private int mDesPort;                           //目标端口号
    private int mLocalPort;

    private static final int buffLength = 1200;
    private static final int VideoLength = 2048;
    private int mState;                             // Tcp的状态变量
    // Tcp通信期间的各个状态常量
    public static final int STATE_NONE = 0;         // 没有状态
    public static final int STATE_LISTEN = 1;       // 监听要来的连接
    public static final int STATE_CONNECTING = 2;   // 正在建立连接
    public static final int STATE_CONNECTED = 3;    // 已经建立连接
    private static final int MSG_WRITE_DATA = 4;

    /** 不同用途的线程 **/
    private AcceptThread mAcceptThread;             //用于监听连接
    private ConnectThread mConnectThread;           //用于建立连接
    private ReadThread mReadThread;                 //连接后的维持读写操作
    private HandlerThread mWriterThread;            //专用于写的线程

    //两个需要在其他类的对象里面实现的接口
    private HandleReceivedData mHandleReceivedData = null;
    private StateChange mStateChange = null;

    private Handler mHandler;

    /**
     * 构造函数——作为client
     * @param localPort 本地绑定端口，localPort == 0，则不绑定固定端口，自动分配
     * @param read_timeout read()的超时时间设置。== 0，则不设置超时
     * @param isServer 是否作为服务端
     */
    public TcpWrapper(int localPort, int read_timeout, boolean isServer){
        mLocalPort = localPort;
        mState = STATE_NONE;

        if (isServer){ //作为server
            try {
//                mServerSocket.bind(new InetSocketAddress(mLocalPort));
                mServerSocket = new ServerSocket(mLocalPort);
            } catch (IOException e) {
                Log.e(TAG, "TcpWrapper: error " + e.getMessage());
                e.printStackTrace();
            }
        } else {  //作为client
//            mSocket = new Socket(mDesIp, mDesPort);       //构造函数里面包括connect()和bind()，绑定的是随机端口
            try {
                mSocket = new Socket();
                mSocket.setReuseAddress(true);                          //是否复用端口
                mSocket.setSoTimeout(read_timeout);                     //设置read（）的超时时间
                mSocket.bind(new InetSocketAddress(mLocalPort));        //固定本地端口
                mSocket.setOOBInline(true);                             //能接受socket协议栈的心跳包 0xff，true——收到，；false——拒绝
            } catch (SocketException e) {
                Log.e(TAG, "TcpWrapper: error " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "TcpWrapper: error " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取蓝牙连接状态
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * tcp 连接(开启线程)
     * @param desIp 目的Ip
     * @param desPort 目的port
     * @param timeout 连接超时时间，== 0 表示超时设置成无限
     */
    public void connect(String desIp, int desPort, int timeout) {
        Log.d(TAG, "connect");
        mDesIp = desIp;
        mDesPort = desPort;

        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
            }
        }

        mConnectThread = new ConnectThread(timeout);
        mConnectThread.start();
    }

    /**
     *  Tcp建立连接的线程，因为socket的connect()是阻塞函数
     */
    private class ConnectThread extends Thread {
        int mmTimeout;

        public ConnectThread(int timeout) {
            mmTimeout = timeout;
            mState = STATE_CONNECTING;

        }

        @Override
        public void run() {
            SocketAddress desAddr = new InetSocketAddress(mDesIp, mDesPort);
            try {
                mSocket.connect(desAddr, mmTimeout); //连接对端socket，阻塞函数
                if (mSocket != null) {
                    Log.i(TAG, "connect: 成功" + mSocket.getInetAddress().toString());
                    if (mStateChange != null)
                        mStateChange.onConnect(mSocket.getRemoteSocketAddress().toString());
                }
                // Reset the ConnectThread because we're done
                synchronized (TcpWrapper.this) {
                    mConnectThread = null;
                }
                //开启读写操作（线程）
                connected();
            } catch (Exception e) {
                try {
                    if (mSocket != null) {
                        mSocket.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (mStateChange != null) {
                    mStateChange.onFail();
                }
                e.printStackTrace();
                Log.e(TAG, "connect: exception = " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tcp建立连接以后，开启读写操作线程
     */
    public void connected() throws IOException {
        Log.d(TAG, "connected");

        // 连接一个设备前，先要关闭已连接的设备
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // 取消当前运行连接的读线程
        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread = null;
        }
        // 取消当前运行的写线程
        if (mWriterThread != null) {
            mInputStream.close();
            mWriterThread.quit();
            mWriterThread = null;
        }
        // 移除当前已发送的消息
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        // 取消accept线程，因为只想连接到一个设备
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        try {
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initWriteThraed();
        mReadThread = new ReadThread();
        mReadThread.start();
        mState = STATE_CONNECTED;
    }

    /**
     * 连接建立后，读写操作线程
     */
    private class ReadThread extends Thread {
        public ReadThread() {
        }

        @Override
        public void run() {
            while (mState == STATE_CONNECTED) {
                int len;
                byte[] rcvData = new byte[buffLength];
                try {
                    len = mInputStream.read(rcvData);           //阻塞函数
                    if (-1 == len) {
                        Log.e(TAG, "receiveData: failed" );
                        if (mStateChange != null)
                            mStateChange.onDisconnect();            //若想执行修改UI操作，需要回归main线程
                        release();
                    } else {
                        Log.i(TAG, "receiveData: " + len);
                        if (mHandleReceivedData != null)
                            mHandleReceivedData.onReceive(rcvData, len);    //若想执行修改UI操作，需要回归main线程
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                mState = STATE_NONE;
                mInputStream.close();
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 作为服务端监听连接
     */
    public void accept() {
        if (mState == STATE_LISTEN) {
            if (mAcceptThread != null) {
                mAcceptThread.cancel();
                mAcceptThread = null;
            }
        }

        mAcceptThread = new AcceptThread();
        mAcceptThread.start();
    }

    /**
     *  Tcp连接监听线程
     */
    private class AcceptThread extends Thread {
        public AcceptThread() {
            mState = STATE_LISTEN;
        }

        @Override
        public void run() {
//            Log.e(TAG, "accept : run: 无限剑制" );
            try {
                mSocket = mServerSocket.accept();           //监听连接
                Log.e(TAG, "accept: 成功" + mSocket.getInetAddress().toString());
                if (mSocket != null) {
                    if (mStateChange != null)
                        mStateChange.onConnect(mSocket.getRemoteSocketAddress().toString());
                }
                //开启读写操作（线程）
                connected();
            } catch (Exception e) {
                if (mStateChange != null)
                    mStateChange.onFail();
                e.printStackTrace();
                Log.e(TAG, "connect: exception = " + e.getMessage());
//                        bIsConnectSuccess = false;
            }
        }

        public void cancel() {
            try {
                mSocket.close();
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 以非同步方式写入读写线程
     *
     * 建立连接后，向对端Socket发送数据
     * @param out The bytes to write
     *
     * 若不在子线程中执行，会报android.os.NetworkOnMainThreadException（不要在主线程中访问网络）
     * 这个从android3.0版本开始的，强制程序不能在主线程中访问网络，要把访问网络放在独立的线程中。
     */
    public void write(byte[] out) {
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            Message message = Message.obtain();
            message.what = MSG_WRITE_DATA;
            message.obj = out;
            mHandler.sendMessage(message);
        }
    }

    /**
     *  初始化写线程
     */
    private void initWriteThraed() {
        mWriterThread = new HandlerThread("write_thread");
        mWriterThread.start();
        mHandler = new Handler(mWriterThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_WRITE_DATA:
                        byte[] data = (byte[]) msg.obj;
                        try {
                            mOutputStream.write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
    }

    /**
     * 释放资源
     */
    public void release(){
        try {
            mState = STATE_NONE;
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
            if (mReadThread != null) {
                mReadThread.cancel();
                mReadThread = null;
            }
            // 移除当前已发送的消息
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
            // 取消当前运行的写线程
            if (mWriterThread != null) {
                mWriterThread.quit();
                mWriterThread = null;
            }
            if (mAcceptThread != null) {
                mAcceptThread.cancel();
                mAcceptThread = null;
            }
            if (mInputStream != null) {
                mInputStream.close();
                mInputStream = null;
            }
            if (mOutputStream != null) {
                mOutputStream.close();
                mOutputStream = null;
            }
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
            if (mServerSocket != null) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 不要瞎用finally，一定要了解清楚，因为无论有没有catch到异常，都会执行finally，这是坑！
         */
        finally {
            if (mInputStream != null) {
                try {
                    mInputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (mOutputStream != null) {
                try {
                    mOutputStream.close();
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException e5) {
                    e5.printStackTrace();
                }
            }
        }
    }

    public interface HandleReceivedData {
        /**
         * @param data      有效数据
         * @param length    有效数据长度
         */
         void onReceive(byte[] data, int length);
    }

    public interface StateChange {
        int length = 0;
        void onConnect(String addr);
        void onDisconnect();
        void onFail();
    }


    public void setOnReceive(HandleReceivedData handleReceivedData) {
        mHandleReceivedData = handleReceivedData;
    }

    public void removeReceiver() {
        mHandleReceivedData = null;
    }

    public void setOnStateChange(StateChange stateChange ){
        mStateChange = stateChange;
    }

    /**
     * 设置Socket的发送缓存区大小
     * @param newSize
     */
    public void setSendBufSize(int newSize) {
        try {
            mSocket.setSendBufferSize(newSize);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
