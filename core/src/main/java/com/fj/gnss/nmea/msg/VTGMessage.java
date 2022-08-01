package com.fj.gnss.nmea.msg;

import android.text.TextUtils;

/**
 * GGAGPS数据
 *
 * @author jarven
 */
public class VTGMessage extends BaseNMEAMessage {

    private static final String HEAD = "$GPVTG";
    private static final String HEAD_1 = "$GNVTG";

    /**
     * 真北地面航向
     */
    private double trueNorthDirection;
    /**
     * 磁北地面航向
     */
    private double magneticNorthDirection;
    /**
     * 速度，以节为单位
     */
    private double speedJ;
    /**
     * 速度
     */
    private double speed;
    /**
     * A=自主定位，D=差分，E=估算，N=数据无效
     */
    private String mode;

    @Override
    public boolean parse(String data) {
        data = data.trim();
        //          1   2   3    4  5    6   7  8 9
        //$GPVTG,246.31,T,252.29,M,0.10,N,0.18,K,A*27
        if (!TextUtils.isEmpty(data) && (data.startsWith(HEAD) || data.startsWith(HEAD_1))) {
            try {
                String[] datas = data.substring(0, data.lastIndexOf("*")).split(",");
                trueNorthDirection = parseDouble(datas[1]);
                magneticNorthDirection = parseDouble(datas[3]);
                speedJ = parseDouble(datas[5]);
                speed = parseDouble(datas[7]);
                mode = datas[9];
                hasParsed = true;
            } catch (Exception e) {
                hasParsed = false;
            }
        }
        return hasParsed;
    }

    public static boolean toBeVTG(String data) {
        return !TextUtils.isEmpty(data) && (data.startsWith(HEAD)
                || data.startsWith(HEAD_1));
    }

    public double getTrueNorthDirection() {
        return trueNorthDirection;
    }

    public void setTrueNorthDirection(double trueNorthDirection) {
        this.trueNorthDirection = trueNorthDirection;
    }

    public double getMagneticNorthDirection() {
        return magneticNorthDirection;
    }

    public void setMagneticNorthDirection(double magneticNorthDirection) {
        this.magneticNorthDirection = magneticNorthDirection;
    }

    public double getSpeedJ() {
        return speedJ;
    }

    public void setSpeedJ(double speedJ) {
        this.speedJ = speedJ;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
