package com.xingyeda.ehome;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.BeanCode;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
/**
 * @ClassName: AcivityRegister
 * @Description: 获取验证码页面
 * @author 李达龙
 * @date 2016-7-6
 */
public class AcivityRegister extends BaseActivity {
    
   @Bind(R.id.register_phone)
    EditText mPhone;// 手机号码输入款
   @Bind(R.id.register_code)
    EditText mAuthCode;// 验证码输入框
   @Bind(R.id.register_getCode)
    TextView mBackCode;// 获取验证码
//   @Bind(R.id.register_getCode)
//    Button mBackCode;// 获取验证码
   @Bind(R.id.register_submit)
    Button mSubmit;// 提交
   @Bind(R.id.register_back)
    TextView mBack;// 返回
    
    
   @Bind(R.id.register_title)
    TextView mTitle;//显示的标题
    private String mType;//页面类型 : 	losepwd——找回密码 		register-注册
    private BeanCode mBeanCode;//验证码的对象
    private int mCodeType;//类型对应的值 :	0——找回密码		1——注册			
    private TimeCount mTime;//倒计时器
    private boolean mIsCode = false;//是否获取了验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.acivity_register);
	ButterKnife.bind(this);
	mTime = new TimeCount(60000, 1000);
	this.init();
	this.event();
    }
   

    private void init() {
	this.mBeanCode = new BeanCode();
	// 解析Bundle
	Bundle bundle = getIntent().getExtras();
	mType = bundle.getString("type");

    }

    private void event() {
	// 找回密码
	if (mType.equals("losepwd")) {
	    mTitle.setText(R.string.find_password);
	    mCodeType = 0;
	}
	// 注册
	else if (mType.equals("register")) {
	    mTitle.setText(R.string.login_register);
	    mCodeType = 1;
	}
    }


	@OnClick({R.id.register_getCode,R.id.register_submit,R.id.register_back})
	public void onClick(View v) {
	    Bundle bundle = new Bundle();
	    String phoneNO = mPhone.getText().toString();
	    String code = mAuthCode.getText().toString();
	    switch (v.getId()) {
	    // 验证码
	    case R.id.register_getCode:
		if (phoneNO.length() == 0) {
			DialogShow.showHintDialog(mContext, getResources().getString(R.string.login_phone_hint));
//		    DialogUtils.getHintDialog(mContext, R.string.login_phone_hint);
		} else if (phoneNO.length() != 11) {
			DialogShow.showHintDialog(mContext, getResources().getString(R.string.phone_error));
//		    DialogUtils.getHintDialog(mContext, R.string.phone_error);
		}else if (!isPhoneNumberValid(phoneNO)) {
			DialogShow.showHintDialog(mContext, "请输入正确的手机号码!");
		}else {
		    mIsCode = true;
		    mTime.start();
		    validationData(ConnectPath.SECURITY_PATH, phoneNO);
		}
		break;
	    // 提交
	    case R.id.register_submit:
	    	
		if (phoneNO.length() == 0) {
			DialogShow.showHintDialog(mContext, getResources().getString(R.string.login_phone_hint));
//		    DialogUtils.getHintDialog(mContext, R.string.login_phone_hint);
		}
//		else if (code.length() == 0) {
//			DialogShow.showHintDialog(mContext, getResources().getString(R.string.register_code_hint));
//		    DialogUtils.getHintDialog(mContext, R.string.register_code_hint);
//		}
	    else {
		    // 找回密码
		    if (mType.equals("losepwd")) {
			if (phoneNO.equals(mBeanCode.getmPhone())
				&& code.equals(mBeanCode.getmCode())) {
			    bundle.putString("Phone", phoneNO);
			    BaseUtils.startActivities(AcivityRegister.this,
				    ActivitySetNewPwd.class, bundle);
			    mIsCode = false;
			    mTime.cancel();
			    AcivityRegister.this.finish();
			} else {
				DialogShow.showHintDialog(mContext, getResources().getString(R.string.verification_code_error));
//			    DialogUtils.getHintDialog(mContext, R.string.verification_code_error);
			}

		    }
		    // 注册
		    else if (mType.equals("register")) {

			if (phoneNO.equals(mBeanCode.getmPhone())
				&& code.equals(mBeanCode.getmCode())) {
			    bundle.putString("Phone", phoneNO);
			    BaseUtils.startActivities(AcivityRegister.this, ActivityInfo.class, bundle);
			    mIsCode = false;
			    mTime.cancel();
			    AcivityRegister.this.finish();
			} else {
				DialogShow.showHintDialog(mContext, getResources().getString(R.string.verification_code_error));
//			    DialogUtils.getHintDialog(AcivityRegister.this, R.string.verification_code_error);
			}

		    }
		}
		break;
	    // 返回
	    case R.id.register_back:
		startActivity(ActivityLogin.class);
		finish();
		break;
	    }
	}

    // 获取验证码
    private void validationData(String path, String phoneNO) {
    Map<String, String> params = new HashMap<String, String>();
	params.put("mPhone", phoneNO);
	params.put("type", mCodeType+"");
	OkHttp.get(mContext,path, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
		
		@Override
		public void parameterError(JSONObject response) {
			mIsCode = false;			
		}
		
		@Override
		public void onResponse(JSONObject response) {
			try {
			    JSONObject code = response.getJSONObject("obj");
			    mBeanCode.setmPhone(code.getString("sessionId"));
			    mBeanCode.setmCode(code.getString("code"));
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
		
		@Override
		public void onFailure() {
			mIsCode = false;
		}
	}));
    }

   class TimeCount extends CountDownTimer {
	public TimeCount(long millisInFuture, long countDownInterval) {
	    super(millisInFuture, countDownInterval);
	}

	@Override
	public void onFinish() {// 计时完毕
	    mBackCode.setText(R.string.register_getcode_text);
	    mBackCode.setTextColor(getResources().getColor(R.color.theme_orange));
	    mBackCode.setClickable(true);
	}

	@Override
	public void onTick(long millisUntilFinished) {// 计时过程
	    if (mIsCode) {
		mBackCode.setClickable(false);// 防止重复点击
		mBackCode.setTextColor(getResources().getColor(R.color.theme_orange));
		mBackCode.setText(millisUntilFinished / 1000 + "s");
	    } else {
		mBackCode.setClickable(true);// 防止重复点击
		mBackCode.setTextColor(getResources().getColor(R.color.theme_orange));
		mBackCode.setText(R.string.register_getcode_text);
	    }
	}
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
		ButterKnife.unbind(this);
		if (mTime != null) {
			mTime.cancel();
		}
    }
    public static boolean isPhoneNumberValid(String phoneNumber) {  
    	   String telRegex = "[1][34578]\\d{9}";  
    	      if (TextUtils.isEmpty(phoneNumber)) return false;  
    	      else return phoneNumber.matches(telRegex); 
  
    	  }  

    
}
