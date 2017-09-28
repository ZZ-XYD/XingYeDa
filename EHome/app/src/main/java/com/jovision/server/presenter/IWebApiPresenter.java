package com.jovision.server.presenter;

import com.jovision.server.listener.ResponseListener;

import java.util.HashMap;

/**
 * Web接口请求
 *
 * @author ye.jian
 */

public interface IWebApiPresenter {
    // 账号库异常前缀
    String LIB_PREFIX = "lib_error_";
    // 接口异常前缀
    String WEB_PREFIX = "web_error_";

    /**
     * 请求
     *
     * @param shortUrl         接口短地址 例:"/deviceManage/deviceListData.do"
     * @param params           请求参数
     * @param callbackListener 回调监听
     */
    <T> void request(String shortUrl, HashMap<String, Object> params,
                     ResponseListener<T> callbackListener);
}
