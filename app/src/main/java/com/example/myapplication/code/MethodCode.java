package com.example.myapplication.code;

import static android.os.Looper.getMainLooper;

import android.os.Handler;
import android.widget.TextView;

//伪代码方法
public class MethodCode {


    //文字后面...跑马灯
    private class function1 {
        private Handler mHandler = new Handler(getMainLooper());
        private Runnable mDotAnimRunable = new Runnable() {
            private String dotStr="";
            TextView textView;
            @Override
            public void run() {
                textView.setText("Hall Sensor abnormal"+dotStr);
                mHandler.postDelayed(this, 500);
                dotStr= dotStr.length()==0 ? "." : dotStr.length()==1 ? ".." : dotStr.length()==2 ? "..." : "";
            }
        };
    }

}
