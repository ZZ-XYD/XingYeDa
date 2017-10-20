package com.jovision.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.DoorFragment;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JVLogin extends BaseActivity {

   @Bind(R.id.jv_number)
    EditText jvNumber;
   @Bind(R.id.jv_add_name)
    EditText jvAddName;
   @Bind(R.id.jv_username)
    EditText jvUsername;
   @Bind(R.id.jv_pwd)
    EditText jvPwd;


    String mid;
    String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jvloging);
        ButterKnife.bind(this);
        init();
    }
    private void init(){
        Bundle intent = getIntent().getExtras();
        mid = intent.getString("id");
        mType = intent.getString("type");
        jvNumber.setText(mid);
    }

    @OnClick({R.id.add_loging_back, R.id.jv_add_but})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_loging_back:
                JVLogin.this.finish();
                break;
            case R.id.jv_add_but:
                if (jvUsername.getText().toString()==null|| jvUsername.getText().toString().equals("") ) {
                    DialogShow.showHintDialog(mContext,"用户名不能为空");
                }else if (jvPwd.getText().toString()==null||jvPwd.getText().toString().equals("")){
                    DialogShow.showHintDialog(mContext,"密码不能为空");
                }else {
                AddSound(mid,jvAddName.getText().toString(),jvUsername.getText().toString(),jvPwd.getText().toString());
                }
                break;
        }
    }

    private void AddSound(String id,String nickname,String username,String pwd){
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        params.put("num", id);
        params.put("name", nickname);
        params.put("username", username);
        params.put("userpwd", pwd);
        if (mType.equals("common")) {
            params.put("type", "buyaotou");
        }else if (mType.equals("shake")) {
            params.put("type", "yaotou");
        }else if (mType.equals("cateye")) {
            params.put("type", "maoyan");
        }
        OkHttp.get(mContext, ConnectPath.ADD_CAMERA, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Intent mIntent = new Intent(DoorFragment.ACTION_NAME);
                    mIntent.putExtra("yaner", "check");
                    mContext.sendBroadcast(mIntent);
                    BaseUtils.showShortToast(mContext,response.getString("msg"));
                    BaseUtils.startActivity(mContext, ActivityHomepage.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}
