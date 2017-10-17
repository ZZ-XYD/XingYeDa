package com.xingyeda.ehome.push;

import android.content.BroadcastReceiver;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;


import com.google.gson.Gson;

import com.ldl.dialogshow.animation.BounceEnter.BounceTopEnter;
import com.ldl.dialogshow.animation.SlideExit.SlideBottomExit;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.MaterialDialog;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.Service.HeartbeatService;
import com.xingyeda.ehome.Test;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.bean.ParkBean;
import com.xingyeda.ehome.bean.PushBean;
import com.xingyeda.ehome.bean.ReceivePush;

import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.ActivityVideo;
import com.xingyeda.ehome.door.DoorFragment;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.tenement.Notice_Activity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.wifiOnOff.MainActivity;
import com.xingyeda.ehome.zhibo.ActivityShareMain;
import com.xingyeda.ehome.zhibo.ActivitySharePlay;

import cn.jpush.android.api.JPushInterface;

import static android.R.attr.entries;
import static com.alipay.sdk.authjs.a.b;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {
    private EHomeApplication mApplication;
    private static final String TAG = "JPush";
    private static final int LOAD_IMAGE = 0;
    private Context mContext;
    private String mImagePath;
	private  Bundle bundle;
	private Gson gson;
	private PushBean bean;

    @SuppressWarnings({ "unused", "static-access" })
    @SuppressLint({ "NewApi", "SimpleDateFormat" })
    @Override
    public void onReceive(Context context, Intent intent) {
	mContext = context;
	mApplication = (EHomeApplication) mContext.getApplicationContext();
		bundle = intent.getExtras();

	if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
	    boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
	}
	int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
	JPushInterface.clearNotificationById(mContext, notificationId);
	if (mApplication.getActivityStack()!=null&&!mApplication.getActivityStack().isEmpty()) {
	    boolean isStart = false;
	    boolean isReturn = true;
	    for (Activity activity : mApplication.getActivityStack()) {
		if (activity.getClass().equals(ActivityLogin.class)) {
		    return;
		}
	    }
	    for (Activity activity : mApplication.getActivityStack()) {
		if (activity.getClass().equals(ActivityHomepage.class)) {
		    isReturn = false;
		}else if(activity.getClass().equals(ActivityShareMain.class)){
		    isReturn = false;
			}
		isStart = true;
	    }
	    if (isStart) {
		if (isReturn) {
		    return;
		}
	    }
	}

	String message = bundle.getString(JPushInterface.EXTRA_EXTRA);

		gson = new Gson();

	if (message != null&& !message.equals("")) {
	    ReceivePush rBean = gson.fromJson(message, ReceivePush.class);
	    bean = rBean.getPushObject();
		MyLog.i("JPush信息："+bean.toString());
		LogUtils.i("JPushMsgId   "+bean.getmMsgId());
		LogUtils.i("JPushMsgId   "+bean.toString());
		if (mApplication.getmPushMap()!=null) {
		Iterator<Map.Entry<String, Boolean>> entries = mApplication.getmPushMap().entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, Boolean> entry = entries.next();
			if (bean.getmMsgId().equals(entry.getKey())) {
				return;
			}
		}
		}
		mApplication.addPushMap(bean.getmMsgId(),true);

	    if (null == bean) {
			return;
		}
	    if (null == bean.getmType()) {
	    	return;
	    }
		if (!bean.getmType().equals("8")) {
			 if (null == bean.getRegId()) {
				return;
			}
		}
	    if (!bean.getmType().equals("3") && !bean.getmType().equals("6") && !bean.getmType().equals("8") && !bean.getmType().equals("11")) {
		if (bean.getmType().equals("2")) {
			if (mApplication.getmCurrentUser()!=null) {
				InformationBase informationBase = new InformationBase(mApplication.getmCurrentUser().getmId(),bean.getmAdminName(),
						bean.getEaddress(), bean.getTitle(), bean.getAlertContent(), bean.getTime(), Integer.valueOf(bean .getSendType()), 0, bean.getPhotograph(),0, 0);
				informationBase.save();
			}
		} else {
			InformationBase informationBase = new InformationBase(mApplication.getmCurrentUser().getmId(),bean.getmAdminName(), bean.getEaddress(), bean.getTitle(),
					bean.getAlertContent(), bean.getTime(), Integer.valueOf(bean.getSendType()), 0, null, -1, -1);
			informationBase.save();
		}
	    }
	    if (bean != null) {
		// 拨号
		if (bean.getmType().equals("2")) {


		    if (SharedPreUtil.getBoolean(mContext, "receivecall")) {
			try {
				Bundle bundle1 = new Bundle();
			    final SimpleDateFormat sdf = new SimpleDateFormat(
				    "yyyy-MM-dd HH:mm:ss");
			    final Date startTime = sdf.parse(bean.getTime());
				if (!("".equals(bean.getmUrl())) && null !=bean.getmUrl()) {
			    videoCallBack(bean.getmUrl());
				}
				bundle1.putString("dongshu", bean.getmUtil());
				bundle1.putString("eid", bean.getEid());
				bundle1.putString("housenum", bean.getHousenum());
				bundle1.putString("type", "dial");
				bundle1.putString("addressData", bean.getEaddress());
				bundle1.putString("jie", bean.getJietong());
				bundle1.putString("echo", bean.getmUrl());
				bundle1.putString("rtmp", bean.getRtmp());
				bundle1.putString("time", bean.getTime());
			    if (mApplication.getmCurrentUser() != null) {
					bundle1.putString("code", bean.getmCode());
				LogUtils.i("呼叫时间 ： "+startTime);
				LogUtils.i("服务器当前时间："+formatTimeInMillis(BaseUtils.getServerTime(mContext)));
				if ((BaseUtils.getServerTime(mContext) - startTime.getTime()) <= 10000) {
				    BaseUtils.startActivities(mContext, ActivityVideo.class, bundle1);

				}
			    }
			} catch (ParseException e) {
			    e.printStackTrace();
			}
		    }

		} else if (bean.getmType().equals("3")) {
		    LogUtils.i("jpush : 挂断提示");
		    for (Activity activity : mApplication.getActivityStack()) {
			if (activity.getClass().equals(ActivityVideo.class)) {
			    SharedPreUtil.put(mContext, "isFinish",true);
			    mApplication.finishActivity(ActivityVideo.class);
			    return;
			}
		    }
		} else if (bean.getmType().equals("4")) {
		    LogUtils.i("jpush : 审核通知");

			Intent mIntent = new Intent(DoorFragment.ACTION_NAME);
			 mIntent.putExtra("yaner", "check");
			 //发送广播
			 mContext.sendBroadcast(mIntent);
		    

		    SharedPreUtil.put(mContext, "isDoor_Upload", true);
		} else if (bean.getmType().equals("5")) {
		    LogUtils.i("jpush : 异地登录");
		    BaseUtils.showShortToast(mContext, "您的帐号在异地登录!");
		    mApplication.finishAllActivity();
		    mApplication.clearData();
		    mApplication.closemTimer();
		    mContext.stopService(new Intent(mContext,
			    HeartbeatService.class));
		    BaseUtils.startActivity(mContext, ActivityLogin.class);
		} else if (bean.getmType().equals("6")) {
		    LogUtils.i("jpush : 其他的接通了通知关掉呼叫界面");
		    for (Activity activity : mApplication.getActivityStack()) {
			if (activity.getClass().equals(ActivityVideo.class)) {
			    mApplication.finishActivity(ActivityVideo.class);
			    return;
			}
		    }
		} else if (bean.getmType().equals("7")) {
		    LogUtils.i("jpush : 物业通知");
		    SharedPreUtil.put(mContext, "isTenement_Upload", true);
			final NormalDialog dialog = DialogShow.showSelectDialogONE(context, bean.getTitle(), bean.getAlertContent());
			dialog.setOnBtnClickL(new OnBtnClickL() {
	            @Override
	            public void onBtnClick() {
	                dialog.dismiss();
	            }
	        });

		}else if (bean.getmType().equals("8")) {

			final MaterialDialog dialog = new MaterialDialog(context);
			dialog.btnNum(2)
					.title( bean.getTitle())
					.content("\t\t" + bean.getAlertContent())
					.btnText(new String[]{"确定","查看详情"})
					.showAnim(new BounceTopEnter())
					.dismissAnim(new SlideBottomExit());
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			dialog.setOnBtnClickL(new OnBtnClickL() {
				// listener
				@Override
				public void onBtnClick() {
					msgCallBack(bean.getmCode());
					dialog.dismiss();
				}
			}, new OnBtnClickL() {
				@Override
				public void onBtnClick() {
					msgCallBack(bean.getmCode());
					Bundle bundle = new Bundle();
					bundle.putString("title", bean.getTitle());
					bundle.putString("content", bean.getAlertContent());
					bundle.putString("time", bean.getTime());
					bundle.putString("imageList", null);
					bundle.putString("bean", "annunciate");
					dialog.dismiss();
					BaseUtils.startActivities(EHomeApplication.getmContext(), Notice_Activity.class, bundle);

				}
			});
		}else if (bean.getmType().equals("9")){//停车场出入消息
			ParkBean parkBean= new ParkBean(mApplication.getmCurrentUser().getmId(),bean.getTitle(),bean.getmMsg(),bean.getTime(),bean.getPhotograph(),0);
			parkBean.save();
		} else if (bean.getmType().equals("11")){//聊天室消息
			String name = bean.getmCode();
			String content = bean.getAlertContent();
			String time = bean.getTime();
			Intent msgIntent = new Intent(ActivitySharePlay.ACTION_NAME);
			msgIntent.putExtra("name", name);
			msgIntent.putExtra("content", content);
			msgIntent.putExtra("time", time);
			mContext.sendBroadcast(msgIntent);
		}

	    }


	}

    }

	public static String formatTimeInMillis(long timeInMillis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeInMillis);
		Date date = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fmt = dateFormat.format(date);

		return fmt;
	}
    @SuppressLint("SimpleDateFormat")
    private String getPhotoFileName() {
	Date date = new Date(System.currentTimeMillis());
	SimpleDateFormat dateFormat = new SimpleDateFormat(
		"'img'_yyyyMMdd_HHmmss");

	return dateFormat.format(date) + ".jpg";
    }

    // 打印所有的 intent extra 数据
    @SuppressLint("NewApi")
    private static String printBundle(Bundle bundle) {
	StringBuilder sb = new StringBuilder();
	for (String key : bundle.keySet()) {
	    if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
		sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
	    } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
		sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
	    } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
		if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
		    Log.i(TAG, "This message has no Extra data");
		    continue;
		}

		try {
		    JSONObject json = new JSONObject(
			    bundle.getString(JPushInterface.EXTRA_EXTRA));
		    @SuppressWarnings("unchecked")
		    Iterator<String> it = json.keys();

		    while (it.hasNext()) {
			String myKey = it.next().toString();
			sb.append("\nkey:" + key + ", value: [" + myKey + " - "
				+ json.optString(myKey) + "]");
		    }
		} catch (JSONException e) {
		    Log.e(TAG, "Get message extra JSON error!");
		}

	    } else {
		sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
	    }
	}
	return sb.toString();
    }
	private void videoCallBack(String mEcho) {
		OkHttp.get(EHomeApplication.getmContext(),mEcho, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
			@Override
			public void onResponse(JSONObject response) {
			}
		}));
	}
	private void msgCallBack(String id) {
		Map<String,String> params = new HashMap<>();
		params.put("id",id);
		OkHttp.get(EHomeApplication.getmContext(), ConnectPath.PUSHMSG_PATH,params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
			@Override
			public void onResponse(JSONObject response) {
			}
		}));
	}

}