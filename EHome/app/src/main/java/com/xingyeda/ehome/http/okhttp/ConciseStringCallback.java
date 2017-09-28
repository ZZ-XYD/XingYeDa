package com.xingyeda.ehome.http.okhttp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.ldl.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;


public class ConciseStringCallback extends StringCallback {

    private static final int SUCCEED = 1;
    private ConciseCallbackHandler<String> mCallbackHandler;
    private Message mMessage;
    private Context mContext;


    public ConciseStringCallback(Context context, ConciseCallbackHandler<String> handler) {
	mContext = context;
	mCallbackHandler = handler;
	mMessage = new Message();
    }
    @Override
    public void onError(Call call, Exception e,int id) {
        BaseUtils.showShortToast(mContext, "连接超时");
    }
    @Override
    public void onResponse(String response,int id) {
	try {
	    JSONObject jobj= new JSONObject(response);
	    if (!jobj.get("status").equals("200")) {
		if (jobj.has("msg")) {
			BaseUtils.showShortToast(mContext, jobj.getString("msg"));
		}
		return;
	    }
	    mMessage.what = SUCCEED;
	    mMessage.obj = response;
	    mHandler.sendMessage(mMessage);	 

	} catch (JSONException e) {
	    e.printStackTrace();
	}
    }
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
	public void handleMessage(Message msg) {
		try {
	    switch (msg.what) {
	    case SUCCEED:
				mCallbackHandler.onResponse(new JSONObject((String) msg.obj));
		break;

	    }
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    };
}
