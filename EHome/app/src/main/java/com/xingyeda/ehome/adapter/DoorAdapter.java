package com.xingyeda.ehome.adapter;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;

import com.google.zxing.WriterException;
import com.jovision.account.JVMaoYanActivity;
import com.jovision.account.JVPlayActivity;
import com.jovision.account.MaoYanSetActivity;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.ActivityVideo;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.menu.ActivityChangeInfo;
import com.xingyeda.ehome.park.ParkHistoryActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

public class DoorAdapter extends BaseAdapter {
    private List<HomeBean> mHomeBeans;
    private LayoutInflater inflater;
    private Context mContext;
    private EHomeApplication mApplication;
    private ShareClickItem mShareClickItem;

    public void share(ShareClickItem shareClickItem) {
        mShareClickItem = shareClickItem;
    }

    public interface ShareClickItem {
        public void onclick(View view, int position);
    }

    public DoorAdapter(Context context, List<HomeBean> mXiaoqu_List) {
        this.mHomeBeans = mXiaoqu_List;
        this.inflater = LayoutInflater.from(context);
        mContext = context;
        mApplication = (EHomeApplication) ((Activity) mContext).getApplication();
    }

    //总共多少条目
    @Override
    public int getCount() {
        return mHomeBeans.size();
    }

    //返回某个条目的内容         position:条目的位置    从0开始
    @Override
    public Object getItem(int position) {
        return mHomeBeans.get(position);
    }

    //返回某个条目的id
    @Override
    public long getItemId(int position) {
        return position;
    }

    //条目长什么样子
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        /**
         * ListView的优化
         * 1.重复利用convertView，减少去将布局文件加载成View的次数（减少IO的次数）
         * 2.不让条目的加载每次都到窗口上找控件
         */

        ViewHolder viewHolder = null;
        if (convertView == null) {
            //将布局加载成View
            convertView = inflater.inflate(R.layout.item_listview_door, null);

            //将控件试用内部类存储起来
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        //动态赋值
        final HomeBean bean = mHomeBeans.get(position);
        if (bean.getmType().equals("1")) {
            viewHolder.tvXiaoqu.setText(bean.getmCommunity());
            viewHolder.tvQishu.setText(bean.getmPeriods());
            viewHolder.tvDongshu.setText(bean.getmUnit());
            viewHolder.tvDoorplate.setText(bean.getmHouseNumber());
            viewHolder.mType.setText(" 门  禁 ");
            viewHolder.imageLogo.setBackgroundResource(R.mipmap.xiaoqu_logo);

            Drawable drawable2 = mContext.getResources().getDrawable(R.drawable.but_share);
            drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
            viewHolder.mOption0.setCompoundDrawables(null, drawable2, null, null);

            Drawable drawable = mContext.getResources().getDrawable(R.drawable.but_open_door);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mOption1.setCompoundDrawables(null, drawable, null, null);

            Drawable drawable1 = mContext.getResources().getDrawable(R.drawable.but_monitoring);
            drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
            viewHolder.mOption2.setCompoundDrawables(null, drawable1, null, null);
            viewHolder.mOption0.setText("分享");
            viewHolder.mOption1.setText("开门");
            viewHolder.mOption2.setText("监控");

//             viewHolder.mOption1.setBackgroundResource(R.drawable.but_open_door);
//             viewHolder.mOption2.setBackgroundResource(R.drawable.but_monitoring);
            viewHolder.mOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!"".equals(bean.getState())) {
                        if ("1".equals(bean.getState())) {
                            DialogShow.showHintDialog(mContext, mContext.getResources().getString(R.string.door_hint_text));
                        } else {
                            openDoor(SharedPreUtil.getString(mContext, "userId", ""), bean.getmEquipmentId(), bean.getmUnitId(), bean.getmHouseNumber());
                        }
                    } else {
                        openDoor(SharedPreUtil.getString(mContext, "userId", ""), bean.getmEquipmentId(), bean.getmUnitId(), bean.getmHouseNumber());
                    }
                }
            });//开门
            viewHolder.mOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!"".equals(bean.getState())) {
                        if ("1".equals(bean.getState())) {
                            DialogShow.showHintDialog(mContext, mContext.getResources().getString(R.string.door_hint_text));
                        } else {
                            monitoring(bean);
                        }
                    } else {
                        monitoring(bean);
                    }
                }
            });//监控
        } else if (bean.getmType().equals("2")) {
            viewHolder.tvXiaoqu.setText(bean.getmCameraName());
            viewHolder.tvQishu.setText("");
            viewHolder.tvDongshu.setText(bean.getmCameraId());
            viewHolder.tvDoorplate.setText("");
            viewHolder.mType.setText(" 摄像机 ");
            viewHolder.imageLogo.setBackgroundResource(R.mipmap.camera_logo);
            Drawable drawable2 = mContext.getResources().getDrawable(R.drawable.but_share);
            drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
            viewHolder.mOption0.setCompoundDrawables(null, drawable2, null, null);

            Drawable drawable = mContext.getResources().getDrawable(R.drawable.but_examine);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mOption1.setCompoundDrawables(null, drawable, null, null);

            Drawable drawable1 = mContext.getResources().getDrawable(R.drawable.but_modification);
            drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
            viewHolder.mOption2.setCompoundDrawables(null, drawable1, null, null);
            viewHolder.mOption0.setText("分享");
            viewHolder.mOption1.setText("监控");
            viewHolder.mOption2.setText("设置");
//             viewHolder.mOption1.setBackgroundResource(R.drawable.but_examine);
//             viewHolder.mOption2.setBackgroundResource(R.drawable.but_modification);
            viewHolder.mOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//监控
                    Bundle bundle = new Bundle();
                    bundle.putString("cameraId", bean.getmCameraId());
                    bundle.putString("cameraName", bean.getmCameraName());
                    bundle.putString("type", "generalCamera");
                    if (bean.getmCameraId() != null) {
                        BaseUtils.startActivities(mContext, JVPlayActivity.class, bundle);
                    } else {
                        DialogShow.showHintDialog(mContext, "摄像头id为空");
                    }
                }
            });
            viewHolder.mOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//修改
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "camera0");
                    bundle.putString("cameraId", bean.getmCameraId());
                    if (bean.getmCameraId() != null) {
                        BaseUtils.startActivities(mContext, MaoYanSetActivity.class, bundle);
                    } else {
                        DialogShow.showHintDialog(mContext, "摄像头id为空");
                    }
                }
            });
        } else if (bean.getmType().equals("3")) {
            viewHolder.tvXiaoqu.setText(bean.getmCameraName());
            viewHolder.tvQishu.setText("");
            viewHolder.tvDongshu.setText(bean.getmCameraId());
            viewHolder.tvDoorplate.setText("");
            viewHolder.mType.setText(" 摇头机 ");
            viewHolder.imageLogo.setBackgroundResource(R.mipmap.shake_logo);

            Drawable drawable2 = mContext.getResources().getDrawable(R.drawable.but_share);
            drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
            viewHolder.mOption0.setCompoundDrawables(null, drawable2, null, null);

            Drawable drawable = mContext.getResources().getDrawable(R.drawable.but_examine);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mOption1.setCompoundDrawables(null, drawable, null, null);

            Drawable drawable1 = mContext.getResources().getDrawable(R.drawable.but_modification);
            drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
            viewHolder.mOption2.setCompoundDrawables(null, drawable1, null, null);
            viewHolder.mOption0.setText("分享");
            viewHolder.mOption1.setText("监控");
            viewHolder.mOption2.setText("设置");
//			viewHolder.mOption1.setBackgroundResource(R.drawable.but_examine);
//			viewHolder.mOption2.setBackgroundResource(R.drawable.but_modification);
            viewHolder.mOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//监控
                    Bundle bundle = new Bundle();
                    bundle.putString("cameraId", bean.getmCameraId());
                    bundle.putString("cameraName", bean.getmCameraName());
                    bundle.putString("type", "shakingCamera");
                    if (bean.getmCameraId() != null) {
                        BaseUtils.startActivities(mContext, JVPlayActivity.class, bundle);
                    } else {
                        DialogShow.showHintDialog(mContext, "摄像头id为空");
                    }
                }
            });
            viewHolder.mOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//修改
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "camera1");
                    bundle.putString("cameraId", bean.getmCameraId());
                    BaseUtils.startActivities(mContext, MaoYanSetActivity.class, bundle);
                }
            });
        } else if (bean.getmType().equals("4")) {
            viewHolder.tvXiaoqu.setText(bean.getmCameraName());
            viewHolder.tvQishu.setText("");
            viewHolder.tvDongshu.setText(bean.getmCameraId());
            viewHolder.tvDoorplate.setText("");
            viewHolder.mType.setText(" 猫  眼 ");
            viewHolder.imageLogo.setBackgroundResource(R.mipmap.cat_eye_logo);

            Drawable drawable2 = mContext.getResources().getDrawable(R.drawable.but_share);
            drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
            viewHolder.mOption0.setCompoundDrawables(null, drawable2, null, null);

            Drawable drawable = mContext.getResources().getDrawable(R.drawable.but_examine);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mOption1.setCompoundDrawables(null, drawable, null, null);

            Drawable drawable1 = mContext.getResources().getDrawable(R.drawable.but_modification);
            drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
            viewHolder.mOption2.setCompoundDrawables(null, drawable1, null, null);
            viewHolder.mOption0.setText("分享");
            viewHolder.mOption1.setText("监控");
            viewHolder.mOption2.setText("设置");
//			viewHolder.mOption1.setBackgroundResource(R.drawable.but_examine);
//			viewHolder.mOption2.setBackgroundResource(R.drawable.but_modification);
            viewHolder.mOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("cameraId", bean.getmCameraId());
                    bundle.putString("type", "see");
                    if (bean.getmCameraId() != null) {
                        BaseUtils.startActivities(mContext, JVMaoYanActivity.class, bundle);
                    } else {
                        DialogShow.showHintDialog(mContext, "摄像头id为空");
                    }
                }
            });//监控
            viewHolder.mOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "camera2");
                    bundle.putString("cameraId", bean.getmCameraId());
                    BaseUtils.startActivities(mContext, MaoYanSetActivity.class, bundle);
                }
            });//修改
        } else if (bean.getmType().equals("5")) {
            viewHolder.tvXiaoqu.setText(bean.getmParkNickName());
            viewHolder.tvQishu.setText("");
            viewHolder.tvDongshu.setText("");
            viewHolder.tvDoorplate.setText("");
            viewHolder.mType.setText(" 停车场 ");
            viewHolder.imageLogo.setBackgroundResource(R.mipmap.park_logo);
            Drawable drawable2 = null;
            if (bean.getmParkLock().equals("1")) {
                drawable2 = mContext.getResources().getDrawable(R.drawable.but_lockset1);
            }else {
                 drawable2 = mContext.getResources().getDrawable(R.drawable.but_lockset);
            }
            drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
            viewHolder.mOption0.setCompoundDrawables(null, drawable2, null, null);

//			Drawable drawable= mContext.getResources().getDrawable(R.drawable.but_deblocking);
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.but_month_card);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            viewHolder.mOption1.setCompoundDrawables(null, drawable, null, null);

            Drawable drawable1 = mContext.getResources().getDrawable(R.drawable.but_modification);
            drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
            viewHolder.mOption2.setCompoundDrawables(null, drawable1, null, null);
            viewHolder.mOption0.setText("锁车");
            viewHolder.mOption1.setText("记录");
//            viewHolder.mOption1.setText("出入记录");
            viewHolder.mOption2.setText("设置");
//			viewHolder.mOption1.setBackgroundResource(R.drawable.but_examine);
//			viewHolder.mOption2.setBackgroundResource(R.drawable.but_modification);
            viewHolder.mOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseUtils.startActivity(mContext, ParkHistoryActivity.class);
                }
            });//出入记录
            viewHolder.mOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "park");
                    bundle.putString("id", bean.getmParkId());
                    BaseUtils.startActivities(mContext, ActivityChangeInfo.class, bundle);
                }
            });//月卡
        }


        viewHolder.mOption0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShareClickItem != null) {
                    mShareClickItem.onclick(v, position);
                }
            }
        });


        return convertView;
    }

    class ViewHolder {

        @Bind(R.id.door_adapter_image)
        ImageView imageLogo;//小区logo
        @Bind(R.id.door_list_xiaoqu)
        TextView tvXiaoqu;//小区
        @Bind(R.id.door_list_qishu)
        TextView tvQishu;//期数
        @Bind(R.id.door_list_dongshu)
        TextView tvDongshu;//栋数
        @Bind(R.id.door_list_doorplate)
        TextView tvDoorplate;  //门牌号
        @Bind(R.id.type_text)
        TextView mType;//设备类型
        @Bind(R.id.door_option0)
        TextView mOption0;//操作1
        @Bind(R.id.door_option1)
        TextView mOption1;//操作1
        @Bind(R.id.door_option2)
        TextView mOption2;//操作2

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void openDoor(String id, String eid, String dongshu, String housenum) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", id);
        params.put("eid", eid);
        params.put("dongshu", dongshu);
        params.put("housenum", housenum);
        OkHttp.get(mContext, ConnectPath.OPENDOOR_PATH, params,
                new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        BaseUtils.showShortToast(mContext,
                                R.string.open_door_prosperity);
                    }
                }));

    }

    private void monitoring(HomeBean bean) {

        Bundle bundle = new Bundle();
        bundle.putString("dongshu", bean.getmUnitId());
        bundle.putString("eid", bean.getmEquipmentId());
        bundle.putString("housenum", bean.getmHouseNumber());
        bundle.putString("type", "monitor");
        bundle.putString("code", null);
        bundle.putString("jie", null);
        bundle.putString("echo", null);
        bundle.putString("rtmp", null);
        bundle.putString(
                "addressData",
                bean.getmCommunity()
                        + bean.getmPeriods()
                        + bean.getmUnit());
        if (bean.getmEquipmentId() != null) {
            BaseUtils.startActivities(mContext, ActivityVideo.class, bundle);
        } else {
            DialogShow.showHintDialog(mContext, mContext.getResources().getString(R.string.not_bind_facility));
        }
    }


}
