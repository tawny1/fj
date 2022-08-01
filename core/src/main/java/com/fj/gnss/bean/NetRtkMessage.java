package com.fj.gnss.bean;

/**
 * @author tony.tang
 * date 2022/4/8 17:36
 * @version 1.0
 * @className NetRtkMessage
 * @description 网络rtk信息
 */
public class NetRtkMessage {
    private boolean isNormal;
    /**
     * 切换的时候往app发请求
     */
    private String errMessage;
    /**
     * 时间数据
     */
    private String timeMessage;

    /**
     * app获取的网络rtk数据
     * @param isNormal 是否正常
     * @param errMessage 错误信息
     * @param timeMessage 时间信息
     */
    public NetRtkMessage(boolean isNormal, String errMessage, String timeMessage) {
        this.isNormal = isNormal;
        this.errMessage = errMessage;
        this.timeMessage = timeMessage;
    }

    public boolean isNormal() {
        return isNormal;
    }

    public void setNormal(boolean normal) {
        isNormal = normal;
    }

    public NetRtkMessage() {
    }



    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(String timeMessage) {
        this.timeMessage = timeMessage;
    }


    @Override
    public String toString() {
        return "NetRtkMessage{" +
                "isNormal=" + isNormal +
                ", errMessage='" + errMessage + '\'' +
                ", timeMessage='" + timeMessage + '\'' +
                '}';
    }
    public NetRtkMessage applyFail(String errMessage){
        this.setNormal(false);
        this.errMessage=errMessage;
        return this;
    }
    public NetRtkMessage applySuc(String timeMessage){
        this.setNormal(true);
        this.timeMessage=timeMessage;
        return this;
    }
}
