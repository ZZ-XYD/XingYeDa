package com.xingyeda.ehome.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.AppUtils;
import com.xingyeda.ehome.util.MyLog;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xingyeda.ehome.base.BaseActivity.mEhomeApplication;

/**
 * Created by LDL on 2017/10/12.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = CrashHandler.class.getSimpleName();
    private Context mContext;
    private static CrashHandler INSTANCE = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private CrashHandler() {

    }


    public static CrashHandler getInstance() {
        return INSTANCE;
    }


    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        final StringBuffer sb = getTraceInfo(ex);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "服务器维护中...", Toast.LENGTH_LONG).show();
                Map<String, String> params = new HashMap<>();
                params.put("model", mEhomeApplication.model);
                MyLog.e(sb);
                MyLog.e("SDK版本："+mEhomeApplication.sdk+";手机型号:"+mEhomeApplication.model+";android系统版本号:"+mEhomeApplication.release+";AppVersions:"+AppUtils.getVersionName(mContext));
                OkHttp.uploadFile(mContext, ConnectPath.LOG_UPDATE, "log", MyLog.fileName(0), params, MyLog.getFile(0), new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }));
                Looper.loop();
            }
        }.start();
        return true;
    }

    public static StringBuffer getTraceInfo(Throwable e) {
        StringBuffer sb = new StringBuffer();
        Throwable ex = e.getCause() == null ? e : e.getCause();
        StackTraceElement[] stacks = ex.getStackTrace();
        for (int i = 0; i < stacks.length; i++) {
            sb.append("class: ").append(stacks[i].getClassName()).append("; method: ").append(stacks[i].getMethodName()).append("; line: ").append(stacks[i].getLineNumber()).append("; Exception: ").append(ex.toString() + "\n");
        }
        Log.d(TAG, sb.toString());
        return sb;
    }

}
