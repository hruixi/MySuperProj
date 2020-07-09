package com.alinket.libraryffmpeg;

import android.os.AsyncTask;

public class FFmpegJNI {
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("ffmpegwrapper");
    }

    public FFmpegJNI() {

    }

    public interface FFmpegInterface {
        void onStart();
        void onProgress(int progress);
        void onEnd(int result);
    }

    public static int execute(String[] commands) {
        return run(commands);
    }

    public static void execute(final String[] commands, final FFmpegInterface ffmpegInterface) {
        new AsyncTask<String[], Integer, Integer>() {
            @Override
            protected Integer doInBackground(String[]... params) {
                return run(params[0]);
            }

            @Override
            protected void onPreExecute() {
                if (ffmpegInterface != null) {
                    ffmpegInterface.onStart();
                }
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (ffmpegInterface != null) {
                    ffmpegInterface.onProgress(values[0]);
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                if (ffmpegInterface != null) {
                    ffmpegInterface.onEnd(result);
                }
            }
        };

    }

    private static native int run(String[] commands);
}
