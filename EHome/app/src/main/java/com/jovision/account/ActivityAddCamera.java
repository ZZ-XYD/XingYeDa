package com.jovision.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.util.BaseUtils;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityAddCamera extends BaseActivity {
	@Bind(R.id.camera_id)
	EditText mCameraId;
	@Bind(R.id.camera_submit)
	TextView mCameraSubmit;
	
	private String mType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_camera);
		ButterKnife.bind(this);
		mType = getIntent().getExtras().getString("type");
		mCameraId.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()!=0) {
					mCameraSubmit.setVisibility(View.VISIBLE);
				}else {
					mCameraSubmit.setVisibility(View.INVISIBLE);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});  
	}

	@OnClick({ R.id.add_camera, R.id.add_back ,R.id.camera_submit})
	public void onClick(View v) {
			Bundle bundle = new Bundle();
		switch (v.getId()) {
		case R.id.add_camera://声波配置
			bundle.putString("type", mType);
			BaseUtils.startActivities(mContext, AcitivitySoundWave.class,bundle);
			break;
		case R.id.add_back://返回
			ActivityAddCamera.this.finish();
			break;
		case R.id.camera_submit://机器码添加
			String id = mCameraId.getText().toString();
			if (mType.equals("common")) {
				if (id.substring(0,1).equals("h")||id.substring(0,1).equals("H")) {
					bundle.putString("type", mType);
					bundle.putString("id", id);
					BaseUtils.startActivities(mContext,JVLogin.class,bundle);
				}else{
					DialogShow.showHintDialog(mContext,"请输入正确的云视通号码");
				}
		}else if (mType.equals("shake")) {
				if (id.substring(0,1).equals("h")||id.substring(0,1).equals("H")) {
					bundle.putString("type", mType);
					bundle.putString("id", id);
					BaseUtils.startActivities(mContext,JVLogin.class,bundle);
				}else{
					DialogShow.showHintDialog(mContext,"请输入正确的云视通号码");
				}
		}else if (mType.equals("cateye")) {
				if (id.substring(0,1).equals("c")||id.substring(0,1).equals("C")) {
				bundle.putString("type", mType);
				bundle.putString("id", id);
				BaseUtils.startActivities(mContext,JVLogin.class,bundle);
				}else{
					DialogShow.showHintDialog(mContext,"请输入正确的云视通号码");
				}
		 }
//			if (id.substring(0).equals("h")||id.substring(0).equals("H")) {
//
//			}
//
//			bundle.putString("type", mType);
//			bundle.putString("id", mCameraId.getText().toString());
//			BaseUtils.startActivities(mContext,JVLogin.class,bundle);
//			AddSound(mCameraId.getText().toString());
			break;
		}
	}
	 @Override
	    protected void onDestroy() {
	        super.onDestroy();
		 ButterKnife.unbind(this);
	    }
//	 private void AddSound(String id){
//		 Map<String, String> params = new HashMap<String, String>();
//		 params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
//		 params.put("num", id);
//		 if (mType.equals("common")) {
//			 params.put("type", "buyaotou");
//		}else if (mType.equals("shake")) {
//			params.put("type", "yaotou");
//		}else if (mType.equals("cateye")) {
//			 params.put("type", "maoyan");
//		 }
// 		 OkHttp.get(mContext, ConnectPath.ADD_CAMERA, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
//
//			@Override
//			public void parameterError(JSONObject response) {
//			}
//
//			@Override
//			public void onResponse(JSONObject response) {
//				try {
//					Intent mIntent = new Intent(DoorFragment.ACTION_NAME);
//					 mIntent.putExtra("yaner", "check");
//					 mContext.sendBroadcast(mIntent);
//					BaseUtils.showShortToast(mContext,response.getString("msg"));
//					ActivityAddCamera.this.finish();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void onFailure() {
//				// TODO Auto-generated method stub
//
//			}
//		}));
//	 }
}
