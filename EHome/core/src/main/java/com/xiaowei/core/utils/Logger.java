package com.xiaowei.core.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xiaowei.core.utils.SimpleLog;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logger {

    private Logger() {
    }

    /**
     * 打印info级别的log
     *
     * @param msg
     */
    public static void i(Object object, String msg) {
        String tagName = getTagName(object);
        SimpleLog.i(tagName, msg);
    }

    @NonNull
    private static String getTagName(Object object) {
        String tagName = object.getClass().getSimpleName();
        if (TextUtils.isEmpty(tagName)) tagName = "AnonymityClass";
        return tagName;
    }

    /**
     * 打印info级别的log
     *
     * @param msg
     */
    public static void i(String msg) {
        SimpleLog.i("LogInfo", msg);
    }

    /**
     * 打印error级别的log
     *
     * @param msg
     */
    public static void e(Object object, String msg) {
        String tagName = getTagName(object);
        SimpleLog.e(tagName, msg);
    }

    /**
     * 打印error级别的log
     *
     * @param msg
     */
    public static void e(String msg) {
        SimpleLog.e("LogInfo", msg);
    }

    /**
     * 打印error级别的log
     *
     * @param object
     * @param e
     */
    public static void e(Object object, Throwable e) {
        String tagName = getTagName(object);
        printError(tagName, e);
    }

    /**
     * 打印error级别的log
     *
     * @param e
     */
    public static void e(Throwable e) {
        printError(e);
    }

    /**
     * 打印异常信息
     *
     * @param e 异常
     */
    private static void printError(Throwable e) {
        printError("LogInfo", e);
    }

    public static void printError(String tag, Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }

        SimpleLog.e(tag, sw.toString());
    }
}
