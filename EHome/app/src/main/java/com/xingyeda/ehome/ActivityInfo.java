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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.jovision.JVBase;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MD5Utils;

import static com.xingyeda.ehome.menu.ActivityChangeInfo.Stringlength;
import static com.xingyeda.ehome.menu.ActivityChangeInfo.checkAccountMark;

/**
 * @ClassName: ActivityInfo
 * @Description: 注册页面
 * @author 李达龙
 * @date 2016-7-6
 */
public class ActivityInfo extends BaseActivity
{
   @Bind(R.id.info_userName)
     EditText mName;//用户名
   @Bind(R.id.info_pwd)
     EditText mPwd;//密码
   @Bind(R.id.info_pwd_again)
     EditText mAgainPwd;//再次输入密码
   @Bind(R.id.info_submit)
     Button mSubmit;//提交
   @Bind(R.id.info_back)
     TextView mAddBack;//返回
   @Bind(R.id.info_sex)
     RadioGroup mSex;//性别
   @Bind(R.id.info_men)
     RadioButton mMen;//男
   @Bind(R.id.info_woman)
     RadioButton mWoman;//女
   @Bind(R.id.info_show_pwd)
     ImageView mShowPwd;//显示密码
   @Bind(R.id.info_hide_pwd)
     ImageView mHidePwd;//影藏密码
   @Bind(R.id.register_loading)
    View mProgressBar;
    

    private String mPhone;
    private String mSexStr = "1";
    
//    private static final int LOGIN = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        ButterKnife.bind(this);
        init();
    }
    private void init()
    {
        this.mPhone = getIntent().getExtras().getString("Phone");
        //控件点击事件
        this.mSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if (checkedId == mMen.getId())
                {
                    mSexStr = "1";
                }
                else if (checkedId == mWoman.getId())
                {
                    mSexStr = "0";
                }
                    
             }
           });
    }

    @OnClick({R.id.info_submit,R.id.info_back,R.id.info_show_pwd,R.id.info_hide_pwd})
        public void onClick(View v)
        {
            String name = mName.getText().toString();
            final String pwd = mPwd.getText().toString();
            String pwd2 = mAgainPwd.getText().toString();
            Editable edit_pwd;
            Editable edit_pwd_again;
            switch (v.getId())
            {
                //提交
            case R.id.info_submit:
                if (Stringlength(name)<=16) {
                    if (checkAccountMark(name)) {
                        if (name == null)
                        {
                            DialogShow.showHintDialog(mContext, getResources().getString(R.string.username_not_null));
                        }
                        else if (pwd == null)
                        {
                            DialogShow.showHintDialog(mContext, getResources().getString(R.string.pwd_not_null));
                        }
                        else if (pwd.length()<6) {
                            DialogShow.showHintDialog(mContext, getResources().getString(R.string.pwd_not_enough));
                        }
                        else {
                            if (pwd.equals(pwd2))
                            {
                                mProgressBar.setVisibility(View.VISIBLE);
                                register(name,pwd);
                            }
                            else
                            {
                                DialogShow.showHintDialog(mContext, getResources().getString(R.string.wrong_pwd));
                            }
                        }
                    }else {
                        DialogShow.showHintDialog(mContext, "用户名包换特殊字符");
                    }
                }else {
                    DialogShow.showHintDialog(mContext, "用户名过长");
                }

                break;
                //返回
            case R.id.info_back:
                startActivity(ActivityLogin.class);
                finish(); 
                break;
            case R.id.info_show_pwd:
                // 显示为普通文本  
                mPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);  
                mAgainPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);  
                // 使光标始终在最后位置  
                edit_pwd = mPwd.getText();  
                edit_pwd_again = mAgainPwd.getText();  
                Selection.setSelection(edit_pwd, edit_pwd.length()); 
                Selection.setSelection(edit_pwd_again, edit_pwd_again.length()); 
                mShowPwd.setVisibility(View.GONE);
                mHidePwd.setVisibility(View.VISIBLE);
            break;
            //隐藏密码
        case R.id.info_hide_pwd:
                // 显示为密码  
                mPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);  
                mAgainPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);  
                // 使光标始终在最后位置  
                edit_pwd = mPwd.getText();  
                edit_pwd_again = mAgainPwd.getText();  
                Selection.setSelection(edit_pwd, edit_pwd.length()); 
                Selection.setSelection(edit_pwd_again, edit_pwd_again.length()); 
                mHidePwd.setVisibility(View.GONE);
                mShowPwd.setVisibility(View.VISIBLE);
            break; 
            }
        }
    
//
    private void register(final String name, final String pwd){
    	Map<String, String> params =new HashMap<String, String>();
        params.put("username", name);
        params.put("pwd", MD5Utils.MD5(pwd));
        params.put("sex", mSexStr);
        params.put("phone", mPhone);
        params.put("beiyongphone", mPhone);
        params.put("img", "");
        params.put("email", "");
        OkHttp.get(mContext,ConnectPath.REGISTER_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
			
			@Override
			public void parameterError(JSONObject response) {
				mProgressBar.setVisibility(View.GONE);
			}
			
			@Override
			public void onResponse(JSONObject response) {
                JVBase.detectionJVId(mContext,name);
				BaseUtils.showLongToast(mContext, "注册成功");
				BaseUtils.startActivity(mContext, ActivityLogin.class);
			}
			
			@Override
			public void onFailure() {
				mProgressBar.setVisibility(View.GONE);
			}
		}));
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
   
}
