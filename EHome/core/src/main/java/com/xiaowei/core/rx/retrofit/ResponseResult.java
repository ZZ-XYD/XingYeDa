package com.xiaowei.core.rx.retrofit;

/**
 * 服务器返回的JSON格式
 * ye.jian
 */
public class ResponseResult<T> {
    public String result;
    public String msg;
    public String errorCode;
    public T data;
}
