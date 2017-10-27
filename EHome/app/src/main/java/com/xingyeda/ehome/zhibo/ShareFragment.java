package com.xingyeda.ehome.zhibo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.RollPagerView;
import com.ldl.okhttp.OkHttpUtils;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.AdvertisingAdapter;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.door.PopWindow;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
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


public class ShareFragment extends Fragment {


    @Bind(R.id.share_no_datas)
    ImageView shareNoDatas;
    @Bind(R.id.share_swipereLayout)
    SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.mRollPagerView)
    RollPagerView mRollPagerView;
//    @Bind(R.id.share_annunciate)
//    ImageView shareAnnunciate;
    @Bind(R.id.share_more)
    ImageView shareMore;
    private View rootView;
    private Context mContext;
    private EHomeApplication mApplication;
    private List<Camera> cameraList = new ArrayList<>();
    private CameraAdapter adapter;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    // 记录最后可见条目
    private int lastVisible;
    private GridLayoutManager mLayoutManager;
    private List<Camera> mDatas;

    // 加载页数
    int addmoreTimes = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_share_main, container, false);
        }
        ButterKnife.bind(this, rootView);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        MyLog.i("ShareFragment启动");
        mContext = this.getActivity();
        mApplication = (EHomeApplication) ((Activity) mContext).getApplication();
        initRollViewPager();
        init();


        mLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        if (mApplication.getmAd() != null) {
//            shareAnnunciate.setImageBitmap(mApplication.getmAd().getmBitmap());
//        } else {
//
//        }
//        editSearch();

        return rootView;
    }
    private void init(){
        MyLog.i("ShareFragment初始化--1");
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addmoreTimes=1;
                getShareList("1", "10");
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 不在滑动和最后可见条目是脚布局时加载更多
//                if (cameraList.size()>=10) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisible + 1 == adapter.getItemCount()) {
                        addmoreTimes++;
                        addShareList(addmoreTimes+"","6");
                    }
//                }
            }

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisible = mLayoutManager.findLastVisibleItemPosition();
            }

        });

        mSwipeLayout.setColorSchemeResources(R.color.theme_orange);
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                if (cameraList==null||cameraList.isEmpty()) {
                    getShareList("1", "10");
                    mSwipeLayout.setRefreshing(true);
                }

            }
        });

        MyLog.i("ShareFragment初始化--0");
    }

    private void getShareList(String pageIndex, String pageSize) {
        MyLog.i("分享直播列表获取--1");
        Map<String, String> params = new HashMap<>();
        params.put("index", pageIndex);
        params.put("size", pageSize);
        OkHttp.get(mContext, ConnectPath.GET_SHARE_CAMERA, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("obj")) {
                        cameraList.clear();
                        JSONObject jobj = (JSONObject) response.get("obj");
                        if (jobj.has("list")) {
                            JSONArray jan = (JSONArray) jobj.get("list");
                            if (jan != null && jan.length() != 0) {
                                for (int i = 0; i < jan.length(); i++) {
                                    Camera camera = new Camera();
                                    JSONObject jobjCamera = jan.getJSONObject(i);
                                    camera.setmRoomId(jobjCamera.has("id") ? jobjCamera.getString("id") : "");
                                    camera.setmName(jobjCamera.has("name") ? jobjCamera.getString("name") : "");
                                    camera.setmEquipmentId(jobjCamera.has("cid") ? jobjCamera.getString("cid") : "");
                                    camera.setmDescribe(jobjCamera.has("describe") ? jobjCamera.getString("describe") : "");
                                    camera.setmImagePath(jobjCamera.has("img") ? jobjCamera.getString("img") : "");
                                    cameraList.add(camera);
                                }

                            }
                        }
                        if (mSwipeLayout!=null) {
                            mSwipeLayout.setRefreshing(false);
                        }
                        if (cameraList != null && !cameraList.isEmpty()) {
                            setAdapter();
                        } else {
                            if (recyclerView!=null && shareNoDatas!=null) {
                                recyclerView.setVisibility(View.GONE);
                                shareNoDatas.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }));
        MyLog.i("分享直播列表获取--0");
    }
    private void setAdapter(){
        adapter = new CameraAdapter(cameraList);
        if (recyclerView!=null && shareNoDatas!=null) {
            recyclerView.setVisibility(View.VISIBLE);
            shareNoDatas.setVisibility(View.GONE);
            recyclerView.setAdapter(adapter);
        }
        adapter.clickItem(new CameraAdapter.ClickItem() {
            @Override
            public void onclick(View view, int position) {
                Camera camera = cameraList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("name", camera.getmName());
                bundle.putString("equipmentId", camera.getmEquipmentId());
                bundle.putString("roomId", camera.getmRoomId());
                bundle.putString("describe", camera.getmDescribe());
                BaseUtils.startActivities(mContext, ActivitySharePlay.class, bundle);
            }
        });
    }
    private void addShareList(String pageIndex, String pageSize) {
        MyLog.i("分享直播列表加载更多--1");
        Map<String, String> params = new HashMap<>();
        params.put("index", pageIndex);
        params.put("size", pageSize);
        OkHttp.get(mContext, ConnectPath.GET_SHARE_CAMERA, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("obj")) {
                        JSONObject jobj = (JSONObject) response.get("obj");
                        if (jobj.has("list")) {
                            JSONArray jan = (JSONArray) jobj.get("list");
                            if (jan != null && jan.length() != 0) {
                                for (int i = 0; i < jan.length(); i++) {
                                    Camera camera = new Camera();
                                    JSONObject jobjCamera = jan.getJSONObject(i);
                                    camera.setmRoomId(jobjCamera.has("id") ? jobjCamera.getString("id") : "");
                                    camera.setmName(jobjCamera.has("name") ? jobjCamera.getString("name") : "");
                                    camera.setmEquipmentId(jobjCamera.has("cid") ? jobjCamera.getString("cid") : "");
                                    camera.setmDescribe(jobjCamera.has("describe") ? jobjCamera.getString("describe") : "");
                                    camera.setmImagePath(jobjCamera.has("img") ? jobjCamera.getString("img") : "");
                                    cameraList.add(camera);
                                }

                            adapter.notifyItemInserted(cameraList.size());
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }));
        MyLog.i("分享直播列表加载更多--0");
    }


    @OnClick({R.id.share_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.share_more:
                PopWindow popWindow = new PopWindow((Activity) mContext);
                popWindow.showPopupWindow(shareMore);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mSwipeLayout!=null) {
            if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);
                getShareList("1", "10");
            }
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OkHttpUtils.getInstance().cancelTag(this);
        ButterKnife.unbind(this);
        MyLog.i("ShareFragment销毁");
    }


    private void initRollViewPager(){
        mRollPagerView.setAnimationDurtion(1000);
        if (mApplication.getmAb_List()!=null){
            mRollPagerView.setAdapter(new AdvertisingAdapter(mRollPagerView,mApplication.getmAb_List()));
        }

    }
}
