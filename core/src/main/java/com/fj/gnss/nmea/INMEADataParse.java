package com.fj.gnss.nmea;

import androidx.annotation.NonNull;

import com.fj.gnss.bean.GnssState;
import com.fj.gnss.callback.RTKCommunicationCallBack;

/**
 * @Description New class.
 * @Author Howard Zhang
 * @Date: 2021/12/30
 */
public interface INMEADataParse {


    void setRTKCommunicationCallBack(RTKCommunicationCallBack callBack);

    /**
     * nmea解析
     *
     * @param data data
     * @return 解析完成返回nema，否则返回空
     */
    void parseNMEA(byte[] data);

    @NonNull
    GnssState getGnssState();
}
