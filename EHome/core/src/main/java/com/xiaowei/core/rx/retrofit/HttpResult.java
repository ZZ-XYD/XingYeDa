package com.xiaowei.core.rx.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * 服务器端返回的json对应实体类
 * ye.jian
 */
public class HttpResult<T> {
    @SerializedName("root")
    public ResponseResult<T> response;

    // --------------------------------
    // # 服务器返回的JSON格式(两种)
    // --------------------------------
    // 第一种(data为对象)
    /*
      {
        "root": {
            "data": {
                "isExist": "true"
            },
            "errorCode": "401",
            "msg": "该用户已存在！",
            "result": "true"
        }
      }
     */
    // 第二种(data为数组)
    /*
      {
        "root": {
            "data": [
                {
                    "deviceGuid": "H2896714",
                    "deviceAddTime": "2016-11-25 15:42:24",
                    "permission": "0",
                    "deviceName": "H2896714"
                }
            ],
            "errorCode": "000",
            "msg": "用户设备列表获取成功",
            "result": "true"
        }
      }
     */
    // -------------------------------------------
    /*
      注:
      其实JSON最外层的root完全可以去掉,历史遗留问题,
      在解析/结果处理的时候会复杂一些
     */
    // -------------------------------------------
}
