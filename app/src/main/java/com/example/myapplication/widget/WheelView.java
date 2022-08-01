package com.example.myapplication.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WheelView extends ScrollView {
    public static final String TAG = WheelView.class.getSimpleName();

    private Context context;
    private LinearLayout views;
    private int scrollDirection = -1;
    private static final int SCROLL_DIRECTION_UP = 0;
    private static final int SCROLL_DIRECTION_DOWN = 1;
    private Paint paint;
    private int viewWidth;
    private List<String> items;

    private int displayItemCount; // 每页显示的数量
    private int selectedIndex = 1; //选中item的下标

    public static final int OFF_SET_DEFAULT = 1;
    private  int offset = OFF_SET_DEFAULT; // 偏移量（需要在最前面和最后面补全）
    private int initialY;   //滑动高度
    private int itemHeight = 0; //每一项itme高度
    private Runnable scrollerTask;
    private int newCheck = 50;

    /**
     * 获取选中区域的边界
     */
    private int[] selectedAreaBorder;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();
        items.addAll(list);
        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        initData();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     *  初始化视图
     *  监听滑动到哪一项
     */
    private void init(Context context) {
        this.context = context;
        this.setVerticalScrollBarEnabled(false);
        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        this.addView(views);
        scrollerTask = () -> {
            int newY = getScrollY();
            if (initialY - newY == 0) { // 滑动停止
                final int remainder = initialY % itemHeight;
                final int divided = initialY / itemHeight;
                if (remainder == 0) {
                    selectedIndex = divided + offset;
                    onSeletedCallBack();
                } else {
                    if (remainder > itemHeight / 2) {
                        WheelView.this.post(() -> {
                            WheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                            selectedIndex = divided + offset + 1;
                            onSeletedCallBack();
                        });
                    } else {
                        WheelView.this.post(() -> {
                            WheelView.this.smoothScrollTo(0, initialY - remainder);
                            selectedIndex = divided + offset;
                            onSeletedCallBack();
                        });
                    }
                }
            } else {
                initialY = getScrollY();
                WheelView.this.postDelayed(scrollerTask, newCheck);
            }
        };
    }

    public void startScrollerTask() {
        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    private void initData() {
        displayItemCount = offset * 2 + 1;
        for (String item : items) {
            if (!TextUtils.isEmpty(item) && item.contains(",")){
                String[] split = item.split(",");
                views.addView(createView(split[0], split[1]));
            }else {
                views.addView(createView("", ""));
            }
        }
        refreshItemView(0);
    }

    /**
     * 创建LinerLayout
     * @param text1 TextView1
     * @param text2 TextView2
     */
    private LinearLayout createView(String text1, String text2) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);
        TextView tv = createTextView(text1);
        TextView tv2 = createTextView(text2);
        linearLayout.addView(tv);
        linearLayout.addView(tv2);
        if (0 == itemHeight) {
            itemHeight = 145;
            linearLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 145));
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
        }
        return linearLayout;
    }

    /**
     * 创建TextView
     * @param text 文本内容
     */
    private TextView createTextView(String text){
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(360, 145));
        tv.setSingleLine(true);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        int padding = dip2px(10);
        tv.setPadding(padding, padding, padding, padding);
        return tv;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
        if (t > oldt) {
            scrollDirection = SCROLL_DIRECTION_DOWN;
        } else {
            scrollDirection = SCROLL_DIRECTION_UP;
        }
    }

    /**
     * 滑动时改变文字大小颜色
     * @param y 滑动高度
     */
    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;
        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }
        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            LinearLayout linearLayout = (LinearLayout) views.getChildAt(i);
            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                TextView itemView = (TextView) linearLayout.getChildAt(j);
                if (null == itemView) {
                    return;
                }
                if (position == i) {
                    itemView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 21);
                    itemView.setTextColor(Color.parseColor("#000000"));
                }else if (position == i - 1 || position == i + 1){
                    itemView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    itemView.setTextColor(Color.parseColor("#999999"));
                } else {
                    itemView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    itemView.setTextColor(Color.parseColor("#CCCCCC"));
                }
            }
        }
    }

    /**
     * 选中区域的高度 top bottom
     */
    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }

    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
    }

    /**
     * 绘制渐变背景
     */
    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
        }
        if (null == paint) {
            paint = new Paint();
            int[] mColors = {
                    Color.parseColor("#FFFFFF"),
                    Color.parseColor("#EEEEEE"),
                    Color.parseColor("#FFFFFF")};
//            LinearGradient linearGradient = new LinearGradient(viewWidth * 1 / 6, obtainSelectedAreaBorder()[0], viewWidth * 5 / 6, obtainSelectedAreaBorder()[1], mColors, null, Shader.TileMode.MIRROR);
            LinearGradient linearGradient = new LinearGradient(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[1], mColors, null, Shader.TileMode.MIRROR);
            paint.setShader(linearGradient);
            paint.setStrokeWidth(dip2px(1f));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
//                Rect rect = new Rect(viewWidth * 1 / 6, obtainSelectedAreaBorder()[0], viewWidth * 5 / 6, obtainSelectedAreaBorder()[1]);
                Rect rect = new Rect(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[1]);
                canvas.drawRect(rect, paint);
            }

            @Override
            public void setAlpha(int alpha) {
            }

            @Override
            public void setColorFilter(ColorFilter cf) {
            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
        super.setBackgroundDrawable(background);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    /**
     * 选中回调
     */
    private void onSeletedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }

    }

    public void setSeletion(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(() -> WheelView.this.smoothScrollTo(0, p * itemHeight));
    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSeletedIndex() {
        return selectedIndex - offset;
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    private OnWheelViewListener onWheelViewListener;

    public OnWheelViewListener getOnWheelViewListener() {
        return onWheelViewListener;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    public static class OnWheelViewListener {
        public void onSelected(int selectedIndex, String item) {
        }
    }
}
