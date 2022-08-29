package com.example.myapplication.activity;

import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.anim.surfaceview.FrameSurfaceView;
import com.example.myapplication.anim.surfaceview.MethodUtil;
import com.example.myapplication.anim.surfaceview.NumberUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class SurfaceActivity extends AppCompatActivity {

    private FrameSurfaceView frameSurfaceView;
    public static final int FRAM_ANIMATION_DURATION = 600;

    private List<Integer> normalBitmaps = Arrays.asList(
            R.drawable.watch_reward_1,
            R.drawable.watch_reward_2,
            R.drawable.watch_reward_3,
            R.drawable.watch_reward_4,
            R.drawable.watch_reward_5,
            R.drawable.watch_reward_6,
            R.drawable.watch_reward_7,
            R.drawable.watch_reward_8,
            R.drawable.watch_reward_9,
            R.drawable.watch_reward_10,
            R.drawable.watch_reward_11,
            R.drawable.watch_reward_12,
            R.drawable.watch_reward_13,
            R.drawable.watch_reward_14,
            R.drawable.watch_reward_15,
            R.drawable.watch_reward_16,
            R.drawable.watch_reward_17,
            R.drawable.watch_reward_18,
            R.drawable.watch_reward_19,
            R.drawable.watch_reward_20,
            R.drawable.watch_reward_21,
            R.drawable.watch_reward_22
    );
    private List<Integer> hugeBitmaps = Arrays.asList(
//            R.raw.frame0,
//            R.raw.frame1,
//            R.raw.frame2,
//            R.raw.frame3,
//            R.raw.frame4,
//            R.raw.frame5,
//            R.raw.frame6,
//            R.raw.frame7,
//            R.raw.frame8,
//            R.raw.frame9,
//            R.raw.frame10,
//            R.raw.frame11,
//            R.raw.frame12,
//            R.raw.frame13,
//            R.raw.frame14,
//            R.raw.frame15,
//            R.raw.frame16,
//            R.raw.frame17,
//            R.raw.frame18,
//            R.raw.frame19
            R.mipmap.pdj_0,
            R.mipmap.pdj_1,
            R.mipmap.pdj_2,
            R.mipmap.pdj_3,
            R.mipmap.pdj_4,
            R.mipmap.pdj_5,
            R.mipmap.pdj_6,
            R.mipmap.pdj_7,
            R.mipmap.pdj_8,
            R.mipmap.pdj_8,
            R.mipmap.pdj_9,
            R.mipmap.pdj_10,
            R.mipmap.pdj_11,
            R.mipmap.pdj_12,
            R.mipmap.pdj_13,
            R.mipmap.pdj_14,
            R.mipmap.pdj_15,
            R.mipmap.pdj_16,
            R.mipmap.pdj_17,
            R.mipmap.pdj_18,
            R.mipmap.pdj_19,
            R.mipmap.pdj_20,
            R.mipmap.pdj_21,
            R.mipmap.pdj_22,
            R.mipmap.pdj_23,
            R.mipmap.pdj_24,
            R.mipmap.pdj_25,
            R.mipmap.pdj_26,
            R.mipmap.pdj_27,
            R.mipmap.pdj_28,
            R.mipmap.pdj_29,
            R.mipmap.pdj_30,
            R.mipmap.pdj_31,
            R.mipmap.pdj_32,
            R.mipmap.pdj_33,
            R.mipmap.pdj_34,
            R.mipmap.pdj_35,
            R.mipmap.pdj_36,
            R.mipmap.pdj_37,
            R.mipmap.pdj_38,
            R.mipmap.pdj_39,
            R.mipmap.pdj_40,
            R.mipmap.pdj_41,
            R.mipmap.pdj_42,
            R.mipmap.pdj_43,
            R.mipmap.pdj_44,
            R.mipmap.pdj_45,
            R.mipmap.pdj_46,
            R.mipmap.pdj_47,
            R.mipmap.pdj_48,
            R.mipmap.pdj_49,
            R.mipmap.pdj_50,
            R.mipmap.pdj_51,
            R.mipmap.pdj_52,
            R.mipmap.pdj_53,
            R.mipmap.pdj_54,
            R.mipmap.pdj_55,
            R.mipmap.pdj_56,
            R.mipmap.pdj_57,
            R.mipmap.pdj_58,
            R.mipmap.pdj_59,
            R.mipmap.pdj_60,
            R.mipmap.pdj_61,
            R.mipmap.pdj_62,
            R.mipmap.pdj_63,
            R.mipmap.pdj_64,
            R.mipmap.pdj_65,
            R.mipmap.pdj_66,
            R.mipmap.pdj_67,
            R.mipmap.pdj_68,
            R.mipmap.pdj_69,
            R.mipmap.pdj_70,
            R.mipmap.pdj_71,
            R.mipmap.pdj_72,
            R.mipmap.pdj_73,
            R.mipmap.pdj_74,
            R.mipmap.pdj_75,
            R.mipmap.pdj_76,
            R.mipmap.pdj_77,
            R.mipmap.pdj_78,
            R.mipmap.pdj_79,
            R.mipmap.pdj_80,
            R.mipmap.pdj_81,
            R.mipmap.pdj_82,
            R.mipmap.pdj_83,
            R.mipmap.pdj_84,
            R.mipmap.pdj_85,
            R.mipmap.pdj_86,
            R.mipmap.pdj_87,
            R.mipmap.pdj_88,
            R.mipmap.pdj_89,
            R.mipmap.pdj_90,
            R.mipmap.pdj_91,
            R.mipmap.pdj_92,
            R.mipmap.pdj_93,
            R.mipmap.pdj_94,
            R.mipmap.pdj_95,
            R.mipmap.pdj_96,
            R.mipmap.pdj_97,
            R.mipmap.pdj_98,
            R.mipmap.pdj_99,
            R.mipmap.pdj_100,
            R.mipmap.pdj_101,
            R.mipmap.pdj_102,
            R.mipmap.pdj_103,
            R.mipmap.pdj_104,
            R.mipmap.pdj_105,
            R.mipmap.pdj_106,
            R.mipmap.pdj_107,
            R.mipmap.pdj_108,
            R.mipmap.pdj_109,
            R.mipmap.pdj_110,
            R.mipmap.pdj_111,
            R.mipmap.pdj_112,
            R.mipmap.pdj_113,
            R.mipmap.pdj_114,
            R.mipmap.pdj_115,
            R.mipmap.pdj_116,
            R.mipmap.pdj_117,
            R.mipmap.pdj_118,
            R.mipmap.pdj_119,
            R.mipmap.pdj_120,
            R.mipmap.pdj_121,
            R.mipmap.pdj_122,
            R.mipmap.pdj_123
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface);

        findViewById(R.id.btn_decode_resource).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long span = MethodUtil.time(new Runnable() {
                    @Override
                    public void run() {
                        BitmapFactory.decodeResource(getResources(), R.drawable.frame4);
                    }
                });
                NumberUtil.average("decode resource", span);
            }
        });

        findViewById(R.id.btn_decode_stream).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long span = MethodUtil.time(new Runnable() {
                    @Override
                    public void run() {
                        InputStream inputStream = getResources().openRawResource(R.raw.frame4);
                        BitmapFactory.decodeStream(inputStream);
                    }
                });
                NumberUtil.average("decode stream", span);
            }
        });

        findViewById(R.id.btn_rapid_decode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long span = MethodUtil.time(new Runnable() {
                    @Override
                    public void run() {
                        InputStream inputStream = getResources().openRawResource(R.raw.frame4);
//                        BitmapDecoder.from(inputStream).decode();
                    }
                });
                NumberUtil.average("rapid decode", span);
            }
        });

        findViewById(R.id.btn_decode_asset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long span = MethodUtil.time(new Runnable() {
                    @Override
                    public void run() {
                        InputStream inputStream = null;
                        try {
                            inputStream = getAssets().open("frame4.png");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        BitmapFactory.decodeStream(inputStream);
                    }
                });
                NumberUtil.average("assets decode", span);
            }
        });
        findViewById(R.id.btn_start_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startAnimationByAnimationDrawable();
            }
        });

        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //play frame animation by FrameSurfaceView which is much more memory-efficient than AnimationDrawable
                frameSurfaceView.setRepeatTimes(FrameSurfaceView.INFINITE);
                frameSurfaceView.start();
            }
        });

        frameSurfaceView = findViewById(R.id.sv_frame);
        frameSurfaceView.setBitmapIds(hugeBitmaps);
        frameSurfaceView.setDuration(FRAM_ANIMATION_DURATION);

    }

    /**
     * play frame animation by AnimationDrawable which will cause a disaster to memory if bitmap is huge(around 1MB)
     */
    private void startAnimationByAnimationDrawable() {
        AnimationDrawable drawable = new AnimationDrawable();
        drawable.addFrame(getResources().getDrawable(R.drawable.frame1), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame2), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame3), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame4), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame5), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame6), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame7), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame8), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame9), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame10), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame11), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame12), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame13), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame14), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame15), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame16), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame17), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame18), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.addFrame(getResources().getDrawable(R.drawable.frame19), FRAM_ANIMATION_DURATION/hugeBitmaps.size());
        drawable.setOneShot(true);

        ImageView ivFrameAnim = ((ImageView) findViewById(R.id.ivFrameAnimation));
        ivFrameAnim.setImageDrawable(drawable);
        drawable.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        frameSurfaceView.destroy();
    }
}
