package com.xiaowei.core.rx.retrofit.exception;

/**
 * 接口异常信息
 *
 * @author ye.jian
 */
public class ApiException extends Exception {
    // 错误code
    private int code;
    // 错误信息
    private String msg;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public ApiException(String msg, int code) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}