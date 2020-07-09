package rui.com.crashlog.widget.customVIew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import rui.com.crashlog.R;

public class NShapeView extends View
{
    private Context mContext;
    private Paint mPaint, paint2;
    private float mR, mCx, mCy;
    private static final int MIX = 1;
    private int mN = 1;
    private float DEGREES_UNIT = 360 / mN; //正N边形每个角  360/mN能整除

    public NShapeView(Context context) {
        this(context, null);

    }

    public NShapeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NShapeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.Rui_风吹麦浪));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(10);

        paint2 = new Paint();
        paint2.setColor(getResources().getColor(R.color.Rui_black));
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(10);
    }

    public float getmN() {
        return mN;
    }

    public void setmN(int mN) {
        this.mN = mN;
        DEGREES_UNIT = 360 / this.mN;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float mW_0 = getMeasuredWidth();   /** getMeasuredWidth方法获得的值是onMeasure()里的setMeasuredDimension()设置的值，它的值在measure()运行后就会确定 **/
        float mH_0 = getMeasuredHeight();  /** 同上，获得的是MeasureHeight **/
        float mW = getWidth();             /** getWidth方法获得是onLayout()方法中传递的四个参数中的mRight-mLeft，它的值是在layout()方法运行后确定的 **/
        float mH = getHeight();            /** 同上，获得的是Height **/



        mCx = mW / 2; /** 外切圆中心X坐标，也是正多边形的中心X坐标 **/
        mCy = mH / 2; /** 外切圆中心Y坐标，也是正多边形的中心Y坐标 **/
        mR = Math.min(mCx, mCy) * 3 / 4; /** 外切圆半径 **/
    }

    int angle = 45;

    public void setAngle(int angle) {
        this.angle = angle;
        invalidate();
    }

    Path path = new Path();
    Path path2 = new Path();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mN < MIX)
            return;

//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);






        /** Path的布尔运算 **/
//        canvas.translate(210, 400);
//        // 画两个圆
//        // 圆1:圆心 = (0,0),半径 = 100
//        // 圆2:圆心 = (50,0),半径 = 100
//         path.addCircle(0, 0, 200, Path.Direction.CW);
//         path2.addCircle(350, 0,200, Path.Direction.CW);
        // 取两个路径的异或集
        /** 布尔参数列表
         * Path.Op.DIFFERENCE——path1不同于path2的区域
         * Path.Op.REVERSE_DIFFERENCE——path2不同于path1的区域
         * Path.Op.INTERSECT——(交集)path1与path2的相交区域
         * Path.Op.UNION——(并集)path1与path2的和
         * Path.Op.XOR——(异或)path1与path2的和减去相交区域 **/
//        path.op(path2, Path.Op.DIFFERENCE);
//        // 画出路径
//         canvas.drawPath(path, mPaint);
//         canvas.drawPath(path2, paint2);




//        RectF rectF = new RectF(0, 100, 600,400);
//        canvas.drawRect(rectF, mPaint);
//        path.lineTo(250, 150);
//        path.arcTo(rectF, 30, 75, false);
        /** 参数dir：指定绘制时是顺时针还是逆时针:CW为顺时针，  CCW为逆时针 **/
//        path.addCircle(300,300,250, Path.Direction.CW);
//        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
//        canvas.drawColor(getResources().getColor(R.color.Rui_gay));
//        canvas.drawPath(path, paint2);

//        path.lineTo(250, 150);
//        path.isRect(new RectF());
//        Log.e(getClass().getSimpleName(), "=======================> path.isEmpty() = " + path.isEmpty());



        /** 绘制多边形，实验Path类
         * (1)使用moveTo（）
         * (2)使用setLastPoint() **/
//         起点默认是(0,0)
//         连接点(400,500)
//        path.lineTo(400, 500);
//         将当前点移动到(300, 300)
//        path.moveTo(300, 300);

        // 将当前点移动到(300, 300)
        // 会影响之前的操作
        // 但不将此设置为新起点
//        path.setLastPoint(300, 300) ;

//         连接点(900, 800)
//        path.lineTo(700, 800);
//         连接点(200,700)
//        path.lineTo(200, 700);
//         闭合路径，即连接当前点和起点
//         即连接(200,700)与起点2(300, 300)
//         注:此时起点已经进行变换
//        path.close();
//         画出路径
//        canvas.drawPath(path, mPaint);




        /** 绘制三角形，用多个三角形组成风车，实验canvas的translate和rotate功能 **/
        // 为了方便观察,将坐标系移到屏幕中央
        canvas.translate(mCx, mCy);
//        //初始三角形
//        canvas.drawLine(0, 0, 0, 180, mPaint);
//        canvas.drawLine(0, 180, -220, 300, mPaint);
//        canvas.drawLine(0, 0, -220, 300, mPaint);
//        for (int i = 0; i < (360 / angle); i++)
//        {
//            canvas.save();
//            canvas.rotate(angle * i);
//            canvas.drawLine(0, 0, 0, 180, mPaint);
//            canvas.drawLine(0, 180, -220, 300, mPaint);
//            canvas.drawLine(0, 0, -220, 300, mPaint);
//            canvas.restore();
//        }
        /** 使用Path画三角形,并填充 **/
        path.lineTo(0, 180);
        path.lineTo(-200, 300);
        path.setFillType(Path.FillType.EVEN_ODD);
        canvas.drawPath(path, mPaint);
        for (int i = 0; i < (360 / angle); i++)
        {
            canvas.save();
            canvas.rotate(angle * i);
            canvas.drawPath(path, mPaint);
            canvas.restore();
        }


        /** 绘制平行四边形，实验canvas的skew功能 **/
        // 初始矩形
//        canvas.drawRect(0, 0, 310, 110, paint2);
        // 向X正方向倾斜45度
//        canvas.save();
//        canvas.skew(1f, 0);
//        canvas.drawRect(10, 10, 310, 210, mPaint);
//        canvas.restore();
//        // 向X负方向倾斜45度
//        canvas.save();
//        canvas.skew(-1f, 0);
//        canvas.drawRect(10, 10, 310, 210, mPaint);
//        canvas.restore();
//        // 向Y正方向倾斜45度
//        canvas.save();
//        canvas.skew(1f, 1f);
//        canvas.drawRect(0, 0, 310, 110, mPaint);
//        canvas.restore();
////        // 向Y负方向倾斜45度
//        canvas.skew(0, -1f);
//        canvas.drawRect(0, 0, 310, 110, mPaint);

//        canvas.drawRect(10, 10, 310, 210, mPaint);


        /** 绘制图片（位图） **/
//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.testimage);
//        Rect src = new Rect(0, 0, bitmap.getWidth() / 6 * mN, bitmap.getHeight() / 6 * mN);
//        Rect dst = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
//        canvas.drawBitmap(bitmap, src, dst, null);


        /** 绘制文字 **/
//         1.创建路径对象
//         Path path = new Path();
//         2. 设置路径轨迹
//         path.cubicTo(40, 50, 640, 450, 540, 600);
//         3. 画路径
//         canvas.drawPath(path,mPaint);
//         4. 画出在路径上的字
//         canvas.drawTextOnPath("在Path上写的字:Carson_Ho", path, 0, 0, mPaint);


        /** 绘制扇形 **/
//        RectF rectF = new RectF(0, 100, 600,400);
//        canvas.drawRect(rectF, mPaint);
//        canvas.drawArc(rectF, 0, 90, false, paint2);
//        canvas.drawArc(rectF, 90, 90, false, paint2);
//        canvas.drawArc(rectF, 0, -90, false, paint2);
//        canvas.drawArc(rectF, -90, -90, false, paint2);


        /** 绘制多边形 **/
//        float d = (float) (2 * mR * Math.sin(Math.toRadians(DEGREES_UNIT / 2)));  /** 外切圆的每段弦长 **/
//        float c = mCy - mR;
//        float y = (d * d + mCy * mCy - c * c - mR * mR) / (2 * (mCy - c));  /** 根据坐标上两点间的距离公式，求 **/
//        float x = (float) (mCx + Math.sqrt(-1 * c * c + 2 * c * y + d * d - y * y));
//
//        for (int i = 0; i < mN; i++) {
//            canvas.save();
//            canvas.rotate(DEGREES_UNIT * i, mCx, mCy);
//            canvas.drawLine(mCx, mCy, mCx, c, mPaint);
//            canvas.drawLine(mCx, c, x, y, mPaint);
//            canvas.restore();
//        }

    }
}
