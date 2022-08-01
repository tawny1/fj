package com.fj.gnss.utils;

/**
 * @author tony.tang
 * date 2022/4/8 15:35
 * @version 1.0
 * @className ViewClickUtil
 * @description
 */
public class ViewClickUtil {

    private static final int CLICK_TIME = 800; //快速点击间隔时间
    private static long lastClickTime = 0;

    /**
     * 判断按钮是否快速点击
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < CLICK_TIME) {//判断系统时间差是否小于点击间隔时间
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
