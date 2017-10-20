package com.xingyeda.ehome.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.ConvenientAdapter;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.ConvenientBean;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.SharedPreUtil;

public class ConvenientActivity extends BaseActivity {
    @Bind(R.id.convenient_list)
    ListView mlist;
    @Bind(R.id.convenient_nodata)
    ImageView mNoData;
    private List<ConvenientBean> mLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convenient);
        ButterKnife.bind(this);
        mLists = new ArrayList<ConvenientBean>();
        if (mEhomeApplication.getmCurrentUser().getmXiaoqu() != null) {
            init();
        } else {
            mlist.setVisibility(View.GONE);
            mNoData.setVisibility(View.VISIBLE);
        }
    }

    private void init() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        if (mEhomeApplication.getmCurrentUser().getmXiaoqu().getmCommunityId() == null || "".equals(mEhomeApplication.getmCurrentUser().getmXiaoqu().getmCommunityId())) {
            params.put("xid", mEhomeApplication.getmCurrentUser().getmXiaoqu().getmCommunityId());
        }
        OkHttp.get(mContext, ConnectPath.CONVENIENCE_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = (JSONArray) response.get("obj");
                    if (jsonArray != null && jsonArray.length() != 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobj = jsonArray.getJSONObject(i);
                            ConvenientBean bean = new ConvenientBean();
                            bean.setmDescription(jobj.has("description") ? jobj.getString("description") : "");
                            bean.setmPhoneNumber(jobj.has("phoneNumber") ? jobj.getString("phoneNumber") : "");
                            mLists.add(bean);
                        }
                    }
                    if (mLists != null && !mLists.isEmpty()) {
                        mlist.setVisibility(View.VISIBLE);
                        mNoData.setVisibility(View.GONE);
                        setList();
                    } else {
                        mlist.setVisibility(View.GONE);
                        mNoData.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));

    }

    private void setList() {
        ConvenientAdapter adapter = new ConvenientAdapter(mContext, mLists);
        mlist.setAdapter(adapter);
        mlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mLists.get(position).getmPhoneNumber()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);

            }
        });

    }

    @OnClick({R.id.convenient_back})
    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.convenient_back:
                ConvenientActivity.this.finish();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
