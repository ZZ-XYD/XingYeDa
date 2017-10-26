package com.xingyeda.ehome;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xingyeda.ehome.Service.KeepLiveReceiver;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.base.PhoneBrand;
import com.xingyeda.ehome.bean.UserInfo;
import com.xingyeda.ehome.door.DoorFragment;
import com.xingyeda.ehome.http.ConnectHttpUtils;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.life.LifeFragment;
import com.xingyeda.ehome.menu.MeFragment;
import com.xingyeda.ehome.tenement.TenementFragment;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.ldl.okhttp.OkHttpUtils;
import com.xingyeda.ehome.zhibo.ShareFragment;

import static com.xingyeda.ehome.base.PhoneBrand.SYS_EMUI;


/**
 * @author 李达龙
 * @ClassName: ActivityHomepage
 * @Description: 主页面
 * @date 2016-7-6
 */
public class ActivityHomepage extends FragmentActivity {

    public static boolean isFlHint = true;
    private boolean afterOnSaveInstanceState = false;

    @Bind(R.id.tabhost)
    public FragmentTabHost mTabHost;
    private String mTabTexts[] = {"门禁", "物业", "直播", "我"};
    private int mTabImage[] = {R.drawable.door_image, R.drawable.property_image, R.drawable.life_image, R.drawable.me_image};
    @SuppressWarnings("rawtypes")
    private Class mFragmentArray[] = {DoorFragment.class, TenementFragment.class, ShareFragment.class, MeFragment.class};
    public static final int DEFAULTCOMMUNITY = 100;
    private EHomeApplication mApplication;
    private Context mContext = this;
    private KeepLiveReceiver keepLiveReceiver;
//	public DBManager mDbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_homepage);

        MyLog.i(this.getClass().getSimpleName() + "启动");

        SharedPreUtil.put(mContext, "isMenuHint", true);
//		mDbManager = new DBManager(this);
        ButterKnife.bind(this);
        mApplication = (EHomeApplication) getApplication();
        mApplication.addActivity(this);


        tabHost();
        SharedPreUtil.put(mContext, "isLife_Upload", true);
        SharedPreUtil.put(mContext, "isTenement_Upload", true);
        SharedPreUtil.put(mContext, "isDoor_Upload", true);

        if (mApplication.getmCurrentUser() != null) {
            mApplication.setmAc_List(HomepageHttp.annunciate(SharedPreUtil.getString(mContext, "userId", ""), mContext));
            mApplication.setmAd(HomepageHttp.ad(mContext));
            if (mApplication.getmCurrentUser().getmXiaoqu() == null) {
                mApplication.setmLife_List(HomepageHttp.life("", mContext));
            } else {
                mApplication.setmLife_List(HomepageHttp.life(mApplication.getmCurrentUser().getmXiaoqu().getmCommunityId(), mContext));
            }
            if (mApplication.getmCurrentUser().getmHeadPhotoUrl() == null || mApplication.getmCurrentUser().getmHeadPhoto() != null) {
                return;
            } else {
                if (mApplication.getmCurrentUser().getmHeadPhotoUrl().startsWith("http")) {
                    HomepageHttp.head(mContext, mApplication.getmCurrentUser().getmHeadPhotoUrl(), mApplication);
                }
            }
            HomepageHttp.menuSet(mContext, SharedPreUtil.getString(mContext, "userId", ""));
        }
        // 初始化
        this.init();
        // 加载数据
        this.event();
        // 版本更新
        this.versionsUpdate();


        keepLiveReceiver = new KeepLiveReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        registerReceiver(keepLiveReceiver, intentFilter);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void tabHost() {
        mTabHost.setup(mContext, getSupportFragmentManager(), R.id.maincontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i < mTabTexts.length; i++) {
            TabSpec spec = mTabHost.newTabSpec(mTabTexts[i]).setIndicator(
                    getView(i));

            mTabHost.addTab(spec, mFragmentArray[i], null);
        }
    }

    private View getView(int i) {
        // 取得布局实例
        View view = View.inflate(mContext, R.layout.tabcontent, null);

        // 取得布局对象
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        TextView textView = (TextView) view.findViewById(R.id.text);

        // 设置图标
        imageView.setImageResource(mTabImage[i]);
        // 设置标题
        textView.setText(mTabTexts[i]);

        return view;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i(this.getClass().getSimpleName() + "销毁");
        OkHttpUtils.getInstance().cancelTag(this);
        ButterKnife.unbind(this);
        EHomeApplication.getInstance().finishActivity(this);
        unregisterReceiver(keepLiveReceiver);
    }

    @Override
    protected void onResume() {
//		initVoipSDK();
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    private void versionsUpdate() {
        if (SharedPreUtil.getBoolean(ActivityHomepage.this, "isUpdate")) {
            SharedPreUtil.put(ActivityHomepage.this, "isUpdate", false);
            // 检查更新
            ConnectHttpUtils.inspectUpdate(ActivityHomepage.this, 0);
        }
    }

    @SuppressLint("ResourceAsColor")
    @SuppressWarnings("static-access")
    private void init() {
        if (0 != SharedPreUtil.getLong(mContext, "time_difference")) {
            OkHttp.get(mContext, ConnectPath.SYSTEMTIME_PATH, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        java.util.Date systemTime = sdf.parse(response.has("obj") ? response.getString("obj") : "");

                        Date curDate = new Date(System.currentTimeMillis());
                        SharedPreUtil.put(mContext, "time_difference", systemTime.getTime() - curDate.getTime());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }));
        }


        // 销毁之前的界面
        List<Activity> list = new ArrayList<Activity>();
        for (Activity activity : mApplication.getActivityStack()) {
            if (activity.getClass().equals(ActivityHomepage.class)) {
                break;
            } else {
                list.add(activity);
            }
        }
        for (Activity activity : list) {
            mApplication.finishActivity(activity);
        }


        // 心跳协议
//		 Intent serviceIntent = new Intent("HeartbeatService");
//		 serviceIntent.putExtra("url", ConnectPath.KEEPLIVE_PATH);
//		 serviceIntent.putExtra("uid", SharedPreUtil.getString(mContext,
//		 "userId"));
//		 startService(serviceIntent);


        if (null != mApplication.getmCurrentUser()) {
            String mipush = null;
            UserInfo userInfo = mApplication.getmCurrentUser();
            if (!SYS_EMUI.equals(PhoneBrand.getSystem())) {
                MiPushClient.setAlias(mContext, userInfo.getmPhone(), null);
                MiPushClient.subscribe(mContext, "p_" + userInfo.getmPhone(), null);
                MiPushClient.subscribe(mContext, "n_" + userInfo.getmUsername(), null);
                mipush = userInfo.getmPhone() + "  p_" + userInfo.getmPhone() + " n_" + userInfo.getmUsername();
            }

            Set<String> set = new HashSet<String>();
            set.add("p_" + userInfo.getmPhone());
            set.add("n_" + userInfo.getmUsername());
            if (userInfo.getmXiaoqu() != null) {
                set.add("x_" + userInfo.getmXiaoqu().getmCommunityId());
                set.add("q_" + userInfo.getmXiaoqu().getmPeriodsId());
                set.add("d_" + userInfo.getmXiaoqu().getmUnitId());
                set.add("m_" + userInfo.getmXiaoqu().getmHouseNumberId());

                if (!SYS_EMUI.equals(PhoneBrand.getSystem())) {
                    MiPushClient.subscribe(mContext, "x_" + userInfo.getmXiaoqu().getmCommunityId(), null);
                    MiPushClient.subscribe(mContext, "q_" + userInfo.getmXiaoqu().getmPeriodsId(), null);
                    MiPushClient.subscribe(mContext, "d_" + userInfo.getmXiaoqu().getmUnitId(), null);
                    MiPushClient.subscribe(mContext, "m_" + userInfo.getmXiaoqu().getmHouseNumberId(), null);
                    mipush = mipush + "x_" + userInfo.getmXiaoqu().getmCommunityId() + " q_" + userInfo.getmXiaoqu().getmPeriodsId() + " d_" + userInfo.getmXiaoqu().getmUnitId() + " m_" + userInfo.getmXiaoqu().getmHouseNumberId();
                }
            }
            if (!SYS_EMUI.equals(PhoneBrand.getSystem())) {
                SharedPreUtil.put(mContext, "mipush", mipush);
            }
            boolean isStop = JPushInterface.isPushStopped(this);

            if (isStop)
                JPushInterface.resumePush(this);

            isStop = JPushInterface.isPushStopped(this);
            PushAliasAndTags pushAliasAndTags = new PushAliasAndTags(userInfo.getmPhone(), set);
            SharedPreUtil.put(mContext, "jpush", userInfo.getmPhone() + set.toString());
//			JPushinit(set,userInfo.getmRemarksPhone());
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, pushAliasAndTags));
        }

    }


    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "设置标记和别名成功";
                    LogUtils.i(logs);
                    MyLog.i("极光初始化成功");
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    SharedPreUtil.put(mContext, "push", code + "   " + alias + "   " + tags);
                    break;
                case 6002:
                    logs = "由于超时未能设置别名和标记。60年代后再试一次。";
                    // 延迟 60 秒来调用 Handler 设置别名
                    PushAliasAndTags bean = new PushAliasAndTags(alias, tags);
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, bean), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
            }
//			ExampleUtil.showToast(logs, getApplicationContext());
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    LogUtils.i("Set alias in handler");
                    // 调用 JPush 接口来设置别名。
                    PushAliasAndTags bean = (PushAliasAndTags) msg.obj;
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            bean.getAlias(),
                            bean.getTags(),
                            mAliasCallback);
                    break;
                default:
                    LogUtils.i("Unhandled msg - " + msg.what);
            }
        }
    };

    private void event() {

    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        afterOnSaveInstanceState = true;
//    }

    // 监听返回按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // 把返回键设置成home键
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

class PushAliasAndTags {
    private String Alias;
    private Set<String> Tags;

    public PushAliasAndTags(String alias, Set<String> tags) {
        Alias = alias;
        Tags = tags;
    }

    public String getAlias() {
        return Alias;
    }

    public void setAlias(String alias) {
        Alias = alias;
    }

    public Set<String> getTags() {
        return Tags;
    }

    public void setTags(Set<String> tags) {
        Tags = tags;
    }
}
