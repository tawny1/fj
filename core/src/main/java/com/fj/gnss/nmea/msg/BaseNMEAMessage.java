package com.fj.gnss.nmea.msg;

import java.io.Serializable;

/**
 * NMEA消息基类
 *
 * @author jarven.ding
 */
public abstract class BaseNMEAMessage implements Serializable {

    private static final long serialVersionUID = 1311834222359743879L;

    protected boolean hasParsed = false;

    /**
     * 解析
     *
     * @param data 原始数据
     * @return success
     */
    protected abstract boolean parse(String data);

    public boolean hasParsed() {
        return hasParsed;
    }

    public double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }

    public long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 验证是否NMEA格式
     *
     * @param rawNMEA
     * @return
     */
    public static boolean isValidForNMEA(String rawNMEA) {
        try {
            boolean valid = true;
            byte[] bytes = rawNMEA.getBytes();
            int checksumIndex = rawNMEA.indexOf("*");
            //NMEA *号后为checksum number
            byte checksumCalcValue = 0;
            int checksumValue;
            //检查开头是否为$
            if ((rawNMEA.charAt(0) != '$') || (checksumIndex == -1)) {
                valid = false;
            }
            if (valid) {
                String val = rawNMEA.substring(checksumIndex + 1, rawNMEA.length()).trim();
                //计算长度,抑或校验
                checksumValue = Integer.parseInt(val, 16);
                for (int i = 1; i < checksumIndex; i++) {
                    checksumCalcValue = (byte) (checksumCalcValue ^ bytes[i]);
                }
                if (checksumValue != checksumCalcValue) {
                    valid = false;
                }
            }
            return valid;
        } catch (Exception e) {
            return false;
        }
    }

}
