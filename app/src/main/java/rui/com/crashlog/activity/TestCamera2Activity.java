package rui.com.crashlog.activity;

import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import rui.com.crashlog.Application.MyApplication;
import rui.com.crashlog.R;
import rui.com.crashlog.TestCode.TestLeakCanary;

public class TestCamera2Activity extends AppCompatActivity
{
    private static final String TAG = "TestCamera2Activity";

    CameraManager mCameraManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_camera2);

        MyApplication.getRefWatcher(this).watch(this);

        TestLeakCanary manager = TestLeakCanary.getInstance(this);
    }
}
