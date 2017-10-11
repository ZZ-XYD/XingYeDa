package com.xingyeda.ehome.tenement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.AnnunciateAdapter;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.AdvertisementBean;
import com.xingyeda.ehome.bean.AnnunciateBean;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.life.ConvenientActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.view.listview.PullToRefreshBase;
import com.xingyeda.ehome.view.listview.PullToRefreshBase.OnRefreshListener;
import com.xingyeda.ehome.view.listview.PullToRefreshMenuView;
import com.xingyeda.ehome.view.listview.SwipeMenuListView;
import com.ldl.okhttp.OkHttpUtils;

/**
 * @ClassName: TenementFragment
 * @Description: 物业管理界面
 * @author 李达龙
 * @date 2016-7-6
 */
@SuppressLint("HandlerLeak")
public class TenementFragment extends Fragment {
	@Bind(R.id.ad_listview)
	PullToRefreshMenuView mListview;
//	@Bind(R.id.addr_str)
//	TextView mAddrStr;
	@Bind(R.id.ad_no_data)
	ImageView mNoData;
//	@Bind(R.id.bg_annunciate)
//	ImageView mAdBg;

	private Context mContext;
	private EHomeApplication mApplication;
	private View rootView;
	private SwipeMenuListView swipeMenuListView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.tenement_fragment, null);
		}
		ButterKnife.bind(this, rootView);
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		MyLog.i("TenementFragment启动");
		this.mContext = this.getActivity();
		mApplication = (EHomeApplication) ((Activity) mContext)
				.getApplication();
//		annunciate();
		setListView();
//		if
		annunciateDatas();
//		if (mApplication.getmAd()!=null) {
//			mAdBg.setImageBitmap(mApplication.getmAd().getmBitmap());
//		}else {
//
//		}
		return rootView;
	}
	private void setListView() {
		mListview.setPullLoadEnabled(false);
		mListview.setScrollLoadEnabled(true);
		mListview.setOnRefreshListener(new OnRefreshListener<SwipeMenuListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
				annunciate();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {

			}
		});
		swipeMenuListView = mListview.getRefreshableView();
		mListview.onRefreshComplete();

		// 操作ListView左滑时的手势操作，这里用于处理上下左右滑动冲突：开始滑动时则禁止下拉刷新和上拉加载手势操作，结束滑动后恢复上下拉操作
		swipeMenuListView
				.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
					@Override
					public void onSwipeStart(int position) {
						mListview.setPullRefreshEnabled(false);
					}

					@Override
					public void onSwipeEnd(int position) {
						mListview.setPullRefreshEnabled(true);
					}
				});

	}
	@OnClick({
//			R.id.bg_annunciate
			R.id.life_convenience
	})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.life_convenience:
				BaseUtils.startActivity(mContext, ConvenientActivity.class);
			break;
//		case R.id.bg_annunciate:
//			if (mApplication.getmAd()!=null) {
//				if (mApplication.getmAd().getmAdPath()!=null) {
//
//				if (mApplication.getmAd().getmAdPath().startsWith("http")) {
//			Bundle bundle = new Bundle();
//			bundle.putString("url", mApplication.getmAd().getmAdPath());
//			bundle.putString("type", mApplication.getmAd().getmTitle());
//			BaseUtils.startActivities(mContext, AdvertisementActivity.class, bundle);
//				}
//				}
//			}
//			break;
		
		}

	}
	// 小区物业通告
	private void annunciate() {
		MyLog.i("小区通告接口---1");
		Map<String, String> params = new HashMap<String, String>();
		params.put("uid", mApplication.getmCurrentUser().getmId());
		params.put("pageIndex", "1");
		params.put("pageSize", "10");
		OkHttp.get(mContext,ConnectPath.ANNUNCIATE_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
						JSONObject jsonObject = (JSONObject) response
								.get("obj");
						List<AnnunciateBean> List = new ArrayList<AnnunciateBean>();
						JSONArray ad_List = (JSONArray) jsonObject
								.get("list");
						if (ad_List != null && ad_List.length() != 0) {
							for (int i = 0; i < ad_List.length(); i++) {
								JSONObject jobj = ad_List
										.getJSONObject(i);
								AnnunciateBean bean = new AnnunciateBean();
								bean.setmTitle(jobj.getString("title"));
								bean.setmContent(jobj
										.getString("content"));
								bean.setmTime(jobj
										.getString("sendTime"));
								List.add(bean);
							}
						mApplication.setmAc_List(List);
					}
						annunciateDatas();
						mListview.onRefreshComplete();
				} catch (JSONException e) {
					e.printStackTrace();
				}				
			}
		}));
		MyLog.i("小区通告接口---0");
	}
	// 小区物业通告数据
  private void annunciateDatas()
  {
	  MyLog.i("小区通告适配器---1");
      List<AnnunciateBean> list = mApplication.getmAc_List();
      
      if (list != null && !list.isEmpty())
      {
      	if (mListview!=null) {
      		mListview.setVisibility(View.VISIBLE);
      		mNoData.setVisibility(View.GONE);
			}
      	else {
      		mNoData.setVisibility(View.VISIBLE);
		}
     
      AnnunciateAdapter adapter = new AnnunciateAdapter(mContext, list);
      swipeMenuListView.setAdapter(adapter);
      adapter.notifyDataSetChanged();
     
      swipeMenuListView.setOnItemClickListener(itemClickListener);
      }
      else
      {
    	  mNoData.setVisibility(View.VISIBLE);
      }
	  MyLog.i("小区通告适配器---0");
  }
  private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener()
{

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {
        AnnunciateBean bean = (AnnunciateBean) swipeMenuListView.getItemAtPosition(position);
        Bundle bundle = new Bundle();
        bundle.putString("title", bean.getmTitle());
        bundle.putString("content", bean.getmContent());
        bundle.putString("time", bean.getmTime());
        bundle.putString("imageList", null);
        bundle.putString("bean", "annunciate");
        BaseUtils.startActivities(mContext, Notice_Activity.class, bundle);
    }
};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		OkHttpUtils.getInstance().cancelTag(this);
		ButterKnife.unbind(getActivity());
		MyLog.i("TenementFragment销毁");
	}

}
