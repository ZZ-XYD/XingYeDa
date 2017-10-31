package com.xingyeda.ehome.menu;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.xingyeda.ehome.ActivityExplain;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.ConnectHttpUtils;
import com.xingyeda.ehome.tenement.AdvertisementActivity;
import com.xingyeda.ehome.util.AppUtils;
import com.xingyeda.ehome.util.BaseUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityAbout extends BaseActivity {
    @Bind(R.id.company_path)
    View mCompany_Path;
    @Bind(R.id.menu_about_back)
    TextView mBack;
    @Bind(R.id.versions)
    TextView versions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        versions.setText(AppUtils.getVersionName(mContext));

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            BaseUtils.startActivity(ActivityAbout.this, ActivityHomepage.class);
            ActivityAbout.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.menu_about_back, R.id.company_path, R.id.graded, R.id.function, R.id.version_updating})
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.menu_about_back:
                BaseUtils.startActivity(ActivityAbout.this, ActivityHomepage.class);
                ActivityAbout.this.finish();
                break;
            case R.id.company_path:
                bundle.putString("url",
                        getResources().getString(R.string.menu_about_url_text));
                bundle.putString("type", "关于我们");
                BaseUtils.startActivities(ActivityAbout.this,
                        AdvertisementActivity.class, bundle);
                break;
            case R.id.graded:
                DialogShow.showHintDialog(mContext, "暂未开放，敬请期待");
                break;
            case R.id.function:
                bundle.putString("type", "about");
                BaseUtils.startActivities(mContext, ActivityExplain.class, bundle);
                break;
            case R.id.version_updating:
                ConnectHttpUtils.inspectUpdate(mContext, 1);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
