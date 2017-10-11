package com.xingyeda.ehome.door;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.xingyeda.ehome.R;

import com.xingyeda.ehome.Service.OpenDoorService;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.SharedPreUtil;


public class ActivityOpenDoor extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_open_door);
        Intent intent = new Intent(ActivityOpenDoor.this, OpenDoorService.class);
        stopService(intent);
        preparePlay();
    }
    private void preparePlay()
    {
        Runnable runnable=new Runnable() { 
            @Override
            public void run() { 
                openDoor();
                mHandler.removeCallbacks(this);
            }
        };
        mHandler.postDelayed(runnable, 1000); 
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
      @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
        }  
    };
    private void openDoor(){
        MyLog.i("一键开门");
    	Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId"));
        params.put("eid", SharedPreUtil.getString(mContext, "eid"));
        params.put("dongshu", SharedPreUtil.getString(mContext, "dongshu"));
        params.put("housenum", SharedPreUtil.getString(mContext, "housenum"));
        OkHttp.get(mContext,ConnectPath.OPENDOOR_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
			
			@Override
			public void parameterError(JSONObject response) {
				ActivityOpenDoor.this.finish();
				Intent home = new Intent(Intent.ACTION_MAIN);  
				home.addCategory(Intent.CATEGORY_HOME);   
				startActivity(home); 
			}
			
			@Override
			public void onResponse(JSONObject response) {
				ActivityOpenDoor.this.finish();
                Intent home = new Intent(Intent.ACTION_MAIN);  
                home.addCategory(Intent.CATEGORY_HOME);   
                startActivity(home);
			}
			
			@Override
			public void onFailure() {
				Intent home = new Intent(Intent.ACTION_MAIN);  
                home.addCategory(Intent.CATEGORY_HOME);   
                startActivity(home);
			}
		}));
        }
}
