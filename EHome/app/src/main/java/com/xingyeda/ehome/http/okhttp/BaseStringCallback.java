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

    private static final int SUCCEED = 1;
    private static final int PARAMETER_FAIL = 2;
    private static final int TIMEOUT = 3;
    private CallbackHandler<String> mCallbackHandler;
    private Message mMessage;
    private Context mContext;
    
    
    public BaseStringCallback(Context context, CallbackHandler<String> handler) {
	mContext = context;
	mCallbackHandler = handler;
	mMessage = new Message();
    }
    @Override
    public void onError(Call call, Exception e,int id) {
        BaseUtils.showShortToast(mContext, "连接超时");
        mHandler.sendEmptyMessage(TIMEOUT);
    }
    @Override
    public void onResponse(String response,int id) {
	try {
	    JSONObject jobj= new JSONObject(response);
	    if (!jobj.get("status").equals("200")) {
		if (jobj.has("msg")) {
			BaseUtils.showShortToast(mContext, jobj.getString("msg"));
		}
		mMessage.what = PARAMETER_FAIL;
		mMessage.obj = response;
		mHandler.sendMessage(mMessage);
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
	    case PARAMETER_FAIL:
				mCallbackHandler.parameterError(new JSONObject((String) msg.obj));
		break;
	    case TIMEOUT:
	    	mCallbackHandler.onFailure();
	    	break;
	    }
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    };
}
