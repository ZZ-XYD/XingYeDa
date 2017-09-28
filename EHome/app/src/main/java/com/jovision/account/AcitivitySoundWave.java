package com.jovision.account;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jovision.AppConsts;
import com.jovision.JVBase;
import com.jovision.JVNetConst;
import com.jovision.JniUtil;
import com.jovision.PlayUtil;
import com.jovision.base.IHandlerLikeNotify;
import com.jovision.base.IHandlerNotify;
import com.ldl.dialogshow.dialog.entity.DialogMenuItem;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.listener.OnOperItemClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.ldl.dialogshow.dialog.widget.NormalListDialog;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.DoorFragment;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AcitivitySoundWave extends BaseActivity implements  IHandlerNotify,IHandlerLikeNotify {
	@Bind(R.id.sound_wave_tltie)
	TextView mTltie;
	@Bind(R.id.sound_save1)
	RelativeLayout mLayout1;
	@Bind(R.id.sound_save2)
	LinearLayout mLayout2;
	@Bind(R.id.wifi_name)
	EditText mWifiName;
	@Bind(R.id.wifi_pwd)
	EditText mWifiPwd;

	@Bind(R.id.sound_save3)
	RelativeLayout mLayout3;
	@Bind(R.id.send_sound_wave)
	ImageView mSendSoundWave;
//	@Bind(R.id.add_next2)
//	TextView mNext;

	@Bind(R.id.sound_save4)
	RelativeLayout mLayout4;
	@Bind(R.id.sound_seek)
	ProgressBar mSendSeek;
	@Bind(R.id.timer)
	TextView mTimer;
	
	
	@Bind(R.id.layout_hint1)
	LinearLayout mLayoutHint1;
	@Bind(R.id.layout_hint2)
	LinearLayout mLayoutHint2;
	private int mPage;
	private String mName;
	private String mPwd;
	private AnimationDrawable mAnimation;
	private TimeCount mTime;// 倒计时器
	private String mType;
	private boolean mIsAdd;

	protected MyHandler handler = new MyHandler(this);
	private IHandlerNotify handlerNotify = this;
	private ArrayList<DialogMenuItem> idList = new ArrayList<DialogMenuItem>();
	
//	private IHandlerLikeNotify handlerNotify = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivity_sound_wave);
		ButterKnife.bind(this);
		mIsAdd = true;
		JniUtil.initElian();
		JniUtil.startSearchLan("");
		mEhomeApplication.setCurrentNotifyer(this);
		mPage = 1;
		mTltie.setText("第一步  准备摄像机");
		mName = getConnectWifiSsid();
		mTime = new TimeCount(30000, 1000);
		mType = getIntent().getExtras().getString("type");
	}

	@OnClick({ R.id.add_back, R.id.add_next, R.id.connect_wifi,
			R.id.send_sound_wave,R.id.add_next2,R.id.timer })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_back:// 返回
			if (mPage == 1) {
				AcitivitySoundWave.this.finish();
			} else if (mPage == 2) {
				mLayout1.setVisibility(View.VISIBLE);
				mLayout2.setVisibility(View.GONE);
				mPage--;
				mTltie.setText("第一步  准备摄像机");
			} else if (mPage == 3) {
				mLayout2.setVisibility(View.VISIBLE);
				mLayout3.setVisibility(View.GONE);
				mPage--;
				mTltie.setText("第二步  连接无线网络");
			} else if (mPage == 4) {
				mLayout3.setVisibility(View.VISIBLE);
				mLayout4.setVisibility(View.GONE);
				mPage--;
				mTltie.setText("第三步  发送声波");
				mAnimation = (AnimationDrawable) mSendSoundWave.getBackground();
				mAnimation.stop();
//				AcitivitySoundWave.this.finish();
			}
			break;
		case R.id.add_next:// 下一步
			mLayout1.setVisibility(View.GONE);
			mLayout2.setVisibility(View.VISIBLE);
			mTltie.setText("第二步  连接无线网络");
			mWifiName.setText(mName);
			mPage++;
			break;
		case R.id.connect_wifi:// 连接WIFI
			if (mWifiName.getText().toString().length() == 0) {
				DialogShow.showHintDialog(mContext, "请输入wifi名称");
			}else {
				mName = mWifiName.getText().toString();
				mPwd = mWifiPwd.getText().toString();
				mLayout2.setVisibility(View.GONE);
				mLayout3.setVisibility(View.VISIBLE);
				mTltie.setText("第三步  发送声波");
				mAnimation = (AnimationDrawable) mSendSoundWave.getBackground();
				mPage++;
			}
			break;
		case R.id.send_sound_wave:// 发送声波
			mAnimation.start();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					String param = String.format(AppConsts.SOUND_WAVE_FORMATTER, mName, mPwd);
					JniUtil.newSendSoundWave(param, 3);
				}
			});
			thread.start();
//			mNext.setClickable(true);
//			mNext.setBackgroundResource(R.drawable.button_theme_orange);
			break;
		case R.id.add_next2:// 连接匹配
			if (mType.equals("cateye")) {
				PlayUtil.searchDevice();
			}else{
				JniUtil.searchLanDev();
			}
			final NormalDialog dialog = DialogShow.showSelectDialog(mContext,"已经听到配置成功语音提示？",2,new String[] { getResources().getString(R.string.cancel),getResources().getString(R.string.confirm)});
			dialog.setOnBtnClickL(new OnBtnClickL() {

				@Override
				public void onBtnClick() {
					dialog.dismiss();
				}
			},new OnBtnClickL() {
				
				@Override
				public void onBtnClick() {
					dialog.dismiss();
					if (mType.equals("cateye")) {
					PlayUtil.searchDevice();
					}else{
					JniUtil.searchLanDev();
					}
					mLayout3.setVisibility(View.GONE);
					mLayout4.setVisibility(View.VISIBLE);
					mTltie.setText("第四步  配置无线");
					mPage++;
					mTime.start();
					mTimer.setClickable(false);// 防止重复点击
				}
			});
			break;
		case R.id.timer:// 重新连接匹配
			if (mType.equals("cateye")) {
				PlayUtil.searchDevice();
			}else{
				JniUtil.searchLanDev();
			}
			mTime.start();
			mTimer.setClickable(false);// 防止重复点击
			mTimer.setBackgroundResource(0);
			start();
			mLayoutHint2.setVisibility(View.GONE);
			mLayoutHint1.setVisibility(View.VISIBLE);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTime!=null) {
			mTime.cancel();
		}
//		JniUtil.stopElian();
//		ButterKnife.unbind(this);
		ButterKnife.unbind(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (mPage == 1) {
				AcitivitySoundWave.this.finish();
			} else if (mPage == 2) {
				mLayout1.setVisibility(View.VISIBLE);
				mLayout2.setVisibility(View.GONE);
				mPage--;
				mTltie.setText("第一步  准备摄像机");
			} else if (mPage == 3) {
				mLayout2.setVisibility(View.VISIBLE);
				mLayout3.setVisibility(View.GONE);
				mPage--;
				mTltie.setText("第二步  连接无线网络");
			} else if (mPage == 4) {
				mLayout3.setVisibility(View.VISIBLE);
				mLayout4.setVisibility(View.GONE);
				mPage--;
				mTltie.setText("第三步  发送声波");
				mAnimation = (AnimationDrawable) mSendSoundWave.getBackground();
				mAnimation.stop();
//				AcitivitySoundWave.this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private String getConnectWifiSsid() {
		WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		if(wifiManager != null){
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();

			if(wifiInfo != null){
				String s = wifiInfo.getSSID();
				if(s.length()>2&&s.charAt(0) == '"'&&s.charAt(s.length() -1) == '"'){
					return s.substring(1,s.length()-1);
				}
			}
		}
		return "";
	}

//	public static String getLocalMacAddressFromWifiInfo(Context context) {
//		WifiManager wifi = (WifiManager) context
//				.getSystemService(Context.WIFI_SERVICE);
//		WifiInfo info = wifi.getConnectionInfo();
//		return info.getMacAddress();
//	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			start();
		}

		@Override
		public void onFinish() {// 计时完毕
			mTimer.setClickable(true);
			stop();
			mTimer.setText("");
			mTimer.setBackgroundResource(R.mipmap.refresh);
			mLayoutHint1.setVisibility(View.GONE);
			mLayoutHint2.setVisibility(View.VISIBLE);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程
			mTimer.setClickable(false);// 防止重复点击
			mTimer.setText(millisUntilFinished / 1000 + "s");
		}
	}
	
	 public void start() {
	        // pBar.setIndeterminate(true);
		 mSendSeek.setIndeterminateDrawable(getResources().getDrawable(R.drawable.sound_animdraw));
		 mSendSeek.setProgressDrawable(getResources().getDrawable(R.drawable.sound_animdraw));
	    }

	    public void stop() {
	    	mSendSeek.setIndeterminateDrawable(getResources().getDrawable(R.mipmap.seek2));
	    	mSendSeek.setProgressDrawable(getResources().getDrawable(R.mipmap.seek2));
	    }
//	    private Handler mHandler = new Handler(){
//	    	@Override
//	    	public void handleMessage(Message msg) {
//	    		super.handleMessage(msg);
//	    		switch (msg.what) {
//				case 0:
//					AddSound((String)msg.obj);
////					mNext.setBackgroundResource(R.drawable.button_theme_orange);
//					break;
//
//				default:
//					break;
//				}
//	    	}
//	    };
@Override
public void onHandler(int what, int arg1, int arg2, Object obj) {
	switch (what) {
		case 0x01: {
			if (idList!=null && !idList.isEmpty()) {

final NormalListDialog dialog = DialogShow.showListDialog(mContext, idList);
			dialog.titleTextSize_SP(18).itemTextSize(18).isTitleShow(true)
					.title("请选择需要添加的云视通设备").setOnOperItemClickL(new OnOperItemClickL() {

				@Override
				public void onOperItemClick(AdapterView<?> parent,View view, int positions, long id) {
					AddSound(idList.get(positions).mOperName);
					dialog.dismiss();

				}
			});
		}
			break;
			}
		//流媒体猫眼广播回调
		case JVNetConst.CALL_CATEYE_SEARCH_DEVICE:{

			Log.e("searchCallBack", "猫眼局域网广播回调:what="+what+";arg1="+arg1+";arg2="+arg2+";obj="+obj.toString());
//                {"privateinfo":"timer_count=1;","timeout":0,"ystno":"C200036683"}
			try {
				if (null != obj){
					JSONObject searchObj = new JSONObject(obj.toString());
					if(1 == searchObj.getInt("timeout")){
//							Log.e("searchCallBack", "搜索完成");
					}
					String id = searchObj.getString("ystno");
					if(null != id && !"".equalsIgnoreCase(id)){
						if (mEhomeApplication.getmCurrentUser().ismCameraAdd()) {
							for (HomeBean bean : mEhomeApplication.getmCurrentUser().getmXiaoquList()) {
								if (bean.getmCameraId()!=null&&!bean.getmCameraId().isEmpty()) {
									String s = bean.getmCameraId();
								if (bean.getmCameraId().equals(id)) {
									return;
								}
									AddSound(id);
								}
							}
						} else {

							AddSound(id);
						}
//							Toast.makeText(AcitivitySoundWave.this,"设备号码是:"+searchObj.getString("ystno"),Toast.LENGTH_LONG).show();
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		}
	}


}
		@Override
		public void onNotify(int what, int arg1, int arg2, Object obj) {

			if (mType.equals("cateye")) {
				handler.sendMessage(handler.obtainMessage(what,arg1,arg2,obj));
			}else{
			 try {
		            JSONObject broadJson = new JSONObject(obj.toString());
		            String ystNum = broadJson.getString("gid") + broadJson.getInt("no");
				 int timeout = broadJson.getInt("timeout");
//				 List<String>  idList = new ArrayList();
				 if ("0"!=ystNum) {
					 if (!"0".equals(ystNum)) {
						 if (mEhomeApplication.getmCurrentUser().ismCameraAdd()) {
							 for (HomeBean bean : mEhomeApplication.getmCurrentUser().getmXiaoquList()) {
								 if (!bean.getmCameraId().equals(ystNum)) {
									 for (DialogMenuItem dialogMenuItem : idList) {
										 if (dialogMenuItem.mOperName.equals(ystNum)) {
											 return;
										 }
									 }
									 idList.add(new DialogMenuItem(ystNum,0));
//									 if (mIsAdd) {
//										 Message msg = new Message();
//										 msg.what = 0;
//										 msg.obj = ystNum;
//										 mHandler.sendMessage(msg);
//										 mIsAdd = false;
////		            		AddSound(ystNum);
//									 }
								 }
							 }
						 } else {
							 for (DialogMenuItem dialogMenuItem : idList) {
								 if (dialogMenuItem.mOperName.equals(ystNum)) {
									 return;
								 }
							 }
							 idList.add(new DialogMenuItem(ystNum,0));
//							 if (mIsAdd) {
//								 Message msg = new Message();
//								 msg.what = 0;
//								 msg.obj = ystNum;
//								 mHandler.sendMessage(msg);
//								 mIsAdd = false;
////		            		AddSound(ystNum);
//							 }
						 }
					 }
//		                String value = ystNum + "-ip=" + broadJson.getString("ip") + "-port=" + broadJson.getInt("port") + "-count=" + broadJson.getInt("count");
//		                int timeout = broadJson.getInt("timeout");

//		                Map<String, String> map = new HashMap<String, String>();
//		                map.put("deviceNum", value);
//
//		                Log.e(TAG, "key=" + ystNum + ";value=" + value);
//		                broadStrList.add(map);
		                if (1 == timeout) {//广播超时
//		                	BaseUtils.showLongToast(mContext, "搜索超时");
		                    handler.sendMessage(handler.obtainMessage(0x01));
		                }
		            }

		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}

		}

		private void AddSound(final String id){
//			mIsAdd = false;
			 final Map<String, String> params = new HashMap<String, String>();
			 params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
			 params.put("num", id);
			if (id.substring(0,1).equals("h")||id.substring(0,1).equals("H")) {
				if (mType.equals("common")) {
					params.put("type", "buyaotou");
				}else if (mType.equals("shake")) {
					params.put("type", "yaotou");
				}else  {
					params.put("type", "buyaotou");
				}
//					params.put("type", "buyaotou");
			}else if (id.substring(0,1).equals("c")||id.substring(0,1).equals("C")) {
				params.put("type", "maoyan");
			}else{
			 if (mType.equals("common")) {
				 params.put("type", "buyaotou");
			}else if (mType.equals("shake")) {
				params.put("type", "yaotou");
			}else if (mType.equals("cateye")) {
				 params.put("type", "maoyan");
			 }
			}
	 		 OkHttp.get(mContext, ConnectPath.ADD_CAMERA, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {

				@Override
				public void parameterError(JSONObject response) {
				}

				@Override
				public void onResponse(JSONObject response) {
					if (id.substring(0,1).equals("h")||id.substring(0,1).equals("H")) {
						if (mIsAdd) {
							mIsAdd = false;
							JVBase.addJvDev(id);
						}
					}else if (id.substring(0,1).equals("c")||id.substring(0,1).equals("C")) {
						if (mIsAdd) {
							mIsAdd = false;
						JVBase.addDev(id);
						}
					}
					try {
						Intent mIntent = new Intent(DoorFragment.ACTION_NAME);
						 mIntent.putExtra("yaner", "check");
						 mContext.sendBroadcast(mIntent);
						BaseUtils.showShortToast(mContext,response.getString("msg"));
						BaseUtils.startActivity(mContext, ActivityHomepage.class);
//						AcitivitySoundWave.this.finish();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onFailure() {
					// TODO Auto-generated method stub

				}
			}));
		 }

	protected class MyHandler extends Handler {

		private AcitivitySoundWave activity;

		public MyHandler(AcitivitySoundWave activity) {
			this.activity = activity;
		}

		@Override
		public void handleMessage(Message msg) {
			activity.handlerNotify.onHandler(msg.what, msg.arg1, msg.arg2, msg.obj);
			super.handleMessage(msg);
		}

	}

}
