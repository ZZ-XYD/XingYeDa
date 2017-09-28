package com.jovision.server.exception;

/**
 * 错误信息
 *
 * @author ye.jian
 */
public class RequestError {
    public int errcode;
    public String errmsg;

    public RequestError(int code, String msg) {
        this.errcode = code;
        this.errmsg = msg;
    }
}
