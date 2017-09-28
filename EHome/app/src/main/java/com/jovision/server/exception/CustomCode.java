package com.jovision.server.exception;


import com.xiaowei.core.rx.retrofit.exception.ERROR;

/**
 * 自定义错误码
 *
 * @author ye.jian
 */

public class CustomCode extends ERROR {
    /**
     * DNS 错误
     */
    public static final int DNS_ERROR = 2000;
    /**
     * 执行超时
     */
    public static final int EXECUTE_TIMEOUT = 2001;
    /**
     * 无网络连接
     */
    public static final int NETWORK_NOT_CONNECTED = 2002;
}
