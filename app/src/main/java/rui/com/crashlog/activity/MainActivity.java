package rui.com.crashlog.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import rui.com.crashlog.AudioManager.AudioService;
import rui.com.crashlog.R;
import rui.com.crashlog.adapter.ButtonPagerAdapter;
import rui.com.crashlog.adapter.IPagerPosition;
import rui.com.crashlog.adapter.ViewPagerAdapter;
import rui.com.crashlog.widget.customVIew.Indicator;
import rui.com.crashlog.widget.customVIew.CustomProgressDialog;

public class MainActivity extends AppCompatActivity implements  ViewPager.OnPageChangeListener, IPagerPosition
{
    private final String TAG = getClass().getSimpleName();

    private ViewPager viewPager = null;
//    private ViewPagerAdapter viewPagerAdapter = null;
//    private Indicator indicator = null;
//    private ButtonPagerAdapter buttonPagerAdapter = null;
    private Button left_forward = null;
    private Button right_forward = null;

    private ViewPager buttonPager = null;

    int currentPage;

    //    private List<Integer> isItemPressed = new ArrayList<>();
    private Toast toast = null;

    private List<Button> mButtons = new ArrayList<>();
    private int mButtons_size;

    public enum  bool
    {
        TRUE,
        FALSE,
    }

    private List<ImageView> imageViews = new ArrayList<>();
    private static final int REQUESTCODE = 1001;

    @Override
    public boolean navigateUpToFromChild(Activity child, Intent upIntent) {
        return super.navigateUpToFromChild(child, upIntent);
    }

    @Override
    public boolean navigateUpTo(Intent upIntent) {
        return super.navigateUpTo(upIntent);
    }

    CustomProgressDialog progressDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /** ---------------------- test ---------------------*/
//        progressDialog = new CustomProgressDialog(this);
////        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setCancelable(true);
//        progressDialog.show();


        initData();
        initView();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            permissionsCheck(new String[] {
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA,
            });
        }
    }

    private View.OnClickListener main_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.left_forward:
                    buttonPager.setCurrentItem(--currentPage);
                    if (currentPage == 0)
                        left_forward.setVisibility(View.INVISIBLE);
                    else
                        left_forward.setVisibility(View.VISIBLE);
                    right_forward.setVisibility(View.VISIBLE);
                    break;
                case R.id.right_forward:
                    buttonPager.setCurrentItem(++currentPage);
                    if (mButtons_size - 1 == currentPage)  /** mButtons.size()计算会花费时间，有可能导致right_forward不消失，故改成变量 **/
                        right_forward.setVisibility(View.INVISIBLE);
                    else
                        right_forward.setVisibility(View.VISIBLE);
                    left_forward.setVisibility(View.VISIBLE);
//                    Log.e("herx___" + TAG, "======================================> onClick().currentPage= " + currentPage);
                    break;
            }
        }
    };

    public void initView()
    {
//        gridView = findViewById(R.id.grid_emoji);
        Indicator indicator = findViewById(R.id.splashIndicator);
        viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        int num = viewPagerAdapter.getCount();
        viewPagerAdapter.setInterface_PagerPosition(this);
        indicator.setCount(num);

        viewPager.setOffscreenPageLimit(num);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(indicator);

        buttonPager = findViewById(R.id.buttons_viewPager);
        ButtonPagerAdapter buttonPagerAdapter = new ButtonPagerAdapter(this, mButtons);
//        buttonPager.setOffscreenPageLimit(0);
        buttonPager.setAdapter(buttonPagerAdapter);
        buttonPager.addOnPageChangeListener(this);
        buttonPager.setCurrentItem(0);
        currentPage = 0;

        left_forward = findViewById(R.id.left_forward);
        left_forward.setOnClickListener(main_onClickListener);
        left_forward.setVisibility(View.INVISIBLE);

        right_forward = findViewById(R.id.right_forward);
        right_forward.setOnClickListener(main_onClickListener);

    }


    public void initData()
    {
//        Log.e("herx___"+TAG, "===============================> isItemPressed.size()=" +isItemPressed.size());
        mButtons.clear();

/** 从xml文件中添加按钮，可以使代码更简洁 **/
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.my_buttons, null);

        Button nShape_button = linearLayout.findViewById(R.id.nShape_dadada);
        nShape_button.setOnClickListener(buttons_onClickListener);
        mButtons.add(nShape_button);

        Button navigation_button = linearLayout.findViewById(R.id.navigation_dadada);
        navigation_button.setOnClickListener(buttons_onClickListener);
        mButtons.add(navigation_button);

        Button okhttp_button = linearLayout.findViewById(R.id.okhttp_dadada);
        okhttp_button.setOnClickListener(buttons_onClickListener);
        mButtons.add(okhttp_button);

        Button rtsp_button = linearLayout.findViewById(R.id.rtsp_dadada);
        rtsp_button.setOnClickListener(buttons_onClickListener);
        mButtons.add(rtsp_button);

        Button constraintLayout = linearLayout.findViewById(R.id.constraintLayout_dadada);
        constraintLayout.setOnClickListener(buttons_onClickListener);
        mButtons.add(constraintLayout);

        Button mediaRecorder_button = linearLayout.findViewById(R.id.mediaRecorderTest_dadada);
        mediaRecorder_button.setOnClickListener(buttons_onClickListener);
        mButtons.add(mediaRecorder_button);

        Button TcpSocket_dadada = linearLayout.findViewById(R.id.TcpSocket_dadada);
        TcpSocket_dadada.setOnClickListener(buttons_onClickListener);
        mButtons.add(TcpSocket_dadada);

        Button Camera2_dadada = linearLayout.findViewById(R.id.Camera2_dadada);
        Camera2_dadada.setOnClickListener(buttons_onClickListener);
        mButtons.add(Camera2_dadada);

        linearLayout.removeAllViews();
        mButtons_size = mButtons.size();



/** 在代码中动态添加按钮，代码较冗余 **/
//        Button nShape_button = new Button(this);
//        nShape_button.setBackground(getResources().getDrawable(R.drawable.bilibili_button));
//        nShape_button.setText("^o^ 彈幕聊天 ^o^");
//        mButtons.add(nShape_button);
//
//        Button bezier_button = new Button(this);
//        bezier_button.setBackground(getResources().getDrawable(R.drawable.bilibili_button));
//        bezier_button.setText("贝塞尔曲线");
//        mButtons.add(bezier_button);
    }

    private View.OnClickListener buttons_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Class<?> targetClass = null;
            switch (v.getId())
            {
                case R.id.nShape_dadada:
//                    Intent intent = new Intent(MainActivity.this, NShape_Activity.class);
                    targetClass = NShape_Activity.class;
                    break;
                case R.id.navigation_dadada:
//                    Intent intent1 = new Intent(MainActivity.this, BezierCurves_Activity.class);
                    targetClass = Bezier_Curves_Activity.class;
                    break;
                case R.id.okhttp_dadada:
                    targetClass = TestOkhttp.class;
                    break;
                case R.id.rtsp_dadada:
                    targetClass = VideoViewActivity.class;
                    break;
                case R.id.constraintLayout_dadada:
                    targetClass = TestConstraintLayoutActivity.class;
                    break;
                case R.id.TcpSocket_dadada:
                    targetClass = TcpSocket_Activity.class;
                    break;
                case R.id.mediaRecorderTest_dadada:
                    targetClass = MediaRecorderTest.class;
                    break;
                case R.id.Camera2_dadada:
                    targetClass = TestCamera2Activity.class;
                    break;
            }
            if (targetClass != null)
            {
                Intent intent = new Intent(MainActivity.this, targetClass);
                startActivity(intent);
            }
        }
    };

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
        AudioService.getInstance().destroyAudioRecord();

//        throw new RuntimeException("I am checking that Crash log of XiaoMi's phone "
//        + "I found input *#*#2846579#*#* on HUAWEI's phone, see crash log!");
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
    }

    @Override
    public int onGetContextPagerPosition()
    {
        return viewPager.getCurrentItem();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        currentPage = position;
        left_forward.setVisibility(View.VISIBLE);
        right_forward.setVisibility(View.VISIBLE);
        if (currentPage == 0)
        {
            left_forward.setVisibility(View.INVISIBLE);
        }
        else if (currentPage == mButtons.size() - 1)
        {
            right_forward.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPageSelected(int position)
    {}

    @Override
    public void onPageScrollStateChanged(int state)
    {}

    /*----------------------------------------------------- 申请权限套路start -----------------------------------------------------*/
    public void permissionsCheck(String[] permissions)
    {
        List<String> permissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            for (String p : permissions) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(p);
                }
            }
            if (!permissionList.isEmpty()) {
                requestPermissions(permissionList.toArray(new String[permissionList.size()]), REQUESTCODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> deniedPers = new ArrayList<>();
        switch (requestCode)
        {
            case REQUESTCODE:
                for (int i = 0; i < permissions.length; i++)
                {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    {
                        deniedPers.add(permissions[i]);
                    }
                }
                if (deniedPers.isEmpty())
                {
                    Log.i(TAG, "所有权限都被授权");
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("授权失败项")
                            .setMessage(printDeniedPermissions(deniedPers))
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                break;
            default:
                break;
        }
    }

    private String printDeniedPermissions(List<String> deniedPers)
    {
        String buffer = "";
//        StringBuffer buffer1 = new StringBuffer();
        for (String p : deniedPers)
        {
//            buffer1.append(p).append(";\n");
            buffer += p + ";\n";
        }
        return buffer;
    }
    /*----------------------------------------------------- 申请权限套路end -----------------------------------------------------*/
}
