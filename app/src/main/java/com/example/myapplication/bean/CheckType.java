package com.example.myapplication.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Wentao.Hu
 */
@IntDef(value = {
        CheckType.TYPE_RTK,
        CheckType.TYPE_ECU,
        CheckType.TYPE_ANTENNA_PRIMARY,
        CheckType.TYPE_ANTENNA_SECONDARY,
        CheckType.TYPE_SENSOR_BLADE})
@Retention(RetentionPolicy.SOURCE)
public @interface CheckType {

    /**
     * RTK
     */
    int TYPE_RTK = 1;

    /**
     * ECU
     */
    int TYPE_ECU = 2;

    /**
     * 主天线
     */
    int TYPE_ANTENNA_PRIMARY = 4;

    /**
     * 副天线
     */
    int TYPE_ANTENNA_SECONDARY = 5;

    /**
     * 铲刀传感器
     */
    int TYPE_SENSOR_BLADE = 6;
}
