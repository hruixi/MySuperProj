package rui.com.crashlog.widget.customVIew;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import java.util.zip.Inflater;

import rui.com.crashlog.R;

public class CustomProgressDialog extends Dialog {
    ObjectAnimator mAnimator = null;
    public static Context a;

    public CustomProgressDialog(Context context) {
        super(context);

        setCancelable(true);
        Window window = getWindow();
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_progress_dialog);
        //去除顶部状态栏
        if (window != null) {
//            int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
//            window.setFlags(flag,flag);

            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(params);
        }

//        View view = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null);
//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        addContentView(view, lp);
//        setContentView(view);
//        setContentView(R.layout.layout_progress_dialog);
    }

    @Override
    public void show() {
        super.show();
    }
}
