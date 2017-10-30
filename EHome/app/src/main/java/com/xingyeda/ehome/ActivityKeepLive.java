package com.xingyeda.ehome;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.xingyeda.ehome.util.MyLog;

public class ActivityKeepLive extends Activity {

    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置Activity布局参数
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("finish")) {
                    ActivityKeepLive.this.finish();
                }
            }
        };

        registerReceiver(br, new IntentFilter("finish"));

//        checkScreen();

        MyLog.i("ActivityKeepLive启动");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkScreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
        MyLog.i("ActivityKeepLive销毁");
    }

    private void checkScreen() {
        PowerManager pm = (PowerManager) ActivityKeepLive.this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn) {
            ActivityKeepLive.this.finish();
        }
    }
}
