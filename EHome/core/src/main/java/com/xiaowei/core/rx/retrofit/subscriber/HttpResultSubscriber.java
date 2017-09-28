package com.xiaowei.core.rx.retrofit.subscriber;



import com.xiaowei.core.rx.retrofit.HttpResult;
import com.xiaowei.core.utils.Logger;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by _SOLID
 * Date:2016/7/27
 * Time:21:27
 */
public abstract class HttpResultSubscriber<T> extends
        Subscriber<HttpResult<T>> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Logger.e(this, e.getMessage());
        e.printStackTrace();
        //在这里做全局的错误处理
        if (e instanceof HttpException) {
            // ToastUtils.getInstance().showToast(e.getMessage());
        }
        _onError(e);
    }

    @Override
    public void onNext(HttpResult<T> t) {
//        if (!t.error)
//            _onSuccess(t.results);
//        else
//            _onError(new Throwable("error=" + t.error));
    }

    public abstract void _onSuccess(T t);

    public abstract void _onError(Throwable e);
}
