package com.fj.gnss.callback;

import com.fj.gnss.bean.GnssState;
import com.fj.gnss.bean.RtkMode;
import com.fj.gnss.ntrip.bean.NtripInfo;

import java.util.List;

/**
 * Description:RTK通讯 回调接口类
 * Date: 2021/10/22
 * Author: Howard.Zhang
 */
public interface RTKCommunicationCallBack {

    boolean isQxNotValid();

    boolean isGnssSimulation();

    boolean isRtkConnected();

    void switchRtkMode(int mode);

    int getSavedRtkMode();

    void saveRtkMode(int mode);

    void saveNtripInfo(NtripInfo ntripInfo);

    void setBaseStationType(boolean isNewBS);

    /**
     * 设置其他基站
     */
    void setOtherBaseStation();

    void pairBaseStation(boolean isNewBS, String pairingCode);


    void pairOtherBaseStation(String hexCode);

    void savePairingCode(String code);

    void saveOtherPairingCode(String hexcode);
    /**
     * 给rtk发送外置电台连接状态
     *
     * @param bytes
     */
    void sendExternalRadioState(byte[] bytes);

    /**
     * 得到差分rtcm流
     *
     * @param rtkMode rtk模式
     * @param rtcm    数据
     */
    void onGetDiffRtcm(RtkMode rtkMode, byte[] rtcm);

    void onGnssStateUpdate(GnssState gnssState);

    void onParsedNmea(String nmea);

    void onParsedGGA(String gga);

    /**
     * 保存ntrip list
     */
    void saveNtripList(List<NtripInfo> ntripInfoList);

    /**
     * 获取ntrip列表
     * @return
     */
    List<NtripInfo> getNtripList();

    String getDeviceSn();

    void deleteNtripInfo(NtripInfo ntripInfo);

    void addNtripInfo(NtripInfo ntripInfo);
}
