package com.xiaowei.core.utils;

import android.os.Environment;

import java.io.File;

/**
 * 常量
 */
public class CoreConst {
    // 软件名
    public static final String APP_NAME = "SOOVVI";
    // 平台识别标识
    public static final String PLATFORM = "android";
    // 路径
    public static final String SD_CARD_PATH = Environment
            .getExternalStorageDirectory().getPath() + File.separator;
    // 应用路径
    public static final String APP_PATH = SD_CARD_PATH + APP_NAME + File
            .separator;
    // 普通日志路径
    public static final String LOG_PATH = APP_PATH + "core" + File.separator;
}
