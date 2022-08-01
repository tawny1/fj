package com.fj.gnss.callback;

public interface OnRtkSwitchListener {

    void onSwitchStart();

    /**
     * rtk模式改变
     *
     * @param rtkMode
     */
    void onSwitchSuccess(int rtkMode);

    void onSwitchFail();
}