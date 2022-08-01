package com.fj.gnss.nmea;

/**
 * Description:网络rtcm管理
 * Date: 2021/10/27
 * Author: Howard.Zhang
 */
public interface INetRtcmManager {

    /**
     * 设置锁
     *
     * @param lock
     */
    void setLock(boolean lock);

    /**
     * 发送gga到网络RTK服务端
     *
     * @param ggaNMEA
     */
    void setGgaNMEA(String ggaNMEA);

    /**
     * 发送rtcm连接
     *
     * @param rtkMode
     */
    void netRTCM(int rtkMode);

    /**
     * 停止RTCM
     */
    void stopRTCM();

    /**
     * 锁住manager
     *
     * @return
     */
    boolean isLock();
}
