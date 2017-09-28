package com.xingyeda.ehome.zhibo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.xingyeda.ehome.R.string.submit;

public class ActivityShareModification extends BaseActivity {
    @Bind(R.id.share_title)
    EditText shareTitle;
    @Bind(R.id.share_describe)
    EditText shareDescribe;
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
                    BaseUtils.showShortToast(mContext,"标题不能为空");
                }else if(TextUtils.isEmpty(shareDescribe.getText())){
                    BaseUtils.showShortToast(mContext,"描述不能为空");
                }else{
                submit();
                }
                break;
        }
    }
    private void submit(){
        Map<String,String> params = new HashMap<>();
        params.put("uid",mEhomeApplication.getmCurrentUser().getmId());
        params.put("id",mRoomId);
        params.put("describe",shareDescribe.getText().toString());
        params.put("title",shareTitle.getText().toString());
        OkHttp.get(mContext, ConnectPath.CAMERA_UPDATE_SHARE,params,new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                BaseUtils.showShortToast(mContext,"修改成功");
                finish();
            }
        }));
    }
}
