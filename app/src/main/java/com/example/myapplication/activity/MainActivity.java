package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.widget.CustomPopWindow;
import com.example.myapplication.widget.TriangleView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CheckActivity.class)));

        findViewById(R.id.btn2).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SurfaceActivity.class)));

        findViewById(R.id.btn3).setOnClickListener(v ->
                Log.e("TAG--->", v.getY() + "")
//                startActivity(new Intent(MainActivity.this, WheelActivity.class))
    );

        findViewById(R.id.btn4).setOnClickListener(v ->
                {
                    Log.e("TAG--->", v.getY() + "");
//                    startActivity(new Intent(MainActivity.this, ProgressActivity.class));
                }
        );

        findViewById(R.id.btn5).setOnClickListener(v -> {
//                startActivity(new Intent(MainActivity.this, TestActivity.class))
            View contentView = LayoutInflater.from(this).inflate(R.layout.popup_left_or_right,null);
            int windowPos[] = calculatePopWindowPos(v, contentView);
            CustomPopWindow  mPopupWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                .size(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                .setFocusable(true)
                .setOutsideTouchable(true)
//                .setAnimationStyle(R.style.AnimUp)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        // popupWindow隐藏时恢复屏幕正常透明度

                    }
                })
                .create()
                .showAtLocation(v, Gravity.START | Gravity.TOP, windowPos[0], windowPos[1]);
        });

        EditText editTextN1 = findViewById(R.id.editTextN1);
        EditText editTextN2 = findViewById(R.id.editTextN2);
        EditText editTextN3 = findViewById(R.id.editTextN3);
        EditText editTextE1 = findViewById(R.id.editTextE1);
        EditText editTextE2 = findViewById(R.id.editTextE2);
        EditText editTextE3 = findViewById(R.id.editTextE3);
        TriangleView triangleView = findViewById(R.id.triangle);

        findViewById(R.id.bt).setOnClickListener(v ->
                {
                    Log.e("TAG--->", v.getY() + "");
                    View contentView = LayoutInflater.from(this).inflate(R.layout.popup_left_or_right,null);
                    int windowPos[] = calculatePopWindowPos(v, contentView);
                    CustomPopWindow  mPopupWindow = new CustomPopWindow.PopupWindowBuilder(this)
                            .setView(contentView)
                            .size(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setFocusable(true)
                            .setOutsideTouchable(true)
//                .setAnimationStyle(R.style.AnimUp)
                            .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    // popupWindow隐藏时恢复屏幕正常透明度

                                }
                            })
                            .create()
                            .showAtLocation(v, Gravity.START | Gravity.TOP, windowPos[0], windowPos[1]);
                }
//                triangleView.test2(Integer.parseInt(editTextN1.getText().toString()), Integer.parseInt(editTextE1.getText().toString()),
//                Integer.parseInt(editTextN2.getText().toString()), Integer.parseInt(editTextE2.getText().toString()),
//                Integer.parseInt(editTextN3.getText().toString()), Integer.parseInt(editTextE3.getText().toString()))
        );

    }


    public static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth =  getScreenWidth(anchorView.getContext());
        Log.e("TAG--->screenWidth", screenWidth + "");
        Log.e("TAG--->screenHeight", screenHeight + "");
        // 测量contentView
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        Log.e("TAG--->windowWidth", windowWidth + "");
        Log.e("TAG--->windowHeight", windowHeight + "");
        Log.e("TAG--->anchorLoc[0]", anchorLoc[0] + "");
        Log.e("TAG--->anchorLoc[1]", anchorLoc[1] + "");
        // 判断需要向上弹出还是向下弹出显示
//        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
//        if (isNeedShowUp) {
//            windowPos[0] = screenWidth - windowWidth;
//            windowPos[1] = anchorLoc[1] - windowHeight;
//        } else {
//            windowPos[0] = screenWidth - windowWidth;
//            windowPos[1] = anchorLoc[1] + anchorHeight;
//        }
        windowPos[0] = anchorLoc[0] - windowWidth;
        windowPos[1] = anchorLoc[1];
        return windowPos;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}