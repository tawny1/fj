package com.fj.gnss;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.fj.construction.ui.toast.MyToast;
import com.fj.gnss.basestation.BaseStationManager;
import com.fj.gnss.basestation.IBaseStationManager;
import com.fj.gnss.bean.GnssState;
import com.fj.gnss.bean.RtkMode;
import com.fj.gnss.callback.IGnssManager;
import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fj.gnss.nmea.INMEADataParse;
import com.fj.gnss.nmea.INetRtcmManager;
import com.fj.gnss.nmea.NMEADataParse;
import com.fj.gnss.nmea.NetRTCMManager;
import com.fj.gnss.ntrip.IFJNtripManager;
import com.fj.gnss.ntrip.NtripManager;
import com.fj.gnss.ntrip.bean.NtripInfo;
import com.fj.gnss.qx.FJQxManager;
import com.fj.gnss.qx.IFJQxManager;
import com.fj.gnss.radio.IExternalRadioManager;
import com.fj.gnss.radio.JZRTKManager;
import com.fj.gnss.service.SwitchRtkService;
import com.fj.gnss.callback.OnRtkSwitchListener;
import com.fj.gnss.utils.NetRtkUtils;
import com.fjdynamics.xlog.FJXLog;

import java.util.Locale;

/**
 * Description:GNSS服务代理类
 * Date: 2021/10/22
 * Author: Howard.Zhang
 */
public class GnssManager implements IGnssManager {

    private static final String TAG = GnssManager.class.getSimpleName();

    private static class SingletonHolder {

        // 非绑定延迟加载，静态初始化线程安全
        private static final GnssManager INSTANCE = new GnssManager();
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private RTKCommunicationCallBack mRTKCommunicationCallBack;//通讯接口

    private final IBaseStationManager mBaseStationManager;//基站对频管理接口

    private final IExternalRadioManager mExtRadioManager;//外置电台管理接口

    private final IFJQxManager mQXManager;//千寻管理端

    private final IFJNtripManager mNtripManager;//ntrip接口管理

    private final INetRtcmManager mNetRtcmManager;//rtcm接口管理

    private final INMEADataParse mNmeaDataParse;//nmea接口管理

    private Context mContext;

    /**
     * 差分改正RTCM流发送时间
     */
    private long rtcmTime;

    /**
     * 是否有105模块，默认存在105模块
     */
    private boolean is105Exist = true;

    private boolean hasExternalRadio = false;

    /**
     * 得到管理对象
     *
     * @return
     */
    public static GnssManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 初始化管理
     */
    private GnssManager() {
        this.mQXManager = new FJQxManager();
        this.mNtripManager = new NtripManager();
        this.mBaseStationManager = new BaseStationManager();
        this.mExtRadioManager = new JZRTKManager();
        this.mNetRtcmManager = new NetRTCMManager();
        this.mNmeaDataParse = new NMEADataParse();
    }

    public IBaseStationManager getBaseStationManager() {
        return mBaseStationManager;
    }

    public IExternalRadioManager getExternalRadioManager() {
        return mExtRadioManager;
    }

    public IFJQxManager getQxManager() {
        return mQXManager;
    }

    public IFJNtripManager getNtripManager() {
        return mNtripManager;
    }

    public INetRtcmManager getNetRtcmManager() {
        return mNetRtcmManager;
    }

    public INMEADataParse getNmeaDataParse() {
        return mNmeaDataParse;
    }

    public void setAutoLink(GnssState gnssState) {
        if (gnssState.isValid()) {
            if (gnssState.getRtkMode() > 0) {
                // 网络RTK自动重连
                int rtkMode = this.mRTKCommunicationCallBack.getSavedRtkMode();
                mNetRtcmManager.netRTCM(rtkMode);
            } else {
                // 基站RTK自动对频
                mNetRtcmManager.stopRTCM();
                mBaseStationManager.autoPairing(gnssState.getGpsState());
            }
        }
    }

    /**
     * 切换rtk
     *
     * @param mode 模式
     * @param time 次数
     */
    public void switchMode(final int mode, final int time, boolean first, OnRtkSwitchListener listener) {
        FJXLog.INSTANCE.d(TAG, "switchMode: time = " + time);
        if (mRTKCommunicationCallBack == null || mRTKCommunicationCallBack.isGnssSimulation()) {
            // GNSS模拟、回调接口未设置，直接失败
            FJXLog.INSTANCE.e(TAG, "switchMode: GNSS模拟、回调接口未设置，直接失败 ");
            if (listener != null) {
                listener.onSwitchFail();
            }
            return;
        } else if (mNmeaDataParse.getGnssState().getRtkMode() == mode) {
            // 当前GNSS模式已经是设置的模式，直接成功
            if (listener != null) {
                listener.onSwitchSuccess(mode);
            }
            if (mRTKCommunicationCallBack != null) {
                mRTKCommunicationCallBack.saveRtkMode(mode);
            }
            return;
        }

        if (first) {
            if (!mRTKCommunicationCallBack.isRtkConnected()) {
                // GNSS模拟、回调接口未设置，直接失败
                FJXLog.INSTANCE.e(TAG, "switchMode: isRtkConnected = false, 直接失败 ");
                if (listener != null) {
                    listener.onSwitchFail();
                }
                return;
            }

            // 首次切换
            if (listener != null) {
                listener.onSwitchStart();
            }
            try {
                // 切换前，关闭Qx和Ntrip服务
                mQXManager.stopRtcmServer();
                mNtripManager.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 切换前
        if (mRTKCommunicationCallBack != null) {
            mRTKCommunicationCallBack.switchRtkMode(mode);
        }

        mHandler.postDelayed(() -> {
            if (time > 1) {
                // 切换失败需要继续检测切换
                switchMode(mode, time - 1, false, listener);
            } else {
                MyToast.error(R.string.operation_failed);
            }
        }, 2000);

    }

    /**
     * 得到通讯对象
     */
    public RTKCommunicationCallBack getRTKCommunicationCallBack() {
        return this.mRTKCommunicationCallBack;
    }

    /**
     * 设置回调通讯接口
     *
     * @param rTKCommunicationCallBack
     */
    @Override
    public void init(RTKCommunicationCallBack rTKCommunicationCallBack) {
        this.mRTKCommunicationCallBack = rTKCommunicationCallBack;

        mQXManager.setRTKCommunicationCallBack(this.mRTKCommunicationCallBack);
        mNtripManager.setRTKCommunicationCallBack(this.mRTKCommunicationCallBack);
        mBaseStationManager.setRTKCommunicationCallBack(this.mRTKCommunicationCallBack);
        mExtRadioManager.setRTKCommunicationCallBack(this.mRTKCommunicationCallBack);
        mNmeaDataParse.setRTKCommunicationCallBack(this.mRTKCommunicationCallBack);
    }

    @Override
    public void initBSManager(String pairingCode) {
        mBaseStationManager.setPairingCode(pairingCode);
    }

    @Override
    public void setReceivePairingCmd() {
        mBaseStationManager.setReceivePairingCmd();
    }

    /**
     * QxManager初始化
     *
     * @param deviceSn
     */
    @Override
    public void initQxManager(String deviceSn) {
        mQXManager.setDeviceSn(deviceSn);
    }

    /**
     * Ntrip初始化
     *
     * @param ntripInfo
     */
    @Override
    public void initNtripManager(NtripInfo ntripInfo) {
        mNtripManager.setNtripInfo(ntripInfo);
    }

    @Override
    public void setRtkSwitchLock(boolean lock) {
        mNetRtcmManager.setLock(lock);
    }

    @Override
    public void parseNMEA(byte[] data) {
        mNmeaDataParse.parseNMEA(data);
    }

    @Override
    public void checkRtkMode() throws InterruptedException {
        SwitchRtkService.autoSwitchRtkMode();
    }

    @Override
    public void startRtkSwitchService(Context context) {
        // 切换RTK模式
        mContext =context;
        context.startService(new Intent(context, SwitchRtkService.class));
        NetRtkUtils.getInstance().setMcontext(context);
    }

    /**
     * 设置差分改正RTCM流发送时间
     *
     * @param rtkMode
     * @param rtcmReceiveTime
     */
    public void setRtcmSendTime(RtkMode rtkMode, long rtcmReceiveTime) {
        // 获取差分rtcm的rtk模式，rtk程序上报的模式一致
        if (rtkMode.getId() == mNmeaDataParse.getGnssState().getRtkMode()) {
            FJXLog.INSTANCE.d(TAG, String.format(Locale.US,
                    "setRtcmSendTime: rtkMode = %s, rtcmReceiveTime = %d", rtkMode.getId(), rtcmReceiveTime));
            this.rtcmTime = rtcmReceiveTime;
        }
    }

    /**
     * RTCM流超时
     *
     * @return
     */
    public boolean isRtcmTimeOut() {
        return System.currentTimeMillis() - rtcmTime > 5000;
    }

    /**
     * 设置有105芯片
     *
     * @return
     */
    public boolean is105Exist() {
        return is105Exist;
    }

    /**
     * 是否有105芯片
     *
     * @param is105Exist
     */
    public void set105Exist(boolean is105Exist) {
        this.is105Exist = is105Exist;
    }

    /**
     * 是否连接过外置电台
     *
     * @return
     */
    public boolean isHasExternalRadio() {
        return hasExternalRadio;
    }

    /**
     * 连接过外置电台
     *
     * @param hasExternalRadio
     */
    @Override
    public void setHasExternalRadio(boolean hasExternalRadio) {
        this.hasExternalRadio = hasExternalRadio;
    }

    @Override
    public long getQxExpireTime() {
        return mQXManager.getExpireTime();
    }

    /**
     * 有内置基站
     *
     * @return
     */
    public boolean isHasInternalRadio() {
        return is105Exist && GnssManager.getInstance().getNmeaDataParse().getGnssState().getRtkHasRadio() == 1;
    }



}
