package rui.com.crashlog.widget.customVIew;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.BounceInterpolator;

public class BezierCurves_2 extends View implements View.OnClickListener
{
    private Paint mPaintBezier;         /** 用于描绘bezier曲线的画笔 **/
    private Paint mPaintAuxiliary;      /** 用于描绘bezier辅助线的画笔 **/
    private Paint mPaintAuxiliaryText;  /** 用于绘制标注文字的画笔 **/

    private float mAuxiliaryX;          /** 控制点的X坐标 **/
    private float mAuxiliaryY;          /** 控制点的Y坐标 **/
    private float mAuxiliary2X;
    private float mAuxiliary2Y;
    private int changeAuxiliary = 0;    //0——修改控制点1,1——修改控制点2

    private float mStartPointX;         /** 起始点 **/
    private float mStartPointY;         /**  **/

    private float mEndPointX;           /** 结束点 **/
    private float mEndPointY;           /**  **/
    private float endY;

    private Path mPath = new Path();    /**  **/

    private float mX,mY;
    private float offset = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    private ValueAnimator valueAnimator = null;
    private boolean isAnimation = false;

    public BezierCurves_2(Context context)
    { super(context); }

    public BezierCurves_2(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mPaintBezier = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBezier.setStyle(Paint.Style.STROKE);
        mPaintBezier.setStrokeWidth(8);

        mPaintAuxiliary = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintAuxiliary.setStyle(Paint.Style.STROKE);
        mPaintAuxiliary.setStrokeWidth(2);
        /** 设置成长度为10（px），间隔为20的虚线 **/
//        mPaintAuxiliary.setPathEffect(new DashPathEffect(new float[]{10, 20, 5, 10}, 0));

        mPaintAuxiliaryText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintAuxiliaryText.setStyle(Paint.Style.STROKE);
        mPaintAuxiliaryText.setTextSize(30);

        setOnClickListener(this);
    }

    public BezierCurves_2(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mStartPointX = getWidth() / 2 - 300;
        mStartPointY = getHeight() / 2 - 200;
        mEndPointX = getWidth() / 2 + 300;
        mEndPointY = getHeight() / 2 - 200;

        endY = h;

        /** 图形变形——确定辅助点位置 **/
        mAuxiliaryX = mStartPointX;
        mAuxiliaryY = mStartPointY;
        mAuxiliary2X = mEndPointX;
        mAuxiliary2Y = mEndPointY;

        /** 初始化动画器 **/
        valueAnimator = ValueAnimator.ofFloat(mStartPointY, endY);
        /** 设置BounceInterpolator()——是动画有弹跳效果 **/
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAuxiliaryY = (float) animation.getAnimatedValue();
                mAuxiliary2Y = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction()) {
/** 通过触摸决定控制点 **/
            /** 单控制点 **/
//            case MotionEvent.ACTION_MOVE:
//                mAuxiliaryX = event.getX();
//                mAuxiliaryY = event.getY();
//                invalidate();
//                break;
            /** 双控制点 **/
            case MotionEvent.ACTION_DOWN: {
                if (changeAuxiliary == 0) {
                    changeAuxiliary = 1;
                } else if (changeAuxiliary == 1) {
                    changeAuxiliary = 0;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                if (changeAuxiliary == 0) {
                    mAuxiliaryX = event.getX();
                    mAuxiliaryY = event.getY();
                } else if (changeAuxiliary == 1) {
                    mAuxiliary2X = event.getX();
                    mAuxiliary2Y = event.getY();
                }
            }
            break;
        }
        invalidate();
        return true;
    }

    @Override
    public void onClick(View view)
    {
        if (!isAnimation)
        {
            isAnimation = true;
            valueAnimator.setFloatValues(mStartPointY, endY - 5);
            valueAnimator.start();
        }
        else
        {
            isAnimation = false;
            valueAnimator.setFloatValues(mAuxiliary2Y, mStartPointY);
            valueAnimator.start();
        }
    }


    /** 1.通过触摸决定控制点,并绘制二阶、三阶贝塞尔曲线
     *  &
     *  2.图形变形 **/
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        mPath.reset();
        mPath.moveTo(mStartPointX, mStartPointY);
        // 描述文字
        canvas.drawText("悟空", mAuxiliaryX, mAuxiliaryY, mPaintAuxiliaryText);
        canvas.drawText("八戒", mAuxiliary2X, mAuxiliary2Y, mPaintAuxiliaryText);
        canvas.drawText("开创者", mStartPointX, mStartPointY, mPaintAuxiliaryText);
        canvas.drawText("终结者", mEndPointX, mEndPointY, mPaintAuxiliaryText);
        // 辅助线
        canvas.drawLine(mStartPointX, mStartPointY, mAuxiliaryX, mAuxiliaryY, mPaintAuxiliary);
        canvas.drawLine(mAuxiliaryX, mAuxiliaryY, mAuxiliary2X, mAuxiliary2Y, mPaintAuxiliary);
        canvas.drawLine(mEndPointX, mEndPointY, mAuxiliary2X, mAuxiliary2Y, mPaintAuxiliary);
        // 辅助点
//        canvas.drawPoint(mAuxiliaryX, mAuxiliaryY, mPaintBezier);
        // 二阶贝塞尔曲线
//        mPath.quadTo(mAuxiliaryX, mAuxiliaryY, mEndPointX, mEndPointY);
        /** 三阶bezier curve **/
        mPath.cubicTo(mAuxiliaryX, mAuxiliaryY, mAuxiliary2X, mAuxiliary2Y, mEndPointX, mEndPointY);

        canvas.drawPath(mPath, mPaintBezier);
    }

}
