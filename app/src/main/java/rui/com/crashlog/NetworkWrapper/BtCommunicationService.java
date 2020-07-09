/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rui.com.crashlog.NetworkWrapper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BtCommunicationService {
    // Debugging
    private static final String TAG = "BtCommunicationService";

    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
//    private static final UUID SECURE_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
//    private static final UUID INSECURE_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    /** 如果是连接蓝牙串口模块使用通用的uuid 00001101-0000-1000-8000-00805F9B34FB；
     * 如果是Android手机间的通信链接；
     * 可以使用生成的uuid，客服端和服务端的uuid必须相同。
     * **/
    // 蓝牙串口模块通用UUID
    private static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //获取随机的UUID
//    private static final UUID DEVICE_UUID = UUID.fromString(BluetoothInit.getUUID_str());

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;
    private boolean bActivityDestroyed = false;     // 用于显示信息的activity是否已经被销毁
    private int mNewState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    /**
     * 构造函数
     *
     * @param context The UI Activity Context
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BtCommunicationService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mNewState = mState;
        mHandler = handler;
    }

    /**
     * 更新用户显示信息
     */
    private synchronized void updateInfoUI() {
        mState = getState();
//        Log.d(TAG, "updateInfoUI() " + mNewState + " -> " + mState);
        mNewState = mState;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, mNewState, -1).sendToTarget();
    }

    /**
     * 获取蓝牙连接状态
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        // 作为服务端监听蓝牙设备
//        if (mSecureAcceptThread == null) {
//            mSecureAcceptThread = new AcceptThread(true);
//            mSecureAcceptThread.start();
//        }
//        if (mInsecureAcceptThread == null) {
//            mInsecureAcceptThread = new AcceptThread(false);
//            mInsecureAcceptThread.start();
//        }

        // Update UI title
        updateInfoUI();
    }

    /**
     * 初始化远端蓝牙设备的连接的线程
     *
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "connect to: " + device);
        // Cancel any thread attempting to make a connection
        // 取消其他试图建立连接的线程
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        // Cancel any thread currently running a connection
        // 取消其他已建立连接的线程
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        // Update UI title
        updateInfoUI();
    }

    /**
     * 管理蓝牙连接的线程
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
        Log.d(TAG, "connected, Socket Type:" + socketType);

        // Cancel the thread that completed the connection
        // 连接一个设备前，先要关闭已连接的设备
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Cancel the accept thread because we only want to connect to one device
        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        // Update UI title
        updateInfoUI();
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        mState = STATE_NONE;
        // Update UI title
//        updateInfoUI();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "无法连接到蓝牙设备");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;

        if (!bActivityDestroyed)
            updateInfoUI();

//        BtCommunicationService.this.start();
    }

    /**
     * 断开连接并通知UI修改
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "与蓝牙设备断开连接");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        mState = STATE_NONE;
        if (!bActivityDestroyed)
            updateInfoUI();

//        BtCommunicationService.this.start();
    }

    /**
     * 监听其他蓝牙设备的连接
     */
    private class AcceptThread extends Thread
    {
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;

        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            try {
                if (secure) {
//                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, SECURE_UUID);
                    tmp = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, DEVICE_UUID);
                } else {
//                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, INSECURE_UUID);
                    tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, DEVICE_UUID);
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {
            Log.d(TAG, "Socket Type: " + mSocketType + " BEGIN mAcceptThread " + this);
            setName("AcceptThread" + mSocketType);

            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + " accept() failed", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BtCommunicationService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // Situation normal. Start the connected thread.
                                connected(socket, socket.getRemoteDevice(), mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "END mAcceptThread, socket Type: " + mSocketType);

        }

        public void cancel() {
            Log.d(TAG, "Socket Type" + mSocketType + "cancel " + this);
            try {
//                mState = STATE_NONE;
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket Type" + mSocketType + "close() of server failed", e);
            }
        }
    }


    /**
     * 该线程用于连接蓝牙设备，因为connect()函数是阻塞的
     */
    private class ConnectThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // 用指定的蓝牙设备获取BluetoothSocket
            try {
                if (secure) {
//                     Log.d(TAG, "DEVICE_UUID: " + DEVICE_UUID.toString());
//                    tmp = device.createRfcommSocketToServiceRecord(SECURE_UUID);
                    tmp = device.createRfcommSocketToServiceRecord(DEVICE_UUID);
                } else {
//                    tmp = device.createInsecureRfcommSocketToServiceRecord(INSECURE_UUID);
                    tmp = device.createInsecureRfcommSocketToServiceRecord(DEVICE_UUID);
                }
            } catch (Exception e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
            Log.e(TAG, "是否连接: " + mmSocket.isConnected());
            mAdapter.cancelDiscovery();
            mState = STATE_CONNECTING;
        }

        public void run()
        {
            Log.i(TAG, "BEGIN mConnectThread SocketType: " + mSocketType);
            setName("ConnectThread" + mSocketType);

            // 连接前请务必先停止发现操作
            try {
                // 阻塞函数
                mmSocket.connect();
            } catch (Exception e) {
                Log.e(TAG,  "蓝牙连接失败：" + e.getMessage());

                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() " + mSocketType + " socket during connection failure", e2);
                }
                e.printStackTrace();
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BtCommunicationService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mState = STATE_NONE;
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    /**
     * 该线程用来接收数据
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            mState = STATE_CONNECTED;
        }

        /** 与蓝牙设备建立连接后，保持读 */
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024 * 2];
            int bytes;

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    if (bytes > 0) {
                        /**
                         * 将数据进行处理或展示
                         * 根据不同的头分发到不同的处理单元
                         **/
//                        dispatchMessage(buffer, bytes);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "run: ", e);
                    connectionLost();
                    break;
                }
            }
        }

        /**
         * 建立连接后，向对端蓝牙设备发送数据
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mState = STATE_NONE;
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    public void setbActivityDestroyed(boolean bActivityDestroyed) {
        this.bActivityDestroyed = bActivityDestroyed;
    }

    //解析出正负位和后一位，只解析两位
//    private String AnalysisPositiveNegative(String numberStr) {
    public static String AnalysisPositiveNegative(String numberStr) {
        char[] chars = numberStr.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        if (chars.length < 1)
            return "ERROR";
        if (chars[0] == '0') {
            if (chars[1] == '0'){
                return "";  //若正负位后面的数字是0
            } else {
                return stringBuilder.append(chars[1]).toString();
            }
        } else if(chars[0] == '1') {
            if (chars[1] == '0') {
                return stringBuilder.append('-').toString(); //若正负位后面的数字是0
            } else {
                return stringBuilder.append('-').append(chars[1]).toString();
            }
        }
        else
            return "ERROR";
    }

    //解析出两位数的字符串，将十六进制字符串转换成十进制字符串
    public static String AnalysisNumber(String num) {
        int number = Integer.parseInt(num, 16);
        if (0 <= number && number < 10)
        {
            return "0" + number;
        }
        else if (number >= 10)
            return String.valueOf(number);
        else
            return "number error";
    }

    //整数的字符串首位去0，例如 "0001123" --> "1123"
    //包括正负位的考虑
    public static String primacyRemove0(String numberStr) {
        char[] chars = numberStr.toCharArray();
        int i;
        if (chars[0] == '-') {         //负数
            i = 1;
            for (; i < chars.length; i++) {
                if (chars[i] != '0')
                    break;
            }
            if (i == chars.length) {
                return String.valueOf(chars[i -1]);
            } else {
                return "-" + numberStr.substring(i);
            }
        } else {                    //正数
            i = 0;
            for (; i < chars.length; i++) {
                if (chars[i] != '0')
                    break;
            }
//        Log.e("Therapy_second_control", "primacyRemove0:i =  " + i);
            if (i == chars.length) {
                return String.valueOf(chars[i -1]);
            } else {
                return numberStr.substring(i);
            }
        }
    }
}
