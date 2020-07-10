package rui.com.crashlog.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import rui.com.crashlog.R;
import rui.com.crashlog.widget.customVIew.NShapeView;

public class NShape_Activity extends AppCompatActivity {

    NShapeView nShapeView;
    Button san, liu, jiu, rotation;
    ObjectAnimator animator = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nshape_);

        initView();
        initMediaPlayer();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void initView()
    {
        nShapeView = (NShapeView) findViewById(R.id.nshape_view);

        san = findViewById(R.id.sanjiaoxing);
        san.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                nShapeView.setmN(6);
                MediaPlayer mediaPlayer = MediaPlayer.create(NShape_Activity.this, R.raw.m4a1);
                mediaPlayer.start();
                nShapeView.setAngle(90);
            }
        });
        liu = findViewById(R.id.liubianxing);
        liu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                nShapeView.setmN(4);
                MediaPlayer mediaPlayer = MediaPlayer.create(NShape_Activity.this, R.raw.wanger);
                mediaPlayer.start();
                nShapeView.setAngle(60);
            }
        });
        jiu = findViewById(R.id.jiubianxing);
        jiu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                nShapeView.setmN(2);
                MediaPlayer mediaPlayer = MediaPlayer.create(NShape_Activity.this, R.raw.wangsan);
                mediaPlayer.start();
                nShapeView.setAngle(45);
            }
        });


        animator = ObjectAnimator.ofFloat(nShapeView, "rotation", 0, 360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.RESTART);
        /** 设置LinearInterpolator，使得动画平滑执行 **/
        animator.setInterpolator(new LinearInterpolator());
        animator.start();


        rotation = findViewById(R.id.rotation);
        rotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (animator.isPaused())
                {
                    animator.resume();
                    MediaPlayer mediaPlayer = MediaPlayer.create(NShape_Activity.this, R.raw.on8k16bit);
                    mediaPlayer.start();
                    rotation.setText("狗带");
                }
                else {
                    animator.pause();
                    MediaPlayer mediaPlayer = MediaPlayer.create(NShape_Activity.this, R.raw.off8k16bit);
                    mediaPlayer.start();
                    rotation.setText("旋转，跳跃");
                }
            }
        });

    }

    private void initMediaPlayer()
    {
    }
}
