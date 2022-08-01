package com.fj.gnss.ntrip;

import com.fj.gnss.callback.RTKCommunicationCallBack;
import com.fj.gnss.ntrip.bean.NtripInfo;

/**
 * Description:Ntrip功能接口类
 * Date: 2021/10/26
 * Author: Howard.Zhang
 */
public interface IFJNtripManager {

    void setRTKCommunicationCallBack(RTKCommunicationCallBack rTKCommunicationCallBack);

    void setNtripInfo(NtripInfo ntripInfo);

    void stop();

    NtripInfo getNtripInfo();

    boolean isNtripEditing();

    NtripManager.NtripState getNtripConnectState();

    void sendGGA(String ggaNMEA);

    void linkSource();

    void setNtripConnectState(NtripManager.NtripState linkToNodeFailed);

    void setDiffRtcm(byte[] bytes);

    void getSource();

    void addNtripListener(NtripManager.NtripListener ntripListener);

    void setNtripEditing(boolean b);

    void removeNtripListener(NtripManager.NtripListener ntripListener);
}
