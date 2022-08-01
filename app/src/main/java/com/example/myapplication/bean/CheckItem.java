package com.example.myapplication.bean;

import android.content.Intent;

/**
 * @author Wentao.Hu
 */
public class CheckItem {

    @CheckType
    public final int type;
    private String name;

    @CheckResult
    private int code = CheckResult.CODE_UNKNOWN;
    private String desc;

    public CheckItem(@CheckType int type, String name) {
        this.type = type;
        this.name = name;
    }

    public CheckItem(@CheckType int type, String name, @CheckResult int code, String desc) {
        this.type = type;
        this.name = name;
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean checked() {
        return code != CheckResult.CODE_UNKNOWN;
    }

    public boolean isOk() {
        return code == CheckResult.CODE_SUCCESS;
    }

    @Override
    public String toString() {
        return "CheckItem{" +
                "type=" + type +
                ", code=" + code +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public Intent toIntent() {
        Intent intent = new Intent();
        intent.putExtra("type", this.type);
        intent.putExtra("name", this.name);
        intent.putExtra("code", this.code);
        intent.putExtra("desc", this.desc);
        return intent;
    }
}
