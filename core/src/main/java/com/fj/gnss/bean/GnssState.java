package com.fj.gnss.bean;

import java.util.Arrays;

/**
 * Gnss状态信息
 * author：jarven.ding
 */
public class GnssState {

    /**
     * 主天线纬度
     */
    private double lat;

    /**
     * 主天线经度
     */
    private double lon;


    /**
     * 主天线海拔
     */
    private double altitude;

    /**
     * 主天线大地高
     */
    private double height;


    /**
     * 主天线NEH
     */
    private double[] neh = {0, 0, 0};

    /**
     * 副天线纬度
     */
    private double lat2;

    /**
     * 副天线经度
     */
    private double lon2;

    /**
     * 副天线海拔
     */
    private double altitude2;

    /**
     * 副天线大地高
     */
    private double height2;

    /**
     * 副天线NEH
     */
    private double[] neh2 = {0, 0, 0};


    /**
     * 北半球
     */
    private boolean northEarth = true;

    /**
     * 东半球
     */
    private boolean eastEarth = true;

    /**
     * 连接卫星数
     */
    private double satelliteNum;

    /**
     * 主天线-RTK连接状态
     * 0 - 未连接
     * 1 - 单点解
     * 2 - 弱点解
     * 4 - 固定解
     * 5 - 浮点解
     * 不等于4 就是异常
     */
    private int gpsState;

    /**
     * 副天线-RTK连接状态
     */
    private int gpsState2;

    /**
     * 原始航向
     */
    private double originHeading = 0;

    /**
     * 纠偏航向
     */
    private double heading = 0;

    /**
     * 速度
     */
    private double speed;

    /**
     * rtk模式
     * 0 - 基站模式
     * 1 - 网络模式
     */
    private int rtkMode;

    /**
     * 基站类型
     * 0 - 老基站
     * 1 - 新基站
     */
    private int rtkType;

    /**
     * 是否有电台
     */
    private int rtkHasRadio;

    /**
     * GGA有效性
     */
    private boolean isValid;

    /**
     * rtk程序版本号
     */
    private int rtkVersion;

    /**
     * rtk板卡芯片类型
     * 0 - 482
     * 1 - 司南
     */
    private int rtkCore;

    /**
     * RTK数据接收时间
     */
    private long receiveTime = 0;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSatelliteNum() {
        return satelliteNum;
    }

    public void setSatelliteNum(double satelliteNum) {
        this.satelliteNum = satelliteNum;
    }

    public int getGpsState() {
        return gpsState;
    }

    public void setGpsState(int gpsState) {
        this.gpsState = gpsState;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getRtkMode() {
        return rtkMode;
    }

    public void setRtkMode(int rtkMode) {
        this.rtkMode = rtkMode;
    }

    public int getRtkType() {
        return rtkType;
    }

    public void setRtkType(int rtkType) {
        this.rtkType = rtkType;
    }

    public int getRtkHasRadio() {
        return rtkHasRadio;
    }

    public void setRtkHasRadio(int rtkHasRadio) {
        this.rtkHasRadio = rtkHasRadio;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public boolean isTimeOut() {
        return System.currentTimeMillis() - receiveTime > 2000;
    }

    public void setReceiveTime(long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public double[] getNeh() {
        return neh;
    }

    public void setNeh(double[] neh) {
        this.neh = neh;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getLat2() {
        return lat2;
    }

    public void setLat2(double lat2) {
        this.lat2 = lat2;
    }

    public double getLon2() {
        return lon2;
    }

    public void setLon2(double lon2) {
        this.lon2 = lon2;
    }

    public double getAltitude2() {
        return altitude2;
    }

    public void setAltitude2(double altitude2) {
        this.altitude2 = altitude2;
    }

    public double getHeight2() {
        return height2;
    }

    public void setHeight2(double height2) {
        this.height2 = height2;
    }

    public double[] getNeh2() {
        return neh2;
    }

    public void setNeh2(double[] neh2) {
        this.neh2 = neh2;
    }

    public int getRtkVersion() {
        return rtkVersion;
    }

    public boolean supportNtripMode() {
        return rtkVersion >= 1135;
    }

    public void setRtkVersion(int rtkVersion) {
        this.rtkVersion = rtkVersion;
    }

    public boolean isNorthEarth() {
        return northEarth;
    }

    public void setNorthEarth(boolean northEarth) {
        this.northEarth = northEarth;
    }

    public boolean isEastEarth() {
        return eastEarth;
    }

    public void setEastEarth(boolean eastEarth) {
        this.eastEarth = eastEarth;
    }

    public int getRtkCore() {
        return rtkCore;
    }

    public void setRtkCore(int rtkCore) {
        this.rtkCore = rtkCore;
    }

    public double getOriginHeading() {
        return originHeading;
    }

    public void setOriginHeading(double originHeading) {
        this.originHeading = originHeading;
    }

    public int getGpsState2() {
        return gpsState2;
    }

    public void setGpsState2(int gpsState2) {
        this.gpsState2 = gpsState2;
    }

    @Override
    public String toString() {
        return "GnssState{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", altitude=" + altitude +
                ", height=" + height +
                ", neh=" + Arrays.toString(neh) +
                ", lat2=" + lat2 +
                ", lon2=" + lon2 +
                ", altitude2=" + altitude2 +
                ", height2=" + height2 +
                ", neh2=" + Arrays.toString(neh2) +
                ", northEarth=" + northEarth +
                ", eastEarth=" + eastEarth +
                ", satelliteNum=" + satelliteNum +
                ", gpsState=" + gpsState +
                ", gpsState2=" + gpsState2 +
                ", originHeading=" + originHeading +
                ", heading=" + heading +
                ", speed=" + speed +
                ", rtkMode=" + rtkMode +
                ", rtkType=" + rtkType +
                ", rtkHasRadio=" + rtkHasRadio +
                ", isValid=" + isValid +
                ", rtkVersion=" + rtkVersion +
                ", rtkCore=" + rtkCore +
                ", receiveTime=" + receiveTime +
                '}';
    }
}
