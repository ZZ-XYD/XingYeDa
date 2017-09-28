package com.xingyeda.ehome;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MD5Utils;
/**
 * @ClassName: ActivitySetNewPwd
 * @Description: 密码修改/找回密码页面
 * @author 李达龙
 * @date 2016-7-6
 */
public class ActivitySetNewPwd extends BaseActivity {

   @Bind(R.id.setnewpwd_pwd)
    EditText mNewPwd;// 新密码
   @Bind(R.id.setnewpwd_pwd_again)
    EditText mNewPwd_Again;// 再次输入新密码
   @Bind(R.id.setnewpwd_submit)
    Button mSetNewPwd;// 提交
   @Bind(R.id.setnewpwd_back)
    TextView mBack;// 返回
   @Bind(R.id.setnewpwd_show_pwd)
    ImageView mShowPwd;
   @Bind(R.id.setnewpwd_hide_pwd)
    ImageView mHidePwd;

    private String mPhone;
//    private static final int LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.acivity_setpwd);
	ButterKnife.bind(this);
	mPhone = getIntent().getExtras().getString("Phone");
    }

//    @SuppressLint("HandlerLeak")
//    private Handler mHandler = new Handler() {
//	@SuppressLint("HandlerLeak")
//	public void handleMessage(Message msg) {
//
//	    switch (msg.what) {
//	    case LOGIN:
//		login(mPhone, (String) msg.obj);
//		break;
//	    }
//
//	};
//    };

    @OnClick({ R.id.setnewpwd_submit, R.id.setnewpwd_back,
	    R.id.setnewpwd_show_pwd, R.id.setnewpwd_hide_pwd })
    public void onClick(View v) {
	String pwd = mNewPwd.getText().toString();
	String pwd2 = mNewPwd_Again.getText().toString();
	Editable edit_pwd;
	Editable edit_pwd_again;
	switch (v.getId()) {
	// 设置新密码
	case R.id.setnewpwd_submit:
	    if (pwd == null) {
	    	DialogShow.showHintDialog(mContext, getResources().getString(R.string.enter_pwd));
//		DialogUtils.getHintDialog(mContext, R.string.enter_pwd);
	    } else if (pwd2 == null) {
	    	DialogShow.showHintDialog(mContext, getResources().getString(R.string.enter_pwd_again));
//		DialogUtils.getHintDialog(mContext, R.string.enter_pwd_again);
	    } else if (pwd.length()<6 || pwd2.length()<6 ) {
        	DialogShow.showHintDialog(mContext, getResources().getString(R.string.pwd_not_enough));
		}else {
		if (pwd.equals(pwd2)) {
		    setNewPwd(pwd, mPhone);
		} else {
			DialogShow.showHintDialog(mContext, getResources().getString(R.string.wrong_pwd));
//		    DialogUtils.getHintDialog(mContext, R.string.wrong_pwd);
		}
	    }
	    break;
	// 返回
	case R.id.setnewpwd_back:
	    startActivity(ActivityLogin.class);
	    finish();
	    break;
	// 显示密码
	case R.id.setnewpwd_show_pwd:
	    // 显示为普通文本
	    mNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
	    mNewPwd_Again
		    .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
	    // 使光标始终在最后位置
	    edit_pwd = mNewPwd.getText();
	    edit_pwd_again = mNewPwd_Again.getText();
	    Selection.setSelection(edit_pwd, edit_pwd.length());
	    Selection.setSelection(edit_pwd_again, edit_pwd_again.length());
	    mShowPwd.setVisibility(View.GONE);
	    mHidePwd.setVisibility(View.VISIBLE);
	    break;
	// 隐藏密码
	case R.id.setnewpwd_hide_pwd:
	    // 显示为密码
	    mNewPwd.setInputType(InputType.TYPE_CLASS_TEXT
		    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	    mNewPwd_Again.setInputType(InputType.TYPE_CLASS_TEXT
		    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	    // 使光标始终在最后位置
	    edit_pwd = mNewPwd.getText();
	    edit_pwd_again = mNewPwd_Again.getText();
	    Selection.setSelection(edit_pwd, edit_pwd.length());
	    Selection.setSelection(edit_pwd_again, edit_pwd_again.length());
	    mHidePwd.setVisibility(View.GONE);
	    mShowPwd.setVisibility(View.VISIBLE);
	    break;
	}
    }

    private void setNewPwd(final String pwd, final String mPhone) {
    	Map<String,String> params = new HashMap<String, String>();
	params.put("pwd", MD5Utils.MD5(pwd));
	params.put("uid", mPhone);
	params.put("regkey", JPushInterface.getRegistrationID(ActivitySetNewPwd.this));
	OkHttp.get(mContext,ConnectPath.NEWPWD_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
		
		@Override
		public void parameterError(JSONObject response) {}
		@Override
		public void onResponse(JSONObject response) {
			BaseUtils.showLongToast(mContext, "注册成功");
			BaseUtils.startActivity(mContext, ActivityLogin.class);
//			Message message = new Message();
//			message.what = LOGIN;
//			message.obj = pwd;
//			mHandler.sendMessage(message);
		}
		@Override
		public void onFailure() {}
	}));
    }

//    private void login(final String mPhone, final String pwd) {
//    Map<String, String> params = new HashMap<String, String>();
//	params.put("userName", mPhone);
//	params.put("userPwd", MD5Utils.MD5(pwd));
//	params.put("AndroidSdk", mEhomeApplication.sdk);
//	params.put("AndroidModel", mEhomeApplication.model);
//	params.put("AndroidRelease", mEhomeApplication.release);
//	params.put("regkey", JPushInterface.getRegistrationID(mContext));
//	params.put("AppVersions", AppUtils.getVersionName(mContext));
//	OkHttp .get(mContext,ConnectPath.LOGIN_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
//		
//		@Override
//		public void parameterError(JSONObject response) {}
//		
//		@Override
//		public void onResponse(JSONObject response) {
//			SharedPreUtil.put(mContext, "userName", mPhone);
//            SharedPreUtil.put(mContext, "userPwd", pwd);
//			ConnectHttpUtils.loginUtils(response,mContext, mPhone, pwd,ActivityHomepage.class);
//		}
//		
//		@Override
//		public void onFailure() {}
//	}));
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
		ButterKnife.unbind(this);
    }

}
