package rui.com.crashlog.activity;

import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.ButterKnife;
import rui.com.crashlog.AudioManager.AudioService;
import rui.com.crashlog.R;

public class TestConstraintLayoutActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "TestConstraintLayoutAct";

    AudioService mAudioService = null;

    @BindView(R.id.record_btn)
    Button record_btn;
    @BindView(R.id.playPcmAudio_btn)
    Button playPcmAudio_btn;
    @BindView(R.id.playWavAudio_btn)
    Button playWavAudio_btn;
//    @BindView(R.id.glSurfaceView)
//    GLSurfaceView glSurfaceView;

//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_constraint_layout);
        ButterKnife.bind(this); /** 必须在setContentView（）函数之后 **/
        initData();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initView() {
//        playPcmAudio_btn.setVisibility(View.INVISIBLE);
//        playWavAudio_btn.setVisibility(View.INVISIBLE);
        setBtnListeners();

//        initOpenGLES();
        initFFmpeg();
    }

    public void initData() {
        mAudioService = AudioService.getInstance();

        boolean[] booleans = new boolean[1024];

    }

    public void setBtnListeners() {
        record_btn.setOnClickListener(this);
        playPcmAudio_btn.setOnClickListener(this);
        playWavAudio_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.record_btn:
                if (!mAudioService.isRecording()) {
                    mAudioService.statAudioRecord();
                    record_btn.setText("停止录音");
//                    playPcmAudio_btn.setVisibility(View.INVISIBLE);
//                    playWavAudio_btn.setVisibility(View.INVISIBLE);
                }
                else {
                    mAudioService.stopAudioRecord();
                    record_btn.setText("开始录音");
//                    playPcmAudio_btn.setVisibility(View.VISIBLE);
//                    playWavAudio_btn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.playPcmAudio_btn:
                mAudioService.playPcmAudio();
                break;
            case R.id.playWavAudio_btn:
                mAudioService.playWavAudio();
                break;
        }
    }

    public void initOpenGLES()
    {
//        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        GLSurfaceView.Renderer renderer = new GLSurfaceView.Renderer() {
            @Override
            /** 创建时调用一次该方法，执行只需要执行一次的操作，例如设置OpenGL环境参数或初始化OpenGL图形对象 **/
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {
                //将背景颜色设置为全黑
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            }

            @Override
            /** 系统在每次重画GLSurfaceView时调用这个方法。使用此方法作为绘制（和重新绘制）图形对象的主要执行方法 **/
            public void onSurfaceChanged(GL10 gl, int width, int height) {
                GLES20.glViewport(0, 0, width, height);
            }

            @Override
            /** 当GLSurfaceView的发生变化时，系统调用此方法，这些变化包括GLSurfaceView的大小或设备屏幕方向的变化。
             *  例如：设备从纵向变为横向时，系统调用此方法。使用此方法来响应GLSurfaceView容器的改变 **/
            public void onDrawFrame(GL10 gl) {
                //重绘背景颜色
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            }
        };

//        glSurfaceView.setRenderer(renderer);
    }

    public void initFFmpeg()
    {}

}
