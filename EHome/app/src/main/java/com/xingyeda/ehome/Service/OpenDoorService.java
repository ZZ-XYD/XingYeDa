package com.xingyeda.ehome.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.door.ActivityOpenDoor;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

public class OpenDoorService extends Service {

    private Context mContext = this;

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	if (!SharedPreUtil.getBoolean(mContext, "isLogin")) {
	    if (SharedPreUtil.getBoolean(mContext, "isChecked")) {
		BaseUtils.startActivity(mContext, ActivityOpenDoor.class);
	    }
	    else {
		BaseUtils.showShortToast(mContext, mContext.getResources().getString(R.string.not_check));
	    }
	} else {
	    BaseUtils.startActivity(mContext, ActivityLogin.class);
	}
	return super.onStartCommand(intent, flags, startId);
    }

}
