package rui.com.crashlog.VideoManager;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class VideoService
{
    private static final String TAG = "VideoService";
    private static final int BitRate            = 200000;   //设置编码码率为2M
    private static VideoService mVideoService   = null;
    private MediaExtractor mMediaExtractor      = null;     //作用是把音频和视频的数据进行分离
    private MediaMuxer mMediaMuxer              = null;     //作用是生成音频或视频文件；还可以把音频与视频混合成一个音视频文件
    private MediaCodec mMediaCodec              = null;     //是基本的多媒体编解码器（音视频编解码组件）,通常和 MediaExtractor, MediaSync, MediaMuxer, MediaCrypto, MediaDrm, Image, Surface, and AudioTrack 一起使用

    private VideoService() {
        mMediaExtractor = new MediaExtractor();
//        mMediaMuxer     = new MediaMuxer();

        try {
            mMediaCodec = MediaCodec.createByCodecName("codecVideo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static VideoService getInstance() {
        if (mVideoService == null)
            mVideoService = new VideoService();
        return mVideoService;
    }

    /**
     *  提取音频轨道和视频轨道
     */
    public void extractTrack() {}

    /**
     *  将音频轨道和视频轨道生成MP4文件等
     */
    public void muxerTrack() {}


    /**
     *  将camera采集的YUV数据进行编码
     */
    public void codecVideo() {
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BitRate);
        /**
         CQ  表示完全不控制码率，尽最大可能保证图像质量；
         CBR 表示编码器会尽量把输出码率控制为设定值；
         VBR 表示编码器会根据图像内容的复杂度（实际上是帧间变化量的大小）来动态调整输出码率，图像复杂则码率高，图像简单则码率低；


         质量要求高、不在乎带宽、解码器支持码率剧烈波动的情况下，可以选择 CQ 码率控制策略。
         VBR 输出码率会在一定范围内波动，对于小幅晃动，方块效应会有所改善，但对剧烈晃动仍无能为力；连续调低码率则会导致码率急剧下降，如果无法接受这个问题，那 VBR 就不是好的选择。
         CBR 的优点是稳定可控，这样对实时性的保证有帮助。所以 WebRTC 开发中一般使用的是CBR。
         **/
        mediaFormat.setInteger(MediaFormat.KEY_BITRATE_MODE, MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR);  //码率调整模式

        mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        /**
         * 动态调整目标码率
         */
        Bundle param = new Bundle();
        param.putInt(MediaCodec.PARAMETER_KEY_VIDEO_BITRATE, BitRate);
        mMediaCodec.setParameters(param);
    }

    /**
     * 视频数据采集,使用Camera采集
     */
    public void getH264Data()
    {
        Camera camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);                  //设置预览画面格式
        parameters.setPreviewSize(1280, 720);            //设置预览分辨率

        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

            }
        });
    }


}
