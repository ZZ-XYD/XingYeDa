package com.xingyeda.ehome.menu;

import java.util.HashMap;
import java.util.Map;

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
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MD5Utils;
import com.xingyeda.ehome.util.SharedPreUtil;

public class ActivityChangePassword extends BaseActivity {

    @Bind(R.id.change_old_pwd)
    EditText mOldPwd;
    @Bind(R.id.change_new_pwd)
    EditText mNewPwd;
    @Bind(R.id.change_new_pwd2)
    EditText mAgainNewPwd;
    @Bind(R.id.change_submit)
    Button mSubmit;
    @Bind(R.id.change_pwd_Back)
    TextView mBack;
    @Bind(R.id.changenewpwd_show_pwd)
    ImageView mShowPwd;
    @Bind(R.id.changenewpwd_hide_pwd)
    ImageView mHidePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            BaseUtils.startActivity(ActivityChangePassword.this,
                    ActivityHomepage.class);
            ActivityChangePassword.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.change_submit, R.id.change_pwd_Back,
            R.id.changenewpwd_show_pwd, R.id.changenewpwd_hide_pwd})
    public void onClick(View v) {
        Editable edit_oldpwd = mOldPwd.getText();
        Editable edit_newpwd = mNewPwd.getText();
        Editable edit_newpwd_again = mAgainNewPwd.getText();
        switch (v.getId()) {
            case R.id.change_submit:
                if (mOldPwd.getText().toString() == null || mOldPwd.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "原密码不能为空");
                } else if (mNewPwd.getText().toString() == null || mNewPwd.getText().toString().equals("") || mAgainNewPwd.getText().toString() == null || mAgainNewPwd.getText().toString().equals("")) {
                    DialogShow.showHintDialog(mContext, "密码不能为空");
                } else if (mNewPwd.getText().toString().length() < 6 || mAgainNewPwd.getText().toString().length() < 6) {
                    DialogShow.showHintDialog(mContext, getResources().getString(R.string.pwd_not_enough));
                } else if (!mAgainNewPwd.getText().toString().equals(mNewPwd.getText().toString())) {
                    DialogShow.showHintDialog(mContext, "两次密码不一致");
                } else if (mOldPwd.getText().toString().equals(mNewPwd.getText().toString())) {
                    DialogShow.showHintDialog(mContext, "修改为原密码，请重新输入新密码");
                } else {
                    changePassword();
                }
                break;
            case R.id.change_pwd_Back:
                ActivityChangePassword.this.finish();
                break;
            case R.id.changenewpwd_show_pwd:
                // 显示为普通文本
                mOldPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                mAgainNewPwd
                        .setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                // 使光标始终在最后位置
                if (edit_oldpwd.length() < 16) {
                    mOldPwd.setSelection(edit_oldpwd.length());
                }
                if (edit_newpwd.length() < 16) {
                    mNewPwd.setSelection(edit_newpwd.length());
                }
                if (edit_newpwd_again.length() < 16) {
                    mAgainNewPwd.setSelection(edit_newpwd_again.length());
                }
                mShowPwd.setVisibility(View.GONE);
                mHidePwd.setVisibility(View.VISIBLE);
                break;
            case R.id.changenewpwd_hide_pwd:
                // 显示为密码
                mOldPwd.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mNewPwd.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                mAgainNewPwd.setInputType(InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                // 使光标始终在最后位置
                edit_oldpwd = mOldPwd.getText();
                edit_newpwd = mNewPwd.getText();
                edit_newpwd_again = mAgainNewPwd.getText();
                if (edit_oldpwd.length() < 16) {
                    mOldPwd.setSelection(edit_oldpwd.length());
                }
                if (edit_newpwd.length() < 16) {
                    mNewPwd.setSelection(edit_newpwd.length());
                }
                if (edit_newpwd_again.length() < 16) {
                    mAgainNewPwd.setSelection(edit_newpwd_again.length());
                }
                mHidePwd.setVisibility(View.GONE);
                mShowPwd.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void changePassword() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
        params.put("pwd1", MD5Utils.MD5(mOldPwd.getText().toString()));
        params.put("pwd2", MD5Utils.MD5(mNewPwd.getText().toString()));
        params.put("pwd3", MD5Utils.MD5(mAgainNewPwd.getText().toString()));
        OkHttp.get(mContext, ConnectPath.CHANGEPWD_PATH, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {

            @Override
            public void parameterError(JSONObject response) {
                ActivityChangePassword.this.finish();
            }

            @Override
            public void onResponse(JSONObject response) {
                SharedPreUtil.put(mContext, "userPwd", mNewPwd.getText().toString());
                BaseUtils.showShortToast(mContext, R.string.password_changed);
                ActivityChangePassword.this.finish();
            }

            @Override
            public void onFailure() {
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
