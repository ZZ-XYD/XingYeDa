package com.jovision.server.utils;

import com.jovision.Utils.ResourcesUnusualUtil;
import com.xiaowei.core.rx.retrofit.exception.ApiException;

/**
 * 共通的工具类
 *
 * @author ye.jian
 */

public class CommonUtils {
    /**
     * 生成错误信息对象
     *
     * @param prefix    字符串前缀
     * @param errorCode 服务器返回的错误码
     * @return
     */
    public static ApiException buildErrorByCode(String prefix, String
            errorCode) {
        ApiException ex;
        String msg = getErrorMsgByCode(prefix, errorCode);
        ex = new ApiException(msg, Integer.parseInt(errorCode));
        return ex;
    }

    /**
     * 根据错误码得到具体的错误信息
     *
     * @param prefix
     * @param errorCode
     * @return
     */
    public static String getErrorMsgByCode(String prefix, String
            errorCode) {
        String errName = prefix + errorCode;
        return ResourcesUnusualUtil.getString(errName);
    }

}
