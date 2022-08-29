package com.example.myapplication.activity;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.anim.FasterAnimationsContainer;
import com.example.myapplication.bean.CheckItem;
import com.example.myapplication.bean.CheckResult;
import com.example.myapplication.bean.CheckType;
import com.example.myapplication.widget.HRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CheckActivity extends Activity {

    private static final String TAG = "CheckFragment";

    private TextView tvWaiting;
    private TextView tvChecking;
    private ImageView ivResult;
    private ImageView ivMachine;
    private RelativeLayout rlCheck;
    private LinearLayout llLayout;
    private HRecyclerView recyclerView;

    private AnimationDrawable anim;

    private FasterAnimationsContainer fasterAnimationsContainer;

    private List<CheckItem> checkItemList = new ArrayList<>();

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Handler mHandler2 = new Handler(Looper.getMainLooper());

    private int waiting = 0;
    private final Runnable waitingAnim = () -> {
        waiting++;
        if (waiting == 4) {
            waiting = 0;
        }
        showWaitingAnim();
    };

    private int index = 0;

    private final Runnable checkRunnable = () -> {
        showCheckResult(checkItemList.get(index));
        index++;
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_start_check);
        tvWaiting = findViewById(R.id.tv_waiting);
        tvChecking = findViewById(R.id.tv_checking);
        ivResult = findViewById(R.id.iv_result);
        llLayout = findViewById(R.id.ll_layout);
        ivMachine = findViewById(R.id.iv_machine);
        rlCheck = findViewById(R.id.rl_check);
        recyclerView = findViewById(R.id.rv_check_list);

        addCheckItem();

        int[] resId = new int[124];

        for (int i = 0; i <= 123; i++) {
            resId[i] = getResources().getIdentifier("pdj_" + i, "mipmap", getPackageName());
        }
        fasterAnimationsContainer = FasterAnimationsContainer.getInstance(ivMachine);
        fasterAnimationsContainer.addAllFrames(resId, 80);
        fasterAnimationsContainer.start();
        fasterAnimationsContainer.setOnAnimationStoppedListener(this::rightAnim);

//        anim = (AnimationDrawable) ivMachine.getBackground();
//        anim.start();

        showCheckItem(checkItemList.get(0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fasterAnimationsContainer.stop();
        mHandler.removeCallbacks(waitingAnim);
        mHandler.removeCallbacksAndMessages(null);
    }

    private void showWaitingAnim() {
        if (waiting == 0) {
            tvWaiting.setText("");
        } else {
            tvWaiting.append(".");
        }
        mHandler.postDelayed(waitingAnim, 300);
    }

    private void addCheckItem() {
        checkItemList.add(new CheckItem(CheckType.TYPE_RTK, "RTK", CheckResult.CODE_SUCCESS, "状态正常"));
        checkItemList.add(new CheckItem(CheckType.TYPE_ECU, "ECU", CheckResult.CODE_SUCCESS, "状态正常"));
        checkItemList.add(new CheckItem(CheckType.TYPE_ANTENNA_PRIMARY, "天线连接", CheckResult.CODE_SUCCESS, "连接异常"));
        checkItemList.add(new CheckItem(CheckType.TYPE_ANTENNA_SECONDARY, "副天线连接", CheckResult.CODE_SUCCESS, "连接异常"));
        checkItemList.add(new CheckItem(CheckType.TYPE_SENSOR_BLADE, "铲刀传感器", CheckResult.CODE_SUCCESS, "连接异常"));
    }


    public void showCheckItem(@NonNull CheckItem checking) {
        rlCheck.setVisibility(View.VISIBLE);
        ivResult.setVisibility(View.INVISIBLE);
        tvChecking.setText(checking.getName());
        mHandler.removeCallbacks(waitingAnim);
        waiting = 0;
        showWaitingAnim();
        enterAnim();

        if (index < checkItemList.size()){
            mHandler2.postDelayed(checkRunnable, 2000);
        }
    }

    public void showCheckResult(@NonNull CheckItem checked) {
        mHandler.removeCallbacks(waitingAnim);
        tvWaiting.setText("...");
        int code = checked.getCode();
        ivResult.setVisibility(View.VISIBLE);
        int res;
        if (code == CheckResult.CODE_SUCCESS) {
            res = R.drawable.ic_check_ok;
        } else {
            res = R.drawable.ic_check_fail;
        }
        ivResult.setImageResource(res);
        iconEnterAnim();
    }

    private void rightAnim() {
        AnimationSet animSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.layout_view_right);
        llLayout.startAnimation(animSet);
        tvChecking.setVisibility(View.GONE);
        tvWaiting.setVisibility(View.GONE);

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(new CheckAdapter());
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_item_view);
        recyclerView.setLayoutAnimation(controller);
    }

    private void enterAnim() {
        AnimationSet animSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.check_view_enter);
        rlCheck.startAnimation(animSet);
    }

    private void iconEnterAnim() {
        AnimationSet animSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.check_icon_enter);
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                exitAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ivResult.startAnimation(animSet);
    }

    private void exitAnim() {
        AnimationSet animSet = (AnimationSet) AnimationUtils.loadAnimation(this, R.anim.check_view_exit);
        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rlCheck.setVisibility(View.INVISIBLE);
                if (index < checkItemList.size()){
                    showCheckItem(checkItemList.get(index));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        rlCheck.startAnimation(animSet);
    }


    private class CheckAdapter extends RecyclerView.Adapter<CheckViewHolder> {

        @NonNull
        @Override
        public CheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(CheckActivity.this).inflate(R.layout.layout_check_item, parent, false);
            CheckViewHolder viewHolder = new CheckViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CheckViewHolder holder, int position) {
            holder.tvCheckName.setText(checkItemList.get(position).getName());
            holder.tvCheckDesc.setText(checkItemList.get(position).getDesc());
        }

        @Override
        public int getItemCount() {
            return checkItemList.size();
        }

    }

    static class CheckViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCheckName, tvCheckDesc;

        public CheckViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCheckName = itemView.findViewById(R.id.tv_check_name);
            tvCheckDesc = itemView.findViewById(R.id.tv_check_desc);
        }
    }

}
