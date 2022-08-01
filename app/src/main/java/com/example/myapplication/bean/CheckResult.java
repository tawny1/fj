package com.example.myapplication.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Wentao.Hu
 */
@IntDef(value = {
        CheckResult.CODE_UNKNOWN,
        CheckResult.CODE_SUCCESS,
        CheckResult.CODE_FAILED})
@Retention(RetentionPolicy.SOURCE)
public @interface CheckResult {

    int CODE_UNKNOWN = -1;
    int CODE_SUCCESS = 0;
    int CODE_FAILED = 1;
}
