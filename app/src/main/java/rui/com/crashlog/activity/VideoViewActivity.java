package rui.com.crashlog.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import rui.com.crashlog.R;

public class VideoViewActivity extends AppCompatActivity
{
    VideoView mVideoView = null;
    private static final String path = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";       //亲测可用
    private static final String path_rtsp = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov";              //亲测可用
//    private static final String path_rtsp = "rtsp://192.168.63.9/live/udp/ch0_0";                               //中惠板子

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        initView();
    }

    public void initView()
    {
        mVideoView = findViewById(R.id.player_vv);
        /*
         * 也可以用这个方法来播放流媒体
         * mVideoView.setVideoURI(Uri.parse(URLstring));
         */
        //设置videoview播放的路径
        mVideoView.setVideoPath(path_rtsp);
        //创建视频播放时的控制器，这个控制器可以自定义。此处是默认的实现
        mVideoView.setMediaController(new MediaController(this));
        //请求焦点
        mVideoView.requestFocus();
        //设置播放监听
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                //设置重放速度
//                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
        //加载结束后开始播放，这行代码可以控制视频的播放。
        mVideoView.start();
    }
}
