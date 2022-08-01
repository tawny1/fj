package com.fj.gnss.board;

/**
 * Description: 板卡消息回调
 * Date: 2021/10/26
 * Author: Howard.Zhang
 */
public interface IBoardCallBack {

    /**
     * 板卡版本号
     *
     * @param sysVersion
     */
    void onSystemVersionFound(int sysVersion);
}
