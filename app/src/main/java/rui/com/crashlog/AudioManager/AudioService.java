package rui.com.crashlog.AudioManager;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioService
{
    private static final String TAG = "AudioService";

    private static AudioService mAudioService        = null;

    private MediaRecorder mMediaRecorder            = null;                                         //可录制出音频文件，MP3，WAV，AMR
    private MediaPlayer mMediaPlayer                = null;                                         //可播放多种格式的声音文件,如MP3，AAC，WAV，OGG，MIDI等,会在framework层创建对应的音频解码器
    private AudioTrack mAudioTrack                  = null;//只能播放已经解码的PCM流，AudioTrack只支持wav格式的音频文件，因为wav格式的音频文件大部分都是PCM流。AudioTrack不创建解码器，所以只能播放不需要解码的wav文件
    private AudioRecord mAudioRecord                = null;                                         //录制出的是原始音频数据（即raw）

    private int recordBufSize                       = 0;                                            //AudioRecord的录音缓存大小
    private int trackBufSize                        = 0;                                            //mAudioTrack的播放缓存大小
//    private byte[] recordData                       = null;
//    private byte[] trackData                        = null;

    private int sampleRateInHz                      = 44100;                                        //音频采样率 44100Hz
    private int recordChannel                       = AudioFormat.CHANNEL_IN_STEREO;                //录制音频通道数.MONO单声道，STEREO立体声
    private int trackChannel                        = AudioFormat.CHANNEL_OUT_STEREO;               //播放音频通道数.MONO单声道，STEREO立体声
    private int audioFormat                         = AudioFormat.ENCODING_PCM_16BIT;               //音频编码制式 PCM-16bt
    private boolean bIsRecording                    = false;                                        //录音开始标志
    private boolean bIsetDataSrc                    = false;                                        //MediaPlay是否设置过数据源

    private static final String AudioPath   = Environment.getExternalStorageDirectory() + "/Rui/Audio/";    //原始音频/音频文件保存路径
    private static final String PcmFile     = "audio.pcm";                                                  //原始音频文件名
    private static final String WavFile     = "audio.wav";                                                  //WAV格式文件名
    private PcmToWav mPcmToWav              = null;

    private AudioService(){
        mPcmToWav = new PcmToWav(sampleRateInHz, recordChannel, audioFormat);

        recordBufSize       = AudioRecord.getMinBufferSize(sampleRateInHz , recordChannel, audioFormat);
        trackBufSize        = AudioTrack.getMinBufferSize(sampleRateInHz, recordChannel, audioFormat);
        Log.d(TAG, "initData: recordBufSize = " + recordBufSize);
        Log.d(TAG, "initData: trackBufSize = " + trackBufSize);
        mAudioRecord        = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                sampleRateInHz,
                                recordChannel,
                                audioFormat,
                                recordBufSize);
        mAudioTrack         = new AudioTrack(AudioManager.STREAM_MUSIC,     //该构造函数在API 26已经过时
                                sampleRateInHz,
                                trackChannel,
                                audioFormat,
                                trackBufSize,
                                AudioTrack.MODE_STREAM);                //AudioTrack有两种数据加载模式（MODE_STREAM和MODE_STATIC），对应的是数据加载模式和音频流类型

        /** 适用于API 23以上——Call requires API level 23 (current min is 19) **/
//        mAudioTrack         = new AudioTrack.Builder()
//                                .setAudioAttributes(new AudioAttributes.Builder()
//                                                    .setUsage(AudioAttributes.USAGE_ALARM)
//                                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                                                    .build())
//                                .setAudioFormat(new AudioFormat.Builder()
//                                                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                                                    .setSampleRate(sampleRateInHz)
//                                                    .setChannelMask(trackChannel)
//                                                    .build())
//                                .setBufferSizeInBytes(trackBufSize)
//                                .build();

        mMediaPlayer = new MediaPlayer();

        Log.e(TAG, "initData:isInitSuccess = " + isInitSuccess() );
    }

    /**
     * 获取单个实例
     * @return 对象
     */
    public static AudioService getInstance() {
        if (mAudioService == null)
            mAudioService = new AudioService();
        return mAudioService;
    }

    /**
     * 音频录制
     */
    public void statAudioRecord()
    {
        mAudioRecord.startRecording();
        bIsRecording = true;

        //起线程录音
        new Thread(new Runnable()
        {
            @Override
            public void run() {
                byte[] recordData = new byte[recordBufSize];

                //文件路径是否存在
                File audioDir = new File(AudioPath);
                if (!audioDir.exists()) {
                    audioDir.mkdirs();
                }
                //文件是否存在
                File audioFile = new File(AudioPath + PcmFile);
                if (!audioFile.exists()) {
                    try {
                        audioFile.createNewFile();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(audioFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (null != fos) {
                    while (bIsRecording) {
                        int read = mAudioRecord.read(recordData, 0 , recordBufSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
//                        Log.e(TAG, "statAudioRecord:read = " + read);
                        if (AudioRecord.ERROR_INVALID_OPERATION != read
                                && AudioRecord.ERROR_BAD_VALUE != read
                                )
                        {
                            try {
//                                Log.e(TAG, "statAudioRecord:recordData.length = " + recordData.length);
                                fos.write(recordData);
                                fos.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopAudioRecord()
    {
        mAudioRecord.stop();
        bIsRecording = false;

        mPcmToWav.pcmToWav(AudioPath + PcmFile, AudioPath + WavFile);
    }

    public void destroyAudioRecord()
    {
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
        }
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        bIsRecording = false;
        mAudioRecord = null;
        mAudioTrack = null;
        mMediaPlayer = null;
    }

    /**
     * 是否已初始化
     * @return
     */
    private boolean isInitSuccess()
    {
        return mAudioRecord != null && mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED;
    }

    /**
     * 是否正在录制中
     * @return
     */
    public boolean isRecording() {
        return bIsRecording;
    }

    /**
     * 播放原始数据的音频,即PCM
     */
    public void playPcmAudio()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] trackData = new byte[trackBufSize];
                try {
                    FileInputStream fis = new FileInputStream(AudioPath + PcmFile);

                    int readCount = 0;
                    while (fis.available() > 0) {
                        Log.d(TAG, "playPcmAudio: fis.available = " + fis.available());
                        readCount = fis.read(trackData);
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                            continue;
                        }
                        if (readCount != 0 && readCount != -1) {
                            mAudioTrack.play();
                            mAudioTrack.write(trackData, 0, readCount);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 播放wav格式的音频
     */
    public void playWavAudio()
    {
        if (mMediaPlayer != null) {
            try {
                if (!bIsetDataSrc) {
                    mMediaPlayer.setDataSource(new File(AudioPath + WavFile).getPath());
                    bIsetDataSrc = true;
                    mMediaPlayer.prepare();
                }
                mMediaPlayer.start();
            } catch (IOException e) {
                Log.e(TAG, "playWavAudio: " + e.getMessage());
            }
        }
    }
}
