package com.example.myapplication.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

public class ShaderView extends View {

    private Paint paint;
    private Path path;

    private float mX;
    private float mY;

    @SuppressLint("NewApi")
    private float offset = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    Bitmap bitmap;

    public ShaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30);
        paint.setStrokeCap(Paint.Cap.ROUND);

        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        paint.setShader(shader);

        path = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.reset();
                float x = event.getX();
                float y = event.getY();
                mX = x;
                mY = y;
                path.moveTo(x, y);
                break;

            case MotionEvent.ACTION_MOVE:
                float x1 = event.getX();
                float y1 = event.getY();
                float preX = mX;
                float preY = mY;
                float dx = Math.abs(x1 - preX);
                float dy = Math.abs(y1 - preY);
                if (dx >= offset || dy >= offset) {
                    // 贝塞尔曲线的控制点为起点和终点的中点
                    float cX = (x1 + preX) / 2;
                    float cY = (y1 + preY) / 2;
                    path.quadTo(preX, preY, cX, cY);
                    mX = x1;
                    mY = y1;
                }
        }

        invalidate();

        return true;
    }
}
