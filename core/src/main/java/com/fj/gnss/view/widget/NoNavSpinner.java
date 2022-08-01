package com.fj.gnss.view.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * Description: 解决Spinner获取监听导致导航栏弹出问题。
 * Date: 2021/9/21
 * Author: Howard.Zhang
 */
@SuppressLint("AppCompatCustomView")
public class NoNavSpinner extends Spinner {

    private OnSelectedListener onSelectedListener;

    public NoNavSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnSelectedListener(null);
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        this.onSelectedListener = listener;
        // 在每次选择之后，关闭底部导航栏
        setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (onSelectedListener != null) {
                    onSelectedListener.onItemSelected(position);
                }
                setFullScreen();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setFullScreen();
            }
        });
    }


    private void setFullScreen() {
        Context context = getContext();
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            View decorView = ((Activity) context).getWindow().getDecorView();
            // 这几个整数参数API是不开放的
            int INVISBLE = 0x00400000 | 0x00200000 | 0x01000000
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(INVISBLE);
        }
    }


    public interface OnSelectedListener {
        void onItemSelected(int position);
    }
}
