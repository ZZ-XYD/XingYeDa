package com.xiaowei.core;

import android.text.TextUtils;


import com.xiaowei.core.utils.CoreConst;
import com.xiaowei.core.utils.SimpleLog;

import org.litepal.LitePalApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 核内库的Application
 *
 * @author ye.jian
 */

public class CoreApplication extends LitePalApplication {
    private static CoreApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
//        ToastUtils.init(this);
        initLog();
    }

    public static CoreApplication getInstance() {
        return mInstance;
    }

    private void initLog() {
        // 根据日期创建日志文件夹
        String subFolder = new SimpleDateFormat("yyyy-MM-dd", Locale
                .CHINA)
                .format(new Date());
        String path = TextUtils.concat(CoreConst.LOG_PATH, subFolder)
                .toString();
        SimpleLog.init(path);
        SimpleLog.enableLogcat(true);
        SimpleLog.enableFile(false);
    }
}
