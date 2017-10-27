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
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.DoorAdapter;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.AnnunciateBean;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.information.PersonalActivity;
import com.xingyeda.ehome.menu.ActivityChangeInfo;
import com.xingyeda.ehome.park.AddParkActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.NetUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
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
import static com.xingyeda.ehome.R.string.share;
import static com.xingyeda.ehome.base.BaseActivity.mEhomeApplication;
import static com.xingyeda.ehome.base.BaseActivity.mScreenH;
import static com.xingyeda.ehome.base.BaseActivity.mScreenW;

/**
 * @author 李达龙
 * @ClassName: FragmentDoor
 * @Description: 门禁界面
 * @date 2016-7-6
 */
public class DoorFragment extends Fragment implements PullToRefreshBase.OnRefreshListener<SwipeMenuListView> {

    @Bind(R.id.door_information)
    ImageView mInformation;
    @Bind(R.id.door_spn)
    TextView mModification;
    @Bind(R.id.door_Listview)
    PullToRefreshMenuView mList;
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

    private String shareUrl;

    private int mFlIndex = 0;
    private View rootView;
    private Context mContext;
    private EHomeApplication mApplication;
    public static final String ACTION_NAME = "RelieveBind";
    // private String[] mDoors = new String[] { "监控", "开门" };
//	public static boolean isFlHint = true;

    private SwipeMenuListView swipeMenuListView;
    private List<AnnunciateBean> mAnnunciateList;

    // private ArrayAdapter<HomeBean> mAdapter;
    private DoorAdapter mAdapter;
    private static final int REQUEST_CODE_SCAN = 0x0000;

    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";

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
//		isFlHint = true;
        // mHead.setImageResource(R.drawable.head);
        mAnnunciateList = new ArrayList<AnnunciateBean>();
        mContext = this.getActivity();
        mApplication = (EHomeApplication) ((Activity) mContext)
                .getApplication();

        registerBoradcastReceiver();
        init();
        setListView();

        return rootView;

    }

    private void init() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(wifBC, filter);
    }

    @OnClick({R.id.door_add, R.id.door_information, R.id.door_spn,
//            R.id.doo_smart_home,
            R.id.share_layout, R.id.share_cancel, R.id.share_confirm})
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
                // BaseUtils.startActivity(mContext, ActivityAddAddress.class);
                break;
            case R.id.door_information:
                BaseUtils.startActivity(mContext, PersonalActivity.class);
                break;
//            case R.id.doo_smart_home:
//                BaseUtils.startActivity(mContext, SmartHomeActivity.class);
//                break;
            case R.id.share_layout:
                shareLayout.setVisibility(View.GONE);
                break;
            case R.id.door_spn:
                Bundle bundle = new Bundle();
                if (mApplication.getmCurrentUser().getmXiaoquList().size() == 0) {
                    DialogShow.showHintDialog(mContext, "请先绑定小区");
                } else if (mApplication.getmCurrentUser().getmXiaoquList().size() == 1) {
                    DialogShow.showHintDialog(mContext, "不可修改当前默认小区");
                } else {
                    bundle.putString("type", "community");
                    BaseUtils.startActivities(mContext, ActivityChangeInfo.class,
                            bundle);
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
        if (null == mApplication.getmCurrentUser()) {
            return;
        }
        // mXiaoqu_List.clear();
        final List<HomeBean> mXiaoqu_List = new ArrayList<HomeBean>();
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", SharedPreUtil.getString(mContext, "userId", ""));
        OkHttp.get(mContext, ConnectPath.RETURN_HOUSE_PATH, params,
                new BaseStringCallback(mContext, new CallbackHandler<String>() {

                    @Override
                    public void parameterError(JSONObject response) {
                    }

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (frozenAccount != null) {
                                frozenAccount.setVisibility(View.GONE);
                            }
                            JSONArray jan2 = (JSONArray) response.get("camera");
                            if (jan2 != null && jan2.length() != 0) {
                                MyLog.i("加载摄像头猫眼：" + jan2);
                                for (int i = 0; i < jan2.length(); i++) {
                                    HomeBean bean = new HomeBean();
                                    mApplication.getmCurrentUser().setmCameraAdd(true);
                                    JSONObject jobj = jan2.getJSONObject(i);
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
                                    mXiaoqu_List.add(bean);
                                }
                            } else {
                                mApplication.getmCurrentUser().setmCameraAdd(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONArray jan1 = (JSONArray) response.get("pllist");
                            if (jan1 != null && jan1.length() != 0) {
                                MyLog.i("加载停车场：" + jan1);
                                for (int i = 0; i < jan1.length(); i++) {
                                    HomeBean bean = new HomeBean();
                                    JSONObject jobj = jan1.getJSONObject(i);
                                    bean.setmType("5");
                                    bean.setmParkId(jobj.has("cplid") ? jobj.getString("cplid") : "");
                                    bean.setmCommunityId(jobj.has("xiaoqu") ? jobj.getString("xiaoqu") : "");
                                    bean.setmParkName(jobj.has("address") ? jobj.getString("address") : "");
                                    bean.setmParkTruckSpace(jobj.has("pnum") ? jobj.getString("pnum") : "");
                                    List<HomeBean> list = DataSupport.findAll(HomeBean.class);
                                    HomeBean baseBean = null;
                                    if (list != null && !list.isEmpty()) {
                                        for (HomeBean homeBean : list) {
                                            if (homeBean.getmParkId().equals(bean.getmParkId())) {
                                                baseBean = homeBean;
                                            }
                                        }
                                    } else {
                                        bean.setmParkNickName(bean.getmParkName());
                                        bean.save();
                                        baseBean = bean;
                                    }
                                    if (baseBean == null) {
                                        bean.setmParkNickName(bean.getmParkName());
                                        mXiaoqu_List.add(bean);
                                    } else {
                                        mXiaoqu_List.add(baseBean);
                                    }


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
                                    JSONObject jobj = jan.getJSONObject(i);
                                    if (jobj.getString("isChecked").equals("1")) {
                                        bean.setmType("1");
                                        if (jobj.has("state")) {
                                            bean.setState(jobj.getString("state"));
                                            if ("1".equals(jobj.getString("state"))) {
                                                frozenAccount.setVisibility(View.VISIBLE);
                                                frozenAccount.setText(jobj.getString("rname") + "已被冻结，有疑问请联系物业管理员");
                                            }
                                        } else {
                                            bean.setState("");
                                        }
                                        bean.setmCommunityId(jobj
                                                .getString("rid"));
                                        bean.setmCommunity(jobj
                                                .getString("rname"));
                                        bean.setmPeriodsId(jobj
                                                .getString("nid"));
                                        bean.setmPeriods(jobj
                                                .getString("nname"));
                                        if (jobj.has("tid")) {
                                            bean.setmUnitId(jobj
                                                    .getString("tid"));
                                            mApplication.addMap(
                                                    jobj.getString("tid"), null);
                                        }
                                        bean.setmUnit(jobj.getString("tname"));
                                        bean.setmHouseNumber(jobj
                                                .getString("hname"));
                                        bean.setmHouseNumberId(jobj
                                                .getString("hid"));
                                        bean.setmIdentityType(jobj
                                                .getString("type"));

                                        if (jobj.has("eid")) {
                                            bean.setmEquipmentId(jobj
                                                    .getString("eid"));
                                        } else {
                                            bean.setmEquipmentId("");
                                        }
                                        if (jobj.has("isDefault")) {
                                            SharedPreUtil.put(mContext, "isChecked", true);
                                            bean.setmIsDefault(jobj
                                                    .getString("isDefault"));

                                            if (jobj.getString("isDefault")
                                                    .equals("1")) {
                                                SharedPreUtil.put(mContext, "eid", jobj.has("eid") ? jobj.getString("eid") : "");
                                                SharedPreUtil.put(mContext, "dongshu", jobj.has("tid") ? jobj.getString("tid") : "");
                                                SharedPreUtil.put(mContext, "housenum", jobj.has("hname") ? jobj.getString("hname") : "");
                                                mApplication.getmCurrentUser()
                                                        .setmXiaoqu(bean);
                                                // if (delete.equals("1"))
                                                // {BaseUtils.startActivity(mContext,ActivityHomepage.class);
                                            }
                                        }
                                    }
                                    mXiaoqu_List.add(bean);
                                }
                                mApplication.getmCurrentUser().setmXiaoquList(
                                        mXiaoqu_List);
                            } else {
                                mApplication.getmCurrentUser().setmXiaoquList(
                                        mXiaoqu_List);
                                upload();
                            }

                            if (delete.equals("2")) {
                                mList.onRefreshComplete();
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
                        if (mList != null) {
                            mList.onRefreshComplete();
                        }
                    }
                }));
        MyLog.i("设备数据列表加载--0");

    }


    private void upload() {
        MyLog.i("设备数据列表适配器加载--1");
        if (mApplication.getmCurrentUser().getmXiaoquList() != null
                && mApplication.getmCurrentUser().getmXiaoquList().size() != 0) {
            mAdapter = new DoorAdapter(mContext, mApplication
                    .getmCurrentUser().getmXiaoquList());
            if (mList != null) {
                swipeMenuListView.setDividerHeight(20);
                swipeMenuListView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
            mNoData.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);

            mAdapter.share(new DoorAdapter.ShareClickItem() {
                @Override
                public void onclick(View view, int position) {
                    MyLog.i("分享按钮");
                    final HomeBean bean = mApplication.getmCurrentUser().getmXiaoquList().get(position);
                    String url = null;
                    if (bean.getmType().equals("1")) { //门禁
                        if (SharedPreUtil.getString(mContext, "share_type").equals("")) {
                            SharedPreUtil.put(mContext, "share_type", 2 + "");

                        }
                        shareText.setText("门禁设备添加扫描");
                        url = ConnectPath.BIND_PATH + "?uid&xiaoqu=" + bean.getmCommunityId()
                                + "&qishu=" + bean.getmPeriodsId() + "&dongshu=" + bean.getmUnitId()
                                + "&housenum=" + bean.getmHouseNumber() + "&type=" + SharedPreUtil.getString(mContext, "share_type")
                                + "&sNcode=" + mApplication.getmCurrentUser().getmSNCode()
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
            mList.setVisibility(View.GONE);
        }

//		swipeMenuListView.setOnItemClickListener(itemClickListener);
        if (mApplication.getmCurrentUser().getmXiaoqu() != null
                && mApplication.getmCurrentUser().getmXiaoqu()
                .getmEquipmentId() != null) {

            if (SharedPreUtil.getBoolean(mContext, "isMenuHint")) {
                SharedPreUtil.put(mContext, "isMenuHint", false);
                menuHint();
            }
        }
        if (mApplication.getmCurrentUser().getmXiaoquList() != null && !mApplication.getmCurrentUser().getmXiaoquList().isEmpty()) {

            if (mApplication.getmCurrentUser().getmXiaoquList().size() == 0) {
                mModification.setText("请先绑定小区");
            } else {
                HomeBean bean = mApplication.getmCurrentUser().getmXiaoqu();
                if (bean != null) {
                    mModification.setText(bean.getmCommunity() + bean.getmPeriods() + bean.getmUnit() + bean.getmHouseNumber());
                }
            }
        }
        MyLog.i("设备数据列表适配器加载--0");
    }


    private void setListView() {
        if (mList == null) {
            return;
        }
        mList.setPullLoadEnabled(false);
        mList.setScrollLoadEnabled(true);
        mList.setOnRefreshListener(this);
        swipeMenuListView = mList.getRefreshableView();
        mList.onRefreshComplete();

        // 创建左滑弹出的item
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // 创建Item
                SwipeMenuItem openItem = new SwipeMenuItem(mContext);
                // 设置item的背景颜色
                openItem.setBackground(new ColorDrawable(Color.RED));
                // 设置item的宽度
                openItem.setWidth(Utils.dip2px(mContext, 90));
                // 设置item标题
                openItem.setTitle("删除");
                // 设置item字号
                openItem.setTitleSize(18);
                // 设置item字体颜色
                openItem.setTitleColor(Color.WHITE);
                // 添加到ListView的Item布局当中
                menu.addMenuItem(openItem);

            }
        };
        // set creator
        swipeMenuListView.setMenuCreator(creator);
        // 操作删除按钮的点击事件
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                HomeBean bean = mApplication.getmCurrentUser().getmXiaoquList().get(position);
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


                return false;
            }
        });

        // 操作ListView左滑时的手势操作，这里用于处理上下左右滑动冲突：开始滑动时则禁止下拉刷新和上拉加载手势操作，结束滑动后恢复上下拉操作
        swipeMenuListView
                .setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
                    @Override
                    public void onSwipeStart(int position) {
                        mList.setPullRefreshEnabled(false);
                    }

                    @Override
                    public void onSwipeEnd(int position) {
                        mList.setPullRefreshEnabled(true);
                    }
                });

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
        params.put("eid", mApplication.getmCurrentUser().getmXiaoqu()
                .getmEquipmentId());
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
                // mFlHint.setVisibility(View.VISIBLE);
                //
                // mFlTitle.setText(mAnnunciateList.get(mFlIndex).getmTitle());
                // mFlContent.setText("\t\t"
                // + mAnnunciateList.get(mFlIndex).getmContent());
            }
        }
        // String hintText = "";
        // for (AnnunciateBean bean : mAnnunciateList) {
        // hintText=hintText+bean.getmTitle()+"  ：  "+bean.getmContent()+"    ";
        // }
        // mDesc.setText(hintText);
        // if (hintText.equals("")) {
        // mDesc.setVisibility(View.GONE);
        // }
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
    public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
        uploadXiaoqu("2");
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {

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