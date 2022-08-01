package com.fj.gnss.qx;

import com.fj.gnss.callback.RTKCommunicationCallBack;

/**
 * Description:千寻管理接口类
 * Date: 2021/10/26
 * Author: Howard.Zhang
 */
public interface IFJQxManager {

    void setRTKCommunicationCallBack(RTKCommunicationCallBack rtkCommunicationCallBack);

    /**
     * 千寻设置sn
     *
     * @param deviceSn
     */
    void setDeviceSn(String deviceSn);

    /**
     * 千寻开启rtcm服务
     */
    void startRtcmServer();

    /**
     * 千寻停止rtcm服务
     */
    void stopRtcmServer();

    /**
     * 千寻设置GGA
     *
     * @param ggaNMEA
     */
    void setGGA(String ggaNMEA);

    long getExpireTime();
}
