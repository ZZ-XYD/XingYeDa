package com.xingyeda.ehome.menu;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.ActivityGuide;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.assist.Shortcut;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.view.SwitchButton;

public class ActivityMenuSet extends BaseActivity {

	@Bind(R.id.set_vocality)
	SwitchButton mVocality;// 声音设置
	@Bind(R.id.set_shake)
	SwitchButton mShake;// 震动设置
	@Bind(R.id.set_wifi)
	SwitchButton mWIFI;// 无线网络设置
	@Bind(R.id.set_3gAnd4g)
	SwitchButton m3gAnd4g;// 移动网络设置
	@Bind(R.id.set_call)
	SwitchButton mSetCall;
	@Bind(R.id.menu_set_Back)
	TextView mBack;
	@Bind(R.id.set_add_opendoor)
	View mAddOpenDoor;

	private boolean mVocalityGet;
	private boolean mShakeGet;
	private boolean mSetCallGet;
	private boolean mWIFIGet;
	private boolean m3gAnd4gGet;
	
	private boolean mVocalitySet;
	private boolean mShakeSet;
	private boolean mSetCallSet;
	private boolean mWIFISet;
	private boolean m3gAnd4gSet;
	private final static  int  SETDATA = 1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_menu_set);
		ButterKnife.bind(this);
		init();
		event();
	}

	private void event() {
		mVocality.setmSetSwitch(!SharedPreUtil.getBoolean(mContext, "vocality"));
        mShake.setmSetSwitch(!SharedPreUtil.getBoolean(mContext, "shake"));
        mWIFI.setmSetSwitch(!SharedPreUtil.getBoolean(mContext, "wifi"));
        m3gAnd4g.setmSetSwitch(!SharedPreUtil.getBoolean(mContext, "3gAnd4g"));
        mSetCall.setmSetSwitch(!SharedPreUtil.getBoolean(mContext, "receivecall"));;
		
		// 设置监听事件
		mVocality.setOnChangeListener(changeListener);
		mShake.setOnChangeListener(changeListener);
		mWIFI.setOnChangeListener(changeListener);
		m3gAnd4g.setOnChangeListener(changeListener);
		mSetCall.setOnChangeListener(changeListener);
	}

	private void init() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("uId", mEhomeApplication.getmCurrentUser().getmId());
		params.put("type", "get");
		OkHttp.get(mContext,ConnectPath.GETSETUP_PATH, params, new BaseStringCallback(
				mContext, new CallbackHandler<String>() {

					@Override
					public void parameterError(JSONObject response) {

					}

					@Override
					public void onResponse(JSONObject response) {
						try {
							JSONObject jobj = (JSONObject) response.get("obj");
							mVocalityGet = jobj.has("voice") ? Boolean.valueOf(jobj.getString("voice")) : true;
							mShakeGet = jobj.has("shock") ? Boolean.valueOf(jobj.getString("shock")): true;
							mSetCallGet = jobj.has("accept") ? Boolean.valueOf(jobj.getString("accept")): true;
							mWIFIGet = jobj.has("wifi") ? Boolean.valueOf(jobj.getString("wifi")): true;
							m3gAnd4gGet = jobj.has("mobileNetwork") ? Boolean.valueOf(jobj.getString("mobileNetwork")) : true;
							
							 mVocalitySet = mVocalityGet;
							 mShakeSet = mShakeGet;
							 mSetCallSet = mSetCallGet;
							 mWIFISet = mWIFIGet;
							 m3gAnd4gSet = m3gAnd4gGet;
//									mHandler.sendEmptyMessage(SETDATA);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure() {

					}
				}));
	}
//	private Handler mHandler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			// 初始化设置
//			 mVocality.setmSetSwitch(mVocalityGet);
//			 mShake.setmSetSwitch(mShakeGet);
//			 mWIFI.setmSetSwitch(mWIFIGet);
//			 m3gAnd4g.setmSetSwitch(m3gAnd4gGet);
//			 mSetCall.setmSetSwitch(true);
//			 mVocalitySet = mVocalityGet;
//			 mShakeSet = mShakeGet;
//			 mSetCallSet = mSetCallGet;
//			 mWIFISet = mWIFIGet;
//			 m3gAnd4gSet = m3gAnd4gGet;
//		}
//	};

	private SwitchButton.OnChangeListener changeListener = new SwitchButton.OnChangeListener() {

		@Override
		public void onChange(SwitchButton sb, boolean state) {
			switch (sb.getId()) {
			// 设置声音
			case R.id.set_vocality:
				mVocalitySet =  !state;
				break;
			// 设置震动
			case R.id.set_shake:
				mShakeSet =  !state;
				break;
			// 设置无线查看视频
			case R.id.set_wifi:
				mWIFISet =  !state;
				break;
			// 设置数据流量查看视频
			case R.id.set_3gAnd4g:
				m3gAnd4gSet =  !state;
				break;
			case R.id.set_call:
				mSetCallSet =  !state;
				break;
			}
		}
	};

	@OnClick({ R.id.menu_set_Back, R.id.set_add_opendoor })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_set_Back:
			ActivityMenuSet.this.finish();
			break;
		case R.id.set_add_opendoor:
			Shortcut.createShortCut(mContext);
			BaseUtils.showShortToast(mContext, R.string.already_add);
			break;
		}
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (!(m3gAnd4gGet == m3gAnd4gSet && mSetCallGet == mSetCallSet && mShakeGet == mShakeSet && mVocalityGet == mVocalitySet && mWIFIGet == mWIFISet)) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("uId", mEhomeApplication.getmCurrentUser().getmId());
			params.put("voice", mVocalitySet+"");
			params.put("shock", mShakeSet+"");
			params.put("accept",mSetCallSet +"");
			params.put("wifi", mWIFISet+"");
			params.put("mobileNetwork", m3gAnd4gSet+"");
			params.put("type", "update");
			OkHttp.get(mContext,ConnectPath.GETSETUP_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
				
				@Override
				public void parameterError(JSONObject response) {
					System.out.println();
				}
				
				@Override
				public void onResponse(JSONObject response) {
					SharedPreUtil.put(mContext, "vocality", mVocalitySet);
					SharedPreUtil.put(mContext, "shake", mShakeSet);
					SharedPreUtil.put(mContext, "wifi", mWIFISet);
					SharedPreUtil.put(mContext, "3gAnd4g", m3gAnd4gSet);
					SharedPreUtil.put(mContext, "receivecall", mSetCallSet);
					BaseUtils.showShortToast(mContext, "修改成功");
				}
				
				@Override
				public void onFailure() {
					
				}
			}));
		}
		ButterKnife.unbind(this);
	}
}
