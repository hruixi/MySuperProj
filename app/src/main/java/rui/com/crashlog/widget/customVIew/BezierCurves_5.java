package rui.com.crashlog.widget.customVIew;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;

import java.util.Calendar;
import java.util.List;

import rui.com.crashlog.R;
import rui.com.crashlog.util.BezierUtil;

public class BezierCurves_5 extends View
    implements View.OnClickListener, SensorEventListener
{
    private Context mContext = null;
    private Paint circlePaint = null;
    private Paint bezierPaint = null;
    private Path mPath = null;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    private int auxiliaryX, auxiliaryY;
    private int auxiliaryX2, auxiliaryY2;
    private int moveX, moveY;

    public BezierCurves_5(Context context)
    {
        super(context);
    }

    public BezierCurves_5(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        mContext = context;
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(getResources().getColor(R.color.Rui_gay));
        bezierPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bezierPaint.setColor(getResources().getColor(R.color.Rui_gay));
        bezierPaint.setStyle(Paint.Style.STROKE);
        bezierPaint.setStrokeWidth(5);
        mPath = new Path();
        setOnClickListener(this);

        getGravity();
    }

    public BezierCurves_5(Context context, AttributeSet attributeSet, int defStyleAttr)
    {
        super(context, attributeSet, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        startX = 100;
        startY = 100;
        moveX = moveY = 100;
        endX = getWidth() - 100;
        endY = auxiliaryY2 = getHeight() - 100;
        auxiliaryX = auxiliaryX2 = getWidth() / 2;
        auxiliaryY = 100;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawCircle(startX, startY, 10, circlePaint);
        canvas.drawCircle(endX, endY, 10, circlePaint);

        mPath.reset();
        mPath.moveTo(startX,startY);
//        mPath.quadTo(auxiliaryX, auxiliaryY, endX, endY);
        mPath.cubicTo(auxiliaryX, auxiliaryY, auxiliaryX2, auxiliaryY2, endX, endY);

        canvas.drawPath(mPath, bezierPaint);
        canvas.drawCircle(moveX, moveY, 30, circlePaint);
    }

    @Override
    public void onClick(View v) {

    }

    public class BezierEvaluator implements TypeEvaluator<PointF>
    {
        private PointF mControlPoint, mControlPoint2;
        public BezierEvaluator(PointF controlPoint)
        {
            this.mControlPoint = controlPoint;
        }

        public BezierEvaluator(PointF controlPoint, PointF controlPoint2)
        {
            this.mControlPoint = controlPoint;
            this.mControlPoint2 = controlPoint2;
        }
        @Override
        public PointF evaluate(float t, PointF startValue, PointF endValue)
        {
//            return BezierUtil.CalculateBezierPointForQuadratic(t, startValue, mControlPoint, endValue);
            return BezierUtil.CalculateBezierPointForCubic(t, startValue, mControlPoint, mControlPoint2, endValue);
        }
    }

    SensorManager sensorManager = null;
    Sensor sensor = null;
    String TAG = getClass().getSimpleName();
    Calendar mCalendar = null;
    private int mX, mY, mZ;
    private long lasttimestamp = 0;
    private int limit = 6;
    private int negative_limit = -6;

    public void getGravity()
    {
        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (null == sensorManager) {
            Log.d(TAG, "deveice not support SensorManager");
        }
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == null) {
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];  /** 获取x轴的加速值 **/
            int y = (int) event.values[1];  /** 获取y轴的加速值 **/
            int z = (int) event.values[2];  /** 获取z轴的加速值 **/

            int px = (mX - x);
//            int px = Math.abs(mX - x);
//            int py = Math.abs(mY - y);
            int py = (mY - y);
            int pz = Math.abs(mZ - z);

//            BezierEvaluator bezierEvaluator = new BezierEvaluator(new PointF(auxiliaryX, auxiliaryY));
//

//            if (maxvalue > 10)
            if ( px > limit || py < negative_limit ) {
                BezierEvaluator bezierEvaluator = new BezierEvaluator(new PointF(auxiliaryX, auxiliaryY), new PointF(auxiliaryX2, auxiliaryY2));
                ValueAnimator animator = ValueAnimator.ofObject(bezierEvaluator,
                        new PointF(startX, startY),
                        new PointF(endX, endY));
                animator.setDuration(1000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        PointF pointF = (PointF) animation.getAnimatedValue();
                        moveX = (int) pointF.x;
                        moveY = (int) pointF.y;
                        invalidate();
                    }
                });
                //        animator.setInterpolator(new BounceInterpolator());
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
            } else if ( px < negative_limit || py > limit ) {
                BezierEvaluator bezierEvaluator = new BezierEvaluator(new PointF(auxiliaryX2, auxiliaryY2), new PointF(auxiliaryX, auxiliaryY));
                ValueAnimator animator = ValueAnimator.ofObject(bezierEvaluator,
                        new PointF(endX, endY),
                        new PointF(startX, startY));
                animator.setDuration(1000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        PointF pointF = (PointF) animation.getAnimatedValue();
                        moveX = (int) pointF.x;
                        moveY = (int) pointF.y;
                        invalidate();
                    }
                });
                animator.setInterpolator(new AccelerateInterpolator());
                animator.start();
            }
            mX = x;
            mY = y;
            mZ = z;
        }
    }

    /**
     * 获取一个最大值
     *
     * @param px
     * @param py
     * @param pz
     * @return
     */
    public int getMaxValue(int px, int py, int pz)
    {
        int max = 0;
        if (px > py && px > pz) {
            max = px;
        } else if (py > px && py > pz) {
            max = py;
        } else if (pz > px && pz > py) {
            max = pz;
        }
        return max;
    }

}
