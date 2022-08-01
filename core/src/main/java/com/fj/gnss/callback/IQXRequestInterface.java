package com.fj.gnss.callback;

/**
 * Description:千寻日包请求DSK DSS信息
 * Date: 2021/11/3
 * Author: Howard.Zhang
 */
public interface IQXRequestInterface {
    /**
     * 告诉主mudule，需要调用获取DSK DSS的接口
     */
    void requestDskDss();
}
