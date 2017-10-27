package com.xingyeda.ehome.base;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.jovision.JniUtil;
import com.jovision.base.BaseJVActivity;
import com.jovision.base.IHandlerLikeNotify;
import com.jovision.server.AccountServiceImpl;
import com.ldl.imageloader.cache.disc.naming.Md5FileNameGenerator;
import com.ldl.imageloader.core.ImageLoader;
import com.ldl.imageloader.core.ImageLoaderConfiguration;
import com.ldl.imageloader.core.assist.QueueProcessingType;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaowei.core.CoreApplication;
import com.xingyeda.ehome.bean.AdvertisementBean;
import com.xingyeda.ehome.bean.AnnunciateBean;
import com.xingyeda.ehome.bean.BeanComplainHistory;
import com.xingyeda.ehome.bean.BeanMaintainHistory;
import com.xingyeda.ehome.bean.LifeBean;
import com.xingyeda.ehome.bean.UserInfo;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.push.TagAliasOperatorHelper;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.LogcatHelper;
import com.ldl.okhttp.OkHttpUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.SharedPreUtil;

import static com.xingyeda.ehome.base.PhoneBrand.SYS_EMUI;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.ACTION_CLEAN;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.ACTION_DELETE;
import static com.xingyeda.ehome.push.TagAliasOperatorHelper.sequence;

@SuppressLint("HandlerLeak")
public class EHomeApplication extends CoreApplication implements IHandlerLikeNotify {

    // user your appid the key.
    private static final String APP_ID = "2882303761517574815";
    // user your appid the key.
    private static final String APP_KEY = "5421757426815";


    private static Context mContext;
    //	private DBManager mManager;
    private IHandlerLikeNotify currentNotifyer;
    private String key = "";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private Map<String, String> mMap = new HashMap<String, String>();

    public Map<String, Boolean> getmPushMap() {
        return mPushMap;
    }

    public void addPushMap(String key, Boolean Value) {
        mPushMap.put(key, Value);
    }

    private Map<String, Boolean> mPushMap = new HashMap<String, Boolean>();


    public void addMap(String key, String Value) {
        mMap.put(key, Value);
    }

    public Map<String, String> getMap() {
        return mMap;
    }

    private UserInfo mCurrentUser = new UserInfo();
    //	private File mImageFile;
    private Timer mTimer = new Timer(true);

    @SuppressWarnings("deprecation")
    public String sdk = android.os.Build.VERSION.SDK; // SDK号
    public String model = android.os.Build.MODEL; // 手机型号
    public String release = android.os.Build.VERSION.RELEASE; // android系统版本号

    // 版本信息
    private String mNow_Versions;

    // wifi的连接状态
    private boolean mIsWifi = false;
    // 移动数据的连接状态
    private boolean mIsMobile = false;


    //广告
    private List<AdvertisementBean> advertisementList;

    private List<AnnunciateBean> mAnnunciateList;
    // 小区公告集合
    private List<AnnunciateBean> mAc_List;
    // private AnnunciateBean mAc_Bean;
    // 广告集合
    private AdvertisementBean mAd;

    //	// 商圈tag
//	private List<LifeTagBean> mLife_Tag_List;
    // 商圈内容
    private List<LifeBean> mLife_List;

    // 投诉
    private BeanComplainHistory mComplaiBean;
    // 维修
    private BeanMaintainHistory mMaintainBean;

//	protected DisplayImageOptions mOptions;

    private boolean mIsDownload;

    private double Latitude;
    private double Longitude;
    private String AddrStr;


    public static Context getmContext() {
        return mContext;
    }


//	public DBManager getmManager() {
//		return mManager;
//	}
//
//	public void setmManager(DBManager mManager) {
//		this.mManager = mManager;
//	}


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAddrStr() {
        return AddrStr;
    }

    public void setAddrStr(String addrStr) {
        AddrStr = addrStr;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }


    public List<AnnunciateBean> getmAnnunciateList() {
        return mAnnunciateList;
    }

    public void setmAnnunciateList(List<AnnunciateBean> mAnnunciateList) {
        this.mAnnunciateList = mAnnunciateList;
    }

    public List<LifeBean> getmLife_List() {
        return mLife_List;
    }

    public void setmLife_List(List<LifeBean> mLife_List) {
        this.mLife_List = mLife_List;
    }

//	public File getmImageFile() {
//		return mImageFile;
//	}
//
//	public void setmImageFile(File mImageFile) {
//		this.mImageFile = mImageFile;
//	}

    public boolean ismIsDownload() {
        return mIsDownload;
    }

    public void setmIsDownload(boolean mIsDownload) {
        this.mIsDownload = mIsDownload;
    }

    public String getmNow_Versions() {
        return mNow_Versions;
    }

    public void setmNow_Versions(String mNow_Versions) {
        this.mNow_Versions = mNow_Versions;
    }

    public BeanComplainHistory getmComplaiBean() {
        return mComplaiBean;
    }

    public void setmComplaiBean(BeanComplainHistory mComplaiBean) {
        this.mComplaiBean = mComplaiBean;
    }

    public BeanMaintainHistory getmMaintainBean() {
        return mMaintainBean;
    }

    public void setmMaintainBean(BeanMaintainHistory mMaintainBean) {
        this.mMaintainBean = mMaintainBean;
    }

    // public AnnunciateBean getmAc_Bean()
    // {
    // return mAc_Bean;
    // }
    //
    // public void setmAc_Bean(AnnunciateBean mAc_Bean)
    // {
    // this.mAc_Bean = mAc_Bean;
    // }

    public void setmAd(AdvertisementBean mAd) {
        this.mAd = mAd;
    }

    public AdvertisementBean getmAd() {
        return mAd;
    }

    public void setmAb_List(List<AdvertisementBean> advertisementList) {
        this.advertisementList = advertisementList;
    }

    public List<AdvertisementBean> getmAb_List() {
        return advertisementList;
    }

    public void setmAc_List(List<AnnunciateBean> mAc_List) {
        this.mAc_List = mAc_List;
    }

    public List<AnnunciateBean> getmAc_List() {
        return mAc_List;
    }

    // public Datas getmDatas() {
    // return mDatas;
    // }
    //
    // public void setmDatas(Datas mDatas) {
    // this.mDatas = mDatas;
    // }

    public UserInfo getmCurrentUser() {
        return mCurrentUser;
    }

    public void setmCurrentUser(UserInfo mCurrentUser) {
        this.mCurrentUser = mCurrentUser;
    }

    // public boolean ismWifi()
    // {
    // return mWifi;
    // }
    //
    // public void setmWifi(boolean mWifi)
    // {
    // this.mWifi = mWifi;
    // }
    //
    // public boolean ismMobile()
    // {
    // return mMobile;
    // }
    //
    // public void setmMobile(boolean mMobile)
    // {
    // this.mMobile = mMobile;
    // }
    //
    public boolean ismIsWifi() {
        return mIsWifi;
    }

    public void setmIsWifi(boolean mIsWifi) {
        this.mIsWifi = mIsWifi;
    }

    public boolean ismIsMobile() {
        return mIsMobile;
    }

    public void setmIsMobile(boolean mIsMobile) {
        this.mIsMobile = mIsMobile;
    }

    public void closemTimer() {
        this.mTimer.cancel();
    }

    public static void initImageLoader(Context context) {
//		DisplayImageOptions options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(null)
//				.showImageForEmptyUri(null)
//				.showImageOnFail(null)
//				.cacheInMemory(true)
//				.cacheOnDisk(true)
//				.considerExifParams(true)
//				.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
//				.build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();

//

        ImageLoader.getInstance().init(config.build());

    }

    /*public void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .memoryCacheExtraOptions(480, 800)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13)
                .discCache(
                        new UnlimitedDiscCache(new File(Util
                                .getExternalStorageDirectory())))
                .discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .imageDownloader(new BaseImageDownloader(context))
                .imageDecoder(new BaseImageDecoder(true))
                .defaultDisplayImageOptions(mOptions).build();
        ImageLoader.getInstance().init(config);
    }

    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(null)
                // 正在加载
                .showImageForEmptyUri(R.drawable.failure)
                // url为空
                .showImageOnFail(R.drawable.failure)
                // 加载失败
                .cacheInMemory(true).cacheOnDisc(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        return options;
    }
*/
    // 极光推送
    private static final String TAG = "EHomeApplication";


    @Override
    public void onCreate() {
        // testPrintErrLog();
        // init(getApplicationContext());
        super.onCreate();
        jvActivityStack = new Stack<BaseJVActivity>();
        activityStack = new Stack<Activity>();
        statusHashMapString = new HashMap<String, String>();
        statusHashMapInteger = new HashMap<String, Integer>();
        statusHashMapBoolean = new HashMap<String, Boolean>();
        mContext = getApplicationContext();
        currentNotifyer = null;
        LogcatHelper.getInstance(this).start();
        Log.d(TAG, "[ExampleApplication] onCreate");
//		mManager = new DBManager(mContext);
        mIsDownload = false;
//		OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        OkHttpUtils.getInstance().setConnectTimeout(100000, TimeUnit.MILLISECONDS);
        initErrorHandler();
        MyLog.delFile();

        if (!SYS_EMUI.equals(PhoneBrand.getSystem())) {
            //小米推送
            if (shouldInit()) {
                MiPushClient.registerPush(this, APP_ID, APP_KEY);
            }
        }

        SDKInitializer.initialize(getApplicationContext());
        initImageLoader(getApplicationContext());
//		mOptions = setDisplayImageOptions();
//		initImageLoader(getApplicationContext());

        JPushInterface.init(this); // 初始化 JPush
        JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志

        // 自身的Application
        singleton = this;

//		 initVoipSDK();

        // 注册 wifi+3/4g网络广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);// 整个网格连接状态改变（wifi/34g）
        this.registerReceiver(wifBC, filter);
    }

    private void initErrorHandler() {
        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }


    private static String PATH_LOGCAT;

    public void init(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_LOGCAT = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "logfile.log";
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath()
                    + File.separator + "logfile.log";
        }
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void counter() {
    }

    public void clearData() {
        mNow_Versions = null;
        mAc_List = null;
        mComplaiBean = null;
        mMaintainBean = null;
        LogUtils.i("登出");
        JPushInterface.stopPush(getApplicationContext());
        setTag(1);
        setTag(2);
    }

    public void setTag(int type) {
        int action = -1;
        boolean isAliasAction = false;
        switch (type) {
            case 1://删除所有tag
                action = ACTION_CLEAN;
                break;
            case 2:
                isAliasAction = true;
                action = ACTION_DELETE;
                break;
        }
        TagAliasOperatorHelper.TagAliasBean tagAliasBean = new TagAliasOperatorHelper.TagAliasBean();
        tagAliasBean.action = action;
        sequence++;
        tagAliasBean.isAliasAction = isAliasAction;
        TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(), sequence, tagAliasBean);
    }

    @Override
    public void onTerminate() {
        freeMe();
        // 程序终止的时候执行
        LogcatHelper.getInstance(this).stop();
        AppExit();
        offLine();
        mMap.clear();
        JPushInterface.stopPush(getApplicationContext());
        Log.d("退出app", "onTerminate");
        super.onTerminate();
    }

    protected void freeMe() {
        AccountServiceImpl.getInstance().release();
        System.exit(0);//关闭JVM清除内存，否则term（）执行后，再启动程序账号库无法正常使用。
        JniUtil.stopSearchLan();
    }

    public void offLine() {
        if (null == mCurrentUser) {
            return;
        }
        Intent intent = new Intent("HeartbeatService");
//		stopService(intent);
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        OkHttp.get(mContext, ConnectPath.REPETITIONLOGIN_PATH, params, new ConciseStringCallback(getApplicationContext(), new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }));
    }

    private static Stack<Activity> activityStack;
    private static EHomeApplication singleton;

    public static Stack<Activity> getActivityStack() {
        return activityStack;
    }

    public static void setActivityStack(Stack<Activity> activityStack) {
        EHomeApplication.activityStack = activityStack;
    }

    // Returns the application instance
    public static EHomeApplication getInstance() {
        return singleton;
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
            mMap.clear();
        } catch (Exception e) {
        }
    }

//	public static void initVoipSDK() {
//
//		if (!ECDevice.isInitialized()) {
//			ECDevice.initial(singleton, new ECDevice.InitListener() {
//				@Override
//				public void onInitialized() {
//					// SDK已经初始化成功
//					ECInitParams params = buildParams();
//					if (params.validate()) {
//						// 判断注册参数是否正确
//						ECDevice.login(params);
//					}
//				}
//
//				@Override
//				public void onError(Exception exception) {
//					System.out.println("ex : " + exception.getMessage());
//					LogUtils.i("云通讯：" + exception.getMessage());
//				}
//			});
//		} else {
//			loginAuto();
//		}
//
//	}
//
//	private static void loginAuto() {
//		ECInitParams params = ECInitParams.createParams();
//		params.setUserid(JPushInterface.getRegistrationID(singleton));
//		params.setAppKey("aaf98f8953cadc690153e5b748654ea9");
//		params.setToken("df8b3eca32b040b603c35e3f304857f5");
//		params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
//		params.setMode(ECInitParams.LoginMode.AUTO);
//
//		ConnectListener();
//
//		if (params.validate()) {
//			// 判断注册参数是否正确
//			ECDevice.login(params);
//		}
//	}
//
//	private static ECInitParams buildParams() {
//		ECInitParams params = ECInitParams.createParams();
//		// 自定义登录方式：
//		// 测试阶段Userid可以填写手机
//		// params.setUserid(JPushInterface.getRegistrationID(getApplicationContext()));
//		// Random ran = new Random();
//		// String id = String.valueOf(ran.nextInt(99999999) * 10000000L
//		// + ran.nextInt(99999999));
//		// LogUtils.i("id:" + id);
//		// params.setUserid(id);
//		params.setUserid(JPushInterface.getRegistrationID(singleton));
//		// params.setUserid("18711018824");
//		params.setAppKey("aaf98f8953cadc690153e5b748654ea9");
//		params.setToken("df8b3eca32b040b603c35e3f304857f5");
//		// 设置登陆验证模式（是否验证密码）NORMAL_AUTH-自定义方式
//		params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
//		// 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
//		// 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
//		// 3 LoginMode（强制上线：FORCE_LOGIN 默认登录：AUTO）
//		params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
//		ConnectListener();
//
//		return params;
//	}
//
//	private static void ConnectListener() {
//		ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
//			public void onConnect() {
//				// 兼容4.0，5.0可不必处理
//
//			}
//
//			@Override
//			public void onDisconnect(ECError error) {
//				// 兼容4.0，5.0可不必处理
//			}
//
//			@Override
//			public void onConnectState(ECDevice.ECConnectState state,
//					ECError error) {
//				// System.out.println("onDisconnect : " + error.errorMsg);
//				LogUtils.i("云通讯 ： state--" + state);
//				// BaseUtils.showLongToast(singleton, "云通讯 ："+state);
//				if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
//
//					LogUtils.i("云通讯 ： 连接错误代码  -  " + error.errorMsg);
//					if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
//						// 账号异地登陆
//						LogUtils.i("云通讯 ： 账号异地登陆");
//					} else {
//						// 连接状态失败
//						LogUtils.i("云通讯 ： 连接状态失败    " + error.errorMsg);
//					}
//					return;
//				} else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
//					// 登陆成功
//					LogUtils.i("云通讯 ： 登陆成功");
//				}
//			}
//		});
//	}

    // wifi+3g网络状态广播接收器
    private BroadcastReceiver wifBC = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);// 获取WIFI连接状态
            mIsWifi = wifi.isConnected();
            // 显示3g网络连接状态
            NetworkInfo mobile = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            mIsMobile = mobile.isConnected();
            // networkState();
            if (!(mIsWifi || mIsMobile)) {
//                BaseUtils.showLongToast(getApplicationContext(), "网络异常，请检查网络");
//				DialogShow.showHintDialog(getApplicationContext(), "网络异常，请检查网络");
            }

        }

    };

    @Override
    public void onNotify(int what, int arg1, int arg2, Object obj) {

    }

    /**
     * 修改当前显示的 Activity 引用
     *
     * @param currentNotifyer
     */
    public void setCurrentNotifyer(IHandlerLikeNotify currentNotifyer) {
        this.currentNotifyer = currentNotifyer;
    }


    /**
     * 底层所有的回调接口
     *
     * @param what
     * @param uchType
     * @param channel
     * @param obj
     */

    public synchronized void onJniNotify(int what, int uchType, int channel, Object obj) {

//		String c = Integer.toHexString(what);
//		Log.i(TAG, "onJniNotify: " +c);
//
//		if (what!=170) {
//			LogUtils.e(c);
//		}

        Log.e(TAG, "onJniNotify: what:" + what + ";arg1:" + uchType + ";arg2:" + channel + ";obj:" + obj);
        if (null != currentNotifyer) {
            Looper.prepare();
            currentNotifyer.onNotify(what, uchType, channel, obj);
        } else {
            Log.e("TAG", "currentNotifyer is null!");
        }

    }

    /**
     * 获取String类型的ap
     *
     * @return
     */
    public HashMap<String, String> getStatusHashMapString() {
        return statusHashMapString;
    }

    /**
     * 获取Integer类型的Map
     *
     * @return
     */
    public HashMap<String, Integer> getStatusHashMapInteger() {
        return statusHashMapInteger;
    }

    /**
     * 获取boolean类型的Map
     *
     * @return
     */
    public HashMap<String, Boolean> getStatusHashMapBoolean() {
        return statusHashMapBoolean;
    }


    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * activity 入栈
     *
     * @param activity
     */
    public void push(BaseJVActivity activity) {
        jvActivityStack.push(activity);
    }

    /**
     * activity 出栈
     */
    public BaseJVActivity pop() {
        return (false == jvActivityStack.isEmpty()) ? jvActivityStack.pop() : null;

    }

    private HashMap<String, String> statusHashMapString;
    private HashMap<String, Integer> statusHashMapInteger;
    private HashMap<String, Boolean> statusHashMapBoolean;

    private Stack<BaseJVActivity> jvActivityStack;


    public IHandlerLikeNotify getCurrentNotifyer() {
        return currentNotifyer;
    }
}
