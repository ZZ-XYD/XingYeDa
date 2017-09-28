package com.jovision.server.listener;

import com.jovision.server.exception.RequestError;

/**
 * 调用Jar中的方法的结果处理
 *
 * @param <T>
 */
public interface ResponseListener<T> {

    void onSuccess(T result);

    void onError(RequestError error);
}
