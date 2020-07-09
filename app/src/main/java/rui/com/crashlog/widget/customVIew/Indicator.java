package rui.com.crashlog.widget.customVIew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import rui.com.crashlog.R;

public class Indicator extends View
                       implements ViewPager.OnPageChangeListener
{
    private Paint paint1 = null;
    private Paint paint2 = null;
    private int weight = 0, hight = 0, radius = 0;
    private int count = 0;
    private float offset = 0;

    public Indicator(Context context)
    {
        super(context);
    }

    public Indicator (Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initView();
    }

    public Indicator (Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView()
    {
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(Color.BLACK);

        paint2 = new Paint(paint1);
        paint2.setColor(getResources().getColor(R.color.Rui_gay));
    }

    public void setCount(int count) {
        this.count = count;
        if (count <= 0)
            this.count = 1;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        weight = w;
        hight = h;
        radius = h / 2;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        float spacing = 0;

        if (count == 1)
        {
            canvas.drawCircle(weight / 2, radius, radius, paint2);
            return;
        }
        else
        {
            spacing = (weight - hight) / (count - 1);
        }

        for (int i = 0; i < count; i++)
        {
            canvas.drawCircle(radius + i * spacing, radius, radius, paint1);
        }
        canvas.save();
        canvas.drawCircle(radius + offset, radius, radius, paint2);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
//        Log.e("Scroll", "position：" + position);
//        Log.e("Scroll", "positionOffset：" + positionOffset);
//        Log.e("Scroll", "positionOffsetPixels：" + positionOffsetPixels);

        float spacing = 0;

        if (count == 1)
            spacing = 0;
        else {
            spacing = (weight - hight) / (count - 1);
        }
        offset = (position + positionOffset) * spacing;
        invalidate();
    }

    @Override
    public void onPageSelected(int position)
    {}

    @Override
    public void onPageScrollStateChanged(int state)
    {}
}
