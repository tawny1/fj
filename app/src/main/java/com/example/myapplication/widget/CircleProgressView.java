package com.example.myapplication.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.myapplication.R;


public class CircleProgressView extends View {

    private static final String TAG = "CircleProgressView";
    private static final int COMPLETE = 360;
    private Paint mPaint;   //画圆、画线
    private Paint mPaintArc;    //画渐变圆弧
    private final int circleOutsideColor; //圆的颜色
    private float mPaintWidth = 8;  //画笔宽度
    private float mPaintTextSize = 23;
    private int mWidth;
    private RectF progressRect;
    private float progress; //进度
    private int radius; //圆心 半径
    private String progressText = "0%";//进度文字
    private int mPaintTextColor = ContextCompat.getColor(getContext(), R.color.color_222);//文字颜色
    private final int[] mColors = {ContextCompat.getColor(getContext(), R.color.color_e8e8e8),
            ContextCompat.getColor(getContext(), R.color.color_2e2a3d)};//圆环渐变色
    private ValueAnimator mValueAnimator;
    private boolean isAnimationStart;
    private AnimFinishListener listener;//动画结束监听

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        circleOutsideColor = array.getColor(R.styleable.CircleProgressView_circleColor2,
                ContextCompat.getColor(context, R.color.color_e8e8e8));
        mPaintWidth = array.getDimension(R.styleable.CircleProgressView_paintTextWidth, mPaintWidth);
        mPaintWidth = array.getDimension(R.styleable.CircleProgressView_paintTextWidth, mPaintWidth);
        mPaintTextSize = array.getDimension(R.styleable.CircleProgressView_paintTextSize, mPaintTextSize);
        mPaintTextColor = array.getColor(R.styleable.CircleProgressView_paintTextColor, mPaintTextColor);
        array.recycle();
        mPaintWidth = dp2px((int) mPaintWidth);
        mPaintTextSize = sp2px((int) mPaintTextSize);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeWidth(mPaintWidth);

        mPaintArc = new Paint();
        mPaintArc.setAntiAlias(true);
        mPaintArc.setDither(true);
        mPaintArc.setStyle(Paint.Style.STROKE);
        mPaintArc.setStrokeWidth(mPaintWidth);
        mPaintArc.setStrokeCap(Paint.Cap.ROUND);

//        test();
    }

    private void test(){
//        progressText = "25%";
        startAnimation();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(circleOutsideColor);

        //画个圆
        canvas.drawCircle(radius, radius, (mWidth - mPaintWidth) / 2f, mPaint);
        //画渐变圆弧  原本是-90° 画笔设置ROUND后，前面圆角需要一点px
        canvas.drawArc(progressRect, -84, progress, false, mPaintArc);
        //画进度文字
        //将坐标原点移到控件中心
        canvas.translate(radius, radius);
        //绘制居中文字
        //文字宽
        float textWidth = mPaint.measureText(progressText);
        //文字baseline在y轴方向的位置
        float baseLineY = Math.abs(mPaint.ascent() + mPaint.descent()) / 2;
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mPaintTextColor);
        mPaint.setTextSize(mPaintTextSize);
        canvas.drawText(progressText, -textWidth / 2, baseLineY, mPaint);
    }

    private float dp2px(int dp) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private float sp2px(int sp) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        radius = mWidth / 2;
        float progressRadius = mPaintWidth / 2;
        progressRect = new RectF(progressRadius, progressRadius, mWidth - progressRadius, mWidth - progressRadius);
        SweepGradient sweepGradient = new SweepGradient(radius, radius, mColors, null);
        //旋转渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90f, (float) radius, (float) radius);
        sweepGradient.setLocalMatrix(matrix);
        mPaintArc.setShader(sweepGradient);
    }

    public void setCurrentProgress(float currentProgress) {
        this.progress = (float) (currentProgress * 3.6);
        progressText = currentProgress + "%";
        invalidate();
    }


    /**
     * 开始动画
     *
     * @param duration 动画时间
     */
    public void startAnimation(int duration) {
        if (mValueAnimator == null) {
            mValueAnimator = ValueAnimator.ofInt(0, 100);
            mValueAnimator.setDuration(duration);
            mValueAnimator.setTarget(0);
            mValueAnimator.setRepeatCount(0);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(animation -> {
                int i = (int) animation.getAnimatedValue();
                progressText = i + "%";
                progress = (int) (COMPLETE * (i / 100f));
                invalidate();
            });
            mValueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (listener!=null)
                        listener.onFinish();
                }
                @Override
                public void onAnimationCancel(Animator animator) {
                }
                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }
        mValueAnimator.start();
        isAnimationStart = true;
    }

    public void setListener(AnimFinishListener listener) {
        this.listener = listener;
    }

    /**
     * 开始动画
     */
    public void startAnimation() {
        progress = 0;
        startAnimation(5000);
    }

    /**
     * 结束动画
     */
    public void stopAnimation() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            isAnimationStart = false;
        }
    }

    /**
     * 是否在动画中
     *
     * @return 是为 true 否则 false
     */
    public boolean isAnimationStart() {
        return isAnimationStart;
    }

    /**
     * 防止内存溢出 未结束动画并退出页面时，需使用此函数，或手动释放此view
     */
    public void detachView() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
            isAnimationStart = false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        detachView();
        super.onDetachedFromWindow();
    }

    public interface AnimFinishListener {
        void onFinish();
    }
}
