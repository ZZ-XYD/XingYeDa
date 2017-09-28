package com.xingyeda.ehome.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;


public class HeartbeatService extends Service implements Runnable
{
    private Thread mThread;
    public int count = 0;
    private boolean isTip = true;
    private boolean isLogin = true;
    private static String mRestMsg;
    private static String mId;
    private static String KEY_REST_MSG = "KEY_REST_MSG";
    private static String CONNECTION_BREAK = "DISCONNECT";//接通
    private static String CONNECT = "CONNECT";//断开

    private Context mContext = this;
    @Override
    public void run()
    {
        SharedPreferences prefer = getSharedPreferences("settings.data",
                Context.MODE_PRIVATE);
        while (isLogin)
        {
            try
            {
                if (count >= 1)
                {
                    Log.i("@qi", "offline");
                    count = 1;
                    if (isTip)
                    {
                        // 判断应用是否在运行
                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        List<RunningTaskInfo> list = am.getRunningTasks(3);
                        for (RunningTaskInfo info : list)
                        {
                            if (prefer.getBoolean("CONNECTION_BREAK", true))
                            {
                                SharedPreferences.Editor editor = prefer.edit();
                                editor.putBoolean(CONNECTION_BREAK, false);
                                editor.putBoolean(CONNECT, true);
                                editor.commit();
                            if (info.topActivity.getPackageName().equals(
                                    "com.xingyeda.ehome"))
                            {
                                // 通知应用，显示提示“连接不到服务器”
//                                Intent intent = new Intent("com.xingyeda.ehome.HeartbeatService");
//                                intent.putExtra("serveMsg", true);
//                                sendBroadcast(intent);
                                break;
                            }
                            }
                        }

                        isTip = false;
                    }
                }
                else {
                    if (prefer.getBoolean("CONNECT", true))
                    {
                        SharedPreferences.Editor editor = prefer.edit();
                        editor.putBoolean(CONNECTION_BREAK, true);
                        editor.putBoolean(CONNECT, false);
                        editor.commit();
//                        Intent intent = new Intent("com.xingyeda.ehome.HeartbeatService");
//                        intent.putExtra("serveMsg", false);
//                        sendBroadcast(intent); 
                    }
                }
                if (mRestMsg != "" && mRestMsg != null)
                {
                    // 向服务器发送心跳包
                    count += 1;
                    mHandler.sendEmptyMessage(0);
//                    sendHeartbeatPackage(mRestMsg);
                }

                Thread.sleep(1000 * 8);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
            case 0:
                sendHeartbeatPackage(mRestMsg);
                break;

            }
        }
    };
    private void sendHeartbeatPackage(String msg)
    {
    	Map<String, String> params = new HashMap<String, String>();
        params.put("uid",mId);
        OkHttp.get(mContext,msg, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
			
			@Override
			public void parameterError(JSONObject response) {
			}
			
			@Override
			public void onResponse(JSONObject response) {
				 count = 0;
	                isTip = true;
			}
			
			@Override
			public void onFailure() {
			}
		}));

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        isLogin=false;
    }

    @SuppressWarnings("deprecation")
    public void onStart(Intent intent, int startId)
    {
        Log.i("@qi", "service onStart");
        // 从本地读取服务器的URL，如果没有就用传进来的URL
//        mRestMsg = getRestMsg();
        
        SharedPreferences prefer = getSharedPreferences("settings.data",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putBoolean(CONNECTION_BREAK, true);
        editor.putBoolean(CONNECT, true);
        editor.commit();
        if (mRestMsg == null || mRestMsg == "")
        {
            mRestMsg = intent.getExtras().getString("url");
            mId = intent.getExtras().getString("uid");
        }
        setRestMsg(mRestMsg);

        mThread = new Thread(this);
        mThread.start();
        count = 0;

        super.onStart(intent, startId);
    }

    public String getRestMsg()
    {
        SharedPreferences prefer = getSharedPreferences("settings.data",
                Context.MODE_PRIVATE);
        Log.i("@qi", "getRestMsg() " + prefer.getString(KEY_REST_MSG, ""));
        return prefer.getString(KEY_REST_MSG, "");
    }

    public void setRestMsg(String restMsg)
    {
        SharedPreferences prefer = getSharedPreferences("settings.data",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefer.edit();
        editor.putString(KEY_REST_MSG, restMsg);
        editor.commit();
    }

}
