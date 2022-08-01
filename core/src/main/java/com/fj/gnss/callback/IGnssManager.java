package com.fj.gnss.callback;

import android.content.Context;

import com.fj.gnss.ntrip.bean.NtripInfo;

/**
 * @Description GNSS库对外暴露的接口
 * @Author Howard Zhang
 * @Date: 2021/12/30
 */
public interface IGnssManager {

    /**
     * GNSS库初始化
     *
     * @param callBack GNSS库回调接口
     */
    void init(RTKCommunicationCallBack callBack);

    /**
     * 初始化基站Manager
     *
     * @param pairingCode 基站对频码
     */
    void initBSManager(String pairingCode);

    /**
     * 初始化千寻Manager
     *
     * @param deviceSn 设备SN
     */
    void initQxManager(String deviceSn);

    /**
     * 初始化NtripManager
     *
     * @param ntripInfo Ntrip服务器、账号等信息
     */
    void initNtripManager(NtripInfo ntripInfo);

    /**
     * 收到基站对频成功回调
     */
    void setReceivePairingCmd();

    /**
     * 设置RTK切换锁
     *
     * @param lock
     */
    void setRtkSwitchLock(boolean lock);

    /**
     * 解析NMEA
     *
     * @param data data
     */
    void parseNMEA(byte[] data);

    /**
     * 开机自检切换RTK模式
     *
     * @throws InterruptedException
     */
    void checkRtkMode() throws InterruptedException;

    /**
     * 开启RTK模式自动切换服务
     *
     * @param context
     */
    void startRtkSwitchService(Context context);

    /**
     * 是否有外置基站
     *
     * @param hasExternalRadio
     */
    void setHasExternalRadio(boolean hasExternalRadio);

    long getQxExpireTime();
}
