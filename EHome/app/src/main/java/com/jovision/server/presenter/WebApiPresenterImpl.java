package com.jovision.server.presenter;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.jovision.Utils.BackgroundHandler;
import com.jovision.Utils.SimpleTask;
import com.jovision.server.AccountServiceImpl;
import com.jovision.server.exception.CustomCode;
import com.jovision.server.exception.RequestError;
import com.jovision.server.listener.ResponseListener;
import com.jovision.server.subscriber.ApiHttpResultSubscriber;
import com.jovision.server.utils.CommonUtils;
import com.jovision.server.utils.DnsXmlUtils;
import com.xiaowei.core.rx.retrofit.HttpResult;
import com.xiaowei.core.rx.retrofit.ObservableProvider;
import com.xiaowei.core.rx.retrofit.exception.ApiException;

import java.util.HashMap;
import java.util.Iterator;

import rx.Observable;

/**
 * Web接口请求
 *
 * @author ye.jian
 */

public class WebApiPresenterImpl implements IWebApiPresenter {
    // 具体的任务、超时时间计算任务
    private SimpleTask mTask, mTimeoutTask;
    // 超时时间(30秒)
    private static final int TIMEOUT = 30 * 1000;

    @Override
    public <T> void request(String shortUrl, HashMap<String, Object> params,
                            final ResponseListener<T> callbackListener) {

        // 2.检查DNS
        if (DnsXmlUtils.isDnsNormal()) {
            // 2.1DNS正常,直接执行请求
            executeRequest(shortUrl, params, callbackListener);
        } else {
            // 2.2DNS异常,重新下载DNS,然后执行请求
            executeDnsTask(shortUrl, params, callbackListener);
        }
    }

    private <T> void executeDnsTask(final String shortUrl, final
    HashMap<String, Object>
            params, final ResponseListener<T> callbackListener) {
        // 任务
        mTask = new SimpleTask() {
            @Override
            public void doInBackground() {
                // DNS检查
                DnsXmlUtils.checkDnsFile();
            }

            @Override
            public void onFinish(boolean canceled) {
                if (!canceled) {
                    mTimeoutTask.cancel();
                    mTimeoutTask = null;
                    if (DnsXmlUtils.isDnsNormal()) {
                        executeRequest(shortUrl, params, callbackListener);
                    } else {
                        callbackListener.onError(new RequestError(CustomCode
                                .DNS_ERROR, CommonUtils.getErrorMsgByCode
                                (LIB_PREFIX, String.valueOf(CustomCode
                                        .DNS_ERROR))));
                    }
                }
            }

            @Override
            protected void onCancel() {
                // 执行超时被Canceled
                callbackListener.onError(new RequestError(CustomCode
                        .DNS_ERROR, CommonUtils.getErrorMsgByCode
                        (LIB_PREFIX, String.valueOf(CustomCode
                                .DNS_ERROR))));
            }
        };

        // 计时任务(计算是否超时)
        mTimeoutTask = new SimpleTask() {
            @Override
            public void doInBackground() {
            }

            @Override
            public void onFinish(boolean canceled) {
                if (!canceled) {
                    mTask.cancel();
                }
            }
        };
        SimpleTask.postDelay(mTimeoutTask, TIMEOUT);

        // 执行任务
        BackgroundHandler.execute(mTask);

    }

    private <T> void executeRequest(String shortUrl, HashMap<String, Object>
            params,
                                    final ResponseListener<T>
                                            callbackListener) {
        // 3.参数设置
        if (params == null) {
            params = new HashMap<>();
        }
        // 追加session id
        params.put("sid", AccountServiceImpl.getInstance()
                .getSession());
        // 4.拼接完整的接口地址
        // 通过短链接标记判断使用的接口地址
        String url;
        if (shortUrl.contains("accountManage")) {
            if (shortUrl.contains("accountreg4Third ")) {
                params.remove("sid");
            }
            url = getUrl(DnsXmlUtils.getAccountInterface
                    (shortUrl), params);
        } else if (shortUrl.contains("alarmManage")) {
            url = getUrl(DnsXmlUtils.getAlarmInterface
                    (shortUrl), params);
        } else if (shortUrl.contains("deviceManage")) {
            url = getUrl(DnsXmlUtils.getDeviceInterface
                    (shortUrl), params);
        } else if (shortUrl.contains("otherManage")) {
            url = getUrl(DnsXmlUtils.getOtherInterface
                    (shortUrl), params);
        } else if (shortUrl.contains("csManage")) {
            url = getUrl(DnsXmlUtils.getCloudInterface
                    (shortUrl), params);
        } else if (shortUrl.contains("tsManage")) {
            params.remove("sid");
            url = getUrl(DnsXmlUtils.getOtherInterface
                    (shortUrl), params);
        } else {
            url = getUrl(shortUrl, params);
        }
        // 5.请求
        Observable<HttpResult<T>> list =  ObservableProvider.getDefault() .loadResult(url);
        list.subscribe(new ApiHttpResultSubscriber<T>() {
            @Override
            public void _onSuccess(T content) {
                callbackListener.onSuccess(convertData(content));
            }

            @Override
            public void _onError(ApiException e) {
                callbackListener.onError(new RequestError(e.getCode(), e
                        .getMsg()));
            }
        });
    }

    /**
     * 拼接请求url(如果有参数会在url后面拼接参数)
     *
     * @param url    接口地址
     * @param params 参数
     * @return
     */
    private <T> String getUrl(String url, HashMap<String, T> params) {
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            StringBuffer sb = null;
            while (it.hasNext()) {
                String key = it.next();
                T value = params.get(key);
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append("?");
                } else {
                    sb.append("&");
                }
                sb.append(key);
                sb.append("=");
                sb.append(value);
            }

            url += sb != null ? sb.toString() : "";
        }

        return url;
    }

    /**
     * 类型转换
     *
     * @param result 服务器返回的data json内容
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T convertData(T result) {
        Gson gson = new Gson();
        String data = gson.toJson(result);
        if (data.startsWith("{")) {
            return (T) JSON.parseObject(data);
        } else if (data.startsWith("[")) {
            return (T) JSON.parseArray(data);
        } else {
            return (T) data;
        }
    }

}
