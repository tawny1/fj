package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.anim.path.PathActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CheckActivity.class)));

        findViewById(R.id.btn2).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SurfaceActivity.class)));

        findViewById(R.id.btn3).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, WheelActivity.class))
    );

        findViewById(R.id.btn4).setOnClickListener(v -> {
                    startActivity(new Intent(MainActivity.this, ProgressActivity.class));
        });

        findViewById(R.id.btn5).setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, PathActivity.class));
        });

        findViewById(R.id.iv_img).setOnClickListener(v -> {
//            AnimationSet animSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
//            findViewById(R.id.iv_img).startAnimation(animSet);
            RotateAnimation rotate = new RotateAnimation(
                    0,
                    360,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(1500);
            rotate.setInterpolator(new LinearInterpolator());
            rotate.setRepeatCount(-1);
            findViewById(R.id.iv_img).startAnimation(rotate);
        });

        EditText editText = findViewById(R.id.editText);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.view).setRotation(Float.parseFloat(editText.getText().toString()));
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}