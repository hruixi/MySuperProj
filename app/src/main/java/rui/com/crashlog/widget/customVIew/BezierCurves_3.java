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

public class BezierCurves_3 extends View implements View.OnClickListener
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

    public BezierCurves_3(Context context)
    { super(context); }

    public BezierCurves_3(Context context, AttributeSet attrs)
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

    public BezierCurves_3(Context context, AttributeSet attrs, int defStyleAttr)
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
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction()) {
/** 通过触摸决定控制点 **/
            /** 单控制点 **/
            case MotionEvent.ACTION_MOVE:
                mAuxiliaryX = event.getX();
                mAuxiliaryY = event.getY();
                break;
            case MotionEvent.ACTION_DOWN: {
                mAuxiliaryX = event.getX();
                mAuxiliaryY = event.getY();
            }
        }
        invalidate();
        return true;
    }

    @Override
    public void onClick(View view)
    { }


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
        canvas.drawText("鹊桥", mAuxiliaryX, mAuxiliaryY, mPaintAuxiliaryText);
        canvas.drawText("开创者", mStartPointX, mStartPointY, mPaintAuxiliaryText);
        canvas.drawText("终结者", mEndPointX, mEndPointY, mPaintAuxiliaryText);
        // 辅助线
        canvas.drawLine(mStartPointX, mStartPointY, mAuxiliaryX, mAuxiliaryY, mPaintAuxiliary);
        canvas.drawLine(mAuxiliaryX, mAuxiliaryY, mEndPointX, mEndPointY, mPaintAuxiliary);
        // 辅助点
        canvas.drawPoint(mAuxiliaryX, mAuxiliaryY, mPaintBezier);
//         二阶贝塞尔曲线
        mPath.quadTo(mAuxiliaryX, mAuxiliaryY, mEndPointX, mEndPointY);

        canvas.drawPath(mPath, mPaintBezier);
    }

}
