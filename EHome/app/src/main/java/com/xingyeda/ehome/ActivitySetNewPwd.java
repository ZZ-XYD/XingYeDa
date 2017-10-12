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
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MD5Utils;

/**
 * @author 李达龙
 * @ClassName: ActivitySetNewPwd
 * @Description: 密码修改/找回密码页面
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_setpwd);
        ButterKnife.bind(this);
        mPhone = getIntent().getExtras().getString("Phone");
    }

    @OnClick({R.id.setnewpwd_submit, R.id.setnewpwd_back,
            R.id.setnewpwd_show_pwd, R.id.setnewpwd_hide_pwd})
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
                } else if (pwd2 == null) {
                    DialogShow.showHintDialog(mContext, getResources().getString(R.string.enter_pwd_again));
                } else if (pwd.length() < 6 || pwd2.length() < 6) {
                    DialogShow.showHintDialog(mContext, getResources().getString(R.string.pwd_not_enough));
                } else {
                    if (pwd.equals(pwd2)) {
                        setNewPwd(pwd, mPhone);
                    } else {
                        DialogShow.showHintDialog(mContext, getResources().getString(R.string.wrong_pwd));
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
                mNewPwd.setSelection(edit_pwd.length());
                mNewPwd_Again.setSelection(edit_pwd_again.length());
//                Selection.setSelection(edit_pwd, edit_pwd.length());
//                Selection.setSelection(edit_pwd_again, edit_pwd_again.length());
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
                mNewPwd.setSelection(edit_pwd.length());
                mNewPwd_Again.setSelection(edit_pwd_again.length());
//                Selection.setSelection(edit_pwd, edit_pwd.length());
//                Selection.setSelection(edit_pwd_again, edit_pwd_again.length());
                mHidePwd.setVisibility(View.GONE);
                mShowPwd.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setNewPwd(final String pwd, final String mPhone) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pwd", MD5Utils.MD5(pwd));
        params.put("uid", mPhone);
        params.put("regkey", JPushInterface.getRegistrationID(ActivitySetNewPwd.this));
        OkHttp.get(mContext, ConnectPath.NEWPWD_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                BaseUtils.showLongToast(mContext, "注册成功");
                BaseUtils.startActivity(mContext, ActivityLogin.class);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
