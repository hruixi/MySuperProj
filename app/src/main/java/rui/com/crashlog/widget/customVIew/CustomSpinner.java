package rui.com.crashlog.widget.customVIew;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import rui.com.crashlog.R;


/**
 * Created by herx on 2018/3/20.
 */

public class CustomSpinner extends LinearLayout
//        implements View.OnClickListener
{
    private LinearLayout view = null;
    private PopupWindow popupWindow = null;
    private ListView listView = null;
    private TextView itemSelect_name0 = null;
    private ImageView showTime = null;
    private ObjectAnimator animator = null;
    private Context context;
    private LayoutInflater mInflater = null;
    private int defaultHeight = 720;

    private MySpinnerAdapter spinner_adapter = null;
    private boolean isPopShow = false;                  // popupWindow是否显示
    private int height = 0;                              // popupWindow的显示高度
    private int position = -1;                          // 保存listView的选中item
//    private int initPosition = -1;                    // 保存退出对讲界面时的选中位置
    private List<ArrayList<String>> groupsAvatarUrls;   // 保存所有的群头像的URLs


    /** 需要展示的数据载体 **/
    private List<String> itemList = new ArrayList<String>();

    public CustomSpinner(Context context)
    {
        super(context);
        this.context = context;
        initView(context);
    }

    public CustomSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public CustomSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    public void initView(Context context)
    {
        mInflater = LayoutInflater.from(context);
        view = (LinearLayout) mInflater.inflate(R.layout.custom_spinner, this, true);

        itemSelect_name0 = (TextView) view.findViewById(R.id.itemSelect_name0);
        showTime = (ImageView) view.findViewById(R.id.showTime);

        animator = ObjectAnimator.ofFloat(showTime,"rotation", 0, 180);
        animator.setDuration(200);

        listView = new ListView(context);
        position = 0;
        spinner_adapter = new MySpinnerAdapter();

        /** 下拉列表的显示框按钮监听器，点击显示popupWindow，并执行animate，再点击撤销popupWindow **/
        showTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.start();
                if (null != itemList)
                {
                    if (null == popupWindow)
                    {
                        //设置加载时的背景颜色
                        listView.setCacheColorHint(0x00000000);
//                        listView.setDividerHeight(2);
                        listView.setDivider(getResources().getDrawable(R.drawable.adapter_spinner_divider, null));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            listView.setBackgroundColor(getResources().getColor(R.color.Rui_custom_spinner_divider, null));
                        } else {
                            listView.setBackgroundColor(getResources().getColor(R.color.Rui_custom_spinner_divider));
                        }
                        listView.setAdapter(spinner_adapter);

                        /** 下拉列表的item的点击事件可自行补充和修改 **/
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int p, long id) {
                                position = p;
                                String tempGrp = itemList.get(p);
                                itemSelect_name0.setText(tempGrp);

                                animator.reverse();
                                popupWindow.dismiss();
                                animator.reverse();
                                isPopShow = true;
                                CustomSpinner.this.view.setTag(getId());
                            }
                        });

                        if (height == 0)
                        {
                            int hei = setPopupWindowTotalHeight(listView);
                            //这里设置下拉框的高度
                            if (hei >= defaultHeight)
                            {
                                popupWindow = new PopupWindow(listView, view.getWidth(), defaultHeight, true);
                            }
                            else
                            {
                                popupWindow = new PopupWindow(listView, view.getWidth(), hei, true);
                            }
                        }
                        else
                        {
                            popupWindow = new PopupWindow(listView, view.getWidth(), height, true);
                        }

//                        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
                        popupWindow.setFocusable(true);
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                isPopShow = true;
                                animator.reverse();
                            }
                        });
                        popupWindow.showAsDropDown(view,0, 0);
                        isPopShow = false;
                    }
                    else
                    {
                        int hei = setPopupWindowTotalHeight(listView);
                        if (hei >= defaultHeight)
                            popupWindow.setHeight(defaultHeight);
                        else
                            popupWindow.setHeight(hei);

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
                onClickCustom();
            }

        });
    }

    public void initdata() {}

    /** 计算popupWindow的总高度 **/
    public static int setPopupWindowTotalHeight(ListView listView)
    {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int popHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            popHeight += listItem.getMeasuredHeight();
        }

        //总高度 = popHeight + 分割线总高度
        int totalHeight = popHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        return totalHeight;
    }

    /** listView的适配器
     * 当groupList更新时，适配器刷新**/
    private class MySpinnerAdapter extends BaseAdapter
    {
        MySpinnerAdapter() {
            //TODO
        }

        @Override
        public int getCount()
        {
            return itemList.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View view = mInflater.inflate(R.layout.custom_spinner_item, null);

            if (position == CustomSpinner.this.position)
            {
                view.setBackgroundColor(Color.parseColor("#4169E1"));
            }

            TextView tv_name = (TextView) view.findViewById(R.id.member_name);
            tv_name.setText(itemList.get(position));
            view.setTag(tv_name);
            return view;
        }

        @Override
        public String getItem(int p)
        {
            return itemList.get(p);
        }

        @Override
        public long getItemId(int p)
        {
            return p;
        }
    }

    public void setSpinnerHeiht(int heiht)
    {
        this.height = heiht;
    }

    /** 当groupList为空时，调用此函数，暂时没有执行内容 **/
    public void onClickCustom()
    {}

    /** 更新数据的入口 **/
    public void updateData(List<String> newList)
    {
        this.itemList = newList;
        spinner_adapter.notifyDataSetChanged();
    }

    /** 获取选中项的文字 **/
    public String getItemSelect_text() {
        return itemSelect_name0.getText().toString();
    }

    /**
     * 设置图标
     * @param srcId 图标的int值
     */
    public void setImageSrc(int srcId) {
        showTime.setImageResource(srcId);
    }

    /**
     * 设置图标的背景颜色，默认颜色是@color/JColor_login_white
     * @param colorId 颜色的id
     */
    public void setImageBackcolor(int colorId) {
        showTime.setBackgroundColor(colorId);
    }

    public void setImageBackground(Drawable background) {
        showTime.setBackground(background);
    }

    public void setImage(int srcId) {
        showTime.setImageResource(srcId);

    }

    /**
     * 设置显示框的背景颜色，默认颜色是@color/JColor_therapy_third_spinner_bg
     * @param colorId 颜色的id
     */
    public void setTextviewBackcolor(int colorId) {
        itemSelect_name0.setBackgroundColor(colorId);
    }

    public void setTextviewBackground(Drawable background) {
        itemSelect_name0.setBackground(background);
    }
}
