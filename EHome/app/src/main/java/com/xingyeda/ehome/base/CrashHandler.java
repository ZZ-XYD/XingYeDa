package com.xingyeda.ehome.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;

import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.AppUtils;
import com.xingyeda.ehome.util.MyLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.xingyeda.ehome.base.BaseActivity.mEhomeApplication;

/**
 * Created by LDL on 2017/10/12.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = CrashHandler.class.getSimpleName();
    private static CrashHandler INSTANCE = new CrashHandler();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;


    private CrashHandler() {
    }


    public static CrashHandler getInstance() {
        return INSTANCE;
    }


    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // if (!handleException(ex) && mDefaultHandler != null) {
        // mDefaultHandler.uncaughtException(thread, ex);
        // } else {
        // android.os.Process.killProcess(android.os.Process.myPid());
        // System.exit(10);
        // }


        new Thread() {
            @Override
            public void run() {
                Looper.prepare();//程序崩溃了
                MyLog.i("SDK:"+mEhomeApplication.sdk+";手机型号:"+mEhomeApplication.model+";android版本:"+mEhomeApplication.release+";AppVersions:"+ AppUtils.getVersionName(mContext));
                Map<String,String> params = new HashMap<>();
                params.put("model", mEhomeApplication.model);
                OkHttp.uploadFile(mContext,ConnectPath.LOG_UPDATE,"log", MyLog.fileName(0),params,MyLog.getFile(0),new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }));
                Looper.loop();
            }
        }.start();
    }


    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        // new Handler(Looper.getMainLooper()).post(new Runnable() {
        // @Override
        // public void run() {
        // new AlertDialog.Builder(mContext).setTitle("提示")
        // .setMessage("程序崩溃了...").setNeutralButton("我知道了", null)
        // .create().show();
        // }
        // });
        return true;
    }
}
