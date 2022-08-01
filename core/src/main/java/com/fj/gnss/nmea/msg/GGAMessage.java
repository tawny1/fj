package com.fj.gnss.nmea.msg;

import android.text.TextUtils;

/**
 * GGAGPS数据
 *
 * @author jarven.ding
 */
public class GGAMessage extends BaseNMEAMessage {

    private static final String HEAD = "$GPGGA";
    private static final String HEAD_1 = "$GNGGA";

    private String timeUTC;
    private double latitude;
    /**
     * 纬度半球
     */
    private String latitudeHemisphere;
    private boolean northEarth = true;
    private double longitude;
    /**
     * 经度半球
     */
    private String longitudeHemisphere;
    private boolean eastEarth = true;
    /**
     * 0初始化， 1单点定位， 2码差分， 3无效PPS， 4固定解， 5浮点解， 6正在估算 7，人工输入固定值， 8模拟模式， 9WAAS差分
     */
    private int gpsState;
    private int satelliteNum;
    /**
     * 海拔
     */
    private double altitude;

    /**
     * 大地高
     */
    private double height;

    @Override
    public boolean parse(String data) {
        data = data.trim();
        //PS# Ｅ N
        //          1           2        3       4        5 6  7  8   9     10  11   12 13  14
        //$GPGGA,085650.00,2602.45831868,N,11935.30095413,E,4,19,0.6,36.255,M,11.151,M,1.0,0000*79
        if (!data.isEmpty() && (data.startsWith(HEAD)
                || data.startsWith(HEAD_1)) && isValidForNMEA(data)) {
            try {
                String[] datas = data.substring(0, data.lastIndexOf("*")).split(",");
                timeUTC = datas[1];
                double latDf = parseDouble(datas[2]);
                latitude = (int) (latDf / 100) + (latDf / 100 - (int) (latDf / 100)) * 100 / 60;
                latitudeHemisphere = datas[3];
                if (latitudeHemisphere.equals("N")) {
                    northEarth = true;
                } else {
                    northEarth = false;
                }
                double lonDf = parseDouble(datas[4]);
                longitude = (int) (lonDf / 100) + (lonDf / 100 - (int) (lonDf / 100)) * 100 / 60;
                longitudeHemisphere = datas[5];
                if (longitudeHemisphere.equals("E")) {
                    eastEarth = true;
                } else {
                    eastEarth = false;
                }
                gpsState = parseInt(datas[6]);
                satelliteNum = parseInt(datas[7]);
                altitude = parseDouble(datas[9]);
                height = parseDouble(datas[11]) + altitude;
                hasParsed = true;
            } catch (Exception e) {
                hasParsed = false;
            }
        }
        return hasParsed;
    }

    public static boolean toBeGGA(String data) {
        return !TextUtils.isEmpty(data) && (data.startsWith(HEAD)
                || data.startsWith(HEAD_1));
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLatitudeHemisphere() {
        return latitudeHemisphere;
    }

    public void setLatitudeHemisphere(String latitudeHemisphere) {
        this.latitudeHemisphere = latitudeHemisphere;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLongitudeHemisphere() {
        return longitudeHemisphere;
    }

    public void setLongitudeHemisphere(String longitudeHemisphere) {
        this.longitudeHemisphere = longitudeHemisphere;
    }

    public int getGpsState() {
        return gpsState;
    }

    public void setGpsState(int gpsState) {
        this.gpsState = gpsState;
    }

    public int getSatelliteNum() {
        if (satelliteNum < 0)
            return 0;
        return satelliteNum;
    }

    public void setSatelliteNum(int satelliteNum) {
        this.satelliteNum = satelliteNum;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
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

    public String getTimeUTC() {
        return timeUTC;
    }

    public void setTimeUTC(String timeUTC) {
        this.timeUTC = timeUTC;
    }
}
