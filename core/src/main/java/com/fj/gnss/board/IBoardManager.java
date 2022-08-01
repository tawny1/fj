package com.fj.gnss.board;

/**
 * Description: 板卡操作接口
 * Date: 2021/10/26
 * Author: Howard.Zhang
 */
public interface IBoardManager {

    /**
     * 检测板卡是否支持重启
     */
    void checkIsCanRestart();

    /**
     * 重启板卡
     */
    void reStartBoard();

    /**
     * 重启
     */
    void rebootRtk();

    /**
     * 设置版本号查询回调
     *
     * @param iBoardCallBack
     */
    void setIBoardCallBack(IBoardCallBack iBoardCallBack);


}
