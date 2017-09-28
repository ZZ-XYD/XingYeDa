package com.xingyeda.ehome;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
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

import com.jovision.server.AccountServiceImpl;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.ldl.imageloader.core.ImageLoader;
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
 * @ClassName: ActivityLogin
 * @Description: 登录页面
 * @author 李达龙
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_login);
	ButterKnife.bind(this);

		if (AccountServiceImpl.getInstance().isLogin) {
		AccountServiceImpl.getInstance().logout();
		}
//		ImageLoader.getInstance().displayImage("drawable://" + R.mipmap.csr_qd_pad,mBackground);
		mBackground.setBackgroundDrawable(getResources().getDrawable(R.mipmap.login_background));

//	mBackground.setScaleType(ScaleType.FIT_XY);
	SharedPreUtil.put(mContext, "isDoor_Upload", true);
	SharedPreUtil.put(mContext, "isTenement_Upload", true);
	SharedPreUtil.put(mContext, "isLoad_More", true);
	SharedPreUtil.put(mContext, "isLife_Upload", true);
//	SharedPreUtil.put(mContext, "isLogin", true);
//	if (!(getIntent().getExtras().getString("cause").equals("timeout"))) {
//	    SharedPreUtil.put(mContext, "userName", "");
//	    SharedPreUtil.put(mContext, "userPwd", "");
//	}
	mEditName.setText(SharedPreUtil.getString(mContext, "userName"));
	mEditPwd.setText(SharedPreUtil.getString(mContext, "userPwd"));
    }

    @OnClick({ R.id.login_button, R.id.losepwd, R.id.register,
	     R.id.login_show_pwd, R.id.login_hide_pwd,R.id.login_sightseer })
    public void onClick(View view) {
	Bundle bundle = new Bundle();
	Editable etable;
	switch (view.getId()) {
	case R.id.login_button:
		mName = mEditName.getText().toString();
	    mPwd= mEditPwd.getText().toString();
	    if (mName.length() == 0) {
	    DialogShow.showHintDialog(mContext, getResources().getString(R.string.enter_account));
//		DialogUtils.getHintDialog(mContext, R.string.enter_account);
	    } else if (mPwd.length() == 0) {
	    	DialogShow.showHintDialog(mContext, getResources().getString(R.string.enter_pwd));
//		DialogUtils.getHintDialog(mContext, R.string.enter_pwd);
	    }else if (!NetUtils.isConnected(mContext)) {
	    	DialogShow.showHintDialog(mContext, "网络异常，请检查网络");
		} else {
		mProgressBar.setVisibility(View.VISIBLE);
		login(mName, MD5Utils.MD5(mPwd));
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
//	case R.id.login_loading:
//	    mProgressBar.setVisibility(View.GONE);
//	    break;
	case R.id.login_show_pwd:
	    mEditPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
	    etable = mEditPwd.getText();
	    Selection.setSelection(etable, etable.length());
	    mShowPwd.setVisibility(View.GONE);
	    mHidePwd.setVisibility(View.VISIBLE);
	    break;
	case R.id.login_hide_pwd:
	    mEditPwd.setInputType(InputType.TYPE_CLASS_TEXT
		    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	    etable = mEditPwd.getText();
	    Selection.setSelection(etable, etable.length());
	    mHidePwd.setVisibility(View.GONE);
	    mShowPwd.setVisibility(View.VISIBLE);
	    break;
		case R.id.login_sightseer://游客进入
			mEhomeApplication.setmCurrentUser(null);
			BaseUtils.startActivity(mContext, ActivityShareMain.class);
			finish();
			break;
	}
    }

    private void login(final String name, final String pwd) {
	Map<String, String> params = new HashMap<String, String>();
	params.put("userName", name);
	params.put("userPwd", pwd);
	params.put("AndroidSdk", mEhomeApplication.sdk);
	params.put("AndroidModel", mEhomeApplication.model);
	params.put("AndroidRelease", mEhomeApplication.release);
	params.put("AppVersions", AppUtils.getVersionName(mContext));
	params.put("regkey", JPushInterface.getRegistrationID(mContext));
	OkHttp.get(mContext,ConnectPath.LOGIN_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {

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
			ConnectHttpUtils.loginUtils(response, mContext, name,pwd, ActivityHomepage.class);
			if (SYS_EMUI.equals(PhoneBrand.getSystem())){
			getToken();
			}
		}

		@Override
		public void onFailure() {
			if (mProgressBar!=null) {
	    		mProgressBar.setVisibility(View.GONE);
			}
		}
	}));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		String[] text = new String[]{getResources().getString(R.string.quit),getResources().getString(R.string.cancel)};
		final NormalDialog dialog = DialogShow.showSelectDialog(mContext, getResources().getString(R.string.is_quit), 2, text);

		dialog.setOnBtnClickL( new OnBtnClickL() {
            @Override
            public void onBtnClick() {
            	 mEhomeApplication.AppExit();
            }
        },
        new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        });
//	    DialogUtils.getDialog(this,R.string.is_quit)
//		    .setPositiveButton(R.string.quit,new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog,int which) {
//				    mEhomeApplication.AppExit();
//				}
//			    })
//		    .setNegativeButton(R.string.cancel,
//			    new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog,
//					int which) {
//				}
//
//			    }).show();
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
