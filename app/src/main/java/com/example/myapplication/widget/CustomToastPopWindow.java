package com.example.myapplication.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;

/**
 * 使用示例：
 * new CustomPopWindow.PopupWindowBuilder(getContext())
 *           .setImageViewVisible(R.drawable.ic_popu_left_right_red, CustomPopWindow.LEFT)
 *           .setText("传感器未标定，无法创建模型")
 *           .setBackground(getResources().getDrawable(R.drawable.bg_red_fill_r3))
 *           .create()
 *           .showAtLocation(view, CustomPopWindow.LEFT,true);
 */
public class CustomToastPopWindow {

    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int TOP = 3;
    public static final int BOTTOM = 4;

    private Context mContext;
    private int mWidth;
    private int mHeight;
    private boolean mIsOutside = true;
    private View mContentView;
    private PopupWindow mPopupWindow;
    private PopupWindow.OnDismissListener mOnDismissListener;
    private boolean mTouchable = true;
    private TextView mTvContent;
    private ImageView mImage;

    private CustomToastPopWindow(Context context) {
        mContext = context;
        if (mContentView == null) {
            mContentView = LayoutInflater.from(mContext).inflate(R.layout.popup_left_or_right, null);
            mTvContent = mContentView.findViewById(R.id.tv_content);
            mImage = mContentView.findViewById(R.id.iv_img);
        }
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    /**
     * 基于父view向下弹出
     * 可设置偏移
     */
    public CustomToastPopWindow showAsDropDown(View anchor, int xOff, int yOff) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, xOff, yOff);
        }
        return this;
    }

    /**
     * 基于父view向下弹出
     */
    public CustomToastPopWindow showAsDropDown(View anchor) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor);
        }
        return this;
    }

    /**
     * 基于父view向下弹出
     * 可设置偏移
     * 可设置屏幕相对位置
     */
    public CustomToastPopWindow showAsDropDown(View anchor, int xOff, int yOff, int gravity) {
        if (mPopupWindow != null) {
            mPopupWindow.showAsDropDown(anchor, xOff, yOff, gravity);
        }
        return this;
    }


    /**
     * 相对于父控件的位置（通过设置Gravity.CENTER，下方Gravity.BOTTOM等 ），可以设置具体位置坐标
     *
     * @param parent  父view
     * @param gravity 基于屏幕的位置
     * @param x   屏幕位置偏移x
     * @param y   屏幕位置偏移y
     */
    public CustomToastPopWindow showAtLocation(View parent, int gravity, int x, int y) {
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(parent, gravity, x, y);
        }
        return this;
    }

    /**
     *
     * @param parent   popupWindow基于弹窗的view
     * @param location  popupWindow弹窗位置（上下左右）
     * @param isDismiss 是否2s后关闭
     * @return
     */
    public CustomToastPopWindow showAtLocation(View parent, int location, boolean isDismiss) {
        int[] windowPos = calculatePopWindowPos(parent, location);
        if (mPopupWindow != null) {
            mPopupWindow.showAtLocation(parent, Gravity.START | Gravity.TOP, windowPos[0], windowPos[1]);
        }
        if (isDismiss) {
            new Handler().postDelayed(() -> {
                if (!((Activity) mContext).isFinishing() && mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
            }, 2000);
        }
        return this;
    }


    /**
     * @param anchorView popupWindow基于弹窗的view
     * @param location   弹窗位置
     * @return 返回屏幕点(x, y)
     */
    public int[] calculatePopWindowPos(final View anchorView, int location) {
        final int[] windowPos = new int[2];
        final int[] anchorLoc = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        final int anchorWidth = anchorView.getWidth();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        // 测量contentView
        mContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = mContentView.getMeasuredHeight();
        final int windowWidth = mContentView.getMeasuredWidth();
        // 如果高度或宽度不够，反转过来
        if (location == CustomToastPopWindow.TOP) {
            windowPos[0] = anchorLoc[0] - windowWidth / 2 + anchorWidth / 2;
            if (anchorLoc[1] < windowHeight){
                setLayoutGravity(BOTTOM);
                windowPos[1] = anchorLoc[1] + windowHeight - (int) dp2px(9);
            }else {
                windowPos[1] = anchorLoc[1] - windowHeight - (int) dp2px(9);
            }
        } else if (location == CustomToastPopWindow.BOTTOM) {
            windowPos[0] = anchorLoc[0] - windowWidth / 2 + anchorWidth / 2;
            if (screenHeight - anchorLoc[1] - anchorHeight < windowHeight){
                setLayoutGravity(BOTTOM);
                windowPos[1] = anchorLoc[1] - windowHeight - (int) dp2px(9);
            }else {
                windowPos[1] = anchorLoc[1] + windowHeight - (int) dp2px(9);
            }
        } else if (location == CustomToastPopWindow.LEFT) {
            windowPos[1] = anchorLoc[1];
            if (anchorLoc[0] < windowWidth){
                setLayoutGravity(RIGHT);
                windowPos[0] = anchorLoc[0] + anchorWidth + (int) dp2px(6);
            }else {
                windowPos[0] = anchorLoc[0] - windowWidth - (int) dp2px(6);
            }
        } else if (location == CustomToastPopWindow.RIGHT) {
            windowPos[1] = anchorLoc[1];
            if (screenWidth - anchorLoc[0] - anchorWidth < windowWidth){
                setLayoutGravity(LEFT);
                windowPos[0] = anchorLoc[0] - anchorWidth + (int) dp2px(6);
            }else {
                windowPos[0] = anchorLoc[0] + anchorWidth + (int) dp2px(6);
            }
        }
        return windowPos;
    }

    /**
     * 构建popupWindow
     */
    private PopupWindow build() {
        if (mWidth != 0 && mHeight != 0) {
            mPopupWindow = new PopupWindow(mContentView, mWidth, mHeight);
        } else {
            mPopupWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.setTouchable(mTouchable);
//        mPopupWindow.setFocusable(mIsFocusable);
//        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(mIsOutside);

        if (mWidth == 0 || mHeight == 0) {
            mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            //如果外面没有设置宽高的情况下，计算宽高并赋值
            mWidth = mPopupWindow.getContentView().getMeasuredWidth();
            mHeight = mPopupWindow.getContentView().getMeasuredHeight();
        }
        mPopupWindow.update();
        return mPopupWindow;
    }

    /**
     * 设置TextView和ImageView位置
     * @param gravity 位置
     */
    private void setLayoutGravity(int gravity) {
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams imgParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (gravity == CustomToastPopWindow.LEFT) {
            textParams.addRule(RelativeLayout.CENTER_VERTICAL);
            textParams.addRule(RelativeLayout.END_OF, R.id.tv_content);
            mImage.setLayoutParams(textParams);
        } else if (gravity == CustomToastPopWindow.RIGHT) {
            mImage.setRotation(180);
            textParams.addRule(RelativeLayout.CENTER_VERTICAL);
            mImage.setLayoutParams(textParams);
            imgParams.addRule(RelativeLayout.END_OF, R.id.iv_img);
            mTvContent.setLayoutParams(imgParams);
        } else if (gravity == CustomToastPopWindow.TOP) {
            textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            textParams.addRule(RelativeLayout.BELOW, R.id.tv_content);
            mImage.setLayoutParams(textParams);
        } else if (gravity == CustomToastPopWindow.BOTTOM) {
            mImage.setRotation(180);
            textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            mImage.setLayoutParams(textParams);
            imgParams.addRule(RelativeLayout.BELOW, R.id.iv_img);
            mTvContent.setLayoutParams(imgParams);
        }
    }

    /**
     * 关闭popWindow
     */
    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * popWindow是否已显示
     */
    public boolean isShowing() {
        if (mPopupWindow != null) {
            return mPopupWindow.isShowing();
        }
        return false;
    }


    public static class PopupWindowBuilder {
        private final CustomToastPopWindow mCustomPopWindow;

        public PopupWindowBuilder(Context context) {
            mCustomPopWindow = new CustomToastPopWindow(context);
        }

        public PopupWindowBuilder size(int width, int height) {
            mCustomPopWindow.mWidth = width;
            mCustomPopWindow.mHeight = height;
            return this;
        }

        public PopupWindowBuilder setText(String text) {
            mCustomPopWindow.mTvContent.setText(text);
            return this;
        }

        public PopupWindowBuilder setTextDrawable(int resId) {
            mCustomPopWindow.mTvContent.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
            return this;
        }

        public PopupWindowBuilder setBackground(Drawable background) {
            mCustomPopWindow.mTvContent.setBackground(background);
            return this;
        }

        public PopupWindowBuilder setImageViewVisible(int resId, int gravity) {
            mCustomPopWindow.mImage.setImageResource(resId);
            mCustomPopWindow.setLayoutGravity(gravity);
            return this;
        }

        public PopupWindowBuilder setOutsideTouchable(boolean outsideTouchable) {
            mCustomPopWindow.mIsOutside = outsideTouchable;
            return this;
        }

        public PopupWindowBuilder setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
            mCustomPopWindow.mOnDismissListener = onDismissListener;
            return this;
        }

        public PopupWindowBuilder setTouchable(boolean touchable) {
            mCustomPopWindow.mTouchable = touchable;
            return this;
        }

        public CustomToastPopWindow create() {
            //构建PopWindow
            mCustomPopWindow.build();
            return mCustomPopWindow;
        }
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    private float dp2px(int dp) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
