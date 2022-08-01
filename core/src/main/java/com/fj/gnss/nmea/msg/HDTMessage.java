package com.fj.gnss.nmea.msg;

import android.text.TextUtils;

/**
 * HDTGPS数据
 *
 * @author jarven.ding
 */
public class HDTMessage extends BaseNMEAMessage {

    private static final String HEAD = "$GPHDT";
    private static final String HEAD_1 = "$GNHDT";

    private double heading;

    @Override
    public boolean parse(String data) {
        data = data.trim();
        //          1           2        3       4        5 6  7  8   9     10  11   12 13  14
        //$GPHDT,123.456,T*00
        if (!TextUtils.isEmpty(data) && (data.startsWith(HEAD)
                || data.startsWith(HEAD_1))) {
            try {
                String[] datas = data.substring(0, data.lastIndexOf("*")).split(",");
                heading = Double.parseDouble(datas[1]);
                hasParsed = true;
            } catch (Exception e) {
                hasParsed = false;
            }
        }
        return hasParsed;
    }

    public static boolean toBeHDT(String data) {
        return !TextUtils.isEmpty(data) && (data.startsWith(HEAD)
                || data.startsWith(HEAD_1));
    }

    public double getHeading() {
        return heading;
    }
}
