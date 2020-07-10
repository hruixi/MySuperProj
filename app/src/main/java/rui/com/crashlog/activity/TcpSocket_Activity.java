package rui.com.crashlog.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import rui.com.crashlog.NetworkWrapper.TcpWrapper;
import rui.com.crashlog.R;

public class TcpSocket_Activity extends AppCompatActivity implements View.OnClickListener {
    private TcpWrapper mTcpWrapper = null;
    private Button mBtnSet, mBtnConnect, mBtnSend;
    private EditText mEditMsg;
    private TextView mClientState, mTvReceive;
    private TcpWrapper.HandleReceivedData mReceivedCallback = new TcpWrapper.HandleReceivedData() {
        @Override
        public void onReceive(final byte[] data, final int length) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvReceive.setText(new String(data,0, length));
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp_socket);

        mBtnSet = (Button)findViewById(R.id.bt_client_set);
        mBtnConnect = (Button)findViewById(R.id.bt_client_connect);
        mBtnSend = (Button)findViewById(R.id.bt_client_send);
        mEditMsg = (EditText)findViewById(R.id.client_sendMsg);
        mClientState = (TextView) findViewById(R.id.client_state);
        mTvReceive = (TextView) findViewById(R.id.client_receive);

        mBtnSet.setOnClickListener(this);
        mBtnConnect.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);

        mTcpWrapper = new TcpWrapper(0, 0,false);
        mTcpWrapper.setOnReceive(mReceivedCallback);
        mTcpWrapper.setOnStateChange(new TcpWrapper.StateChange() {
            @Override
            public void onConnect(final String addr) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TcpSocket_Activity.this, "连接成功" + addr, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDisconnect() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TcpSocket_Activity.this, "断开连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFail() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TcpSocket_Activity.this, "断开连接", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTcpWrapper != null) {
            mTcpWrapper.release();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_client_set:
                set();
                break;
            case R.id.bt_client_connect:
                mTcpWrapper.connect(mServerIp, mServertPort, 0);
                break;
            case R.id.bt_client_send:
                sendTxt();
                break;
            default:
                break;
        }
    }

    private String mServerIp = "192.168.1.15";  //服务端ip地址
    private int mServertPort = 8086; //服务端端口,默认为8086，可以进行设置
    private void set(){
        View setview = LayoutInflater.from(this).inflate(R.layout.dialog_clientset, null);


//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_clientset, null, false);
//        View.inflate()



        final EditText ipAddress = (EditText) setview.findViewById(R.id.edtt_ipaddress);
        final EditText editport = (EditText)setview.findViewById(R.id.client_port);
        Button ensureBtn = (Button)setview.findViewById(R.id.client_ok);

        ipAddress.setText(R.string.server_IpAddress);
        editport.setText(R.string.server_IpPort);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(setview); //设置dialog显示一个view
        final AlertDialog dialog = builder.show(); //dialog显示
        ensureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String port = editport.getText().toString();
                mServerIp = ipAddress.getText().toString();
                if(!TextUtils.isEmpty(port) && port.length() >0){
                    mServertPort = Integer.parseInt(port);
                }

                ipAddress.setText(mServerIp);
                editport.setText(port);

                dialog.dismiss(); //dialog消失
            }
        });
    }

    private void sendTxt(){
        if(mTcpWrapper.getState() != TcpWrapper.STATE_CONNECTED){
            Toast.makeText(this, "没有连接", Toast.LENGTH_SHORT).show();
            return;
        }
        String str = mEditMsg.getText().toString();
        if(str.length() == 0)
            return;
        mTcpWrapper.write(str.getBytes());
    }
}