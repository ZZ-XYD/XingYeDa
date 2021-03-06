package com.xingyeda.ehome.door;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.percent.PercentFrameLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.jovision.JVBase;
import com.jovision.account.ActivityAddCamera;
import com.jovision.account.MaoYanSetActivity;
import com.ldl.dialogshow.dialog.entity.DialogMenuItem;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.listener.OnOperItemClickL;
import com.ldl.dialogshow.dialog.widget.MaterialDialog;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.ldl.dialogshow.dialog.widget.NormalListDialog;
import com.ldl.okhttp.OkHttpUtils;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.DoorAdapter;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.base.LitePalUtil;
import com.xingyeda.ehome.bean.AnnunciateBean;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.bean.UserInfo;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.information.PersonalActivity;
import com.xingyeda.ehome.menu.ActivityChangeInfo;
import com.xingyeda.ehome.park.AddParkActivity;
import com.xingyeda.ehome.util.AppUtils;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.NetUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.util.SpaceItemDecoration;
import com.xingyeda.ehome.view.listview.PullToRefreshBase;
import com.xingyeda.ehome.view.listview.PullToRefreshMenuView;
import com.xingyeda.ehome.view.listview.SwipeMenu;
import com.xingyeda.ehome.view.listview.SwipeMenuCreator;
import com.xingyeda.ehome.view.listview.SwipeMenuItem;
import com.xingyeda.ehome.view.listview.SwipeMenuListView;
import com.xingyeda.ehome.view.listview.Utils;
import com.xingyeda.ehome.wifiOnOff.SmartHomeActivity;
import com.xingyeda.ehome.zhibo.ActivityShareModification;
import com.xingyeda.ehome.zhibo.ActivityVideoShare;
import com.xingyeda.ehome.zxing.android.CaptureActivity;
import com.xingyeda.ehome.zxing.encode.CodeCreator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.isDefault;
import static android.R.attr.path;
import static android.R.id.list;
import static android.app.Activity.RESULT_OK;
import static com.xiaomi.channel.commonutils.misc.a.f;
import static com.xingyeda.ehome.R.string.share;
import static com.xingyeda.ehome.base.BaseActivity.mEhomeApplication;
import static com.xingyeda.ehome.base.BaseActivity.mScreenH;
import static com.xingyeda.ehome.base.BaseActivity.mScreenW;
import static com.xingyeda.ehome.base.ConnectPath.LOCK_CAR;

/**
 * @author 李达龙
 * @ClassName: FragmentDoor
 * @Description: 门禁界面
 * @date 2016-7-6
 */
public class DoorFragment extends Fragment {

    @Bind(R.id.door_information)
    ImageView mInformation;
    @Bind(R.id.door_spn)
    TextView mModification;
    @Bind(R.id.door_add)
    ImageView mAddImage;
    @Bind(R.id.no_data)
    ImageView mNoData;
    @Bind(R.id.frozen_account)
    TextView frozenAccount;
    @Bind(R.id.share_img)
    ImageView shareImg;
    @Bind(R.id.share_layout)
    PercentFrameLayout shareLayout;
    @Bind(R.id.share_text)
    TextView shareText;
    @Bind(R.id.share_icon)
    ImageView shareIcon;
    @Bind(R.id.share_hint)
    TextView shareHint;
    @Bind(R.id.share_hint_layout)
    PercentFrameLayout shareHintLayout;
    @Bind(R.id.door_frame)
    FrameLayout door_frame;
    @Bind(R.id.door_swipereLayout)
    SwipeRefreshLayout mSwipereLayout;
    @Bind(R.id.door_Listview)
    RecyclerView mListview;

    private String shareUrl;

    private int mFlIndex = 0;
    private View rootView;
    private Context mContext;
    private EHomeApplication mApplication;
    public static final String ACTION_NAME = "RelieveBind";

    private List<AnnunciateBean> mAnnunciateList;

    private DoorAdapter mAdapter;
    private static final int REQUEST_CODE_SCAN = 0x0000;

    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private List<HomeBean> mCameraList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.door_fragment, container, false);
        }
        ButterKnife.bind(this, rootView);
        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }
        MyLog.i("DoorFragment启动");
        mAnnunciateList = new ArrayList<AnnunciateBean>();
        mContext = this.getActivity();
        mApplication = (EHomeApplication) ((Activity) mContext).getApplication();
        mCameraList = LitePalUtil.getCameraList();


        mListview.setLayoutManager(new LinearLayoutManager(mContext));
        mListview.setHasFixedSize(true);
        mListview.setItemAnimator(new DefaultItemAnimator());


        registerBoradcastReceiver();
        init();

        return rootView;

    }

    private void init() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(wifBC, filter);


        mSwipereLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//刷新
                uploadXiaoqu("2");
            }
        });

        mSwipereLayout.setColorSchemeResources(R.color.theme_orange);
        mSwipereLayout.post(new Runnable() {
            @Override
            public void run() {//第一次刷新
                if (mAdapter == null) {
                    uploadXiaoqu("0");
//                    mListview.addItemDecoration(new SpaceItemDecoration(20));
                    mSwipereLayout.setRefreshing(true);
                } else {
                    uploadXiaoqu("2");
                }


            }
        });

    }

    @OnClick({R.id.door_add, R.id.door_information, R.id.door_spn, R.id.share_layout, R.id.share_cancel, R.id.share_confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.door_add:
                ArrayList<DialogMenuItem> list = new ArrayList<DialogMenuItem>();
                list.add(new DialogMenuItem("绑定门禁", 0));
                list.add(new DialogMenuItem("添加猫眼", 0));
                list.add(new DialogMenuItem("添加摄像头", 0));
                list.add(new DialogMenuItem("添加摇头机", 0));
                list.add(new DialogMenuItem("添加停车场", 0));
                list.add(new DialogMenuItem("快速添加", 0));
                final NormalListDialog dialog = DialogShow.showListDialog(mContext,
                        list);
                dialog.titleTextSize_SP(18).itemTextSize(18).isTitleShow(true)
                        .title("请操作").setOnOperItemClickL(new OnOperItemClickL() {

                    @Override
                    public void onOperItemClick(AdapterView<?> parent,
                                                View view, int positions, long id) {
                        Bundle bundle = new Bundle();
                        switch (positions) {
                            case 0:// 绑定小区
                                BaseUtils.startActivity(mContext, ActivityAddAddress.class);
                                break;
                            case 1:// 猫眼
                                bundle.putString("type", "cateye");
                                BaseUtils.startActivities(mContext, ActivityAddCamera.class, bundle);
                                //								BaseUtils.startActivity(mContext, JVMaoYanPlay.class);
                                break;
                            case 2://普通摄像头
                                bundle.putString("type", "common");
                                BaseUtils.startActivities(mContext, ActivityAddCamera.class, bundle);
                                break;
                            case 3://摇头机
                                bundle.putString("type", "shake");
                                BaseUtils.startActivities(mContext, ActivityAddCamera.class, bundle);
                                break;
                            case 4://添加停车场
                                BaseUtils.startActivity(mContext, AddParkActivity.class);
                                break;
                            case 5://快速添加
                                Intent intent = new Intent(mContext, CaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE_SCAN);
                                break;
                        }
                        dialog.dismiss();

                    }
                });
                break;
            case R.id.door_information:
                BaseUtils.startActivity(mContext, PersonalActivity.class);
                break;
            case R.id.share_layout:
                shareLayout.setVisibility(View.GONE);
                break;
            case R.id.door_spn:
                Bundle bundle = new Bundle();
                if (LitePalUtil.getCommunityList() != null) {
                    if (LitePalUtil.getCommunityList().size() == 0) {
                        DialogShow.showHintDialog(mContext, "请先绑定小区");
                    } else if (LitePalUtil.getCommunityList().size() == 1) {
                        DialogShow.showHintDialog(mContext, "不可修改当前默认小区");
                    } else {
                        bundle.putString("type", "community");
                        BaseUtils.startActivities(mContext, ActivityChangeInfo.class,
                                bundle);
                    }
                } else {
                    DialogShow.showHintDialog(mContext, "请先绑定小区");
                }
                break;
            case R.id.share_cancel:
                if (shareHintLayout.isShown()) {
                    shareHintLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.share_confirm:
                if (shareHintLayout.isShown()) {
                    shareHintLayout.setVisibility(View.GONE);
                    shareAdd(shareUrl);
                }
                break;

        }

    }

    public void uploadXiaoqu(final String delete) {
        MyLog.i("设备数据列表加载--1，type = " + delete);
        if (null == LitePalUtil.getUserInfo()) {
            return;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        params.put("version", AppUtils.getVersionCode(mContext));
        OkHttp.get(mContext, ConnectPath.RETURN_HOUSE_PATH, params,
                new BaseStringCallback(mContext, new CallbackHandler<String>() {

                    @Override
                    public void parameterError(JSONObject response) {
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            LitePalUtil.deleteHomeListAll();
                            if (frozenAccount != null) {
                                frozenAccount.setVisibility(View.GONE);
                            }
                            JSONArray jan2 = (JSONArray) response.get("camera");
                            UserInfo userInfo = new UserInfo();
                            if (jan2 != null && jan2.length() != 0) {
                                MyLog.i("加载摄像头猫眼：" + jan2);
                                for (int i = 0; i < jan2.length(); i++) {
                                    HomeBean bean = new HomeBean();
                                    userInfo.setmCameraAdd(true);
                                    JSONObject jobj = jan2.getJSONObject(i);
                                    bean.setmId(SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"));
                                    bean.setmCameraId(jobj.has("serialNumber") ? jobj.getString("serialNumber") : "");
                                    bean.setmCameraName(jobj.has("name") ? jobj.getString("name") : "");
                                    if (jobj.has("type")) {
                                        if (jobj.getString("type").equals("yaotou")) {
                                            bean.setmType("3");
                                        } else if (jobj.getString("type").equals("buyaotou")) {
                                            bean.setmType("2");
                                        } else if (jobj.getString("type").equals("maoyan")) {
                                            bean.setmType("4");
                                        }
                                    }
                                    LitePalUtil.addCameraList(bean);
                                }
                            } else {
                                userInfo.setmCameraAdd(false);
                            }
                            LitePalUtil.setUserInfo(userInfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONArray jan1 = (JSONArray) response.get("pllist");
                            if (jan1 != null && jan1.length() != 0) {
                                MyLog.i("加载停车场：" + jan1);
                                for (int i = 0; i < jan1.length(); i++) {
                                    JSONObject jobj = jan1.getJSONObject(i);
                                    HomeBean bean = new HomeBean();
                                    bean.setmId(SharedPreUtil.getString(mContext, "userId"));
                                    bean.setmType("5");
                                    bean.setmParkId(jobj.has("cplid") ? jobj.getString("cplid") : "");
                                    bean.setmCommunityId(jobj.has("xiaoqu") ? jobj.getString("xiaoqu") : "");
                                    bean.setmParkName(jobj.has("address") ? jobj.getString("address") : "");
                                    bean.setmParkTruckSpace(jobj.has("pnum") ? jobj.getString("pnum") : "");
                                    bean.setmParkLock(jobj.has("lock") ? jobj.getString("lock") : "");
                                    bean.setmParkNickName(jobj.has("address") ? jobj.getString("address") : "");
                                    LitePalUtil.addParkList(bean);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONArray jan = (JSONArray) response.get("obj");
                            if (jan != null && jan.length() != 0) {
                                MyLog.i("加载门禁：" + jan);
                                for (int i = 0; i < jan.length(); i++) {
                                    HomeBean bean = new HomeBean();
                                    bean.setmId(SharedPreUtil.getString(mContext, "userId"));
                                    bean.setmType("1");
                                    JSONObject jobj = jan.getJSONObject(i);
                                    if (jobj.getString("isChecked").equals("1")) {
                                        if (jobj.has("state")) {
                                            bean.setState(jobj.getString("state"));
                                            if ("1".equals(jobj.getString("state"))) {
                                                frozenAccount.setVisibility(View.VISIBLE);
                                                frozenAccount.setText(jobj.getString("rname") + "已被冻结，有疑问请联系物业管理员");
                                            }
                                        } else {
                                            bean.setState("");
                                        }
                                        bean.setmCommunityId(jobj.has("rid")?jobj
                                                .getString("rid"):"");
                                        bean.setmCommunity(jobj.has("rname")?jobj
                                                .getString("rname"):"");
                                        bean.setmPeriodsId(jobj.has("nid")?jobj
                                                .getString("nid"):"");
                                        bean.setmPeriods(jobj.has("nname")?jobj
                                                .getString("nname"):"");
                                        if (jobj.has("tid")) {
                                            bean.setmUnitId(jobj
                                                    .getString("tid"));
                                            mApplication.addMap(
                                                    jobj.getString("tid"), null);
                                        }
                                        bean.setmUnit(jobj.has("tname")?jobj
                                                .getString("tname"):"");
                                        bean.setmHouseNumber(jobj.has("hname")?jobj
                                                .getString("hname"):"");
                                        bean.setmHouseNumberId(jobj.has("hid")?jobj
                                                .getString("hid"):"");
                                        bean.setmIdentityType(jobj.has("type")?jobj
                                                .getString("type"):"");
                                        bean.setmPhone(jobj.has("phone")?jobj.getString("phone"):"");
                                        bean.setmBase(jobj.has("base")?jobj.getString("base"):"");
                                        if (jobj.has("eid")) {
                                            bean.setmEquipmentId(jobj
                                                    .getString("eid"));
                                        } else {
                                            bean.setmEquipmentId("");
                                        }
                                        if (jobj.has("isDefault")) {
                                            SharedPreUtil.put(mContext, "isChecked", true);
                                            bean.setmIsDefault(jobj.getString("isDefault"));

                                            if (jobj.getString("isDefault").equals("1")) {
                                                SharedPreUtil.put(mContext, "eid", jobj.has("eid") ? jobj.getString("eid") : "");
                                                SharedPreUtil.put(mContext, "dongshu", jobj.has("tid") ? jobj.getString("tid") : "");
                                                SharedPreUtil.put(mContext, "housenum", jobj.has("hname") ? jobj.getString("hname") : "");
                                                LitePalUtil.setHomeBean(bean);

                                                if (bean != null) {
                                                    if (mModification != null) {
                                                        mModification.setText(bean.getmCommunity() + bean.getmPeriods() + bean.getmUnit() + bean.getmHouseNumber());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    LitePalUtil.addHomeList(bean);
                                }
                            } else {
                                if (mModification != null) {
                                    mModification.setText("请先绑定小区");
                                }
                                upload();
                            }
                            if (mSwipereLayout != null) {
                                mSwipereLayout.setRefreshing(false);
                            }
                            if (delete.equals("2")) {
                                upload();
                            }
                            if (delete.equals("0")) {
                                upload();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure() {
                        if (mSwipereLayout != null) {
                            mSwipereLayout.setRefreshing(false);
                        }
                    }
                }));
        MyLog.i("设备数据列表加载--0");

    }


    private void upload() {
        MyLog.i("设备数据列表适配器加载--1");
        List<HomeBean> mCamera = LitePalUtil.getCameraList();
        if (mCameraList!=null && !mCameraList.isEmpty() && mCamera!=null && !mCamera.isEmpty()) {
            for (HomeBean homeBean : mCameraList) {
                boolean flag = true ;
                for (HomeBean bean : mCamera) {
                    if (homeBean.getmCameraId().equals(bean.getmCameraId())) {
                        flag = false;
                    }
                }
                if (flag) {
                    JVBase.delDev(homeBean.getmCameraId());
                }

            }
//        for (HomeBean homeBean : mCameraList) {
//            boolean b = mCamera.contains(homeBean);
//            if (!mCamera.contains(homeBean)) {
//                JVBase.delDev(homeBean.getmCameraId());
//            }
//        }
        }
        if (LitePalUtil.getHomeList() != null && LitePalUtil.getHomeList().size() != 0) {
            if (mListview == null) {
                return;
            }
//            if (mAdapter!=null) {
//                mAdapter.notifyDataSetChanged();
//            }else{
            mAdapter = new DoorAdapter(LitePalUtil.getHomeList());
            mListview.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
//            }
            mNoData.setVisibility(View.GONE);
            mListview.setVisibility(View.VISIBLE);
            mAdapter.longClick(new DoorAdapter.LongClick() {
                @Override
                public void onLongClick(View view, int position) {
                    HomeBean bean = LitePalUtil.getHomeList().get(position);
                    if (bean.getmType().equals("1")) {
                        dialog(1, R.string.whether_relieve_bind, bean);
                    } else if (bean.getmType().equals("2")) {
                        dialog(2, R.string.is_remove_camera, bean);
                    } else if (bean.getmType().equals("3")) {
                        dialog(2, R.string.is_remove_camera, bean);
                    } else if (bean.getmType().equals("4")) {
                        dialog(2, R.string.is_remove_cateye, bean);
                    } else if (bean.getmType().equals("5")) {
                        dialog(3, R.string.is_remove_park, bean);
                    }
                }
            });
            mAdapter.clickIco(new DoorAdapter.ClickItem() {
                @Override
                public void onclick(View view, int position) {
                    MyLog.i("分享按钮");
                    final HomeBean bean = LitePalUtil.getHomeList().get(position);
                    String url = null;
                    if (bean.getmType().equals("1")) { //门禁
                        if (SharedPreUtil.getString(mContext, "share_type").equals("")) {
                            SharedPreUtil.put(mContext, "share_type", 2 + "");

                        }
                        shareText.setText("门禁设备添加扫描");
                        url = ConnectPath.BIND_PATH + "?uid&xiaoqu=" + bean.getmCommunityId()
                                + "&qishu=" + bean.getmPeriodsId() + "&dongshu=" + bean.getmUnitId()
                                + "&housenum=" + bean.getmHouseNumber() + "&type=" + SharedPreUtil.getString(mContext, "share_type")
                                + "&sNcode=" + LitePalUtil.getUserInfo().getmSNCode()
                                + "&clientType=1" + "," + bean.getmCommunity() + bean.getmPeriods() + bean.getmUnit() + bean.getmHouseNumber() + "," + bean.getmType();
                    } else if (bean.getmType().equals("2")) { //摄像机
                        shareText.setText("摄像机设备添加扫描");
                        url = ConnectPath.ADD_CAMERA + "?uid&num=" + bean.getmCameraId()
                                + "&name=" + bean.getmCameraName()
                                + "&username=" + SharedPreUtil.getString(mContext, "userName")
                                + "&userpwd=" + SharedPreUtil.getString(mContext, "userPwd")
                                + "&type=buyaotou" + "," + bean.getmCameraId() + "," + bean.getmType();
                    } else if (bean.getmType().equals("3")) { //摇头机
                        shareText.setText("摇头机设备添加扫描");
                        url = ConnectPath.ADD_CAMERA + "?uid&num=" + bean.getmCameraId()
                                + "&name=" + bean.getmCameraName()
                                + "&username=" + SharedPreUtil.getString(mContext, "userName")
                                + "&userpwd=" + SharedPreUtil.getString(mContext, "userPwd")
                                + "&type=yaotou" + "," + bean.getmCameraId() + "," + bean.getmType();
                    } else if (bean.getmType().equals("4")) { //猫眼
                        shareText.setText("猫眼设备添加扫描");
                        url = ConnectPath.ADD_CAMERA + "?uid&num=" + bean.getmCameraId()
                                + "&name=" + bean.getmCameraName()
                                + "&username=" + SharedPreUtil.getString(mContext, "userName")
                                + "&userpwd=" + SharedPreUtil.getString(mContext, "userPwd")
                                + "&type=maoyan" + "," + bean.getmCameraId() + "," + bean.getmType();
                    }
                    if (bean.getmType().equals("2") || bean.getmType().equals("3")) {
                        final String path = url;
                        ArrayList<DialogMenuItem> list = new ArrayList<DialogMenuItem>();
                        list.add(new DialogMenuItem("分享", 0));
                        list.add(new DialogMenuItem("直播", 0));
                        list.add(new DialogMenuItem("取消", 0));
                        final NormalListDialog dialog = DialogShow.showListDialog(mContext,
                                list);
                        dialog.titleTextSize_SP(18).itemTextSize(18).isTitleShow(true)
                                .title("请操作").setOnOperItemClickL(new OnOperItemClickL() {

                            @Override
                            public void onOperItemClick(AdapterView<?> parent,
                                                        View view, int positions, long id) {
                                Bundle bundle = new Bundle();
                                switch (positions) {
                                    case 0:// 分享
                                        if (path != null) {
                                            ViewGroup.LayoutParams para = shareImg.getLayoutParams();
                                            para.width = mScreenW / 4 * 3;//修改宽度
                                            para.height = mScreenW / 4 * 3;//修改高度
                                            shareImg.setLayoutParams(para);
                                            shareLayout.setVisibility(View.VISIBLE);
                                            try {
                                                Bitmap bitmap = CodeCreator.createQRCode(path);
                                                shareImg.setImageBitmap(bitmap);
                                            } catch (WriterException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            BaseUtils.showShortToast(mContext, "分享失败");
                                        }
                                        break;
                                    case 1:// 直播
                                        bundle.putString("equipmentId", bean.getmCameraId());
                                        bundle.putString("mhousenumberId", bean.getmHouseNumberId());
                                        BaseUtils.startActivities(mContext, ActivityVideoShare.class, bundle);
                                        break;
                                    case 2:// 取消
                                        dialog.dismiss();
                                        break;
                                }
                                dialog.dismiss();

                            }
                        });
                    } else if (bean.getmType().equals("5")) {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id", bean.getmParkId());
                        OkHttp.get(mContext, ConnectPath.LOCK_CAR, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                uploadXiaoqu("2");
                                if (bean.getmParkLock().equals("0")) {
                                    BaseUtils.showShortToast(mContext, "锁车成功");
                                } else if (bean.getmParkLock().equals("1")) {
                                    BaseUtils.showShortToast(mContext, "解锁成功");
                                }
                            }
                        }));
                    } else {
                        if (url != null) {
                            ViewGroup.LayoutParams para = shareImg.getLayoutParams();
                            para.width = mScreenW / 4 * 3;//修改宽度
                            para.height = mScreenW / 4 * 3;//修改高度
                            shareImg.setLayoutParams(para);
                            shareLayout.setVisibility(View.VISIBLE);
                            try {
                                Bitmap bitmap = CodeCreator.createQRCode(url);
                                shareImg.setImageBitmap(bitmap);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                        } else {
                            BaseUtils.showShortToast(mContext, "分享失败");
                        }
                    }


                }
            });
        } else {
            mNoData.setVisibility(View.VISIBLE);
            mListview.setVisibility(View.GONE);
        }

        if (LitePalUtil.getHomeBean() != null && LitePalUtil.getHomeBean().getmEquipmentId() != null) {
            if (SharedPreUtil.getBoolean(mContext, "isMenuHint")) {
                SharedPreUtil.put(mContext, "isMenuHint", false);
                menuHint();
            }
        }
        if (LitePalUtil.getHomeList() != null && !LitePalUtil.getHomeList().isEmpty()) {

            if (LitePalUtil.getHomeList().size() == 0) {
                if (mModification != null) {
                    mModification.setText("请先绑定小区");
                }
            } else {
                HomeBean bean = LitePalUtil.getHomeBean();
                if (bean != null) {
                    if (mModification != null) {
                        mModification.setText(bean.getmCommunity() + bean.getmPeriods() + bean.getmUnit() + bean.getmHouseNumber());
                    }
                }
            }
        }
        MyLog.i("设备数据列表适配器加载--0");
    }

    private void dialog(final int type, int comtent, final HomeBean bean) {
        final NormalDialog dialog = DialogShow.showSelectDialog(mContext, getResources().getString(comtent));
        dialog.setOnBtnClickL(new OnBtnClickL() {

            @Override
            public void onBtnClick() {
                dialog.dismiss();
            }

        }, new OnBtnClickL() {

            @Override
            public void onBtnClick() {
                dialog.dismiss();
                MyLog.i("设备删除选择--1");
                switch (type) {
                    case 1:
                        final String isDefault = bean.getmIsDefault();
                        if (isDefault.equals("1")) {

                            final NormalDialog ensure_dialog = DialogShow
                                    .showSelectDialog(
                                            mContext,
                                            getResources()
                                                    .getString(
                                                            R.string.confirm_relieve_bind));
                            ensure_dialog.setOnBtnClickL(
                                    new OnBtnClickL() {

                                        @Override
                                        public void onBtnClick() {
                                            ensure_dialog.dismiss();
                                        }
                                    }, new OnBtnClickL() {

                                        @Override
                                        public void onBtnClick() {
                                            ensure_dialog.dismiss();
                                            relieveBind(bean, isDefault);
                                        }
                                    });
                        } else {
                            relieveBind(bean, isDefault);
                        }
                        break;
                    case 2:
                        MaoYanSetActivity.updateCameraName(mContext, "delete", bean.getmCameraId(), "");
                        break;
                    case 3:
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("cpkId", bean.getmParkId());
                        OkHttp.get(mContext, ConnectPath.CAR_REMOVE, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                uploadXiaoqu("0");
                            }
                        }));
                        break;

                }
                MyLog.i("设备删除选择--0");
            }
        });
    }

    // 解除绑定
    private void relieveBind(HomeBean bean, String isDefault) {
        MyLog.i("设备删除接口--1;bean = " + bean.toString());
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        params.put("dongshu", bean.getmUnitId());
        params.put("hid", bean.getmHouseNumberId());
        OkHttp.get(mContext, ConnectPath.CLEARBIND_PATH, params,
                new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        BaseUtils.showShortToast(mContext,
                                R.string.relieve_bind_prosperity);
                        uploadXiaoqu("0");
                        // Intent mIntent = new Intent(ACTION_NAME);
                        // mIntent.putExtra("yaner", "delete");
                        // // 发送广播
                        // mContext.sendBroadcast(mIntent);
                    }
                }));
        MyLog.i("设备删除接口--0");
    }

    private void menuHint() {
        MyLog.i("通知弹出--1");
        Map<String, String> params = new HashMap<String, String>();
        params.put("eid", LitePalUtil.getHomeBean().getmEquipmentId());
        OkHttp.get(mContext, ConnectPath.MENUHINT_PATH, params,
                new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = (JSONArray) response
                                    .get("obj");
                            if (jsonArray != null && jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jobj = jsonArray
                                            .getJSONObject(i);
                                    AnnunciateBean bean = new AnnunciateBean();
                                    bean.setmTitle(jobj.getString("title"));
                                    bean.setmContent(jobj.getString("content"));
                                    bean.setmTime(jobj.getString("createTime"));
                                    mAnnunciateList.add(bean);
                                }
                            }
                            setMenuHint();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }));
        MyLog.i("通知弹出--0");
    }

    private void setMenuHint() {
        MyLog.i("设置弹出通知内容--1");
        if (ActivityHomepage.isFlHint) {
            if (mAnnunciateList != null && !mAnnunciateList.isEmpty()) {
                ActivityHomepage.isFlHint = false;
                final MaterialDialog dialog = DialogShow.showMessageDialog(
                        mContext, mAnnunciateList.get(mFlIndex).getmTitle(),
                        "\t\t" + mAnnunciateList.get(mFlIndex).getmContent(),
                        3, new String[]{"确定", "下一条", "上一条"});
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnBtnClickL(new OnBtnClickL() {// left btn click
                    // listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                }, new OnBtnClickL() {// right btn click listener
                    @Override
                    public void onBtnClick() {
                        if (mFlIndex < (mAnnunciateList.size() - 1)) {
                            mFlIndex++;
                            dialog.setTitle(mAnnunciateList.get(
                                    mFlIndex).getmTitle());
                            dialog.setContent("\t\t"
                                    + mAnnunciateList.get(mFlIndex)
                                    .getmContent());
                        } else {
                            BaseUtils.showShortToast(mContext, "后面没有了");
                        }

                    }
                }, new OnBtnClickL() {// middle btn click listener
                    @Override
                    public void onBtnClick() {
                        if (mFlIndex > 0) {
                            mFlIndex--;
                            dialog.setTitle(mAnnunciateList.get(
                                    mFlIndex).getmTitle());
                            dialog.setContent("\t\t"
                                    + mAnnunciateList.get(mFlIndex)
                                    .getmContent());
                        } else {
                            BaseUtils.showShortToast(mContext, "前面没有了");
                        }
                    }
                });
            }
        }
        MyLog.i("设置弹出通知内容--0");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mContext.unregisterReceiver(mBroadcastReceiver);
        mContext.unregisterReceiver(wifBC);
        OkHttpUtils.getInstance().cancelTag(this);
        ButterKnife.unbind(this);
        MyLog.i("DoorFragment销毁");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetUtils.isConnected(mContext)) {
            if (SharedPreUtil.getBoolean(mContext, "isDoor_Upload")) {
                SharedPreUtil.put(mContext, "isDoor_Upload", false);
                this.uploadXiaoqu("0");
            } else {
                upload();
            }
        }
    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(DoorFragment.ACTION_NAME);
        // 注册广播
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DoorFragment.ACTION_NAME)) {
                uploadXiaoqu("0");
            }
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyLog.i("扫描返回--1");
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(DECODED_CONTENT_KEY);
//                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                String spStr[] = content.split(",");
                shareUrl = spStr[0].replaceAll("uid", "uid=" + SharedPreUtil.getString(mContext, "userId", ""));

                shareHintLayout.setVisibility(View.VISIBLE);
                if ("1".equals(spStr[2])) {
                    shareIcon.setBackgroundResource(R.mipmap.xiaoqu_logo);
                    shareHint.setText("是否添加" + spStr[1]);
                } else if ("2".equals(spStr[2])) {
                    shareIcon.setBackgroundResource(R.mipmap.camera_logo);
                    shareHint.setText("是否添加设备" + spStr[1]);
                } else if ("3".equals(spStr[2])) {
                    shareIcon.setBackgroundResource(R.mipmap.shake_logo);
                    shareHint.setText("是否添加设备" + spStr[1]);
                } else if ("4".equals(spStr[2])) {
                    shareIcon.setBackgroundResource(R.mipmap.cat_eye_logo);
                    shareHint.setText("是否添加设备" + spStr[1]);
                }
            }

        }
        MyLog.i("扫描返回--0");

    }

    private void shareAdd(String url) {
        MyLog.i("扫描快速添加--1");
        OkHttp.get(mContext, url, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("200")) {
                        BaseUtils.showShortToast(mContext, R.string.add_prosperity);
                        uploadXiaoqu("0");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }));
        MyLog.i("扫描快速添加--0");
    }


    // wifi+3g网络状态广播接收器
    private BroadcastReceiver wifBC = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifiNetworkInfo.isConnected() && !mobileNetworkInfo.isConnected()) {
                door_frame.setVisibility(View.GONE);
            } else if (!wifiNetworkInfo.isConnected() && mobileNetworkInfo.isConnected()) {
                door_frame.setVisibility(View.GONE);
            } else {
                door_frame.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, "当前网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    };
}