package com.fj.gnss.nmea.msg;

import android.text.TextUtils;

/**
 * HDTGPS数据
 *
 * @author jarven.ding
 */
public class SettingMessage extends BaseNMEAMessage {

    private static final String HEAD = "$SETTING";

    private int rtkMode;
    private int rtkType;
    private int rtkHasRadio;
    private int rtkVersion;
    //0 482 1 司南
    private int rtkCore = -1;

    @Override
    public boolean parse(String data) {
        data = data.trim();
        //          1           2        3       4        5 6  7  8   9     10  11   12 13  14
        //$SETTING,0,0,1,*00
        if (!TextUtils.isEmpty(data) && (data.startsWith(HEAD))) {
            try {
                String[] datas = data.substring(0, data.lastIndexOf("*")).split(",");
                rtkMode = Integer.parseInt(datas[1]);
                rtkType = Integer.parseInt(datas[2]);
                rtkHasRadio = Integer.parseInt(datas[3]);
                if (datas.length == 5) {
                    try {
                        rtkVersion = Integer.parseInt(datas[4]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (datas.length == 6) {
                    rtkCore = Integer.parseInt(datas[4]);
                    try {
                        rtkVersion = Integer.parseInt(datas[5]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                hasParsed = true;
            } catch (Exception e) {
                hasParsed = false;
            }
        }
        return hasParsed;
    }

    public static boolean toBeSetting(String data) {
        return !TextUtils.isEmpty(data) && (data.startsWith(HEAD));
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

    public int getRtkVersion() {
        return rtkVersion;
    }

    public void setRtkVersion(int rtkVersion) {
        this.rtkVersion = rtkVersion;
    }

    public int getRtkCore() {
        return rtkCore;
    }

    public void setRtkCore(int rtkCore) {
        this.rtkCore = rtkCore;
    }
}
