package com.fj.gnss.basestation;

import com.fj.gnss.callback.OnFrequencyPairingListener;
import com.fj.gnss.callback.RTKCommunicationCallBack;

/**
 * Description:
 * Date: 2021/10/29
 * Author: Howard.Zhang
 */
public interface IBaseStationManager {

    void setRTKCommunicationCallBack(RTKCommunicationCallBack callBack);


    /**
     * @param hexCode  7=>
     * hzValue                [0~3]	4	uint32_t	频次	所有值*1000
     * rate     [4-5]	2	uint32_	波特率枚举	4800:0,8600:1,19200:2
     * protocol [6-7]	2	uint32_	电台协议枚举	TRIMTALK:0，TRIMTALK3:1，Tansparent-EDT:2,SATEL:3
     */
    void setOtherPairingCode(String hexCode);
    /**
     * 对频码 记录对频码
     *
     * @param pairingCode
     */
    void setPairingCode(String pairingCode);

    /**
     * 设置其他基站对拼码
     * @param hexCode
     */
    void setPairingOtherCode(String hexCode);



    void setReceivePairingCmd();

    void autoPairing(int gpsState);

    /**
     * 对频
     *
     * @param code             对频码
     * @param time             次数
     * @param hasInternalRadio 是否内置电台
     * @param first            是否第一次
     * @param listener         回调
     */
    void pairBaseStation(String code, int time, boolean hasInternalRadio, boolean first, OnFrequencyPairingListener listener);

    /**
     *
     * @param hexCode
     *  hzValue         [0~3]	4	uint32_t	频次	所有值*1000
     *  rate     [4-5]	2	uint32_	波特率枚举	4800:0,8600:1,19200:2
     *  protocol [6-7]	2	uint32_	电台协议枚举	TRIMTALK:0
     * @param time  次数
     * @param hasInternalRadio 是否内置电台
     * @param first 是否第一次
     * @param listener 回调
     *                 hzValue
     *                 00066A94 01 01
     */
    void pairBaseOtherStation(String hexCode, int time, boolean hasInternalRadio, boolean first, OnFrequencyPairingListener listener);

    String getPairingCode();


    void stopPair();
}
