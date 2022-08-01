package com.fj.gnss.radio;

import com.fj.gnss.callback.RTKCommunicationCallBack;

/**
 * Description:
 * Date: 2021/10/29
 * Author: Howard.Zhang
 */
public interface IExternalRadioManager {

    void setRTKCommunicationCallBack(RTKCommunicationCallBack callBack);

    void configPublicCmd(String deviceSn);

}
