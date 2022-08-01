package com.fj.gnss.utils;

import com.fj.construction.tools.helper.MapEntryComparator;
import com.fjdynamics.xlog.FJXLog;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * 数据处理
 *
 * @author jarven.ding
 * 2020/12/2 Howard.Zhang 合并了本地多个DataUtil类
 */
public class DataUtil {
    private static final String TAG = "DataUtil";

    /**
     * 16位int[]转byte[]
     */
    public static byte[] intArr2byte2(int[] res) {
        int byteNum = res.length * 2;
        byte[] targets = new byte[byteNum];
        int index = 0;
        for (int i = 0; i < res.length; i++) {
            targets[index++] =
                    (byte) ((res[i] >> 8) & 0xff);// 次低位
            targets[index++] =
                    (byte) (res[i] & 0xff);// 最低位
        }
        return targets;
    }

    /**
     * int[]转byte[]
     */
    public static byte[] intArr2byte4(int[] res) {
        int byteNum = res.length * 4;
        byte[] targets = new byte[byteNum];
        int index = 0;
        for (int re : res) {
            targets[index++] = (byte) (re >>> 24);// 最高位,无符号右移。
            targets[index++] = (byte) ((re >> 16) & 0xff);// 次高位
            targets[index++] = (byte) ((re >> 8) & 0xff);// 次低位
            targets[index++] = (byte) (re & 0xff);// 最低位
        }
        return targets;
    }

    /**
     * 32位int转byte[]
     */
    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (res & 0xff);// 最低位
        targets[2] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[1] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[0] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    public static String bytes2HexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (byte aData : data) {
            int value = aData & 0xff;
            sb.append(HEX[value / 16]).append(HEX[value % 16]);
        }
        return sb.toString();
    }

    public static String bytes2LogHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (byte aData : data) {
            int value = aData & 0xff;
            sb.append(HEX[value / 16]).append(HEX[value % 16]).append(" ");
        }
        return sb.toString();
    }

    public static byte string2byte(String byteString) {
        byte b = 0;
        if (byteString.length() == 2) {
            b = (byte) (Integer.valueOf(byteString.substring(0, 2), 16) & 0xff);
        }
        return b;
    }

    public static byte[] cutByteArray(byte[] data, int start, int stop) {
        byte[] newData = null;
        if (data != null && data.length > 0 && stop <= data.length && stop - start > 0) {
            newData = new byte[stop - start];
            System.arraycopy(data, start, newData, 0, stop - start);
        }
        return newData;
    }

    /**
     * 长度为2的byte数组，转为int
     * 高位在前，低位在后
     *
     * @param ary byte数组
     * @return int数值
     */
    public static int byteArr2ToInt(byte[] ary) {
        if (ary.length != 2) {
            return 0;
        }
        int value;
        value = (int) ((ary[0] << 8 & 0xFF00) | (ary[1] & 0xFF));
        return value;
    }

    public static int byte2int(byte b) {
        return b & 0xff;
    }

    public static String getDataMode(byte mode) {
        String modeTip;
        switch (mode) {
            case 0x00:
                modeTip = "Command mode";
                break;
            case 0x01:
                modeTip = "J1939 mode";
                break;
            case 0x02:
                modeTip = "OBD mode";
                break;
            case 0x03:
                modeTip = "Can mode";
                break;
            default:
                modeTip = "unknow";
                break;
        }
        return modeTip;
    }

    public static byte[] string2byteArray(String s) {
        byte[] data;
        data = new byte[s.length() / 2];
        for (int j = 0; j < data.length; j++) {
            data[j] = (byte) (Integer.valueOf(s.substring(j * 2, j * 2 + 2), 16) & 0xff);
        }
        return data;
    }

    /**
     * int转为长度为2的字节数组
     *
     * @param ary
     * @return
     */
    public static byte[] int2byteArray(int ary) {
        byte[] result = new byte[2];
        result[0] = (byte) ((ary >> 8) & 0xFF);
        result[1] = (byte) ((ary) & 0xFF);
        return result;
    }

    //dec(170)=hex(AA)
    public static String IntToHex(int n) {
        char[] ch = new char[20];
        int nIndex = 0;
        while (true) {
            int m = n / 16;
            int k = n % 16;
            if (k == 15) {
                ch[nIndex] = 'F';
            } else if (k == 14) {
                ch[nIndex] = 'E';
            } else if (k == 13) {
                ch[nIndex] = 'D';
            } else if (k == 12) {
                ch[nIndex] = 'C';
            } else if (k == 11) {
                ch[nIndex] = 'B';
            } else if (k == 10) {
                ch[nIndex] = 'A';
            } else {
                ch[nIndex] = (char) ('0' + k);
            }
            nIndex++;
            if (m == 0) {
                break;
            }
            n = m;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(ch, 0, nIndex);
        sb.reverse();
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.toUpperCase();
        int len = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[len];
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
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

    /**
     * 把a和b（从begin到end个字节）拼接起来
     *
     * @param a
     * @param b
     * @param begin b的开始位置
     * @param end   b的结束位置
     * @return
     */
    public static byte[] spliceByte(byte[] a, byte[] b, int begin, int end) {
        byte[] add = new byte[a.length + end - begin];
        int i = 0;
        for (i = 0; i < a.length; i++) {
            add[i] = a[i];
        }
        for (int k = begin; k < end; k++, i++) {
            add[i] = b[k];
        }
        return add;
    }

    /**
     * 读取指定长度数据,阻塞读取
     *
     * @param in
     * @param length
     * @return
     */
    public static byte[] getBytes(InputStream in, int length) throws IOException {
        byte[] bytes = new byte[length];
        // 已经成功读取的字节的个数
        int readCount = 0;

        while (readCount < length) {
            int len = in.read(bytes, readCount, length - readCount);
            if (len > 0) {
                readCount += len;
            }
        }
        return bytes;
    }

    /**
     * 将一个单字节的byte转换成32位的int
     *
     * @param b byte
     * @return convert result
     */
    public static int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    /**
     * @param b
     * @return int[]   convert result
     * @// TODO: 2019/9/30  将一个单字节的Byte转换成8位数组 lj
     */
    public static int[] byteToBitArr(byte b) {
        int n0, n1, n2, n3, n4, n5, n6, n7;
        n0 = (b & 0x01) == 0x01 ? 1 : 0;
        n1 = (b & 0x02) == 0x02 ? 1 : 0;
        n2 = (b & 0x04) == 0x04 ? 1 : 0;
        n3 = (b & 0x08) == 0x08 ? 1 : 0;
        n4 = (b & 0x10) == 0x10 ? 1 : 0;
        n5 = (b & 0x20) == 0x20 ? 1 : 0;
        n6 = (b & 0x40) == 0x40 ? 1 : 0;
        n7 = (b & 0x80) == 0x80 ? 1 : 0;

        return new int[]{n7, n6, n5, n4, n3, n2, n1, n0,};
    }

    /**
     * 将一个单字节的Byte转换成十六进制的数
     *
     * @param b byte
     * @return convert result
     */
    public static String byteToHex(byte b) {
        int i = b & 0xFF;
        return Integer.toHexString(i);
    }

    /**
     * 将一个4byte的数组转换成32位的int
     *
     * @param buf bytes buffer
     * @param
     * @return convert result
     */
    public static long unsigned4BytesToInt(byte[] buf, int pos) {
        int firstByte = 0;
        int secondByte = 0;
        int thirdByte = 0;
        int fourthByte = 0;
        int index = pos;
        firstByte = (0x000000FF & ((int) buf[index]));
        secondByte = (0x000000FF & ((int) buf[index + 1]));
        thirdByte = (0x000000FF & ((int) buf[index + 2]));
        fourthByte = (0x000000FF & ((int) buf[index + 3]));
        index = index + 4;
        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;
    }

    /**
     * 将16位的short转换成byte数组
     *
     * @param s short
     * @return byte[] 长度为2
     */
    public static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * 将32位整数转换成长度为4的byte数组
     *
     * @param s int
     * @return byte[]
     */
    public static byte[] intToByteArray(int s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 4; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * long to byte[]
     *
     * @param s long
     * @return byte[]
     */
    public static byte[] longToByteArray(long s) {
        long temp = s;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[7 - i] = Long.valueOf(temp & 0xff).byteValue();
            temp = temp >> 8;// 向右移8位
        }
        return b;
    }

    /**
     * 16位int转byte[]
     */
    public static byte[] int2byte2(int res) {
        byte[] targets = new byte[2];
        targets[0] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[1] = (byte) (res & 0xff);// 最低位
        return targets;
    }

    /**
     * 16位int转byte[]
     */
    public static byte[] int2byte1(int res) {

        byte[] targets = new byte[1];
        targets[0] = (byte) (res & 0xff);// 最低位
        return targets;
    }

    /**
     * 将长度为32的byte数组转换为16位int
     *
     * @param res byte[]
     * @return int
     */
    public static int byte2int(byte[] res) {
        return res[3] & 0xFF |
                (res[2] & 0xFF) << 8 |
                (res[1] & 0xFF) << 16 |
                (res[0] & 0xFF) << 24;
    }


    /**
     * 将长度为4的byte数组转换为float
     *
     * @param res byte[]
     * @return int
     */
    public static float byte2Float(byte[] res, int scale) {
        int result = res[3] & 0xFF |
                (res[2] & 0xFF) << 8 |
                (res[1] & 0xFF) << 16 |
                (res[0] & 0xFF) << 24;
        return (float) result / 100;
    }

    public static float byte2float(byte[] res) {
        int result = res[3] & 0xFF |
                (res[2] & 0xFF) << 8 |
                (res[1] & 0xFF) << 16 |
                (res[0] & 0xFF) << 24;
        return (float) result;
    }

    public static short byte2short(byte[] b) {
        short l = 0;
        for (int i = 0; i < 2; i++) {
            l <<= 8; //<<=和我们的 +=是一样的，意思就是 l = l << 8
            l |= (b[i] & 0xff); //和上面也是一样的  l = l | (b[i]&0xff)
        }
        return l;
    }

    /**
     * *short类型数据，发送方先*100，转为int，转为2个byte发送出来，然后接收方需要先补足为4个byte数组，转成float，但是要*10，getFloat是/1000
     */
    public static float byte2float2(byte[] res) {
        byte[] shortToIntBytes = {0x00, 0x00, res[0], res[1]};
        return getFloat(shortToIntBytes, 0) * 10;
    }

    public static int byte2short2(byte[] res) {
        byte[] shortToIntBytes = {0x00, 0x00, res[0], res[1]};
        return byte2int(shortToIntBytes);
    }


    public static byte[] byteMerger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static byte[] double2Bytes(double d) {
//        long value = Double.doubleToRawLongBits(d);
//        byte[] byteRet = new byte[8];
//        for (int i = 0; i < 8; i++) {
//            byteRet[i] = (byte) ((value >> 8 * i) & 0xff);
//        }
//
//        return byteRet;
        long s = (long) (d * (Math.pow(10, 8)));

        return longToByteArray(s);
    }

    public static double bytes2Double(byte[] arr) {
        long value = bytesToLong(arr);
        return (value / (Math.pow(10, 8)));
    }

    /**
     * @// TODO: 2019/7/16 收割机使用，除以10的mi次方 ,然后8字节byte数组转64位（8字节）double
     * lj
     */
    public static double bytes2Double(byte[] arr, int mi) {
        long value = bytesToLong(arr);
        return (value / (Math.pow(10, mi)));
    }

    // float转换为byte[2]数组
    public static byte[] float2Byte2(float f) {
        short intbits = (short) (f * (Math.pow(10, 2)));
        //将float里面的二进制串解释为short整数
        return shortToByteArray(intbits);
    }

    // double转换为byte[2]数组
    public static byte[] double2Byte2(double f) {
        short intbits = (short) (f * (Math.pow(10, 2)));
        //将float里面的二进制串解释为short整数
        return shortToByteArray(intbits);
    }

    // float转换为byte[4]数组
    public static byte[] float2Byte(float f) {
        int intbits = (int) (f * (Math.pow(10, 3)));
        //将float里面的二进制串解释为int整数
        return int2byte(intbits);
    }

    public static long bytesToLong(byte[] buffer) {
        long values = 0;
        for (int i = 0; i < 8; i++) {
            values <<= 8;
            values |= (buffer[i] & 0xff);
        }
        return values;
    }

    // 从byte数组的index处的连续4个字节获得一个float
    public static float getFloat(byte[] arr, int index) {
        int s = byte2int(arr);
        return (float) (s / (Math.pow(10, 3)));
    }

    // 从byte数组的index处的连续4个字节获得一个int
    public static int getInt(byte[] arr, int index) {
        return (0xff000000 & (arr[index + 0] << 24)) | (0x00ff0000 & (arr[index + 1] << 16)) | (0x0000ff00 & (arr[index + 2] << 8)) | (0x000000ff & arr[index + 3]);
    }

    /**
     * @return ba src的前count位的的值
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        try {
            System.arraycopy(src, begin, bs, 0, count);
        } catch (Exception e) {
            FJXLog.INSTANCE.d("DataUtil", "subBytes: src = " + Arrays.toString(src));
            e.printStackTrace();
        }
        return bs;
    }

//    public static byte[] copyBytes(byte[] src,byte[] dest, int begin, int count) {
//        System.arraycopy(src, 0, dest, begin, count);
//    }

    /**
     * 有符号，int 占 2 个字节
     */
    public static int convertTwoSignInt(byte b1, byte b2) { // signed
        return (b2 << 8) | (b1 & 0xFF);
    }

    /**
     * 有符号，int 占 2 个字节
     */
    public static int convertTwoSignInt(byte[] b) { // signed
        byte b1 = b[0];
        byte b2 = b[1];
        return (b2 << 8) | (b1 & 0xFF);
    }

    /**
     * 有符号, int 占 4 个字节
     */
    public static int convertFourSignInt(byte b1, byte b2, byte b3, byte b4) {
        return (b4 << 24) | (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }

    /**
     * 无符号，int 占 2 个字节
     */
    public static int convertTwoUnsignInt(byte b1, byte b2)      // unsigned
    {
        return (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }

    /**
     * 无符号, int 占 4 个字节
     */
    public static long convertFoutUnsignLong(byte b1, byte b2, byte b3, byte b4) {
        return (long) (b4 & 0xFF) << 24 | (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b1 & 0xFF);
    }

    /**
     * int整数转换为4字节的byte数组
     *
     * @param i 整数
     * @return byte数组
     */
    public static byte[] intToByte4(int i) {
        byte[] targets = new byte[4];
        targets[3] = (byte) (i & 0xFF);
        targets[2] = (byte) (i >> 8 & 0xFF);
        targets[1] = (byte) (i >> 16 & 0xFF);
        targets[0] = (byte) (i >> 24 & 0xFF);
        return targets;
    }

    /**
     * int整数转换为2字节的byte数组
     *
     * @param i 整数
     * @return byte数组
     */
    public static byte[] intToByte2(int i) {
        byte[] targets = new byte[2];
        targets[1] = (byte) (i & 0xFF);
        targets[0] = (byte) (i >> 8 & 0xFF);
        return targets;
    }

    /**
     * byte数组转换为int整数
     *
     * @param bytes byte数组
     * @param off   开始位置
     * @return int整数
     */
    public static int byte4ToInt(byte[] bytes, int off) {
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int b2 = bytes[off + 2] & 0xFF;
        int b3 = bytes[off + 3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    /**
     * byte数组转换为int整数
     *
     * @param bytes byte数组
     * @param off   开始位置
     * @return int整数
     */
    public static int byte2ToInt(byte[] bytes, int off) {
        String hexStr = conver2HexStr(bytes);
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int result = (b0 << 8) | b1;
        return result;
    }

    /**
     * byte数组转换为int/10整数
     *
     * @param bytes byte数组
     * @param off   开始位置
     * @return int整数
     */
    public static int byte2intDiv10(byte[] bytes, int off) {
        String hexStr = conver2HexStr(bytes);
        int b0 = bytes[off] & 0xFF;
        int b1 = bytes[off + 1] & 0xFF;
        int result = (b0 << 8) | b1;
        return result / 10;
    }

    /**
     * byte数组转换为二进制字符串,每个字节以","隔开
     **/
    public static String conver2HexStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2));
        }
        return result.toString().substring(0, result.length() - 1);
    }

    /**
     * 以大端模式将int转成byte[]
     */
    public static byte[] int4BytesBig(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * 以小端模式将int转成byte[]
     *
     * @param value
     * @return
     */
    public static byte[] int4byteLittle(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    public static byte[] int2byteLittle(int value) {
        byte[] src = int4byteLittle(value);
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return new byte[]{src[1], src[0]};
    }

    /**
     * 以大端模式将byte[]转成int
     */
    public static int bytesToIntBig(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    /**
     * 以小端模式将byte[]转成int
     */
    public static int bytesToIntLittle(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        return value;
    }

    public static int byte2int1(byte[] runtimeStatusByte) {
        byte[] targetBytes = new byte[]{0x00, 0x00, 0x00, runtimeStatusByte[0]};
        int b0 = targetBytes[0] & 0xFF;
        int b1 = targetBytes[1] & 0xFF;
        int b2 = targetBytes[2] & 0xFF;
        int b3 = targetBytes[3] & 0xFF;
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    public static int byte2int2(byte[] runtimeWpIndexByte) {
        byte[] targetBytes = new byte[]{runtimeWpIndexByte[0], runtimeWpIndexByte[1]};
        int b0 = targetBytes[0] & 0xFF;
        int b1 = targetBytes[1] & 0xFF;
        return (b0 << 8) | b1;
    }

    public static int byte2int2LSB(byte[] runtimeWpIndexByte) {
        byte[] targetBytes = new byte[]{runtimeWpIndexByte[1], runtimeWpIndexByte[0], 0x00, 0x00};
        int b0 = targetBytes[0] & 0xFF;
        int b1 = targetBytes[1] & 0xFF;
        int b2 = targetBytes[2] & 0xFF;
        int b3 = targetBytes[3] & 0xFF;
        return (b3 << 24) | (b2 << 16) | (b1 << 8) | b0;
    }

    public static int bytes2Int2LSB(byte[] b) {
        int sum = 0;
        int len = 2;

        for (int i = 1; i > -1; i--) {
            int n = ((int) b[i]) & 0xff;
            n <<= (--len) * 8;
            sum += n;
        }
        return sum;
    }

    // 任意长度
    public static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    // 任意长度
    public static int toIntLSB(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }

    public static short getUint8(short s) {
        return (short) (s & 0x00ff);
    }

    public static int getUint16(int i) {
        return i & 0x0000ffff;
    }

    public static long getUint32(long l) {
        return l & 0x00000000ffffffff;
    }

    public static String byte2hex(byte[] var0) {
        String var1 = "";
        if (null == var0) {
            return var1;
        } else {
            for (int var2 = 0; var2 < var0.length; ++var2) {
                String var3 = Integer.toHexString(var0[var2] & 255);
                if (var3.length() == 1) {
                    var3 = "0" + var3;
                }

                var1 = var1 + " " + var3;
            }

            return var1;
        }
    }

    public static double[] listTodouble(List<Double> list) {
        Double[] doubles = new Double[list.size()];
        list.toArray(doubles);
        if (doubles == null) {
            return null;
        }
        double[] result = new double[doubles.length];
        for (int i = 0; i < doubles.length; i++) {
            result[i] = doubles[i].doubleValue();
        }
        return result;
    }

    /**
     * 计算偏移精度(均值与标准差求和)
     *
     * @param OffsetSet
     * @return
     */
    public static double calculationPrecision(double[] OffsetSet) {
        double sum = 0;
        for (int i = 0; i < OffsetSet.length; i++) {
            sum += OffsetSet[i];
            //数组的总和
        }
        double average = sum / OffsetSet.length;
        int total = 0;
        for (int i = 0; i < OffsetSet.length; i++) {
            total += (OffsetSet[i] - average) * (OffsetSet[i] - average);
            //方差
        }
        double standardDeviation = Math.sqrt(total / (OffsetSet.length - 1));
        //标准差
        return average + standardDeviation;
    }

    /**
     * 求校验和的算法
     *
     * @param b 需要求校验和的字节数组
     * @return 校验和
     */
    public static byte sumCheck(byte[] b, int len) {
        int sum = 0;
        for (int i = 0; i < len; i++) {
            sum = sum + b[i];
        }
        return (byte) (sum & 0xff);
    }

    public static int getHeightFour(byte data) {
        int heightfour;
        heightfour = ((data & 0xf0) >> 4);
        return heightfour;
    }

    //获取低四位
    public static int getLowFour(byte data) {
        int lowfour;
        lowfour = (data & 0x0f);
        return lowfour;
    }

    /*
     * 将字节数组转换成16进制字符串
     * */
    public static String encodeHexStr(final byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] digital = "0123456789ABCDEF".toCharArray();
        char[] result = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            result[i * 2] = digital[(bytes[i] & 0xf0) >> 4];
            result[i * 2 + 1] = digital[bytes[i] & 0x0f];
        }
        return new String(result);
    }

    /*
     * 加签算法
     * */
    public static <T> String doHmacSHA2(String path, Map<String, T> params, String key, String timestamp) {

        List<Map.Entry<String, T>> parameters = new ArrayList<Map.Entry<String, T>>(params.entrySet());
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return null;
        }
        if (path != null && path.length() > 0) {
            mac.update(path.getBytes(StandardCharsets.UTF_8));
        }
        if (parameters != null) {
            Collections.sort(parameters, new MapEntryComparator<String, T>());
            for (Map.Entry<String, T> parameter : parameters) {
                byte[] name = parameter.getKey().getBytes(StandardCharsets.UTF_8);
                Object value = parameter.getValue();
                if (value instanceof Collection) {
                    for (Object o : (Collection) value) {
                        mac.update(name);
                        if (o != null) {
                            mac.update(o.toString().getBytes(StandardCharsets.UTF_8));
                        }
                    }
                } else {
                    mac.update(name);
                    if (value != null) {
                        mac.update(value.toString().getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        }
        if (timestamp != null && timestamp.length() > 0) {
            mac.update(timestamp.toString().getBytes(StandardCharsets.UTF_8));
        }
        return encodeHexStr(mac.doFinal());
    }

}
