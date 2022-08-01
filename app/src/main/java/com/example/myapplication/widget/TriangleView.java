package com.example.myapplication.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TriangleView extends View {

    private Paint paint;
    private Point point;
    private ValueAnimator valueAnimator;
    private Path path;
    private Path animPath;

    private int[] point1 = new int[2];
    private int[] point2 = new int[2];
    private int[] point3 = new int[2];

    private int ovalWidth = 20;
    private int ovalHeight = 10;

    private PathMeasure pathMeasure;

    private float value = 0;

    RectF rectF;

    public TriangleView(Context context) {
        this(context, null);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TriangleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int[] getPoint1() {
        return point1;
    }

    public void setPoint1(int[] point1) {
        this.point1 = point1;
    }

    public int[] getPoint2() {
        return point2;
    }

    public void setPoint2(int[] point2) {
        this.point2 = point2;
    }

    public int[] getPoint3() {
        return point3;
    }

    public void setPoint3(int[] point3) {
        this.point3 = point3;
    }

    private void init(){
        test();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(4);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        point = new Point();
        path = new Path();
        animPath = new Path();

        path.reset();
        path.moveTo(point1[0], point1[1]);
        path.lineTo(point2[0], point2[1]);
        path.lineTo(point3[0],  point3[1]);
        path.close();

        pathMeasure = new PathMeasure();
        animPath.reset();
        animPath.lineTo(0, 0);
        pathMeasure.setPath(path, false);
//        rectF = new RectF(400, 600, 600, 400);
        rectF = new RectF();

        startAnimation();
    }


    private void test(){
        setPoint1(new int[]{370, 360});
        setPoint2(new int[]{820, 810});
        setPoint3(new int[]{220, 610});
    }

    public void test2(int n1, int e1, int n2, int e2, int n3, int e3){
        setPoint1(new int[]{n1, e1});
        setPoint2(new int[]{n2, e2});
        setPoint3(new int[]{n3, e3});
        invalidate();

        path.reset();
        path.moveTo(point1[0], point1[1]);
        path.lineTo(point2[0], point2[1]);
        path.lineTo(point3[0],  point3[1]);
        path.close();

        animPath.reset();
        animPath.lineTo(0, 0);
        pathMeasure.setPath(path, false);
        valueAnimator.start();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = getWidth();
        int height = getHeight();
        point.x = width / 2;
        point.y = height / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawOval();

//        if (length < 100){
//            canvas.drawLine(point.x, point.y, point.x + length, point.y - length, paint);
//        }
//        if (length >= 100 && length < 200){
//            canvas.drawLine(point.x + length, point.y - length, point.x - length*2, point.y - length, paint);
//        }
//        if (length >=  200){
//            canvas.drawLine(point.x - length*2, point.y - length, point.x, point.y, paint);
//        }210  100  250  130

//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.parseColor("#E5F4FE"));
//        rectF.set(330, 330, 410, 390);
//        canvas.drawOval(rectF, paint);
//
//        paint.setColor(Color.parseColor("#B7E0FD"));
//        rectF.set(340, 340, 400, 380);
//        canvas.drawOval(rectF, paint);
//
//        paint.setColor(Color.parseColor("#0091FA"));
//        rectF.set(350, 350, 390, 370);
//        canvas.drawOval(rectF, paint);
//
//
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.parseColor("#E5F4FE"));
//        rectF.set(780, 780, 860, 840);
//        canvas.drawOval(rectF, paint);
//
//        paint.setColor(Color.parseColor("#B7E0FD"));
//        rectF.set(790, 790, 850, 830);
//        canvas.drawOval(rectF, paint);
//
//        paint.setColor(Color.parseColor("#0091FA"));
//        rectF.set(800, 800, 840, 820);
//        canvas.drawOval(rectF, paint);


        x(canvas);


        paint.setColor(Color.parseColor("#0091FA"));
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.moveTo(point1[0], point1[1]);
        path.lineTo(point2[0], point2[1]);
        path.lineTo(point3[0],  point3[1]);
        path.close();
        canvas.drawPath(path, paint);



        paint.setColor(Color.RED);
        canvas.drawPath(animPath, paint);
    }

    private void x(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#E5F4FE"));
        rectF.set(point1[0] - ovalWidth * 3, point1[1] - ovalHeight * 3, point1[0] + ovalWidth * 3, point1[1] + ovalHeight * 3);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.parseColor("#B7E0FD"));
        rectF.set(point1[0] - ovalWidth * 2, point1[1] - ovalHeight * 2, point1[0] + ovalWidth * 2, point1[1] + ovalHeight * 2);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.parseColor("#0091FA"));
        rectF.set(point1[0] - ovalWidth, point1[1] - ovalHeight, point1[0] + ovalWidth, point1[1] + ovalHeight);
        canvas.drawOval(rectF, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#E5F4FE"));
        rectF.set(point2[0] - ovalWidth * 3, point2[1] - ovalHeight * 3, point2[0] + ovalWidth * 3, point2[1] + ovalHeight * 3);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.parseColor("#B7E0FD"));
        rectF.set(point2[0] - ovalWidth * 2, point2[1] - ovalHeight * 2, point2[0] + ovalWidth * 2, point2[1] + ovalHeight * 2);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.parseColor("#0091FA"));
        rectF.set(point2[0] - ovalWidth, point2[1] - ovalHeight, point2[0] + ovalWidth, point2[1] + ovalHeight);
        canvas.drawOval(rectF, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#E5F4FE"));
        rectF.set(point3[0] - ovalWidth * 3, point3[1] - ovalHeight * 3, point3[0] + ovalWidth * 3, point3[1] + ovalHeight * 3);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.parseColor("#B7E0FD"));
        rectF.set(point3[0] - ovalWidth * 2, point3[1] - ovalHeight * 2, point3[0] + ovalWidth * 2, point3[1] + ovalHeight * 2);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.parseColor("#0091FA"));
        rectF.set(point3[0] - ovalWidth, point3[1] - ovalHeight, point3[0] + ovalWidth, point3[1] + ovalHeight);
        canvas.drawOval(rectF, paint);
    }

    private void test2(){
        setPoint1(new int[]{370, 362});
        setPoint2(new int[]{820, 812});
        setPoint3(new int[]{220, 612});
    }
    private void drawOval(@NonNull Canvas canvas){
        //画第一个点三层椭圆
        // 40   25
        // 60   45
        // 80   65
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#E5F4FE"));
        rectF.set(330, 330, 410, 395);
        canvas.drawOval(rectF, paint);
        paint.setColor(Color.parseColor("#B7E0FD"));
        rectF.set(340, 340, 400, 385);
        canvas.drawOval(rectF, paint);
        paint.setColor(Color.parseColor("#0091FA"));
        rectF.set(350, 350, 390, 375);
        canvas.drawOval(rectF, paint);
        //画第二个点三层椭圆
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#E5F4FE"));
        rectF.set(780, 780, 860, 845);
        canvas.drawOval(rectF, paint);
        paint.setColor(Color.parseColor("#B7E0FD"));
        rectF.set(790, 790, 850, 835);
        canvas.drawOval(rectF, paint);
        paint.setColor(Color.parseColor("#0091FA"));
        rectF.set(800, 800, 840, 825);
        canvas.drawOval(rectF, paint);
        //画第三个点三层椭圆
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#E5F4FE"));
        rectF.set(180, 580, 260, 645);
        canvas.drawOval(rectF, paint);
        paint.setColor(Color.parseColor("#B7E0FD"));
        rectF.set(190, 590, 250, 635);
        canvas.drawOval(rectF, paint);
        paint.setColor(Color.parseColor("#0091FA"));
        rectF.set(200, 600, 240, 625);
        canvas.drawOval(rectF, paint);
    }


    public void startAnimation(){
        if (valueAnimator == null){
            valueAnimator = ValueAnimator.ofFloat(0f, 1f);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new AccelerateInterpolator());
            valueAnimator.setRepeatCount(0);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    value = (float) animation.getAnimatedValue();
                    float end = pathMeasure.getLength() * value;
                    pathMeasure.getSegment(0, end, animPath, true);
                    invalidate();
                }
            });
            valueAnimator.start();
        }
    }
}
