package rui.com.crashlog.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rui.com.crashlog.HttpRev.ST_AddCase;
import rui.com.crashlog.HttpRev.Translation;
import rui.com.crashlog.R;

public class TestOkhttp extends AppCompatActivity
{
    String TAG = getClass().getSimpleName();
    OkHttpClient okHttpClient = new OkHttpClient();
    String responseBody = "";
    File img = null;
    LinearLayout link = null;
    ScrollView content_sl = null;
    RelativeLayout content_img = null;
    ImageView image_iv = null;
    ProgressDialog progressDialog = null;
    WebView webView = null;
    private static final String BaseURL = "http://192.168.1.100:8080/premed/";
    private static final String BaseURL2 = "http://192.168.1.44:8080/premed/";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_okhttp);

        initView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(img != null && img.exists())
            img.delete();
    }

    public void initView()
    {
        link = findViewById(R.id.container_ll);
        content_sl = findViewById(R.id.container_sl);
        content_img = findViewById(R.id.container_img);
        image_iv = findViewById(R.id.image_iv);
        image_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content_sl.setVisibility(View.INVISIBLE);
                content_img.setVisibility(View.INVISIBLE);
                image_iv.setVisibility(View.INVISIBLE);
                link.setVisibility(View.VISIBLE);
            }
        });

        webView = findViewById(R.id.webview);
        /*设置支持JavaScript*/
        webView.getSettings().setJavaScriptEnabled(true);
        /*设置支持JavaScript弹窗*/
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.setWebChromeClient(new WebChromeClient() {
//            //获取网站标题
//            @Override
//            public void onReceivedTitle(WebView view, String title) {
//                Log.e(TAG, "onReceivedTitle: " + title);
//                super.onReceivedTitle(view, title);
//            }
//            /*加载进度*/
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                super.onProgressChanged(view, newProgress);
//            }
//        });
        webView.setWebViewClient(new WebViewClient() {
            /*开始加载网页*/
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            /*网页加载完成*/
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        link.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                doGet();
                doRetrofitGet();
            }
        });
        link.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDownloadAsynFile(1);
            }
        });
        link.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDownloadAsynFile(2);
            }
        });
        link.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGlidePicture();
            }
        });
        link.getChildAt(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doWebviewDisplay(4);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }

    public void showText(String content)
    {
        progressDialog.dismiss();
        link.setVisibility(View.INVISIBLE);
        image_iv.setVisibility(View.INVISIBLE);
        content_img.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);
        content_sl.setVisibility(View.VISIBLE);

        TextView tv = new TextView(this);
//        ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
//        tv.setGravity();
        tv.setText(content);

        content_sl.addView(tv);
//        wv.
    }

    public void showImage(File img)
    {
        progressDialog.dismiss();
        link.setVisibility(View.INVISIBLE);
        image_iv.setVisibility(View.INVISIBLE);
        content_sl.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);
        content_img.setVisibility(View.VISIBLE);

        content_img.removeAllViews();
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final ImageView image = new ImageView(this);
        image.setImageURI(Uri.fromFile(img));
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_iv.setVisibility(View.INVISIBLE);
                content_img.setVisibility(View.INVISIBLE);
                content_sl.setVisibility(View.INVISIBLE);
                link.setVisibility(View.VISIBLE);
            }
        });
        content_img.addView(image, lp);
//        content_img.setImageURI(Uri.fromFile(img));
    }

    public String getHttpUrl(int index)
    {
        TextView tv = (TextView) link.getChildAt(index);
        return tv.getText().toString().trim();
    }

    /** Okhttp 3.0
     * 简单获得http响应体，纯文本形式展示 **/
    public void doGet()
    {
        progressDialog.setMessage("Content loading....");
        progressDialog.show();

        Request request = new Request.Builder()
                                    .url(getHttpUrl(0))
                                    .get()
                                    .build();
        try
        {
            okHttpClient.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    progressDialog.dismiss();
                    showText(e.toString() + " \nGET失败");
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException
                {
                    Log.e(TAG,  "response.code() = " + response.code());
                    if (response == null)
                    {
                        Log.e(TAG, response.cacheResponse().toString());
                        Log.e(TAG, response.body().toString());
                    }
                    else
                    {
                        responseBody = response.body().string();
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                showText(responseBody + " \nGET成功");
                            }
                        });
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            showText(e.toString() + " \nGET失败");
            Log.e(TAG, e.toString());
        }
    }

    /** Okhttp 3.0
     * 异步下载网络文件——加载网络图片文件 **/
    public void doDownloadAsynFile(int index)
    {
        progressDialog.setMessage("Picture Downloading...");
        progressDialog.show();

        Request request = new Request.Builder()
                                    .url(getHttpUrl(index))
                                    .build();
        /** 回调函数都工作在子线程中，若想更新UI，需要使用Handler回归到主线程
         *  在此使用Activity下的runOnUiThread(new Runnable()) **/
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                progressDialog.dismiss();
                showText(e.toString() + " \nGET失败");
                Log.e(TAG, "==========================================> onFailure\n");
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                Log.e(TAG, "==========================================> onResponse");
                InputStream is = response.body().byteStream();
                img = new File("/sdcard/Pictures/first.jpg");
//                File img = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                try
                {
                    FileOutputStream os = new FileOutputStream(img);
                    byte[] buff = new byte[1024];
                    int len = 0;
                    while ((len = is.read(buff)) != -1)
                    {
//                        Log.e(TAG, "==========================================> buff = " + buff);
                        os.write(buff, 0, len);
                    }
                    os.flush();
                }catch (IOException e)
                {
                    Log.e(TAG, "IOException");
                    Log.e(TAG, e.toString());
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        Log.e(TAG, "==========================================> onResponse_run()");
                        showImage(img);
                    }
                });
            }
        });
    }

    /** 测试用Glide框架加载图片 **/
    public void doGlidePicture()
    {
        link.setVisibility(View.INVISIBLE);
        content_img.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.INVISIBLE);
        image_iv.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(getHttpUrl(3))
                .into(image_iv);
//                .into(new Glide)
    }

    /** 测试用WebView展示http的响应体 **/
    public void doWebviewDisplay(int index)
    {
        link.setVisibility(View.INVISIBLE);
        content_sl.setVisibility(View.INVISIBLE);
        content_img.setVisibility(View.INVISIBLE);
        image_iv.setVisibility(View.INVISIBLE);
        webView.setVisibility(View.VISIBLE);

        webView.loadUrl(getHttpUrl(index));
    }

    /** retrofit 2.0
     *  GET
     * 请求百度翻译API **/
    public interface GetRequest_Interface
    {
//        @GET("ajax.php?a=fy&f=auto&t=auto&w=we%20are")
//        @GET("ajax.php")
        @GET("doctorController/addCase")
        /** 无参数 URL中包括参数 **/
//        retrofit2.Call<Translation> getCall();
        /** 带参数 @Query **/
//        retrofit2.Call<Translation> getCall(@Query("a") String a, @Query("f") String f, @Query("t") String t, @Query("w") String w);

        retrofit2.Call<ST_AddCase> getCall(@Query("deviceId") String deviceId,                      //设备id
                                           @Query("hospitalName") String hospitalName,              //医院名称
                                           @Query("name") String name,                              //病人姓名
                                           @Query("idNumber") String idNumber,                      //病人身份证号
                                           @Query("doctorName") String doctorName,                  //医生姓名

                                           @Query("doctorNumber") String doctorNumber,              //医生账号
                                           @Query("sex") String sex,                                //病人姓名
                                           @Query("subject") String subject,                        //科目
                                           @Query("bedNumber") String bedNumber,                    //床号
                                           @Query("chiefComplaint") String chiefComplaint,          //病人主诉
                                           @Query("chiefPhysician") String chiefPhysician,          //主任医师

                                           @Query("clinicalDiagnosis") String clinicalDiagnosis,    //临床诊断
                                           @Query("date") String date,                              //诊断日期
                                           @Query("imageNumber") String imageNumber,                //影像号
                                           @Query("cycle") String cycle,                            //周期（第一周第一次治疗）
                                           @Query("endImage") String endImage,                      //核素结束图像

                                           @Query("imageConclusion") String imageConclusion,        //核素图像结论
                                           @Query("nuclideImage") String nuclideImage,              //核素图像
                                           @Query("point") Integer point,                           //点数
                                           @Query("position") String position,                      //治疗位置
                                           @Query("strikeFrequency") Integer strikeFrequency,       //打击次数

                                           @Query("treatmentArea") String treatmentArea,            //治疗区域
                                           @Query("colorDopplerImage") String colorDopplerImage,    //彩超图像
                                           @Query("electrocardiogram") String electrocardiogram,    //心电图
                                           @Query("advice") String advice,                          //门诊号（送诊号）
                                           @Query("inpatientNumber") String inpatientNumber,        //住院号

                                           @Query("department") String department,                  //科室
                                           @Query("address") String address,                        //地址
                                           @Query("age") Integer age,                               //年龄
                                           @Query("birthday") String birthday,                      //生日
                                           @Query("checkNumber") String checkNumber,                //

                                           @Query("condition") String condition,                    //什么病
                                           @Query("endConclusion") String endConclusion,            //核素结束结论
                                           @Query("endDate") String endDate,                        //结束日期
                                           @Query("phone") String phone,                            //手机号
                                           @Query("starDate") String starDate,                      //入院日期

                                           @Query("wardNumber") String wardNumber,                  //病房号
                                           @Query("work") String work,                              //工作
                                           @Query("workUnit") String workUnit                       //工作单位
        );

        /** 带参数 @QueryMap **/
//        retrofit2.Call<Translation> getCall(@QueryMap Map<String, String> params);

        // 注解里传入 网络请求 的部分URL地址
        // Retrofit把网络请求的URL分成了两部分：一部分放在Retrofit对象里，另一部分放在网络请求接口里
        // 如果接口里的url是一个完整的网址，那么放在Retrofit对象里的URL可以忽略
        // getCall()是接受网络请求数据的方法
    }

    public void doRetrofitGet()
    {
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
                .baseUrl(BaseURL2) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //retrofit与rxjava的结合使用
                .build();

        // 创建 网络请求接口 的实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        //对 发送请求 进行封装
//        retrofit2.Call<Translation> call = request.getCall();
//        retrofit2.Call<Translation> call = request.getCall("fy", "auto", "auto", "we%20love");
        Map<String, String> map = new HashMap<>();
//        map.put("a", "fy");
//        map.put("f", "auto");
//        map.put("t", "auto");
//        map.put("w", "we%20family");

        retrofit2.Call call = request.getCall(
                "A-a-15",
                "测试医院",
                "harry",
                "1211111113111151111",
                "张医生",

                "doctorNumber",
                "男",
                "内科",
                "001",
                "病人主诉",
                "张医生",

                "临床诊断",
                "2018/1/8",
                "No:123123123123123",
                "第一周 第一次治疗",
                "/premed/imgs/imgsxr/bedimage.png",

                "核素图像结论",
                "/premed/imgs/imgsxr/bedimage.png",
                1000,
                "左心房",
                100,

                "心脏",
                "/premed/imgs/imgsxr/bedimage.png",
                "/premed/imgs/imgsxr/bedimage.png",
                "12138",
                "12138",

                "放射科",
                "上海市浦东新区",
                18,
                "1995/10/10",
                "10",

                "心脏病",
                "核素结束结论",
                "2018/1/1",
                "1234567809",
                "2018/1/1",

                "12138",
                "IT",
                "上海市浦东新区"
        );

        //发送网络请求(异步)
       call.enqueue(new retrofit2.Callback<ST_AddCase>() {
           @Override
           public void onResponse(retrofit2.Call<ST_AddCase> call, retrofit2.Response<ST_AddCase> response)
           {
               // 处理返回的数据结果
               if (response.body() != null)
               {
                   showText(response.body().showStr());
               }
               else
               {
                   Log.e(TAG, "响应体为空: " );
                   Toast.makeText(TestOkhttp.this, "响应体为空", Toast.LENGTH_SHORT).show();
               }
           }

           @Override
           public void onFailure(retrofit2.Call<ST_AddCase> call, Throwable t) {
               Log.e(TAG, "onFailure: " + t.getMessage() );
               Toast.makeText(TestOkhttp.this, "连接失败", Toast.LENGTH_SHORT).show();
           }
       });
    }



}
