package com.xingyeda.ehome;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

import com.jovision.server.AccountServiceImpl;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.ldl.imageloader.core.ImageLoader;
import com.mob.MobSDK;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.PhoneBrand;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.ConnectHttpUtils;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.AppUtils;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MD5Utils;
import com.xingyeda.ehome.util.NetUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.zhibo.ActivityShareMain;

import static com.xingyeda.ehome.ActivityLogo.getToken;
import static com.xingyeda.ehome.base.PhoneBrand.SYS_EMUI;

/**
 * @author 李达龙
 * @ClassName: ActivityLogin
 * @Description: 登录页面
 * @date 2016-7-6
 */
public class ActivityLogin extends BaseActivity {

    @Bind(R.id.login_phone)
    EditText mEditName;
    @Bind(R.id.login_userpwd)
    EditText mEditPwd;
    @Bind(R.id.login_button)
    Button mLogin;
    @Bind(R.id.losepwd)
    TextView mLosepwd;
    @Bind(R.id.register)
    TextView mRegister;
    @Bind(R.id.login_loading)
    View mProgressBar;
    @Bind(R.id.login_show_pwd)
    ImageView mShowPwd;
    @Bind(R.id.login_hide_pwd)
    ImageView mHidePwd;
    @Bind(R.id.login_background)
    ImageView mBackground;
    private String mName;
    private String mPwd;

    private PlatformDb platDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        if (AccountServiceImpl.getInstance().isLogin) {
            AccountServiceImpl.getInstance().logout();
        }
        mBackground.setBackgroundDrawable(getResources().getDrawable(R.mipmap.login_background));

        SharedPreUtil.put(mContext, "isDoor_Upload", true);
        SharedPreUtil.put(mContext, "isTenement_Upload", true);
        SharedPreUtil.put(mContext, "isLoad_More", true);
        SharedPreUtil.put(mContext, "isLife_Upload", true);
        mEditName.setText(SharedPreUtil.getString(mContext, "userName"));
        mEditPwd.setText(SharedPreUtil.getString(mContext, "userPwd"));
        MobSDK.init(mContext,"214357362328d","ac5f9099ffb9fe286b9c8e7d99a99b44");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @OnClick({R.id.login_button, R.id.losepwd, R.id.register,
            R.id.login_show_pwd, R.id.login_hide_pwd, R.id.login_sightseer,R.id.qq_login})
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        Editable etable;
        switch (view.getId()) {
            case R.id.login_button:
                mName = mEditName.getText().toString();
                mPwd = mEditPwd.getText().toString();
                if (mName.length() == 0) {
                    DialogShow.showHintDialog(mContext, getResources().getString(R.string.enter_account));
                } else if (mPwd.length() == 0) {
                    DialogShow.showHintDialog(mContext, getResources().getString(R.string.enter_pwd));
                } else if (!NetUtils.isConnected(mContext)) {
                    DialogShow.showHintDialog(mContext, "网络异常，请检查网络");
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    SharedPreUtil.put(mContext, "flag", "0");
                    login(mName, MD5Utils.MD5(mPwd),"");
                }
                break;
            case R.id.losepwd:
                bundle.putString("type", "losepwd");
                startActivities(AcivityRegister.class, bundle);
                break;
            case R.id.register:
                bundle.putString("type", "register");
                startActivities(AcivityRegister.class, bundle);
                break;
            case R.id.login_show_pwd:
                mEditPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                etable = mEditPwd.getText();
                mEditPwd.setSelection(etable.length());
//                Selection.setSelection(etable, etable.length());
                mShowPwd.setVisibility(View.GONE);
                mHidePwd.setVisibility(View.VISIBLE);
                break;
            case R.id.login_hide_pwd:
                mEditPwd.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                etable = mEditPwd.getText();
                mEditPwd.setSelection(etable.length());
//                Selection.setSelection(etable, etable.length());
                mHidePwd.setVisibility(View.GONE);
                mShowPwd.setVisibility(View.VISIBLE);
                break;
            case R.id.login_sightseer://游客进入
                mEhomeApplication.setmCurrentUser(null);
                BaseUtils.startActivity(mContext, ActivityShareMain.class);
                finish();
                break;
            case R.id.qq_login://qq登陆
                mProgressBar.setVisibility(View.VISIBLE);
                Platform qq= ShareSDK.getPlatform(QQ.NAME);
                authorize(qq);
                break;
        }
    }

    /**
     * 执行授权,获取用户信息
     *
     * @param plat
     */
    private void authorize(Platform plat) {
        if (plat == null) {
            return;
        }

        // 使用SSO授权。有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权。
        plat.SSOSetting(false);
//        plat.authorize();
        // 参数null表示获取当前授权用户资料
        plat.showUser(null);
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
                String headImageUrl = null;//头像
                String token;//token
                String gender;//年龄
                String userId;
                String name = null;//用户名

                // 用户资源都保存到res
                // 通过打印res数据看看有哪些数据是你想要的
                if (action == Platform.ACTION_USER_INFOR) {
                    platDB = platform.getDb(); // 获取数平台数据DB
                    if (platform.getName().equals(Wechat.NAME)) {

                        // 通过DB获取各种数据
                        token = platDB.getToken();
                        userId = platDB.getUserId();
                        name = platDB.getUserName();
                        gender = platDB.getUserGender();
                        headImageUrl = platDB.getUserIcon();
                        if ("m".equals(gender)) {
                            gender = "1";
                        } else {
                            gender = "2";
                        }

                    } else if (platform.getName().equals(SinaWeibo.NAME)) {
                        // 微博登录
                    } else if (platform.getName().equals(QQ.NAME)) {
                        // QQ登录
                        token = platDB.getToken();
                        userId = platDB.getUserId();
                        name = hashMap.get("nickname").toString(); // 名字
                        gender = hashMap.get("gender").toString(); // 年龄
                        headImageUrl = hashMap.get("figureurl_qq_2").toString(); // 头像figureurl_qq_2 中等图，figureurl_qq_1缩略图
                        String city = hashMap.get("city").toString(); // 城市
                        String province = hashMap.get("province").toString(); // 省份
//                        getUserInfo(name, headImageUrl);
                        SharedPreUtil.put(mContext, "flag", "1");
                        login(userId, "",name);

                    }
                }


            }

            @Override
            public void onError(Platform platform, int action, Throwable throwable) {
                Toast.makeText(mContext, "错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Platform platform, int action) {

            }
        });
        plat.removeAccount(true);

    }

    private void login(final String userName, final String pwd,String name) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("userName", userName);
        params.put("userPwd", pwd);
        params.put("flag", SharedPreUtil.getString(mContext, "flag"));
        params.put("name", name);
        params.put("AndroidSdk", mEhomeApplication.sdk);
        params.put("AndroidModel", mEhomeApplication.model);
        params.put("AndroidRelease", mEhomeApplication.release);
        params.put("AppVersions", AppUtils.getVersionName(mContext));
        params.put("regkey", JPushInterface.getRegistrationID(mContext));
        OkHttp.get(mContext, ConnectPath.LOGIN_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {

            @Override
            public void parameterError(JSONObject response) {
                try {
                    DialogShow.showHintDialog(mContext, response.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(JSONObject response) {
                SharedPreUtil.put(mContext, "userName", mName);
                SharedPreUtil.put(mContext, "userPwd", mPwd);
                ConnectHttpUtils.loginUtils(response, mContext, userName, pwd, ActivityHomepage.class);
                if (SYS_EMUI.equals(PhoneBrand.getSystem())) {
                    getToken();
                }
            }

            @Override
            public void onFailure() {
                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        }));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            String[] text = new String[]{getResources().getString(R.string.quit), getResources().getString(R.string.cancel)};
            final NormalDialog dialog = DialogShow.showSelectDialog(mContext, getResources().getString(R.string.is_quit), 2, text);

            dialog.setOnBtnClickL(new OnBtnClickL() {
                                      @Override
                                      public void onBtnClick() {
                                          dialog.superDismiss();
                                          mEhomeApplication.AppExit();
                                      }
                                  },
                    new OnBtnClickL() {
                        @Override
                        public void onBtnClick() {
                            dialog.dismiss();
                        }
                    });
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
