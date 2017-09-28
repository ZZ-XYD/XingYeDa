package com.jovision.server.subscriber;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.jovision.server.utils.CommonUtils;
import com.xiaowei.core.rx.retrofit.HttpResult;
import com.xiaowei.core.rx.retrofit.exception.ApiException;
import com.xiaowei.core.rx.retrofit.exception.ExceptionEngine;
import com.xiaowei.core.utils.Logger;

import rx.Subscriber;

/**
 * 自定义结果处理订阅类
 * ye.jian
 */
public abstract class ApiHttpResultSubscriber<T> extends
        Subscriber<HttpResult<T>> {

    // 服务器返回的错误信息在APP中对应的字符串的前缀
    private static final String SERVER_CODE_PREFIX = "server_error_";
    // 服务器接口返回的错误信息在APP中对应的字符串的前缀
    private static final String ERROR_CODE_PREFIX = "web_error_";

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Logger.e(e);
        /*
          在这里(handleException)做全局的错误处理
         */
        ApiException exception = ExceptionEngine.handleException(e);
        _onError(CommonUtils.buildErrorByCode(SERVER_CODE_PREFIX, String.valueOf
                (exception.getCode())));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onNext(HttpResult<T> t) {
        if ("true".equalsIgnoreCase(t.response.result)) {
            _onSuccess(t.response.data);
        } else {
            String errorCode = t.response.errorCode;
            if (TextUtils.equals(errorCode, "203")) {
                doSpecialErrorHandle(t.response.data);
            } else {
                _onError(CommonUtils.buildErrorByCode(ERROR_CODE_PREFIX,
                        errorCode));
            }
        }
    }

    public abstract void _onSuccess(T result);

    public abstract void _onError(ApiException e);

    // ----------------------------------------------------
    // # 对于接口返回的错误码为203的信息(设备被绑定)进行特殊处理
    // ----------------------------------------------------
    private void doSpecialErrorHandle(T data) {
        Gson gson = new Gson();
        String dataStr = gson.toJson(data);
        JSONObject item = JSON.parseObject(dataStr);
        String mail = item.getString("mail");
        String phone = item.getString("phone");
        String username = item.getString("username");

        String errmsg = CommonUtils.getErrorMsgByCode(ERROR_CODE_PREFIX,
                "203");
        if (!TextUtils.isEmpty(phone)) {
            errmsg = errmsg + " [" +hidePhone(phone) + "]";
        }else if (!TextUtils.isEmpty(mail)) {
            errmsg = errmsg + " [" +hideMail(mail) + "]";
        }else if (!TextUtils.isEmpty(username)) {
            errmsg = errmsg + " [" +hideMail(username) + "]";
        }

        _onError(new ApiException(errmsg, 203));
    }

    /**
     * 手机号中间四位用*代替
     */
    private String hidePhone(String phone) {
        String retString;
        String errmsg = CommonUtils.getErrorMsgByCode
                (ERROR_CODE_PREFIX, "203_account");
        String star = "****";
        if (phone.length() > 7) {
            retString = errmsg + phone.substring(0, 3) + star + phone
                    .substring(7);
        } else {
            retString = errmsg + phone;
        }
        return retString;
    }

    /**
     * 邮箱隐藏方式，只隐藏@号前面部分字符
     */
    private String hideMail(String mail) {
        String retString;
        String errmsg = CommonUtils.getErrorMsgByCode
                (ERROR_CODE_PREFIX, "203_account");
        String star = "*";
        String stars = "";
        int at;
        if (mail.contains("@")) {
            at = mail.indexOf("@");
        } else {
            at = mail.length();
        }
        String value = mail.substring(0, at);
        int replace = value.length() / 3;
        for (int i = 0; i < value.length() - (replace * 2); i++) {
            stars += star;
        }
        value = value.substring(0, replace) + stars + value.substring(value
                .length() - replace);
        retString = errmsg + value + mail.substring(at);
        return retString;
    }
}
