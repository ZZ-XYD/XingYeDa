package com.xingyeda.ehome.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xingyeda.ehome.ActivityGuide;
import com.xingyeda.ehome.ActivityKeepLive;

public class KeepLiveReceiver extends BroadcastReceiver {

    public KeepLiveReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Intent it = new Intent(context, ActivityKeepLive.class);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(it);
        }else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            context.sendBroadcast(new Intent("finish"));
//            Intent main = new Intent(Intent.ACTION_MAIN);
//            main.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            main.addCategory(Intent.CATEGORY_HOME);
//            context.startActivity(main);
        }
    }
}
