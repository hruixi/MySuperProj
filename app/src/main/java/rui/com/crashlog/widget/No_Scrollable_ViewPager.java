package rui.com.crashlog.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class No_Scrollable_ViewPager extends ViewPager {
    public No_Scrollable_ViewPager(Context context) {
        super(context);
    }

    public No_Scrollable_ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写这两个方法就可以拦截滑动...
     * */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
