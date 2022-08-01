package com.example.myapplication.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.widget.WheelView;

import java.util.Arrays;

public class WheelActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String[] PLANETS = new String[]{"Mercury,Mercury", "Venus,Venus", "Earth,Earth", "Mars,Mars", "Mars,Mars", "Mars,Mars",
            "Mars,Mars", "Mars,Mars", "Mars,Mars", "Mars,Mars", "Jupiter,Jupiter", "Uranus,Uranus", "Neptune,Neptune", "Pluto,Pluto"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheel);
        WheelView wva = (WheelView) findViewById(R.id.main_wv);

        wva.setSeletion(10);
        wva.setOffset(2);
        wva.setItems(Arrays.asList(PLANETS));
        wva.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(TAG, "selectedIndex: " + selectedIndex + ", item: " + item);
            }
        });
        Log.d(TAG, "selectedIndex: " + wva.getSeletedIndex() + ", item: " + wva.getSeletedItem());
        findViewById(R.id.main_show_dialog_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_show_dialog_btn:
                View outerView = LayoutInflater.from(this).inflate(R.layout.wheel_view, null);
                WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
                wv.setOffset(2);
                wv.setItems(Arrays.asList(PLANETS));
                wv.setSeletion(10);
                wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                    @Override
                    public void onSelected(int selectedIndex, String item) {
                        Log.d(TAG, "[Dialog]selectedIndex: " + selectedIndex + ", item: " + item);
                    }
                });

                new AlertDialog.Builder(this)
                        .setTitle("WheelView in Dialog")
                        .setView(outerView)
                        .setPositiveButton("OK", null)
                        .show();

                break;
        }
    }
}
