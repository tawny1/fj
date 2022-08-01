package com.example.myapplication.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.example.myapplication.R;

/**
 *
 */
public class CustomSeekbarView extends androidx.appcompat.widget.AppCompatSeekBar {
    public CustomSeekbarView(Context context) {
        super(context, null);
    }

    public CustomSeekbarView(Context context, AttributeSet attrs) {
        super(context, attrs, R.style.TestTheme);
    }

    public CustomSeekbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getViewSize(int measureSpec) {
        int viewSize = 0;
        //获取测量模式
        int mode = MeasureSpec.getMode(measureSpec);
        //获取大小
        int size = MeasureSpec.getSize(measureSpec);
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                //如果没有指定大小，就设置为默认大小
                break;
            case MeasureSpec.AT_MOST:
                //如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                viewSize = size;
                break;
            case MeasureSpec.EXACTLY:
                //如果是固定的大小，那就不要去改变它
                viewSize = size;
                break;
            default:
        }
        return viewSize;
    }
}
