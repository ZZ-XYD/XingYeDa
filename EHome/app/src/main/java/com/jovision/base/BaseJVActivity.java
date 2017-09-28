package com.jovision.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.Window;

import com.xingyeda.ehome.base.EHomeApplication;

/**
 * Created by juyang on 16/3/22.
 */
public abstract class BaseJVActivity extends Activity implements IHandlerNotify, IHandlerLikeNotify {

    protected MyHandler handler = new MyHandler(this);
    private IHandlerNotify handlerNotify = this;

    /**
     * 初始化设置，不要在这里写费时的操作
     */
    protected abstract void initSettings();

    /**
     * 初始化界面，不要在这里写费时的操作
     */
    protected abstract void initUi();

    /**
     * 保存设置，不要在这里写费时的操作
     */
    protected abstract void saveSettings();

    /**
     * 释放资源、解锁、删除不用的对象，不要在这里写费时的操作
     */
    protected abstract void freeMe();

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ((EHomeApplication) getApplication()).push(this);
        ((EHomeApplication) getApplication()).setCurrentNotifyer(this);
        initSettings();
        initUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveSettings();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ((EHomeApplication) getApplication()).pop();
        freeMe();
        super.onDestroy();
    }

    protected class MyHandler extends android.os.Handler {

        private BaseJVActivity activity;

        public MyHandler(BaseJVActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            activity.handlerNotify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
            super.handleMessage(msg);
        }

    }
}
