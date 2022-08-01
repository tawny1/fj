package com.fj.gnss.bean;

/**
 * Description:网络通讯枚举类
 * Date: 2021/10/21
 * Author: Howard.Zhang
 */
public enum RtkMode {

    /**
     * 基站RTK
     */
    RTK_MODE_BASE_STATION(0),

    /**
     * QX网络RTK
     */
    RTK_MODE_QX_NET(1),

    /**
     * Ntrip网络RTK
     */
    RTK_MODE_NTRIP(2);

    /**
     * 通讯模式type
     */
    private int id;


    RtkMode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
