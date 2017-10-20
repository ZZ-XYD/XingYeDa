package com.xingyeda.ehome.zhibo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
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

public class ActivityShareModification extends BaseActivity {
    @Bind(R.id.share_title)
    EditText shareTitle;
    @Bind(R.id.share_describe)
    EditText shareDescribe;
    @Bind(R.id.share_loading)
    FrameLayout shareLoading;
    private String mRoomId;
    private String mTitle;
    private String mDescribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_modification);
        ButterKnife.bind(this);
        mRoomId = getIntent().getExtras().getString("roomId");
        mTitle = getIntent().getExtras().getString("title");
        mDescribe = getIntent().getExtras().getString("describe");
        shareTitle.setText(mTitle);
        shareDescribe.setText(mDescribe);
    }


    @OnClick({R.id.personal_back, R.id.share_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.personal_back:
                finish();
                break;
            case R.id.share_submit:
                if (TextUtils.isEmpty(shareTitle.getText())) {
                    BaseUtils.showShortToast(mContext, "标题不能为空");
                } else if (TextUtils.isEmpty(shareDescribe.getText())) {
                    BaseUtils.showShortToast(mContext, "描述不能为空");
                } else {
                    submit();
                }
                break;
        }
    }

    private void submit() {
        shareLoading.setVisibility(View.VISIBLE);
        Map<String, String> params = new HashMap<>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        params.put("id", mRoomId);
        params.put("describe", shareDescribe.getText().toString());
        params.put("title", shareTitle.getText().toString());
        OkHttp.get(mContext, ConnectPath.CAMERA_UPDATE_SHARE, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                shareLoading.setVisibility(View.GONE);
                BaseUtils.showShortToast(mContext, "修改成功");
                finish();
            }

            @Override
            public void parameterError(JSONObject response) {
                shareLoading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {
                shareLoading.setVisibility(View.GONE);
            }
        }));
    }
}
