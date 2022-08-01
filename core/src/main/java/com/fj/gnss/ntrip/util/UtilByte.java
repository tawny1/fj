package com.fj.gnss.ntrip.util;

import android.annotation.SuppressLint;

import java.nio.charset.StandardCharsets;

/**
 * author:Jarven.ding
 * create:2020/3/28
 */
public class UtilByte {

    @SuppressLint("NewApi")
    public static String getString_UTF8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] get(byte[] array, int offset, int length) {
        if (offset + length > array.length) {
            byte[] result = new byte[array.length - offset];
            System.arraycopy(array, offset, result, 0, array.length - offset);
            return result;
        } else {
            byte[] result = new byte[length];
            System.arraycopy(array, offset, result, 0, length);
            return result;
        }
    }

}
