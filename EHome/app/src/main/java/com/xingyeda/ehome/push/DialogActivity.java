package com.xingyeda.ehome.push;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.tenement.AnnunciateActivity;
import com.xingyeda.ehome.tenement.Notice_Activity;
import com.xingyeda.ehome.util.BaseUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogActivity extends BaseActivity {

    @Bind(R.id.title_text)
    TextView titleText;
    @Bind(R.id.content_text)
    TextView contentText;
    @Bind(R.id.dialog_view_details)
    TextView dialogViewDetails;

    private String mType;
    private String mTitle;
    private String mContent;
    private String mTime;
    private String mCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mType = getIntent().getExtras().getString("type");
        mTitle = getIntent().getExtras().getString("title");
        mContent = getIntent().getExtras().getString("content");
        mTime = getIntent().getExtras().getString("time");
        mCode = getIntent().getExtras().getString("code");


        if (mType.equals("7")) {
            dialogViewDetails.setVisibility(View.GONE);
        }

        titleText.setText(mTitle);
        contentText.setText(mContent);
    }


    @OnClick({R.id.dialog_confirm, R.id.dialog_view_details})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialog_confirm:
                if (mType.equals("8")) {
                    msgCallBack(mCode);
                }
                finish();
                break;
            case R.id.dialog_view_details:
                if (mType.equals("8")) {
                    msgCallBack(mCode);
                }
                BaseUtils.startActivity(mContext, AnnunciateActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("title", mTitle);
//                bundle.putString("content", mContent);
//                bundle.putString("time", mTime);
//                bundle.putString("imageList", null);
//                bundle.putString("bean", "annunciate");
//                BaseUtils.startActivities(mContext, Notice_Activity.class, bundle);
                finish();
                break;
        }
    }

    private void msgCallBack(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        OkHttp.get(mContext, ConnectPath.PUSHMSG_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }));
    }
}
