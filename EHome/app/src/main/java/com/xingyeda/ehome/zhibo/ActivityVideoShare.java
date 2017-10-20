package com.xingyeda.ehome.zhibo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

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

public class ActivityVideoShare extends BaseActivity {

    @Bind(R.id.share_headline)
    TextView shareHeadline;
    @Bind(R.id.share_equipment)
    EditText shareEquipment;
    @Bind(R.id.share_title)
    EditText shareTitle;
    @Bind(R.id.share_describe)
    EditText shareDescribe;
    @Bind(R.id.share_loading)
    FrameLayout shareLoading;
    private String mEquipmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_share);
        ButterKnife.bind(this);

        mEquipmentId = getIntent().getExtras().getString("equipmentId");//获取设备号
        if (mEquipmentId != null) {
            shareEquipment.setText(mEquipmentId);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @OnClick({R.id.share_back, R.id.share_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.share_back:
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
        params.put("cnum", shareEquipment.getText().toString());
        params.put("title", shareTitle.getText().toString());
        params.put("describe", shareDescribe.getText().toString());
        OkHttp.get(mContext, ConnectPath.SHARE_CAMERA, params, new BaseStringCallback(mContext, new CallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                shareLoading.setVisibility(View.GONE);
                BaseUtils.showShortToast(mContext, "分享成功");
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
