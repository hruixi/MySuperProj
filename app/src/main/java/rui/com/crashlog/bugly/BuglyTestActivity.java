package rui.com.crashlog.bugly;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.bugly.crashreport.CrashReport;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import rui.com.crashlog.R;

public class BuglyTestActivity extends AppCompatActivity {
    private Unbinder mUnbinder;
//    @BindView(R.id.crash_btn)
//    Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugly_test);
//        mUnbinder = ButterKnife.bind(this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mUnbinder.unbind();
    }

//    @OnClick(R.id.crash_btn)
//    public void onBtnClick(View view) {
//        CrashReport.testJavaCrash();
//    }
}
