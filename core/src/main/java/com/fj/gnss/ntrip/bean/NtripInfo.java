package com.fj.gnss.ntrip.bean;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * 差分信息
 * author:Jarven.ding
 * create:2020/3/28
 */
public class NtripInfo implements Serializable {

    private static final long serialVersionUID = -964757565215662477L;

    private String uuid;

    /**
     * 公司
     */
    private String companyName;

    private String ipAddress;

    private int port;

    private String sourcePoint;

    private String username;

    private String password;

    @Expose(serialize = false, deserialize = false)
    private boolean isAutoLink;

    /**
     * 是否选中
     */
    @Expose(serialize = false, deserialize = false)
    private boolean isSelected;

    /**
     * 是否连接
     */
    @Expose(serialize = false, deserialize = false)
    private boolean isConnected;


    public NtripInfo() {
    }

    public NtripInfo(String uuid, String companyName, String ipAddress, int port, String sourcePoint, String username, String password, boolean isAutoLink, boolean isSelected,
                     boolean isConnected) {
        this.uuid = uuid;
        this.companyName = companyName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.sourcePoint = sourcePoint;
        this.username = username;
        this.password = password;
        this.isAutoLink = isAutoLink;
        this.isSelected = isSelected;
        this.isConnected = isConnected;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSourcePoint() {
        return sourcePoint;
    }

    public void setSourcePoint(String sourcePoint) {
        this.sourcePoint = sourcePoint;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAutoLink() {
        return isAutoLink;
    }

    public void setAutoLink(boolean autoLink) {
        isAutoLink = autoLink;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    @Override
    public String toString() {
        return "NtripInfo{" +
                "ipAddress='" + ipAddress + '\'' +
                ", port=" + port +
                ", sourcePoint='" + sourcePoint + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isAutoLink=" + isAutoLink +
                ", companyName='" + companyName + '\'' +
                ", isSelect=" + isSelected +
                ", isConnect=" + isConnected +
                '}';
    }

    /**
     * 判断两个对象是否相同的值，主要是和之前老对象对齐
     * String 值
     *
     * @return
     */
    public String eqHashCode() {
        return "" + ipAddress + "" + port + "" + sourcePoint + "" + username + ":" + password;
    }

    /**
     * 判断和其他的ntrip是否相同
     *
     * @param info
     * @return
     */
    public boolean eqOtherNtrip(NtripInfo info) {
        if (info == null) {
            return false;
        }
        return eqHashCode().equals(info.eqHashCode());
    }

}
