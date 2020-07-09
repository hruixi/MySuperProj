package rui.com.crashlog.widget.customVIew;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import rui.com.crashlog.R;

public class CustomCalendarView extends LinearLayout {
    private static final String TAG = "CustomCalendarView";

    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private LinearLayout view = null;
    private PopupWindow popupWindow = null;
    private TextView mDate_textView = null;
    private ImageView mCalendar_imageView = null;
    private CalendarView mCalendarView = null;

    private boolean isPopShow = false;                  // popupWindow是否显示
    private static final int HEIGHT = 320;
    private int mHeight = HEIGHT;                              // popupWindow的显示高度

    public CustomCalendarView(Context context) {
        this(context, null);
    }

    /**
     * 从XML文件里加载时调用的构造函数
     * @param context 上下文
     * @param attrs 从XML加载时的属性值
     */
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        mInflater = LayoutInflater.from(context);
        view = (LinearLayout) mInflater.inflate(R.layout.custom_calendarview, this, true);

        mDate_textView = (TextView) view.findViewById(R.id.dateString_TV);
        mCalendar_imageView = (ImageView) view.findViewById(R.id.calendar_IV);

        mCalendarView = new CalendarView(context);
        mCalendarView.setBackground(context.getDrawable(R.drawable.customcalendar_common_bg));

        mCalendar_imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == popupWindow) {
                    mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                        @Override
                        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
//                            Log.d(TAG, "onSelectedDayChange: " + month);
                            String date = year + "/" + (month + 1) + "/" + dayOfMonth;
                            mDate_textView.setText(date);
                            popupWindow.dismiss();
                            isPopShow = true;
                        }
                    });

                    popupWindow = new PopupWindow(mCalendarView, view.getWidth(), mHeight, true);
                    popupWindow.setFocusable(true);
                    popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            isPopShow = true;
                        }
                    });
                    popupWindow.showAsDropDown(view,0, 0);
                    isPopShow = false;
                } else {
                    if(isPopShow)
                    {
                        popupWindow.showAsDropDown(view, 0, 0);
                        isPopShow = false;
                    }
                    else
                    {
                        popupWindow.dismiss();
                        isPopShow = true;
                    }
                }
            }
        });
    }
}
