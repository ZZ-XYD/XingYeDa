package com.xingyeda.ehome.adapter;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;

import com.google.zxing.WriterException;
import com.google.zxing.maxicode.MaxiCodeReader;
import com.jovision.account.JVMaoYanActivity;
import com.jovision.account.JVPlayActivity;
import com.jovision.account.MaoYanSetActivity;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.ActivityVideo;
import com.xingyeda.ehome.door.ActivityVideoTest;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.menu.ActivityChangeInfo;
import com.xingyeda.ehome.park.ParkHistoryActivity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

public class DoorAdapter  extends RecyclerView.Adapter<DoorAdapter.ViewHolder> {
    private Context mContext;
    private List<HomeBean> mList;
    private ClickItem mShareClickItem;
    private LongClick mLongClick;


    public interface ClickItem{
        public void onclick(View view ,int position);
    }
    public interface LongClick{
        public void onLongClick(View view ,int position);
    }
    public void longClick(LongClick longClick){
        mLongClick=longClick;
    }
    public void clickIco(ClickItem clickItem){
        mShareClickItem=clickItem;
    }

    public DoorAdapter(List<HomeBean> list) {
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_listview_door, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final HomeBean bean = mList.get(position);
        if ("1".equals(bean.getmBase())) {
            holder.tvXiaoqu.setText(bean.getmCommunity());
            holder.imageLogo.setBackgroundResource(R.mipmap.xiaoqu_logo);
            holder.mOption0.setText("设置");
            holder.mOption1.setText("电话");
            holder.mOption2.setText("摄像头");
            setImage(holder.mOption0, R.drawable.but_share);
            setImage(holder.mOption1, R.drawable.but_open_door);
            setImage(holder.mOption2, R.drawable.but_monitoring);

            holder.mOption0.setOnClickListener(new View.OnClickListener() {//监控
                @Override
                public void onClick(View v) {
                }
            });
            holder.mOption1.setOnClickListener(new View.OnClickListener() {//监控
                @Override
                public void onClick(View v) {
                    if (!"".equals(bean.getmPhone())) {
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bean.getmPhone()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mContext.startActivity(intent);

                    }
                }
            });
            holder.mOption2.setOnClickListener(new View.OnClickListener() {//监控
                @Override
                public void onClick(View v) {
                    if (!"".equals(bean.getmYunNumber())) {
                        Bundle bundle = new Bundle();
                        bundle.putString("cameraId", bean.getmYunNumber());
                        bundle.putString("cameraName", "物业中心");
                        bundle.putString("type", "shakingCamera");
                        BaseUtils.startActivities(mContext, JVPlayActivity.class, bundle);
                    }
                }
            });
        } else {
        if ("1".equals(bean.getmType())) {
            holder.tvXiaoqu.setText(bean.getmCommunity());
            holder.tvQishu.setText(bean.getmPeriods());
            holder.tvDongshu.setText(bean.getmUnit());
            holder.tvDoorplate.setText(bean.getmHouseNumber());
            holder.mType.setText(" 门  禁 ");
            holder.imageLogo.setBackgroundResource(R.mipmap.xiaoqu_logo);

            holder.mOption0.setText("分享");
            holder.mOption1.setText("开门");
            holder.mOption2.setText("监控");
            setImage(holder.mOption0, R.drawable.but_share);
            setImage(holder.mOption1, R.drawable.but_open_door);
            setImage(holder.mOption2, R.drawable.but_monitoring);

            holder.mOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//开门
                    if (!"".equals(bean.getState())) {
                        if ("1".equals(bean.getState())) {
                            DialogShow.showHintDialog(mContext, mContext.getResources().getString(R.string.door_hint_text));
                        } else {
                            openDoor(SharedPreUtil.getString(mContext, "userId"), bean.getmEquipmentId(), bean.getmUnitId(), bean.getmHouseNumber());
                        }
                    } else {
                        openDoor(SharedPreUtil.getString(mContext, "userId"), bean.getmEquipmentId(), bean.getmUnitId(), bean.getmHouseNumber());
                    }
                }
            });
            holder.mOption2.setOnClickListener(new View.OnClickListener() {//监控
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
            });
        } else if ("5".equals(bean.getmType())) {
            holder.tvXiaoqu.setText(bean.getmParkNickName());
            holder.tvQishu.setText("");
            holder.tvDongshu.setText("");
            holder.tvDoorplate.setText("");
            holder.mType.setText(" 停车场 ");
            holder.imageLogo.setBackgroundResource(R.mipmap.park_logo);

            holder.mOption0.setText("锁车");
            holder.mOption1.setText("记录");
            holder.mOption2.setText("设置");
            if ("1".equals(bean.getmParkLock())) {
                setImage(holder.mOption0, R.drawable.but_lockset1);
            } else {
                setImage(holder.mOption0, R.drawable.but_lockset);
            }
            setImage(holder.mOption1, R.drawable.but_month_card);
            setImage(holder.mOption2, R.drawable.but_modification);

            holder.mOption1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//出入记录
                    BaseUtils.startActivity(mContext, ParkHistoryActivity.class);
                }
            });
            holder.mOption2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//月卡
                    Bundle bundle = new Bundle();
                    bundle.putString("type", "park");
                    bundle.putString("id", bean.getmParkId());
                    BaseUtils.startActivities(mContext, ActivityChangeInfo.class, bundle);
                }
            });
        }


        holder.mOption0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mShareClickItem != null) {
                    mShareClickItem.onclick(v, position);
                }
            }
        });
    }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
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
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mLongClick!=null) {
                        mLongClick.onLongClick(v, getLayoutPosition());
                    }
                    return false;
                }
            });
        }
    }

    private void setImage(TextView view,int res){
        Drawable drawable2 = mContext.getResources().getDrawable(res);
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        view.setCompoundDrawables(null, drawable2, null, null);
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
                if (bean.getmVersions()!=null && "forAlice".equals(bean.getmVersions())) {
                    BaseUtils.startActivities(mContext, ActivityVideoTest.class, bundle);
                }else{
                    BaseUtils.startActivities(mContext, ActivityVideo.class, bundle);
                }
        } else {
            DialogShow.showHintDialog(mContext, mContext.getResources().getString(R.string.not_bind_facility));
        }
    }
}