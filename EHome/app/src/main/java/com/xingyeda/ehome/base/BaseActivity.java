package com.xingyeda.ehome.base;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.Toast;

import com.ldl.imageloader.utils.L;
import com.umeng.analytics.MobclickAgent;
import com.xingyeda.ehome.util.LogUtils;
import com.ldl.okhttp.OkHttpUtils;
import com.xingyeda.ehome.util.MyLog;

//自定义父类
public class BaseActivity extends Activity {
    private Intent mIntent;
    private Toast mToast;

    public static EHomeApplication mEhomeApplication;
    public static int mScreenW, mScreenH;
    public Context mContext = this;
    //搜集所有的Activity
    public static List<Activity> mActivitieList = new ArrayList<Activity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        EHomeApplication.getInstance().addActivity(this);
        mActivitieList.add(this);

        //得到屏幕的长宽
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenW = outMetrics.widthPixels;
        mScreenH = outMetrics.heightPixels;

        LogUtils.d(this.getClass().getSimpleName() + "...onCreate.......");
        MyLog.i(this.getClass().getSimpleName()+"启动");
        mEhomeApplication = (EHomeApplication) getApplication();
    }

    //当界面即将可见的时候调用  一般也用来初始化的工作
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    //界面可见，并且用户可以与其进行交互的时候
    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    //界面刚不可见
    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(this.getClass().getSimpleName() + "...onStop.......");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i(this.getClass().getSimpleName()+"销毁");
        OkHttpUtils.getInstance().cancelTag(this);
        EHomeApplication.getInstance().finishActivity(this);
        mActivitieList.remove(this);
        mEhomeApplication.finishActivity(this);
    }

    //界面跳转
    protected void startActivity(Class<?> cls) {
        startActivities(cls, null);
    }

    protected void startActivities(Class<?> cls, Bundle bdl) {
        if (mIntent == null) {
            mIntent = new Intent(this, cls);
        }
        mIntent.setClass(this, cls);
        if (bdl != null) {
            mIntent.putExtras(bdl);
        }
        startActivity(mIntent);
    }

    //toast
    protected void showShortToast(CharSequence msg) {
        if (null == mToast) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(msg);
        mToast.show();
    }

    protected void showShortToast(int resId) {
        showShortToast(this.getResources().getText(resId));
    }

    protected void showLongToast(CharSequence msg) {
        if (null == mToast) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setText(msg);
        mToast.show();
    }

    protected void showLongToast(int resId) {
        showShortToast(this.getResources().getText(resId));
    }
}
