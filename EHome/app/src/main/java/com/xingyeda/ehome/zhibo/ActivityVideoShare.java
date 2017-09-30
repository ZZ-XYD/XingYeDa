package com.xingyeda.ehome.zhibo;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.AESUtils;
import com.xingyeda.ehome.util.BaseUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.xingyeda.ehome.R.string.submit;

public class ActivityVideoShare extends BaseActivity {

    @Bind(R.id.share_headline)
    TextView shareHeadline;
    @Bind(R.id.share_equipment)
    EditText shareEquipment;
    @Bind(R.id.share_title)
    EditText shareTitle;
    @Bind(R.id.share_describe)
    EditText shareDescribe;
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

        Map<String, String> params = new HashMap<>();
        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
        params.put("cnum", shareEquipment.getText().toString());
        params.put("title", shareTitle.getText().toString());
        params.put("describe", shareDescribe.getText().toString());
        OkHttp.get(mContext, ConnectPath.SHARE_CAMERA, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                BaseUtils.showShortToast(mContext, "分享成功");
                finish();
            }
        }));
    }
}
