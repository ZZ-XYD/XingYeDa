package com.xingyeda.ehome.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.tenement.ActivityComplainAndService;
import com.xingyeda.ehome.util.BaseUtils;

public class SetActivity extends BaseActivity {
	@Bind(R.id.my_suggest_maintain)
	LinearLayout mSuggestMaintain;
	@Bind(R.id.my_set)
	LinearLayout mMySet;
	@Bind(R.id.my_title)
	TextView mTltie;
	
	private String mType;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_activity);
		ButterKnife.bind(this);
		mType = getIntent().getExtras().getString("type");
		if (mType.equals("set")) {
			mMySet.setVisibility(View.VISIBLE);
			mTltie.setText("系统设置");
		}else if (mType.equals("suggest")) {
			mSuggestMaintain.setVisibility(View.VISIBLE);
			mTltie.setText("建议/维修");
		}
	}
	@OnClick({ R.id.my_back,R.id.my_setting, R.id.my_setnewpwd,R.id.my_switchover_login,R.id.my_suggest,R.id.my_maintain })
    public void onClick(View v) {
		Bundle bundle = new Bundle();
	switch (v.getId()) {
	case R.id.my_back://设置
		SetActivity.this.finish();
	    break;
	case R.id.my_setting://设置
		BaseUtils.startActivity(mContext, ActivityMenuSet.class);
		break;
	case R.id.my_setnewpwd://修改密码
		BaseUtils.startActivity(mContext, ActivityChangePassword.class);
	    break;
	case R.id.my_switchover_login://切换登陆
		final NormalDialog dialog = DialogShow.showSelectDialog(mContext,"是否退出用户",2,new String[] { getResources().getString(R.string.cancel),getResources().getString(R.string.confirm)});
		dialog.setOnBtnClickL(new OnBtnClickL() {

			@Override
			public void onBtnClick() {
				dialog.dismiss();
			}
		},new OnBtnClickL() {
			@Override
			public void onBtnClick() {
				mEhomeApplication.offLine();
				mEhomeApplication.clearData();
				mEhomeApplication.closemTimer();
				mEhomeApplication.setmCurrentUser(null);
				BaseUtils.startActivity(mContext, ActivityLogin.class);
				dialog.superDismiss();
				mEhomeApplication.finishAllActivity();
			}
		});
		break;
	case R.id.my_suggest://建议
		bundle.putString("type", "tousu");
        BaseUtils.startActivities(mContext,ActivityComplainAndService.class, bundle);
		break;
	case R.id.my_maintain://维修
		  bundle.putString("type", "weixiutype");
          BaseUtils.startActivities(mContext,ActivityComplainAndService.class, bundle);
		break;
	}
    }
	 @Override
	    protected void onDestroy() {
	        super.onDestroy();
		 ButterKnife.unbind(this);
	    }
}
