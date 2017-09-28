package com.xiaowei.core.rx.retrofit.exception;

import com.google.gson.JsonParseException;
import com.xiaowei.core.rx.retrofit.exception.ApiException;
import com.xiaowei.core.rx.retrofit.exception.ERROR;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.concurrent.TimeoutException;

import retrofit2.adapter.rxjava.HttpException;


/**
 * 异常处理引擎
 *
 * @author ye.jian
 */
public class ExceptionEngine {

    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    public static ApiException handleException(Throwable e) {
        ApiException ex;
        if (e instanceof HttpException) {             //HTTP错误
            HttpException httpException = (HttpException) e;
            ex = new ApiException(e, ERROR.HTTP_ERROR);
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    /*
                      不需要将文字提出来,实际上是通过错误码查找到的错误信息
                      以下同
                     */
                    ex.setMsg("协议出错");
                    break;
            }
            return ex;
        } else if (e instanceof UnknownHostException) {
            ex = new ApiException(e, ERROR.HTTP_ERROR);
            ex.setMsg("协议出错");
            return ex;
        } else if (e instanceof ServerException) {    //服务器返回的错误
            ServerException resultException = (ServerException) e;
            ex = new ApiException(resultException, resultException.getCode());
            ex.setMsg(resultException.getMsg());
            return ex;
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            ex = new ApiException(e, ERROR.PARSE_ERROR);
            ex.setMsg("解析错误");            //均视为解析错误
            return ex;
        } else if (e instanceof ConnectException
                || e instanceof SocketTimeoutException
                || e instanceof TimeoutException) {
            ex = new ApiException(e, ERROR.NETWORD_ERROR);
            ex.setMsg("网络错误");  //均视为网络错误
            return ex;
        } else if (e instanceof ClassCastException) {
            ex = new ApiException(e, ERROR.TYPE_ERROR);
            ex.setMsg("类型转换错误");  //类型转换错误(对服务器返回的data进行类型转换时失败)
            return ex;
        } else {
            ex = new ApiException(e, ERROR.UNKNOWN);
            ex.setMsg("未知错误");          //未知错误
            return ex;
        }
    }

}
