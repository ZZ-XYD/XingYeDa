package com.xingyeda.ehome;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

//


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.xingyeda.ehome.Service.OpenDoorService;
import com.xingyeda.ehome.Service.SharePasswordService;
import com.xingyeda.ehome.adapter.AdapterGuidePager;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.AESUtils;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.SdkErrorCode;

/**
 * @author 李达龙
 * @ClassName: ActivityGuide
 * @Description: 引导页
 * @date 2016-7-6
 */
@SuppressLint("SimpleDateFormat")
public class ActivityGuide extends BaseActivity {


    @Bind(R.id.guide_viewpager)
    ViewPager mGuide_ViewPager;
    @Bind(R.id.guide_point)
    LinearLayout mGuide_point;
    @Bind(R.id.guide_into)
    TextView mGuide_into;


    private int[] mImgIds = new int[]{R.mipmap.page1, R.mipmap.page2, R.mipmap.page3};
    private ArrayList<ImageView> mImageViews;

    private AdapterGuidePager mAdapter;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startIntent = new Intent(mContext, SharePasswordService.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", mEhomeApplication.getmCurrentUser().getmId());
        startIntent.putExtras(bundle);
        startService(startIntent);

        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }


        SharedPreUtil.put(mContext, "time_difference", 0l);


        String type = getIntent().getStringExtra("type");
        initVoipSDK();

        if (type != null) {
            if (type.equals("openDoor")) {
                Intent intent = new Intent(mContext, OpenDoorService.class);
                getSystemTime();
                startService(intent);
                ActivityGuide.this.finish();
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
        } else {
            setContentView(R.layout.activity_guide);
            ButterKnife.bind(this);
            if (!SharedPreUtil.getBoolean(mContext, "isFirstRun")) {
                getSystemTime();
                BaseUtils.startActivity(mContext, ActivityLogo.class);
                ActivityGuide.this.finish();
                return;
            }
            init();
            getSystemTime();
            imageData();
            event();
        }


    }

    private void getSystemTime() {
//		Map<String,String> params = new HashMap<>();
//		String id=null;
//		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd");
//		String date = sDateFormat.format(new java.util.Date());
//
//		try {
//		 id = AESUtils.Encrypt("3818app!"+date,"2239381822393818");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//			params.put("id", id.replaceAll("\n",""));
//		OkHttp.get(mContext,ConnectPath.CREATE_KEY,params,new BaseStringCallback(mContext, new CallbackHandler<String>() {
//			@Override
//			public void onResponse(JSONObject response) {
//				try {
//					mEhomeApplication.setKey(response.has("obj")?response.getString("obj"):"");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//
//			}
//
//			@Override
//			public void parameterError(JSONObject response) {
//
//			}
//
//			@Override
//			public void onFailure() {
//
//			}
//		}));
        OkHttp.get(mContext, ConnectPath.SYSTEMTIME_PATH, new BaseStringCallback(mContext, new CallbackHandler<String>() {

            @Override
            public void parameterError(JSONObject response) {
                BaseUtils.startActivity(ActivityGuide.this, ActivityLogo.class);
                ActivityGuide.this.finish();
            }

            @Override
            public void onResponse(JSONObject response) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    Date systemTime = sdf.parse(response.has("obj") ? response.getString("obj") : "");

                    Date curDate = new Date(System.currentTimeMillis());
                    LogUtils.i("服务器时间  : " + systemTime + "手机时间 : " + curDate + "时间差 : " + (systemTime.getTime() - curDate.getTime()));
                    SharedPreUtil.put(mContext, "time_difference", systemTime.getTime() - curDate.getTime());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure() {

            }
        }));
    }

    private void init() {
        JPushInterface.setAliasAndTags(mContext, "ALL", new HashSet<String>(),
                new TagAliasCallback() {
                    @Override
                    public void gotResult(int arg0, String arg1,
                                          Set<String> arg2) {
                        LogUtils.d(arg0 + "");
                        LogUtils.d(arg1);
                        LogUtils.d(arg2.toString());
                    }
                });


    }

    private void imageData() {
        mImageViews = new ArrayList<ImageView>();
        for (int i = 0; i < mImgIds.length; i++) {
            ImageView imageview = new ImageView(mContext);
            imageview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageview.setScaleType(ScaleType.FIT_XY);
            imageview.setBackgroundResource(mImgIds[i]);
            mImageViews.add(imageview);

        }
        for (int i = 0; i < mImgIds.length; i++) {
            View view = new View(mContext);
            view.setEnabled(true);
            view.setBackgroundResource(R.drawable.guide_point);
            LayoutParams params = new LayoutParams(
                    30, 30);
            params.rightMargin = 10;
            view.setId(i);
            mGuide_point.addView(view, params);

            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    for (int i = 0; i < mGuide_point.getChildCount(); i++) {
                        if (id == mGuide_point.getChildAt(i).getId()) {
                            mGuide_point.getChildAt(i).setEnabled(false);
                        } else {
                            mGuide_point.getChildAt(i).setEnabled(true);
                        }
                    }
                    mGuide_ViewPager.setCurrentItem(id);
                }

            });
        }

        mGuide_point.getChildAt(0).setEnabled(false);

        mAdapter = new AdapterGuidePager(mGuide_ViewPager, mImageViews);
        mGuide_ViewPager.setAdapter(mAdapter);

        mHandler.postDelayed(new AdRunnable(), 3000);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
        }

        ;
    };

    private void event() {
        mGuide_ViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageSelected(int index) {
                for (int i = 0; i < mGuide_point.getChildCount(); i++) {
                    if (i == index) {
                        mGuide_point.getChildAt(i).setEnabled(false);
                    } else {
                        mGuide_point.getChildAt(i).setEnabled(true);
                    }
                }
                if (index == (mImgIds.length - 1)) {
                    mGuide_into.setVisibility(View.VISIBLE);
                    mGuide_point.setVisibility(View.GONE);
                } else {
                    mGuide_into.setVisibility(View.GONE);
                    mGuide_point.setVisibility(View.VISIBLE);
                }

            }

        });
    }

    private class AdRunnable implements Runnable {

        @Override
        public void run() {
            if (mGuide_ViewPager == null)
                return;

            int index = mGuide_ViewPager.getCurrentItem();
            index++;

            mGuide_ViewPager.setCurrentItem(index);

            mHandler.postDelayed(this, 5000);
        }

    }

    @OnClick(R.id.guide_into)
    public void OnClick() {
        SharedPreUtil.put(mContext, "isFirstRun", false);
        SharedPreUtil.put(mContext, "shortcut_icon", true);
        BaseUtils.startActivity(ActivityGuide.this, ActivityLogo.class);
        ActivityGuide.this.finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(mContext);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(mContext);
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void initVoipSDK() {

        if (!ECDevice.isInitialized()) {
            ECDevice.initial(mContext, new ECDevice.InitListener() {
                @Override
                public void onInitialized() {
                    // SDK已经初始化成功
                    ECInitParams params = buildParams();
                    if (params.validate()) {
                        // 判断注册参数是否正确
                        ECDevice.login(params);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    System.out.println("ex : " + exception.getMessage());
                    LogUtils.i("云通讯：" + exception.getMessage());
                }
            });
        } else {
            loginAuto();
        }

    }

    private void loginAuto() {
        ECInitParams params = ECInitParams.createParams();
        params.setUserid(JPushInterface
                .getRegistrationID(getApplicationContext()));
        params.setAppKey("aaf98f8953cadc690153e5b748654ea9");
        params.setToken("df8b3eca32b040b603c35e3f304857f5");
        params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        params.setMode(ECInitParams.LoginMode.AUTO);

        eventSdk();

        if (params.validate()) {
            // 判断注册参数是否正确
            ECDevice.login(params);
        }
    }

    private ECInitParams buildParams() {
        ECInitParams params = ECInitParams.createParams();
        // 自定义登录方式：
        // 测试阶段Userid可以填写手机
        params.setUserid(JPushInterface
                .getRegistrationID(getApplicationContext()));
        // Random ran = new Random();
        // String id = String.valueOf(ran.nextInt(99999999) * 10000000L
        // + ran.nextInt(99999999));
        // LogUtils.i("id:" + id);
        // params.setUserid(id);
        // params.setUserid("18711018824");
        params.setAppKey("aaf98f8953cadc690153e5b748654ea9");
        params.setToken("df8b3eca32b040b603c35e3f304857f5");
        // 设置登陆验证模式（是否验证密码）NORMAL_AUTH-自定义方式
        params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        // 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
        // 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
        // 3 LoginMode（强制上线：FORCE_LOGIN 默认登录：AUTO）
        params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);

        eventSdk();
        return params;
    }

    private void eventSdk() {
        ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
            public void onConnect() {
                // 兼容4.0，5.0可不必处理

            }

            @Override
            public void onDisconnect(ECError error) {
                // 兼容4.0，5.0可不必处理
            }

            @Override
            public void onConnectState(ECDevice.ECConnectState state,
                                       ECError error) {
                LogUtils.i("云通讯 ： state--" + state);
                if (state == ECDevice.ECConnectState.CONNECT_FAILED) {
                    LogUtils.i("云通讯 ： 连接错误代码  -  " + error.errorMsg);
                    if (error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                        // 账号异地登陆
                        LogUtils.i("云通讯 ： 账号异地登陆");
                    } else {
                        // 连接状态失败
                        LogUtils.i("云通讯 ： 连接状态失败    " + error.errorMsg);
                    }
                    return;
                } else if (state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
                    // 登陆成功
                    // if (mSpnTitle!=null) {
                    // mSpnTitle.getChildAt(0).setEnabled(true);
                    // }
                    LogUtils.i("云通讯 ： 登陆成功");
                }
            }
        });
    }
}