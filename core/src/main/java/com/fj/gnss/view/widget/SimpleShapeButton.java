package com.fj.gnss.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.appcompat.widget.AppCompatButton;

import com.fj.gnss.R;

/**
 * @ClassName SimpleShapeButton
 * @Description 圆角矩形按钮
 * @Author FJD
 * @Date 2021/9/27
 */
public class SimpleShapeButton extends AppCompatButton {
    private GradientDrawable gradientDrawable;

    public SimpleShapeButton(Context context) {
        this(context, null);
    }

    public SimpleShapeButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SimpleShapeButton(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.SimpleShapeButton);
        int solidColor = typedArray.getColor(R.styleable.SimpleShapeButton_solidColor, Color.TRANSPARENT);
        int cornerRadius = typedArray.getDimensionPixelSize(R.styleable.SimpleShapeButton_cornerRadius, 0);
        int strokeColor = typedArray.getColor(R.styleable.SimpleShapeButton_strokeColor, Color.TRANSPARENT);
        int strokeWidth = typedArray.getDimensionPixelSize(R.styleable.SimpleShapeButton_strokeWidth, 0);
        typedArray.recycle();
        gradientDrawable.setColor(solidColor);
        gradientDrawable.setCornerRadius(cornerRadius);
        gradientDrawable.setStroke(strokeWidth, strokeColor);
        this.setBackground(gradientDrawable);
        setGravity(Gravity.CENTER);
    }
}
