package rui.com.crashlog.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rui.com.crashlog.R;

public class ViewPagerAdapter extends PagerAdapter
{
    Context mContext;
    private int count = 0;
    private List<Integer> imageIds = new ArrayList<Integer>();
//    private List<List<Integer>> isItemPressed = new ArrayList<>();
    private List<Integer> isItemPressed = new ArrayList<>();
    private IPagerPosition pagerPosition;

    private final String TAG = getClass().getSimpleName();

    private final Integer[] DEFAULT_EMO_RES_IDS = {
            R.drawable.tt_e0, R.drawable.tt_e1,
            R.drawable.tt_e2, R.drawable.tt_e3, R.drawable.tt_e4, R.drawable.tt_e5,
            R.drawable.tt_e6, R.drawable.tt_e7, R.drawable.tt_e8, R.drawable.tt_e9,
            R.drawable.tt_e10, R.drawable.tt_e11, R.drawable.tt_e12, R.drawable.tt_e13,
            R.drawable.tt_e14, R.drawable.tt_e15, R.drawable.tt_e16, R.drawable.tt_e17,
            R.drawable.tt_e18, R.drawable.tt_e19, R.drawable.tt_e20, R.drawable.tt_e21,
            R.drawable.tt_e22, R.drawable.tt_e23, R.drawable.tt_e24, R.drawable.tt_e25,
            R.drawable.tt_e26, R.drawable.tt_e27, R.drawable.tt_e28, R.drawable.tt_e29,
            R.drawable.tt_e30, R.drawable.tt_e31, R.drawable.tt_e32, R.drawable.tt_e33,
            R.drawable.tt_e34, R.drawable.tt_e35, R.drawable.tt_e36, R.drawable.tt_e37,
            R.drawable.tt_e38, R.drawable.tt_e39, R.drawable.tt_e40, R.drawable.tt_e41,
            R.drawable.tt_e42, R.drawable.tt_e43, R.drawable.tt_e44, R.drawable.tt_e45
    };

    public ViewPagerAdapter(Context context)
    {
        mContext = context;

        for (Integer id : DEFAULT_EMO_RES_IDS)
        {
            count += 1;
            imageIds.add(id);
            isItemPressed.add(0);
//            Log.e("herx___", "==============> ViewPagerAdapter.id= " + id + "\n");
        }
    }

    public void setInterface_PagerPosition(IPagerPosition pagerPosition) {
        this.pagerPosition = pagerPosition;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position)
    {
//        Log.e("herx___" + TAG, "======================================> instantiateItem().position= " + position);

        GridView gridView = new GridView(mContext);

        /** setClipToPadding(false)——表示item不會被padding切掉 **/
        gridView.setClipToPadding(false);
        gridView.setPadding(48,42, 48, 42);

        /** ------------------------------- **/
        /** 以下几个属性建议都设置（尤其是ColumnWidth属性和setStretchMode(GridView.STRETCH_COLUMN_WIDTH)必须有其中一个），即确保ColumnWidth有数值，
         *  有可能在ViewPager中嵌套GridView会不显示内容 **/
        gridView.setNumColumns(4);
        gridView.setHorizontalSpacing(0);
        gridView.setVerticalSpacing(120);
        gridView.setColumnWidth(120);
        gridView.setStretchMode(GridView.STRETCH_SPACING);
        /** ------------------------------- **/
//        gridView.setGravity(Gravity.CENTER);
        int end = (position + 1) * 20 > count ? count : (position + 1) * 20;
//        GridAdapter gridAdapter = new GridAdapter(mContext, imageIds.subList(position * 20, end));
        GridAdapter gridAdapter = new GridAdapter(mContext, 0, imageIds.subList(position * 20, end));
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(grideViewItemListener);

        container.addView(gridView);
        return gridView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        Log.e("herx___" + TAG, "======================================> destroyItem().position= " + position);
        GridView gridView = (GridView) object;
        container.removeView(gridView);
    }

    @Override
    public int getCount()
    {
        return (count / 20) + 1;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == object;
    }

    private AdapterView.OnItemClickListener grideViewItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int pager_Position = pagerPosition.onGetContextPagerPosition();
//            Log.e("herx___"+TAG, "===============================> onItemClick().getItemPosition(view)= " + pager_Position);
            int currentPosition = pager_Position * 20 + position;
            if (isItemPressed.get(currentPosition) == 0)
            {
                isItemPressed.set(currentPosition, 1);

                ObjectAnimator animator_scaleX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), view.getScaleX() + (float)1.9);
                ObjectAnimator animator_scaleY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), view.getScaleY() + (float)1.0);
                animator_scaleX.setDuration(200);
                animator_scaleY.setDuration(200);
                animator_scaleX.start();
                animator_scaleY.start();
            }
            else if(isItemPressed.get(currentPosition) == 1)
            {
                isItemPressed.set(currentPosition, 0);

                ObjectAnimator animator_scaleX = ObjectAnimator.ofFloat(view, "scaleX", view.getScaleX(), view.getScaleX() - (float)1.9);
                ObjectAnimator animator_scaleY = ObjectAnimator.ofFloat(view, "scaleY", view.getScaleY(), view.getScaleY() - (float)1.0);
                animator_scaleX.setDuration(200);
                animator_scaleY.setDuration(200);
                animator_scaleX.start();
                animator_scaleY.start();
            }
        }
    };

//    public interface PagerPosition{
//        int getContextPagerPosition();
//    }
}
