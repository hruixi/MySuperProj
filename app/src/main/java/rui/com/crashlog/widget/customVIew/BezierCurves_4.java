package rui.com.crashlog.widget.customVIew;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import rui.com.crashlog.R;

public class BezierCurves_4 extends View implements View.OnClickListener
{
    private Paint mPaint = null;
    private Path mPath;
    private int mWaveLength = 1000;
    private int mOffset;
    private int mScreenHeight;
    private int mScreenWidth;
    private int mWaveCount;
    private int mCenterY;

    ValueAnimator animator = null;
    private boolean isAnimation = false;

    public BezierCurves_4(Context context)
    {
        super(context);
    }

    public BezierCurves_4(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(getResources().getColor(R.color.Rui_gay));
        setOnClickListener(this);
        mPath = new Path();
    }

    public BezierCurves_4(Context context, AttributeSet attributeSet, int defStyleAttr)
    {
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenHeight = h;
        mScreenWidth = w;
        mWaveCount = (int) Math.round(mScreenWidth / mWaveLength + 1.5);
        mCenterY = mScreenHeight / 2;

        animator = ValueAnimator.ofInt(0, mWaveLength);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                mOffset = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        mPath.reset();
        mPath.moveTo(-mWaveLength + mOffset, mCenterY);
        for (int i = 0; i < mWaveCount; i++)
        {
            // + (i * mWaveLength)
            // + mOffset
            mPath.quadTo((-mWaveLength * 3 / 4) + (i * mWaveLength) + mOffset, mCenterY + 100, (-mWaveLength / 2) + (i * mWaveLength) + mOffset, mCenterY);
            mPath.quadTo((-mWaveLength / 4) + (i * mWaveLength) + mOffset, mCenterY - 100, i * mWaveLength + mOffset, mCenterY);
        }
        mPath.lineTo(mScreenWidth, mScreenHeight);
        mPath.lineTo(0, mScreenHeight);
        mPath.close();  //封闭成闭合图形
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void onClick(View v)
    {
        if (!animator.isStarted())
        {
            animator.start();
        }
        else if (animator.isPaused())
        {
            animator.resume();
        }
        else {
            animator.pause();
        }
    }
}
