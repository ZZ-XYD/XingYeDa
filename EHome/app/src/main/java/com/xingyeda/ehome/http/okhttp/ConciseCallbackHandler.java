package com.xingyeda.ehome.http.okhttp;

import org.json.JSONObject;

public abstract class ConciseCallbackHandler<T> {
	public abstract void onResponse(JSONObject response);
}
