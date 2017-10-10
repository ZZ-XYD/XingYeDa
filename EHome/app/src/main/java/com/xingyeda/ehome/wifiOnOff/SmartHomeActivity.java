package com.xingyeda.ehome.wifiOnOff;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aochn.cat110appsdk.Cat110SDKActivity;
import com.jovision.account.MaoYanSetActivity;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.OnOffAdaper;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.OnOffBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.DoorFragment;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SpaceItemDecoration;
import com.xingyeda.ehome.view.PercentLinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xingyeda.ehome.base.BaseActivity.mEhomeApplication;


public class SmartHomeActivity extends Cat110SDKActivity {

    private static final String TAG = "SmartHomeActivity";

    @Bind(R.id.smart_home)
    RecyclerView smartHome;//数据列表
    @Bind(R.id.smart_home_show_layout)
    PercentRelativeLayout smartHomeShowLayout;//数据显示界面
    @Bind(R.id.smart_home_add_layout)
    PercentRelativeLayout smartHomeAddLayout;//添加界面
    @Bind(R.id.smart_home_add_layout1)
    PercentLinearLayout smartHomeAddLayout1;//验证界面
    @Bind(R.id.smart_home_code)
    EditText smartHomeCode;//验证码
    @Bind(R.id.smart_home_add_layout2)
    PercentLinearLayout smartHomeAddLayout2;//wifi界面
    @Bind(R.id.on_off_wifi)
    EditText onOffWifi;//wifi名称
    @Bind(R.id.on_off_pwd)
    EditText onOffPwd;//wifi密码
    @Bind(R.id.smart_home_add_layout3)
    PercentRelativeLayout smartHomeAddLayout3;//搜索界面
    @Bind(R.id.on_off_time)
    TextView onOffTime;//倒读秒
    @Bind(R.id.smart_home_getCode)
    TextView smartHomeGetCode;
    @Bind(R.id.no_datas)
    ImageView noDatas;


    private OnOffAdaper mAdapter;
    public Context mContext = this;
    private List<OnOffBean> mDatas;
    private EHomeApplication mApplication;
    private boolean mIsLogined = false;//是否登陆
    private int page = 0;//加载界面的页面数
    private long mQueryId;//验证码
    private InputMethodManager imm;
    private CountDownTimer mTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_home);
        ButterKnife.bind(this);
//        EHomeApplication.getInstance().addActivity(this);
        mApplication = (EHomeApplication) getApplication();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mDatas = new ArrayList<>();
        initSdk();
        smartHome.setLayoutManager(new LinearLayoutManager(this));
        smartHome.addItemDecoration(new SpaceItemDecoration(20));
        login();
        even();
//        init();

    }

    //查询
    private void even() {
//        mDatas.add(new OnOffBean("a", 1147209389));
//        mAdapter = new OnOffAdaper(mContext, mDatas);
//        smartHome.setAdapter(mAdapter);
//        init();
        Map<String, String> params = new HashMap<>();
        params.put("uid", mApplication.getmCurrentUser().getmId());
        OkHttp.get(mContext, ConnectPath.QUERY_ON_OFF, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mDatas.clear();
                    JSONArray jan2 = (JSONArray) response.get("obj");
                    if (jan2 != null && jan2.length() != 0) {
                        for (int i = 0; i < jan2.length(); i++) {
                            OnOffBean bean = new OnOffBean();
                            JSONObject jobj = jan2.getJSONObject(i);
                            bean.setId(jobj.has("serialNumber") ? Integer.valueOf(jobj.getString("serialNumber")) : 0);
                            bean.setName(jobj.has("name") ? jobj.getString("name") : "");
                            mDatas.add(bean);
                        }
                        noDatas.setVisibility(View.GONE);
                        mAdapter = new OnOffAdaper(mContext, mDatas);
                        smartHome.setAdapter(mAdapter);
                        init();
                    } else {
                        noDatas.setVisibility(View.VISIBLE);
                        mAdapter = new OnOffAdaper(mContext, mDatas);
                        smartHome.setAdapter(mAdapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }));
    }


    private void init() {
        //长按
        mAdapter.longItem(new OnOffAdaper.LongItem() {
            @Override
            public void onLongclick(View view, final int position) {
                //删除
                final NormalDialog dialog = DialogShow.showSelectDialog(mContext,"是否删除设备",2,new String[] { getResources().getString(R.string.cancel),getResources().getString(R.string.confirm)});
                dialog.setOnBtnClickL(new OnBtnClickL() {

                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },new OnBtnClickL() {
                    @Override
                    public void onBtnClick() {
                        socketDelete(mDatas.get(position).getId());
                        dialog.dismiss();
                    }
                });
//                socketDelete(mDatas.get(position).getId());
            }
        });
        //开
        mAdapter.smartHomeOn(new OnOffAdaper.ButtonInterface() {
            @Override
            public void onclick(View view, int position) {
                socketOn(mDatas.get(position).getId());
            }
        });
        //关
        mAdapter.smartHomeOff(new OnOffAdaper.ButtonInterface() {
            @Override
            public void onclick(View view, int position) {
                socketOff(mDatas.get(position).getId());
            }
        });
        //设置
        mAdapter.smartHomeSet(new OnOffAdaper.ButtonInterface() {
            @Override
            public void onclick(View view, int position) {
//                BaseUtils.showLongToast(mContext, "设置");
//                socketDelete(mDatas.get(position).getId());
                Bundle bundle = new Bundle();
                bundle.putString("type", "on_off");
                bundle.putString("cameraId", mDatas.get(position).getId()+"");
                BaseUtils.startActivities(mContext, MaoYanSetActivity.class, bundle);

            }
        });
    }

    protected void initSdk() {
        setEventListener(new OnEventListener() {
            public void onConnectionStatusChanged(int status,
                                                  String reason,
                                                  int refuseReason,
                                                  long flag) {
                if (CONNECTION_STATUS_LOGIN_SUCCESS == status) {
                    mIsLogined = true;
                    Log.i(TAG, "登陆成功");
                    if (page == 1) {
                        page = 2;
                        smartHomeAddLayout1.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.VISIBLE);
                    }
                } else {
                    mIsLogined = false;
                    Log.i(TAG, "登陆失败");
                    BaseUtils.showShortToast(mContext,"账户登录失败，请核对后再试");
                    //((Button)findViewById(R.id.btnLogin)).setText("Login");
                }
                Log.i(TAG, "连接状态在改变 status:" + status + " reason:" + reason + " refuseReason:" + refuseReason + " flag:" + flag);
            }

            public void onRefreshLoginName(String loginName) {
                Log.i(TAG, "刷新登录名 loginName:" + loginName + "");
            }

            public void onRefreshDeviceList() {
                Log.i(TAG, "刷新设备列表 deviceCount:" + getDeviceCount());
                long pid = 0;
                for (int i = 0; i < getDeviceCount(); ++i) {
                    Device item = getDeviceItem(i);
//                    if (mDatas!=null&&!mDatas.isEmpty()) {
//                    for (OnOffBean mData : mDatas) {
//                        if (mData.getId() != item.pid) {
//                            if (pid != item.pid) {
//                                pid = item.pid;
//                                AddSound(item.pid + "");
//                            }
//                        }
//                    }
//                    }else{
//                        if (pid != item.pid) {
//                            pid = item.pid;
//                            AddSound(item.pid + "");
//                        }
//                    }
                    Log.i(TAG, "刷新设备列表 device[" + i + "]:" + item.pid);
                }
            }

            //1147209389
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
                    if (mDatas != null && !mDatas.isEmpty()) {
                        for (OnOffBean mData : mDatas){
                            if (mData.getId() != pid) {
                                AddSound(pid + "");
                            }
                        }
                    }else {
                        AddSound(pid + "");
                    }

//                    long pid = 0;
//                    for (int i = 0; i < getDeviceCount(); ++i) {
//                        Device item = getDeviceItem(i);
//                        if (mDatas != null && !mDatas.isEmpty()) {
//                            for (OnOffBean mData : mDatas) {
//                                if (mData.getId() != item.pid) {
//                                    if (pid != item.pid) {
//                                        pid = item.pid;
//                                        AddSound(item.pid + "");
//                                    }
//                                }
//                            }
//                        } else {
//                            if (pid != item.pid) {
//                                pid = item.pid;
//                                AddSound(item.pid + "");
//                            }
//                        }
//                    }
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

    //开
    public void socketOn(int pid) {
        if (pid > 0) {
            Device destDevice = findDeviceByPid(pid);
            sendCommandToDevice(destDevice.pid, destDevice.nativeIndex, "wifi_pluge_control", "\"1\"",
                    new OnSendCommandToDeviceResultListener() {
                        public void onResult(int result) {

                        }
                    });
        }
    }

    //关
    public void socketOff(int pid) {
        if (pid > 0) {
            Device destDevice = findDeviceByPid(pid);
            sendCommandToDevice(destDevice.pid, destDevice.nativeIndex, "wifi_pluge_control", "\"0\"",
                    new OnSendCommandToDeviceResultListener() {
                        public void onResult(int result) {

                        }
                    });
        }
    }

    //删除
    public void socketDelete(final int pid) {
        if (pid > 0) {
            Device item = findDeviceByPid(pid);
            deleteBind(pid, item.nativeIndex,
                    new OnSimpleResultListener() {
                        public void onResult(int result) {
                            if (result == 0) {
//                                MaoYanSetActivity.updateCameraName(mContext, "delete", mDatas.get(position).getId() + "", "");
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
                                params.put("flag", "delete");
                                params.put("num", pid + "");
                                OkHttp.get(mContext, ConnectPath.ADD_CAMERA, params, new ConciseStringCallback(mEhomeApplication.getmContext(), new ConciseCallbackHandler<String>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        even();
                                    }
                                }));
                            }else{
                                BaseUtils.showLongToast(mContext,"删除失败");
                            }
                        }
                    });
        }
    }

    // TODO: Use hashmap, don't use this O(N) function to search device.
    //使用hashmap,不要使用这个O(N)函数来搜索设备
    Device findDeviceByPid(long pid) {
        int i;
        for (i = 0; i < getDeviceCount(); ++i) {
            Device item = getDeviceItem(i);
            if (item.pid == pid) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
//        EHomeApplication.getInstance().finishActivity(this);
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (page == 0) {
                quit();
//                finish();
                BaseUtils.startActivity(mContext, ActivityHomepage.class);
            } else {
                if (mIsLogined) {
                    if (page == 2) {
                        page = 1;
                        smartHomeAddLayout3.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.VISIBLE);
                    } else if (page == 1) {
                        page = 0;
                        smartHomeShowLayout.setVisibility(View.VISIBLE);
                        smartHomeAddLayout.setVisibility(View.GONE);
                        smartHomeAddLayout1.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.GONE);
                        smartHomeAddLayout3.setVisibility(View.GONE);
                    }

                } else {
                    if (page == 3) {
                        page = 2;
                        smartHomeAddLayout3.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.VISIBLE);
                    } else if (page == 2) {
                        page = 0;
                        smartHomeShowLayout.setVisibility(View.VISIBLE);
                        smartHomeAddLayout.setVisibility(View.GONE);
                        smartHomeAddLayout1.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.GONE);
                        smartHomeAddLayout3.setVisibility(View.GONE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                    } else if (page == 1) {
                        page = 0;
                        smartHomeShowLayout.setVisibility(View.VISIBLE);
                        smartHomeAddLayout.setVisibility(View.GONE);
                        smartHomeAddLayout1.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.GONE);
                        smartHomeAddLayout3.setVisibility(View.GONE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                    }

                }
            }
        }
        return false;
    }

//    @OnClick(R.id.smart_home_add)
//    public void onViewClicked() {
////        BaseUtils.startActivity(mContext,OnOffAddActivity.class);
//        startBind("xyd", "22393818",
//                new OnBindStatusListener() {
//                    public void onCountdown(int remainSeconds) {
//                        Log.i(TAG, "倒计时读秒:" + remainSeconds);
//                    }
//
//                    public void onDeviceConnectedWithRouter() {
//                        Log.i(TAG, "设备路由器");
//                    }
//
//                    public void onBindResult(int result) {
//                        Log.i(TAG, "绑定结果:" + result);
//                    }
//                });
//    }

    private void login() {
        String oemCert = "1684ea8f2b44aa8e233b019f3e7e190056402e77d22a420fc1111d1395b79820d3412fff71bd4981e3265d758b94bd42cdee8d9c141ffce2167a0cae7897ab59eb7606442e20d180b00e13b43305e7a815a36f9e3cfc02018e4f4000a6b7876d7b24fabf9e796ca8b70473d71f7dd6380d11fdbe08c4f500fbf7425af47ce0c2";
        String username = mApplication.getmCurrentUser().getmPhone();
        String password ="123456";
        String captcha = "";
        Log.i(TAG, "login username:" + username + "   password" + password);
        login(oemCert, username, password, captcha);
    }

    @OnClick({R.id.smart_home_add, R.id.on_off_add_back, R.id.smart_home_getCode, R.id.smart_home_submit, R.id.smart_home_next, R.id.on_off_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.smart_home_add://添加开关
                onOffWifi.setText(getConnectWifiSsid());
                smartHomeShowLayout.setVisibility(View.GONE);
                smartHomeAddLayout.setVisibility(View.VISIBLE);
                page = 1;
                if (mIsLogined) {
                    smartHomeAddLayout2.setVisibility(View.VISIBLE);
                } else {
                    mTimer = new CountDownTimer(60 * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            smartHomeGetCode.setClickable(false);// 防止重复点击
                            smartHomeGetCode.setText(millisUntilFinished / 1000 + "s");
                        }

                        @Override
                        public void onFinish() {
                            smartHomeGetCode.setClickable(true);// 防止重复点击
                            smartHomeGetCode.setText(R.string.register_getcode_text);
                        }
                    };
                    smartHomeAddLayout1.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.on_off_add_back://返回
                if (mIsLogined) {
                    if (page == 2) {
                        page = 1;
                        smartHomeAddLayout3.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.VISIBLE);
                    } else if (page == 1) {
                        page = 0;
                        smartHomeShowLayout.setVisibility(View.VISIBLE);
                        smartHomeAddLayout.setVisibility(View.GONE);
                        smartHomeAddLayout1.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.GONE);
                        smartHomeAddLayout3.setVisibility(View.GONE);
                    }

                } else {
                    if (page == 3) {
                        page = 2;
                        smartHomeAddLayout3.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.VISIBLE);
                    } else if (page == 2) {
                        page = 0;
                        smartHomeShowLayout.setVisibility(View.VISIBLE);
                        smartHomeAddLayout.setVisibility(View.GONE);
                        smartHomeAddLayout1.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.GONE);
                        smartHomeAddLayout3.setVisibility(View.GONE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                    } else if (page == 1) {
                        page = 0;
                        smartHomeShowLayout.setVisibility(View.VISIBLE);
                        smartHomeAddLayout.setVisibility(View.GONE);
                        smartHomeAddLayout1.setVisibility(View.GONE);
                        smartHomeAddLayout2.setVisibility(View.GONE);
                        smartHomeAddLayout3.setVisibility(View.GONE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                        }
                    }

                }
                break;
            case R.id.smart_home_getCode://获取验证码
                mTimer.start();
                sendAccountRegister(mApplication.getmCurrentUser().getmPhone(), "", new OnSendAccountRegisterResultListener() {
                    @Override
                    public void onResult(int result, long queryId, int durationSecs, int captchaPhase) {
                        if (result == 0) {
                            mQueryId = queryId;
                        }
                    }

                });
                break;
            case R.id.smart_home_submit://注册
                if (mQueryId != 0) {
                    submitAccountRegister(mApplication.getmCurrentUser().getmPhone(), "123456", mQueryId, smartHomeCode.getText().toString(), new OnSubmitAccountRegisterResultListener() {
                        @Override
                        public void onResult(int result, long newUid) {
                            if (result == 0) {
                                login();
                                //关闭软键盘
                                if (imm != null) {
                                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                                }

                                login();

                            } else {
                                DialogShow.showHintDialog(mContext, "验证码错误，请重新输入");
                            }
                        }
                    });
                } else {
                    DialogShow.showHintDialog(mContext, "请先获取验证码");
                }
//                page++;
//                smartHomeAddLayout1.setVisibility(View.GONE);
//                smartHomeAddLayout2.setVisibility(View.VISIBLE);
                break;
            case R.id.smart_home_next://提交wifi
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                bind();
                if (page == 1) {
                    page = 2;
                } else if (page == 2) {
                    page = 3;
                }
                smartHomeAddLayout2.setVisibility(View.GONE);
                smartHomeAddLayout3.setVisibility(View.VISIBLE);
                break;
            case R.id.on_off_next://重新绑定
                bind();
                break;
        }
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

    private void bind() {
        Log.i(TAG, "wifi :" + onOffWifi.getText().toString() + "   pwd" + onOffPwd.getText().toString());
        startBind(onOffWifi.getText().toString(), onOffPwd.getText().toString(),
                new OnBindStatusListener() {
                    public void onCountdown(int remainSeconds) {
                        onOffTime.setText(remainSeconds + "");
                        Log.i(TAG, "倒计时读秒:" + remainSeconds);
                    }

                    public void onDeviceConnectedWithRouter() {
                        Log.i(TAG, "设备路由器");
                    }

                    public void onBindResult(int result) {
                        if (result == -1) {
                            BaseUtils.showLongToast(mContext, "绑定失败");
                        } else if (result == 0) {

                        }
                        Log.i(TAG, "绑定结果:" + result);
                    }
                });
    }

    private void AddSound(final String id) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", mApplication.getmCurrentUser().getmId());
        params.put("num", id);
        params.put("type", "switch");

        OkHttp.get(mContext, ConnectPath.ADD_CAMERA, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                page = 0;
                smartHomeShowLayout.setVisibility(View.VISIBLE);
                smartHomeAddLayout.setVisibility(View.GONE);
                smartHomeAddLayout1.setVisibility(View.GONE);
                smartHomeAddLayout2.setVisibility(View.GONE);
                smartHomeAddLayout3.setVisibility(View.GONE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                even();

            }
        }));
    }

    long findFirstOnlinePid() {
        int i;
        for (i = 0; i < getConfigCount(); ++i) {
            Config item = getConfigItem(i);
            if (item.configName.equals("_online") &&
                    item.configValue.equals("1") &&
                    null != findDeviceByPid(item.pid)) {
                return item.pid;
            }
        }
        return 0;
    }

//    private void updateCameraName(final Context context, final String type, final String id, String name) {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
//        params.put("flag", "update");
//        params.put("num", id);
//        params.put("name", name);
//        OkHttp.get(mContext, ConnectPath.ADD_CAMERA, params,new BaseStringCallback(mContext, new CallbackHandler<String>() {
//
//                    @Override
//                    public void parameterError(JSONObject response) {
//                    }
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                    }
//
//                    @Override
//                    public void onFailure() {
//                    }
//                }));
//    }

}
