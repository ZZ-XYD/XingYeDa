package com.xiaowei.core.rx.retrofit.func;


import com.xiaowei.core.rx.retrofit.exception.ExceptionEngine;

import rx.Observable;
import rx.functions.Func1;

/**
 * 错误处理方法
 *
 * @author ye.jian
 * @deprecated
 */

public class ErrorFunc<T> implements Func1<Throwable, Observable<T>> {
    @Override
    public Observable<T> call(Throwable throwable) {
        return Observable.error(ExceptionEngine.handleException(throwable));
    }
}
