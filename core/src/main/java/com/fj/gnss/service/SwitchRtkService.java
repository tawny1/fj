package com.fj.gnss.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.fj.construction.tools.helper.BackgroundLooper;
import com.fj.gnss.GnssManager;
import com.fj.gnss.bean.GnssState;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fjdynamics.xlog.FJXLog;

import java.util.Locale;

/**
 * @ClassName SwitchRtkService
 * @Description 根据设置自动更正RTK模式服务
 * @Author FJD
 * @Date 2021/9/27
 */
public class SwitchRtkService extends Service {

    private static final String TAG = SwitchRtkService.class.getSimpleName();

    private final Handler mHandler = new Handler(BackgroundLooper.getLooper());

    private final Runnable checkRtkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                checkNeedSwitchRtkMode();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 每15s检测一下RTK是否跟设置一致
            mHandler.postDelayed(checkRtkRunnable, 15000);
        }
    };

    /**
     * 仅供自检的时候自动恢复RTK模式使用
     *
     * @throws InterruptedException
     */
    public static void autoSwitchRtkMode() throws InterruptedException {
        RTKCommunicationCallBack rtkCommCallback = GnssManager.getInstance().getRTKCommunicationCallBack();
        if (rtkCommCallback == null || !rtkCommCallback.isRtkConnected()) {
            // 回调接口还没初始化，或者RTK根本就没启动，就跳过此次检测
            FJXLog.INSTANCE.e(TAG, "autoSwitchRtkMode: rtkCommCallback invalid");
            return;
        }
        int savedRtkMode = rtkCommCallback.getSavedRtkMode();
        if (rtkCommCallback.isQxNotValid() && savedRtkMode == 1) {
            FJXLog.INSTANCE.d(TAG, "autoSwitchRtkMode: 千寻服务不可用，设置RTK模式 = 1，需要切换！");
            savedRtkMode = 2;
            // 环境不支持QX网络RTK（1），首先需要更改本地保存的RTK模式
            rtkCommCallback.saveRtkMode(savedRtkMode);
        }

        GnssState gnssState = GnssManager.getInstance().getNmeaDataParse().getGnssState();
        int gnssRtkMode = gnssState.getRtkMode();
        FJXLog.INSTANCE.d(TAG, String.format(Locale.US, "autoSwitchRtkMode: 当前RTK模式 = %d, 设置RTK模式 = %d", gnssRtkMode, savedRtkMode));
        if (gnssRtkMode != savedRtkMode) {
            // 切换到本地设置
            rtkCommCallback.switchRtkMode(savedRtkMode);
            Thread.sleep(100);
            GnssManager.getInstance().getNmeaDataParse().getGnssState().setReceiveTime(0);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FJXLog.INSTANCE.d(TAG, "onStartCommand");
        mHandler.removeCallbacks(checkRtkRunnable);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.post(checkRtkRunnable);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 检查是否需要切换RTK模式
     */
    private void checkNeedSwitchRtkMode() throws InterruptedException {
        if (GnssManager.getInstance().getNetRtcmManager().isLock()) {
            // 如果用户正在切换RTK模式
            FJXLog.INSTANCE.e(TAG, "checkNeedSwitchRtkMode: switch locked");
            return;
        }
        autoSwitchRtkMode();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeCallback();
    }

    private void removeCallback() {
        mHandler.removeCallbacks(checkRtkRunnable);
        mHandler.removeCallbacksAndMessages(null);
    }


}
