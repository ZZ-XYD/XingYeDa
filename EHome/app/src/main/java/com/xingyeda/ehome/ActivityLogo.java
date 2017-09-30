package com.xingyeda.ehome;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;


import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapView;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiAvailability;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.api.HuaweiApiClient.OnConnectionFailedListener;
import com.huawei.hms.api.HuaweiApiClient.ConnectionCallbacks;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.jovision.AppConsts;
import com.jovision.Jni;
import com.jovision.JniUtil;
import com.jovision.Utils.ResourcesUnusualUtil;
import com.jovision.server.AccountServiceImpl;
import com.jovision.server.utils.DnsXmlUtils;
import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.assist.Shortcut;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.PhoneBrand;
import com.xingyeda.ehome.http.ConnectHttpUtils;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.AppUtils;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.MD5Utils;
import com.xingyeda.ehome.util.NetUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.wifiOnOff.MainActivity;


import static com.xingyeda.ehome.base.PhoneBrand.SYS_EMUI;

/**
 * @ClassName: ActivityLogo
 * @Description: 广告页
 * @author 李达龙
 * @date 2016-7-6
 */
public class ActivityLogo extends BaseActivity implements ConnectionCallbacks, OnConnectionFailedListener {

	static {
		System.loadLibrary("gnustl_shared");
		System.loadLibrary("stlport_shared");
		System.loadLibrary("tools");
		System.loadLibrary("nplayer");
		System.loadLibrary("alu");
		System.loadLibrary("play");

		System.loadLibrary("cat110");
	}
   @Bind(R.id.logo_image)
    ImageView mBackground;
    private String mImagePath;
    private static final int IMAGE = 1;

	public static HuaweiApiClient huaweiApiClient;

    LocationClient mLocClient;
    BitmapDescriptor mCurrentMarker;
    MapView mMapView;
    BaiduMap mBaiduMap;
    boolean isFirstLoc = true; // 是否首次定位
    Bundle bundle;
    ImageLoader imageLoader;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_logo);
	ButterKnife.bind(this);

		JniUtil.createDirectory(AppConsts.LOG_PATH);
		JniUtil.createDirectory(AppConsts.CAPTURE_PATH);
		JniUtil.createDirectory(AppConsts.VIDEO_PATH);
		JniUtil.createDirectory(AppConsts.DOWNLOAD_PATH);

		try {
			JniUtil.initSDK(mEhomeApplication, AppConsts.LOG_PATH, "113.222.33.176");
//			Jni.init(mContext, 9200, CAT_STREAM_PATH, "", 0);
//			Jni.strMedPlayerInit(null);
			Jni.strMedPlayerInit(null,AppConsts.LOG_PATH);
		} catch (IOException e) {

			e.printStackTrace();
		}
		if (SYS_EMUI.equals(PhoneBrand.getSystem())){
		huaweiApiClient = new HuaweiApiClient.Builder(this).addApi(HuaweiPush.PUSH_API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
		huaweiApiClient.connect();
		}


		initAccount();
		ResourcesUnusualUtil.register(this);
	bundle = new Bundle();
	loginImage();
//	imageLoader = ImageLoader.getInstance(3, Type.LIFO);
//	PackageManager pm = getPackageManager();
//	  boolean flag = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.ACCESS_COARSE_LOCATION", "com.xingyeda.ehome"));
//	  boolean flag1 = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.ACCESS_FINE_LOCATION", "com.xingyeda.ehome"));
//	  boolean flag2 = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.ACCESS_WIFI_STATE", "com.xingyeda.ehome"));
//	        if (flag) {         //有这个权限，做相应处理
	 // 地图初始化
        mMapView = new MapView(this);
        mMapView.setVisibility(View.GONE);
        mBaiduMap = mMapView.getMap();
//        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
//        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(new BDLocationListener() {

	    @Override
	    public void onReceiveLocation(BDLocation location) {
		 if (isFirstLoc) {
	                isFirstLoc = false;
	                mEhomeApplication.setLatitude(location.getLatitude());
	                mEhomeApplication.setLongitude(location.getLongitude());
	                mEhomeApplication.setAddrStr(location.getAddrStr());
//	                Toast.makeText(mContext, location.getLatitude()+"    "+location.getLongitude()+"   "+ location.getAddrStr()+"   "+location.getCity(),Toast.LENGTH_SHORT).show();
//	                LogUtils.i("经纬度  ： "+location.getLatitude()+"    "+location.getLongitude());
	            }
	    }
	});
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();



		Shortcut.createShortCut(mContext);//一键开门

	JPushInterface.init(mContext);
	SharedPreUtil.put(mContext, "isUpdate", true);
	SharedPreUtil.put(mContext, "isDoor_Upload", true);
	SharedPreUtil.put(mContext, "isTenement_Upload", true);
	SharedPreUtil.put(mContext, "isLoad_More", true);
	SharedPreUtil.put(mContext, "isLife_Upload", true);
	SharedPreUtil.put(mContext, "isChecked", false);
	Animation animation = AnimationUtils.loadAnimation(mContext,
		R.anim.logo);
	animation.setFillAfter(true);
	animation.setAnimationListener(new Animation.AnimationListener() {
	    @Override
	    public void onAnimationStart(Animation animation) {}
	    @Override
	    public void onAnimationRepeat(Animation animation) {}
	    @Override
	    public void onAnimationEnd(Animation animation) {
		if (!SharedPreUtil.getBoolean(mContext, "isLogin")) {
		    final String name = SharedPreUtil.getString(mContext,"userName");
		    final String pwd = SharedPreUtil.getString(mContext, "userPwd");
//		    RequestParams params = new RequestParams();
		    Map<String, String> params = new HashMap<String, String>();
		    params.put("userName", name);
		    params.put("userPwd", MD5Utils.MD5(pwd));
		    params.put("AndroidSdk", mEhomeApplication.sdk);
		    params.put("AndroidModel", mEhomeApplication.model);
		    params.put("AndroidRelease", mEhomeApplication.release);
		    params.put("regkey",JPushInterface.getRegistrationID(mContext));
		    params.put("AppVersions", AppUtils.getVersionName(mContext));
 		    OkHttp.get(mContext,ConnectPath.LOGIN_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {

				@Override
				public void parameterError(JSONObject response) {
					bundle.putString("cause", "normal");
				    BaseUtils.startActivities(mContext, ActivityLogin.class, bundle);
//				    ActivityLogo.this.finish();
				}

				@Override
				public void onResponse(JSONObject response) {
					if (SYS_EMUI.equals(PhoneBrand.getSystem())){
					getToken();
					}
					ConnectHttpUtils.loginUtils(response,ActivityLogo.this, name, pwd,ActivityHomepage.class);
				}

				@Override
				public void onFailure() {
					bundle.putString("cause", "timeout");
				    BaseUtils.startActivities(mContext, ActivityLogin.class, bundle);
//				    ActivityLogo.this.finish();
				}
			}));
		} else {
		    bundle.putString("cause", "normal");
		    BaseUtils.startActivities(mContext, ActivityLogin.class, bundle);
//		    ActivityLogo.this.finish();
		}
	    }
	});
	mBackground.setAnimation(animation);
    }


	@Override
	protected void onStart() {
		super.onStart();
		if (SYS_EMUI.equals(PhoneBrand.getSystem())){
		HuaweiApiAvailability.getInstance().isHuaweiMobileServicesAvailable(this);
		huaweiApiClient.connect();
		}
	}
	@Override
	public void onConnected() {
//		Log.i(TAG, "在连接,是否连接: " + huaweiApiClient.isConnected());
	}

	@Override
	public void onConnectionSuspended(int cause) {
//		Log.i(TAG, "连接中断,原因: " + cause + ", 是否连接: " + huaweiApiClient.isConnected());
	}

	@Override
	public void onConnectionFailed( ConnectionResult connectionResult) {
		int s = connectionResult.getErrorCode();
		if ("1".equals(s)||"2".equals(s)) {
			BaseUtils.showLongToast(mContext,"检测到您的华为移动版本过低，建议马上升级到最新版本，否则有小概率收不到设备的呼叫");
		}
//		if (SYS_EMUI.equals(PhoneBrand.getSystem())){
//		huaweiApiClient.connect();
//		}
	}

	@Override
    protected void onPause() {
	super.onPause();
	mMapView.onPause();
//	ActivityLogo.this.finish();
    }

    @Override
    protected void onResume() {
	super.onResume();
	mMapView.onResume();

    }


    private Handler mHandler = new Handler() {
	@Override
	public void handleMessage(Message msg) {
	    switch (msg.what) {
	    case IMAGE:
	    	if (mBackground!=null) {
//				OkHttp.getImage(mContext,ConnectPath.IMAGE_PATH + (String) msg.obj, mBackground);
	    		ImageLoader.getInstance().displayImage(ConnectPath.IMAGE_PATH + (String) msg.obj, mBackground);
			}
		break;
	    }
	}
    };

    private void loginImage() {

    	if (!NetUtils.isConnected(mContext)) {
    		BaseUtils.startActivity(mContext, ActivityLogin.class);
//		    ActivityLogo.this.finish();
		}
    	else {
    OkHttp.get(mContext,ConnectPath.LOGINIMAGE_PATH, new BaseStringCallback(mContext, new CallbackHandler<String>() {

		@Override
		public void parameterError(JSONObject response) {
//			ActivityLogo.this.finish();
		}

		@Override
		public void onResponse(JSONObject response) {
			try {
			JSONObject jobj = response.getJSONObject("obj");
		    mImagePath = jobj.has("strvalue") ? jobj.getString("strvalue") : "";

		    Message msg = new Message();
		    msg.obj = mImagePath;
		    msg.what = IMAGE;
		    mHandler.sendMessage(msg);
			} catch (JSONException e) {
			    e.printStackTrace();
			}

		}
		@Override
		public void onFailure() {

		}
	}));
    	}
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
	// 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
		ButterKnife.unbind(this);
    }

	private void initAccount() {
		// DNS检查
		if (DnsXmlUtils.isUpdateLocalDnsFile()) {
			DnsXmlUtils.resetDnsData();
			DnsXmlUtils.downloadDnsFile();
		}
		// 账号初始化
		AccountServiceImpl.getInstance().init();
	}

	public static void getToken() {
		if (!isConnected()) {
//			Log.d(TAG, "获得令牌失败,HMS是断开的。");
			return;
		}

		// 异步调用方式
		PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(huaweiApiClient);
		tokenResult.setResultCallback(new ResultCallback<TokenResult>() {

			@Override
			public void onResult(TokenResult result) {
			}

		});
	}
	public static boolean isConnected() {
		if (huaweiApiClient != null && huaweiApiClient.isConnected()) {
			return true;
		} else {
			return false;
		}
	}
}
