package com.fj.gnss.callback;

/**
 * 对频监听器
 */
public interface OnFrequencyPairingListener {

    /**
     * 对频结果回调
     *
     * @param isSuccess 是否对频成功，获取到2、4、5
     */
    void onPairingResult(boolean isSuccess);

    /**
     * rtk对频设置成功回调
     */
    void onReceivePairingCmd();

}