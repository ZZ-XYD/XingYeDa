package com.xingyeda.ehome.http.okhttp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ldl.okhttp.callback.StringCallback;
import com.xingyeda.ehome.util.MyLog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;


public class ConciseStringCallback extends StringCallback {

    private ConciseCallbackHandler<String> mCallbackHandler;
    private Context mContext;


    public ConciseStringCallback(Context context, ConciseCallbackHandler<String> handler) {
	mContext = context;
	mCallbackHandler = handler;
    }
    @Override
    public void onError(Call call, Exception e,int id) {
        BaseUtils.showShortToast(mContext, "连接超时");
		MyLog.i("连接超时");
    }
    @Override
    public void onResponse(String response,int id) {
		MyLog.i("返回数据："+response);
	try {
	    JSONObject jobj= new JSONObject(response);
	    if (!jobj.get("status").equals("200")) {
		if (jobj.has("msg")) {
			BaseUtils.showShortToast(mContext, jobj.getString("msg"));
		}
		return;
	    }

		mCallbackHandler.onResponse(new JSONObject(response));

	} catch (JSONException e) {
	    e.printStackTrace();
	}
    }
}
