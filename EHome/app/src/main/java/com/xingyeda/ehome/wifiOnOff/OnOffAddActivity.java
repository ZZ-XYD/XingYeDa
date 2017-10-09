package com.xingyeda.ehome.wifiOnOff;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aochn.cat110appsdk.Cat110SDKActivity;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.id;
import static com.xingyeda.ehome.base.BaseActivity.mEhomeApplication;

public class OnOffAddActivity extends Cat110SDKActivity {
    @Bind(R.id.on_off_wifi)//wifi名字
            EditText onOffWifi;
    @Bind(R.id.on_off_pwd)//wifi密码
            EditText onOffPwd;
    @Bind(R.id.smart_home_register)//注册
            LinearLayout register;
    @Bind(R.id.on_off_add_step1)//设置wifi界面
            LinearLayout onOffAddStep1;
    @Bind(R.id.on_off_time)//查找倒计时
            TextView onOffTime;
    @Bind(R.id.on_off_add_step2)//查找添加界面
            RelativeLayout onOffAddStep2;
    @Bind(R.id.on_off_next)
    TextView onOffNext;
    @Bind(R.id.on_off_add_hint)
    ImageView onOffAddHint;
    @Bind(R.id.smart_home_code)
    EditText smartHomeCode;

    private int page = 0;
    private InputMethodManager imm;
    public Context mContext = this;
    private static final String TAG = "OnOffAddActivity";
    private long mQueryId;
    private EHomeApplication mApplication;
    private boolean isBind = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSdk();
        setContentView(R.layout.activity_on_off_add);
        ButterKnife.bind(this);
        mApplication = (EHomeApplication) getApplication();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        onOffWifi.setText(getConnectWifiSsid());
        //设置失去焦点
        onOffWifi.clearFocus();
        onOffWifi.setFocusable(false);
        if (SharedPreUtil.getBoolean(mContext,"onOffLogin")) {
            if (page == 0) {
                onOffNext.setVisibility(View.VISIBLE);
                page++;
                register.setVisibility(View.GONE);
                onOffAddStep1.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initSdk() {
        setEventListener(new OnEventListener() {
            public void onConnectionStatusChanged(int status,
                                                  String reason,
                                                  int refuseReason,
                                                  long flag) {
                if (CONNECTION_STATUS_LOGIN_SUCCESS == status) {
                    Log.i(TAG, "登陆成功");
                } else {
                    Log.i(TAG, "登陆失败");
                    //((Button)findViewById(R.id.btnLogin)).setText("Login");
                }
                Log.i(TAG, "连接状态在改变 status:" + status + " reason:" + reason + " refuseReason:" + refuseReason + " flag:" + flag);
            }

            public void onRefreshLoginName(String loginName) {
                Log.i(TAG, "刷新登录名 loginName:" + loginName + "");
            }

            public void onRefreshDeviceList() {
                Log.i(TAG, "刷新设备列表 deviceCount:" + getDeviceCount());
                int i;
                for (i = 0; i < getDeviceCount(); ++i) {
                    Device item = getDeviceItem(i);
                    Log.i(TAG, "刷新设备列表 device[" + i + "]:" + item.pid);
                }
            }

            public void onRefreshMsgList() {
                Log.i(TAG, "刷新信息列表");
            }

            public void onRefreshConfigList() {
                Log.i(TAG, "刷新信息列表");
            }

            public void onRefreshMsgCount() {
                Log.i(TAG, "刷新信息计数");
            }

            public void onRefreshConfigItem(long pid, String configName) {
                Log.i(TAG, "更新配置项 pid:" + pid + " configName:" + configName);
                if (configName.equals("_online")) {
                    //添加设备
                    if (isBind) {
                        isBind = false;
                        AddSound(pid+"");
                    }
                }
            }

            public void onInitComplete() {
                Log.i(TAG, "初始化完成");
            }

            public void onLocalDataPrepared() {
                Log.i(TAG, "本地数据准备");
            }

            public void onQuit() {
                Log.i(TAG, "onQuit");
            }
        });
    }

    @OnClick({R.id.on_off_add_back, R.id.on_off_next, R.id.smart_home_submit, R.id.smart_home_getCode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.on_off_add_back:
                if (page == 1) {
                    if (!SharedPreUtil.getBoolean(mContext,"onOffLogin")) {
                    page--;
                    onOffNext.setVisibility(View.GONE);
                    register.setVisibility(View.VISIBLE);
                    onOffAddStep1.setVisibility(View.GONE);
                    }else {
                        BaseUtils.startActivity(mContext, SmartHomeActivity.class);
                    }
                } else if (page == 2) {
                    onOffNext.setVisibility(View.VISIBLE);
                    page--;
                    onOffAddStep1.setVisibility(View.VISIBLE);
                    onOffAddStep2.setVisibility(View.GONE);
                } else {
                    BaseUtils.startActivity(mContext, SmartHomeActivity.class);
                }
                break;
            case R.id.on_off_next:
                //关闭软键盘
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                if (page == 1) {
                    onOffNext.setText("重新绑定");
                    bind();
                    page++;
                    onOffAddStep1.setVisibility(View.GONE);
                    onOffAddStep2.setVisibility(View.VISIBLE);
                }else if (page == 2){
                    bind();
                }
                break;
            case R.id.smart_home_submit:

                submitAccountRegister(mApplication.getmCurrentUser().getmPhone(), SharedPreUtil.getString(mContext, "userPwd"), mQueryId, smartHomeCode.getText().toString(), new OnSubmitAccountRegisterResultListener() {
                    @Override
                    public void onResult(int result, long newUid) {
                        if (result == 0) {
                            login();
                            //关闭软键盘
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                            }

                            if (page == 0) {
                                onOffNext.setVisibility(View.VISIBLE);
                                page++;
                                register.setVisibility(View.GONE);
                                onOffAddStep1.setVisibility(View.VISIBLE);
                            }
                        } else {
                            DialogShow.showHintDialog(mContext, "验证码错误，请重新输入");
                        }
                    }
                });
                break;
            case R.id.smart_home_getCode:
                sendAccountRegister(mApplication.getmCurrentUser().getmPhone(), "", new OnSendAccountRegisterResultListener() {
                    @Override
                    public void onResult(int result, long queryId, int durationSecs, int captchaPhase) {
                        if (result == 0) {
                            mQueryId = queryId;
                        }
                    }

                });
                break;
        }
    }
    private void bind(){
        Log.i(TAG, "wifi :"+onOffWifi.getText().toString() + "   pwd"+onOffPwd.getText().toString());
        startBind(onOffWifi.getText().toString(),onOffPwd.getText().toString(),
                new OnBindStatusListener() {
                    public void onCountdown(int remainSeconds) {
                        onOffTime.setText("onCountdown remainSeconds:" + remainSeconds);
                        Log.i(TAG, "倒计时读秒:" + remainSeconds);
                    }
                    public void onDeviceConnectedWithRouter() {
                        Log.i(TAG, "设备路由器");
                    }
                    public void onBindResult(int result) {
                        if (result==-1) {
                            BaseUtils.showLongToast(mContext,"绑定失败");
                        }
                        Log.i(TAG, "绑定结果:" + result);
                    }
                });
//        startBind("xyd", "22393818",
//                new OnBindStatusListener() {
//                    public void onCountdown(int remainSeconds) {
////                        ((TextView)findViewById(R.id.txtBindStatus)).setText("onCountdown remainSeconds:" + remainSeconds);
//                        Log.i(TAG, "倒计时读秒:" + remainSeconds);
//                    }
//                    public void onDeviceConnectedWithRouter() {
////                        ((TextView)findViewById(R.id.txtBindStatus)).setText("设备路由器");
//                        Log.i(TAG, "设备路由器");
//                    }
//                    public void onBindResult(int result) {
////                        ((TextView)findViewById(R.id.txtBindStatus)).setText("绑定结果:" + result);
//                        Log.i(TAG, "绑定结果:" + result);
//                    }
//                });
    }

    private void login() {

        String oemCert = "1684ea8f2b44aa8e233b019f3e7e190056402e77d22a420fc1111d1395b79820d3412fff71bd4981e3265d758b94bd42cdee8d9c141ffce2167a0cae7897ab59eb7606442e20d180b00e13b43305e7a815a36f9e3cfc02018e4f4000a6b7876d7b24fabf9e796ca8b70473d71f7dd6380d11fdbe08c4f500fbf7425af47ce0c2";
        String username = mApplication.getmCurrentUser().getmPhone();
        String password = SharedPreUtil.getString(mContext, "userPwd");
        String captcha = "";
        login(oemCert, username, password, captcha);
    }

    private String getConnectWifiSsid() {
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            if (wifiInfo != null) {
                String s = wifiInfo.getSSID();
                if (s.length() > 2 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"') {
                    return s.substring(1, s.length() - 1);
                }
            }
        }
        return "";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (page == 1) {
                if (!SharedPreUtil.getBoolean(mContext,"onOffLogin")) {
                    page--;
                    onOffNext.setVisibility(View.GONE);
                    register.setVisibility(View.VISIBLE);
                    onOffAddStep1.setVisibility(View.GONE);
                }else {
                    BaseUtils.startActivity(mContext, SmartHomeActivity.class);
                }
            } else if (page == 2) {
                page--;
                onOffAddStep1.setVisibility(View.VISIBLE);
                onOffAddStep2.setVisibility(View.GONE);
            } else {
                BaseUtils.startActivity(mContext, SmartHomeActivity.class);
            }
        }
        return false;
    }
    private void AddSound(final String id){
        final Map<String, String> params = new HashMap<String, String>();
        params.put("uid", mApplication.getmCurrentUser().getmId());
        params.put("num", id);
        params.put("type", "switch");

        OkHttp.get(mContext, ConnectPath.ADD_CAMERA, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                    BaseUtils.startActivity(mContext, SmartHomeActivity.class);
            }
        }));
    }

}
