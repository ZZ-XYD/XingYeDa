package com.xingyeda.ehome.life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.LifeAdpater;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.LifeBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.tenement.AdvertisementActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.view.LineGridView;
import com.ldl.okhttp.OkHttpUtils;

/**
 * @ClassName: LifeFragment
 * @Description: 商圈界面
 * @author 李达龙
 * @date 2016-7-6
 */
public class LifeFragment extends Fragment {

	@Bind(R.id.life_gridview)
	LineGridView mGridView;
	@Bind(R.id.life_nodata)
	ImageView mNoData;

	private View rootView;
	private Context mContext;
	private EHomeApplication mApplication;
//	private List<LifeBean> mApplication.getmLife_List() ;
	private static final int CONTENT = 1;
	private List<LifeBean> list;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.life_fragment, null);
		}
		ButterKnife.bind(this, rootView);
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		mContext = this.getActivity();
		mApplication = (EHomeApplication) ((Activity) mContext).getApplication();
//		mApplication.getmLife_List()  = new ArrayList<LifeBean>();
		init();
//		if (mApplication.getmCurrentUser().getmXiaoqu() != null) {
//			setList();
//		}else {
//			mGridView.setVisibility(View.GONE);
//			mNoData.setVisibility(View.VISIBLE);
//		}

		return rootView;
	}
@Override
public void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	if (mApplication.getmLife_List() != null) {
//		setList();
		mHandler.sendEmptyMessage(CONTENT);
		init();
	}else {
		mGridView.setVisibility(View.GONE);
		mNoData.setVisibility(View.VISIBLE);
	}
}
	private void init() {
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("xId", mApplication.getmCurrentUser().getmXiaoqu().getmCommunityId());
//		OkHttp.get(mContext,ConnectPath.LIFETAG_PATH,params , new BaseStringCallback(
//				mContext, new CallbackHandler<String>() {
//
//					@Override
//					public void parameterError(JSONObject response) {
//					}
//
//					@Override
//					public void onResponse(JSONObject response) {
//						try {
//							JSONArray jsonArray = (JSONArray) response.get("obj");
//							if (jsonArray != null && jsonArray.length() != 0) {
//								for (int i = 0; i < jsonArray.length(); i++) {
//									LifeBean bean = new LifeBean();
//									JSONObject jobj = jsonArray.getJSONObject(i);
//									bean.setmId(jobj.has("id") ? jobj.getString("id") : "");
//									bean.setmName(jobj.has("name") ? jobj.getString("name") : "");
//									bean.setmPath(jobj.has("img") ? jobj.getString("img") : "");
//									bean.setmType(jobj.has("type") ? jobj.getString("type") : "");
//									bean.setmContent(jobj.has("content") ? jobj.getString("content") : "");
//
//									mApplication.getmLife_List() .add(bean);
//								}
//								mHandler.sendEmptyMessage(CONTENT);
//							}
//
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//
//					}
//
//					@Override
//					public void onFailure() {
//					}
//				}));

	}
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CONTENT:
				setList();
				break;
			}
			
		};
	};

	private void setList() {
		final List<LifeBean> list = mApplication.getmLife_List();
		if (list != null && !list.isEmpty()) {
			mGridView.setVisibility(View.VISIBLE);
			mNoData.setVisibility(View.GONE);
		LifeAdpater adpater = new LifeAdpater(mContext,list ,mGridView.getMeasuredHeight()/3);
//		mGridView.setStretchMode(c);
//		mGridView.setColumnWidth(mGridView.getMeasuredHeight()/3);
		mGridView.setAdapter(adpater);
		adpater.notifyDataSetChanged();
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				LifeBean bean = list.get(position);
				if (bean.getmType().equals("1")) {
					Bundle bundle = new Bundle();
				    bundle.putString("url", bean.getmContent());
				    bundle.putString("type", bean.getmName());
				    BaseUtils.startActivities(mContext,
					    AdvertisementActivity.class, bundle);
				}else if (bean.getmType().equals("2")) {
					Uri uri = Uri.parse("tel:" + bean.getmContent());
					Intent intent = new Intent(Intent.ACTION_DIAL, uri);
					startActivity(intent);
				}else {
					DialogShow.showHintDialog(mContext, "暂无数据，敬请期待");
				}
			}
		});
		}else {
			mGridView.setVisibility(View.GONE);
			mNoData.setVisibility(View.VISIBLE);
		}
	}

	@OnClick({ R.id.life_convenience })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.life_convenience:
			BaseUtils.startActivity(mContext, ConvenientActivity.class);
			break;

		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		OkHttpUtils.getInstance().cancelTag(this);
		ButterKnife.unbind(this);
	}
}
