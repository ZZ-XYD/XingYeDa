package com.xingyeda.ehome.zhibo;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.ldl.dialogshow.dialog.entity.DialogMenuItem;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.listener.OnOperItemClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.ldl.dialogshow.dialog.widget.NormalListDialog;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.BaseActivity;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.BaseUtils;

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


public class ActivityMyShare extends BaseActivity {

    @Bind(R.id.my_share_recyclerview)
    RecyclerView myShareRecyclerview;
    @Bind(R.id.my_share_no_datas)
    ImageView myShareNoDatas;
    private List<Camera> cameraList = new ArrayList<>();
    private CameraAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_share);
        ButterKnife.bind(this);
        getShareList();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        myShareRecyclerview.setLayoutManager(layoutManager);
    }

    private void getShareList() {
        Map<String, String> params = new HashMap<>();
        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
        OkHttp.get(mContext, ConnectPath.CAMERA_MY_SHARE, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("obj")) {
                        cameraList.clear();
                        JSONArray jobj = (JSONArray) response.get("obj");
                        if (jobj != null && jobj.length() != 0) {
                            for (int i = 0; i < jobj.length(); i++) {
                                Camera camera = new Camera();
                                JSONObject jobjCamera = jobj.getJSONObject(i);
                                camera.setmRoomId(jobjCamera.has("id") ? jobjCamera.getString("id") : "");
                                camera.setmName(jobjCamera.has("name") ? jobjCamera.getString("name") : "");
                                camera.setmEquipmentId(jobjCamera.has("cid") ? jobjCamera.getString("cid") : "");
                                camera.setmDescribe(jobjCamera.has("describe") ? jobjCamera.getString("describe") : "");
                                camera.setmImagePath(jobjCamera.has("image") ? jobjCamera.getString("image") : "");
                                cameraList.add(camera);
                            }
                        }
                        if (cameraList != null && !cameraList.isEmpty()) {
                            adapter = new CameraAdapter(cameraList);
                            myShareRecyclerview.setVisibility(View.VISIBLE);
                            myShareNoDatas.setVisibility(View.GONE);
                            myShareRecyclerview.setAdapter(adapter);
                            adapter.clickItem(new CameraAdapter.ClickItem() {
                                @Override
                                public void onclick(View view, final int position) {
                                    final Camera camera = cameraList.get(position);
                                    ArrayList<DialogMenuItem> list = new ArrayList<DialogMenuItem>();
                                    list.add(new DialogMenuItem("删除", 0));
                                    list.add(new DialogMenuItem("修改", 0));
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
                                                case 0:// 删除
                                                    final NormalDialog dialogDelete = DialogShow.showSelectDialog(mContext,"是否删除分享",2,new String[] { getResources().getString(R.string.cancel),getResources().getString(R.string.confirm)});
                                                    dialogDelete.setOnBtnClickL(new OnBtnClickL() {

                                                        @Override
                                                        public void onBtnClick() {
                                                            dialogDelete.dismiss();
                                                        }
                                                    },new OnBtnClickL() {
                                                        @Override
                                                        public void onBtnClick() {
                                                            delete(camera.getmRoomId());
                                                            dialogDelete.dismiss();
                                                        }
                                                    });

                                                    break;
                                                case 1:// 修改
                                                    bundle.putString("roomId", camera.getmRoomId());
                                                    bundle.putString("title", camera.getmName());
                                                    bundle.putString("describe", camera.getmDescribe());
                                                    BaseUtils.startActivities(mContext, ActivityShareModification.class, bundle);
                                                    break;
                                                case 2:// 取消
                                                    dialog.dismiss();
                                                    break;
                                            }
                                            dialog.dismiss();

                                        }
                                    });
                                }
                            });
                        } else {
                            myShareRecyclerview.setVisibility(View.GONE);
                            myShareNoDatas.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraList != null && !cameraList.isEmpty()) {
            getShareList();
        }
    }

    private void delete(String roomId) {
        Map<String, String> params = new HashMap<>();
        params.put("uid", mEhomeApplication.getmCurrentUser().getmId());
        params.put("roomId", roomId);
        OkHttp.get(mContext, ConnectPath.CAMERA_DELETE_SHARE, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
                BaseUtils.showShortToast(mContext,"删除成功");
                getShareList();
            }
        }));
    }

    @OnClick(R.id.my_share_back)
    public void onViewClicked() {
        finish();
    }
}
