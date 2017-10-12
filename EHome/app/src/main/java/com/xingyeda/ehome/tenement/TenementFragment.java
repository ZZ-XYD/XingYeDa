package com.xingyeda.ehome.tenement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ldl.okhttp.OkHttpUtils;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.AnnunciateAdapter;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.AnnunciateBean;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.life.ConvenientActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author 李达龙
 * @ClassName: TenementFragment
 * @Description: 物业管理界面
 * @date 2016-7-6
 */
@SuppressLint("HandlerLeak")
public class TenementFragment extends Fragment {

    @Bind(R.id.ad_no_data)
    ImageView mNoData;
    @Bind(R.id.ad_recycler_view)
    RecyclerView adRecyclerView;
    @Bind(R.id.ad_swipereLayout)
    SwipeRefreshLayout adSwipereLayout;

    private Context mContext;
    private EHomeApplication mApplication;
    private View rootView;
    private LinearLayoutManager mLayoutManager;
    private AnnunciateAdapter mAdapter;
    // 加载页数
    private int addmoreTimes = 1;
    // 记录最后可见条目
    private int lastVisible;

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
        mApplication = (EHomeApplication) ((Activity) mContext).getApplication();
        mLayoutManager = new LinearLayoutManager(mContext);
        adRecyclerView.setLayoutManager(mLayoutManager);
        adRecyclerView.setHasFixedSize(true);
        adRecyclerView.setItemAnimator(new DefaultItemAnimator());
        init();
        return rootView;
    }
    private void init() {
        adSwipereLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addmoreTimes = 1;
                annunciate(addmoreTimes+"","10",0);
            }
        });
        adRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 不在滑动和最后可见条目是脚布局时加载更多
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisible + 1 == mAdapter.getItemCount()) {
                    addmoreTimes++;
                    annunciate(addmoreTimes+"","6",1);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisible = mLayoutManager.findLastVisibleItemPosition();
            }

        });

        adSwipereLayout.setColorSchemeResources(R.color.theme_orange);
        adSwipereLayout.post(new Runnable() {
            @Override
            public void run() {
                adSwipereLayout.setRefreshing(true);
                annunciate(addmoreTimes+"","10",0);
            }
        });
    }


    @OnClick({R.id.life_convenience})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.life_convenience:
                BaseUtils.startActivity(mContext, ConvenientActivity.class);
                break;
        }

    }

    // 小区物业通告
    private void annunciate(String pageIndex, String pageSize, final int type) {
        MyLog.i("小区通告接口---1");
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", mApplication.getmCurrentUser().getmId());
        params.put("pageIndex", pageIndex);
        params.put("pageSize", pageSize);
        OkHttp.get(mContext, ConnectPath.ANNUNCIATE_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = (JSONObject) response
                            .get("obj");
                    List<AnnunciateBean> list = null;
                    switch (type){
                        case 0:
                            list = new ArrayList<>();
                            break;
                        case 1:
                            list = mApplication.getmAc_List();
                            break;
                    }

                    JSONArray ad_List = (JSONArray) jsonObject.get("list");
                    if (ad_List != null && ad_List.length() != 0) {
                        for (int i = 0; i < ad_List.length(); i++) {
                            JSONObject jobj = ad_List.getJSONObject(i);
                            AnnunciateBean bean = new AnnunciateBean();
                            bean.setmTitle(jobj.has("title")?jobj.getString("title"):"");
                            bean.setmContent(jobj.has("content")?jobj.getString("content"):"");
                            bean.setmTime(jobj.has("sendTime")?jobj.getString("sendTime"):"");
                            list.add(bean);
                        }
                        mApplication.setmAc_List(list);
                    }
                    annunciateDatas();
                    if (adSwipereLayout!=null) {
                        adSwipereLayout.setRefreshing(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));
        MyLog.i("小区通告接口---0");
    }

    // 小区物业通告数据
    private void annunciateDatas() {
        MyLog.i("小区通告适配器---1");
        final List<AnnunciateBean> list = mApplication.getmAc_List();

        if (list != null && !list.isEmpty()) {
            if (adSwipereLayout != null) {
                adSwipereLayout.setVisibility(View.VISIBLE);
                mNoData.setVisibility(View.GONE);
            } else {
                adSwipereLayout.setVisibility(View.GONE);
                mNoData.setVisibility(View.VISIBLE);
            }

            mAdapter = new AnnunciateAdapter(list);
            adRecyclerView.setAdapter(mAdapter);
            mAdapter.clickItem(new AnnunciateAdapter.ClickItem() {
                @Override
                public void onclick(View view, int position) {
                    AnnunciateBean bean = list.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", bean.getmTitle());
                    bundle.putString("content", bean.getmContent());
                    bundle.putString("time", bean.getmTime());
                    bundle.putString("imageList", null);
                    bundle.putString("bean", "annunciate");
                    BaseUtils.startActivities(mContext, Notice_Activity.class, bundle);
                }
            });
            mAdapter.notifyDataSetChanged();
        } else {
            adSwipereLayout.setVisibility(View.GONE);
            mNoData.setVisibility(View.VISIBLE);
        }
        MyLog.i("小区通告适配器---0");
    }

//    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
//
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position,
//                                long id) {
//            AnnunciateBean bean = (AnnunciateBean) swipeMenuListView.getItemAtPosition(position);
//            Bundle bundle = new Bundle();
//            bundle.putString("title", bean.getmTitle());
//            bundle.putString("content", bean.getmContent());
//            bundle.putString("time", bean.getmTime());
//            bundle.putString("imageList", null);
//            bundle.putString("bean", "annunciate");
//            BaseUtils.startActivities(mContext, Notice_Activity.class, bundle);
//        }
//    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkHttpUtils.getInstance().cancelTag(this);
        ButterKnife.unbind(getActivity());
        MyLog.i("TenementFragment销毁");
    }

}
