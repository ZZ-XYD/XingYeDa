package com.xingyeda.ehome.http.okhttp;

import okhttp3.Call;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ldl.okhttp.callback.StringCallback;
import com.xingyeda.ehome.base.EHomeApplication;


public class BaseStringCallback extends StringCallback {

    private CallbackHandler<String> mCallbackHandler;
    private Context mContext;
    
    
    public BaseStringCallback(Context context, CallbackHandler<String> handler) {
	mContext = context;
	mCallbackHandler = handler;
    }
    @Override
    public void onError(Call call, Exception e,int id) {
        BaseUtils.showShortToast(mContext, "连接超时");
		mCallbackHandler.onFailure();
//        mHandler.sendEmptyMessage(TIMEOUT);
    }
    @Override
    public void onResponse(String response,int id) {
	try {
	    JSONObject jobj= new JSONObject(response);
	    if (!jobj.get("status").equals("200")) {
		if (jobj.has("msg")) {
			BaseUtils.showShortToast(mContext, jobj.getString("msg"));
		}
			mCallbackHandler.parameterError(new JSONObject(response));
		return;
	    }
		mCallbackHandler.onResponse(new JSONObject(response));

	} catch (JSONException e) {
	    e.printStackTrace();
	}
    }
}
