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

public class BezierCurves_1 extends View {
    private Paint mPaintBezier;
    /**
     * 用于描绘bezier曲线的画笔
     **/
    private Paint mPaintAuxiliary;
    /**
     * 用于描绘bezier辅助线的画笔
     **/
    private Paint mPaintAuxiliaryText;

    private Path mPath = new Path();
    /**  **/
    private float mX, mY;
    private float offset = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    private ValueAnimator valueAnimator = null;

    public BezierCurves_1(Context context) {
        super(context);
    }

    public BezierCurves_1(Context context, AttributeSet attrs) {
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
    }

    public BezierCurves_1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void clear() {
        mPath.reset();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
/** 通过触摸决定控制点 **/
//            case MotionEvent.ACTION_MOVE:
//                mAuxiliaryX = event.getX();
//                mAuxiliaryY = event.getY();
//                invalidate();
//                break;
//            case MotionEvent.ACTION_DOWN:
//                {
//                    if (changeAuxiliary == 0) {
//                        changeAuxiliary = 1;
//                    } else if (changeAuxiliary == 1) {
//                        changeAuxiliary = 0;
//                    }
//                }
//            case MotionEvent.ACTION_MOVE:
//                {
//                    if (changeAuxiliary == 0)
//                    {
//                        mAuxiliaryX = event.getX();
//                        mAuxiliaryY = event.getY();
//                    }
//                    else if (changeAuxiliary == 1)
//                    {
//                        mAuxiliary2X = event.getX();
//                        mAuxiliary2Y = event.getY();
//                    }
//                }
//                invalidate();
//                break;
/** 通过触摸移动，绘制平滑曲线 **/
            case MotionEvent.ACTION_DOWN:
//                mPath.reset();
                mX = event.getX();
                mY = event.getY();
                mPath.moveTo(mX, mY);
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float preX = mX;
                float preY = mY;
                float dX = Math.abs(x - preX);
                float dY = Math.abs(y - preY);
                if (dX >= offset || dY >= offset)
                {
                    // 贝塞尔曲线的控制点为起点和终点的中点
                    float cX = (x + preX) / 2;
                    float cY = (y + preY) / 2;
                    mPath.quadTo(preX, preY, cX, cY);
                    mX = x;
                    mY = y;
                }
        }
        invalidate();
        return true;
    }

    /** 模仿手写板 **/
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaintBezier);
    }
}
