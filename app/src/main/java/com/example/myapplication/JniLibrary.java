package com.example.myapplication;

public class JniLibrary {
    static {
//        System.loadLibrary("libQt5Core");
//        E:\AndroidProject\MyApplication\app\src\main\jniLibs\armeabi-v7a\libMathematicalTools.so
//        System.load("/src/main/jniLibs/armeabi-v7a/libMathematicalTools.so");
        System.loadLibrary("MathematicalTools");

    }

    public native double UMTAngleToRadian(double angle);
}
