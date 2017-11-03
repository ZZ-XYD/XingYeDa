package com.xingyeda.ehome.zhibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.jude.rollviewpager.RollPagerView;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.ldl.imageloader.core.ImageLoader;
import com.ldl.imageloader.core.assist.FailReason;
import com.ldl.imageloader.core.listener.ImageLoadingListener;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.HomepageHttp;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.AdvertisingAdapter;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.AdvertisementBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.PopWindow;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
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
import cn.jpush.android.api.JPushInterface;

public class ActivityShareMain extends BaseActivity {
    @Bind(R.id.sightseer_no_datas)
    ImageView mNoDatas;
    @Bind(R.id.sightseer_swipereLayout)
    SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.sightseer_RollPagerView)
    RollPagerView mRollPagerView;
    @Bind(R.id.sightseer_more)
    ImageView mMore;
    private List<Camera> cameraList = new ArrayList<>();
    private CameraAdapter adapter;
    @Bind(R.id.sightseer_recycler_view)
    RecyclerView recyclerView;
    // 记录最后可见条目
    private int lastVisible;
    private GridLayoutManager mLayoutManager;

    // 加载页数
    int addmoreTimes = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sightseer_main);
        ButterKnife.bind(this);
        init();
//        ad(mContext);
//        if (mEhomeApplication.getActivityStack()!=null&&!mEhomeApplication.getActivityStack().isEmpty()) {
//            for (Activity activity : mEhomeApplication.getActivityStack()) {
//                if (activity.getClass().equals(ActivityShareMain.class)) {
//                    break;
//                } else {
//                    mEhomeApplication.finishActivity(activity);
//                }
//            }
//        }

        if (JPushInterface.isPushStopped(this))
            JPushInterface.resumePush(this);


        mLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getShareList("1", "10");

//        if (mEhomeApplication.getmAd() != null) {
//            mAnnunciate.setImageBitmap(mEhomeApplication.getmAd().getmBitmap());
//        } else {
//            ad(mContext);
//        }
        initRollViewPager();
    }

    private void init() {
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                addmoreTimes = 1;
                getShareList("1", "10");
            }
        });
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 不在滑动和最后可见条目是脚布局时加载更多
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisible + 1 == adapter.getItemCount()) {
                    addmoreTimes++;
                    addShareList(addmoreTimes + "", "6");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisible = mLayoutManager.findLastVisibleItemPosition();
            }

        });

        mSwipeLayout.setColorSchemeResources(R.color.theme_orange);
        mSwipeLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeLayout.setRefreshing(true);
            }
        });
    }

    private void getShareList(String pageIndex, String pageSize) {
        MyLog.i("获取分享直播列表：pageIndex" + pageIndex + ";pageSize:" + pageSize);
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
                        mSwipeLayout.setRefreshing(false);
                        if (cameraList != null && !cameraList.isEmpty()) {
                            adapter = new CameraAdapter(cameraList);
                            recyclerView.setVisibility(View.VISIBLE);
                            mNoDatas.setVisibility(View.GONE);
                            recyclerView.setAdapter(adapter);
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
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            mNoDatas.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }));
    }

    private void addShareList(String pageIndex, String pageSize) {
        MyLog.i("加载分享直播列表：pageIndex" + pageIndex + ";pageSize:" + pageSize);
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
    }


    @OnClick({R.id.sightseer_more, R.id.sightseer_door, R.id.sightseer_tenement, R.id.sightseer_life, R.id.sightseer_info})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sightseer_door:
                hint();
                break;
            case R.id.sightseer_tenement:
                hint();
                break;
            case R.id.sightseer_life:
                break;
            case R.id.sightseer_info:
                hint();
                break;
            case R.id.sightseer_more:
                SightseerPopWindow popWindow = new SightseerPopWindow((Activity) mContext);
                popWindow.showPopupWindow(mMore);
                break;
        }
    }

    private void hint() {
        final NormalDialog dialog = DialogShow.showSelectDialog(mContext, "此功能暂无权限，是否登录？", 2, new String[]{getResources().getString(R.string.cancel), getResources().getString(R.string.confirm)});
        dialog.setOnBtnClickL(new OnBtnClickL() {

            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }
        }, new OnBtnClickL() {
            @Override
            public void onBtnClick() {
                BaseUtils.startActivity(mContext, ActivityLogin.class);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (cameraList != null && !cameraList.isEmpty()) {
            getShareList("1", "10");
        }
    }

    // 监听返回按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            BaseUtils.startActivity(mContext, ActivityLogin.class);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initRollViewPager() {
        mRollPagerView.setAnimationDurtion(1000);
        mRollPagerView.setAdapter(new AdvertisingAdapter(mRollPagerView, mEhomeApplication.getmAb_List()));
    }
}
