package com.fj.gnss.bean;

/**
 * @author tony.tang
 * date 2022/4/8 17:59
 * @version 1.0
 * @className NetRequestMessage
 * @description 子模块fragment向app发事件
 */
public class NetRtkRequestMessage {
    private boolean isRequest;

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }

    public NetRtkRequestMessage() {
    }

    public NetRtkRequestMessage(boolean isRequest) {
        this.isRequest = isRequest;
    }

    @Override
    public String toString() {
        return "NetRtkRequestMessage{" +
                "isRequest=" + isRequest +
                '}';
    }
}
