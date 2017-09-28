package com.xingyeda.ehome.adapter;

import android.content.Context;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.bean.OnOffBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.id.list;

/**
 * Created by LDL on 2017/6/9.
 */

public class OnOffAdaper extends RecyclerView.Adapter<OnOffAdaper.OnOffViewHoler> {

    List<OnOffBean> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    private ButtonInterface mSmartHomeOn;
    private ButtonInterface mSmartHomeOff;
    private ButtonInterface mSmartHomeSet;
//    private AdapterView.OnItemLongClickListener onLongItemListener;
    private LongItem mViewClick;

    public OnOffAdaper(Context context, List<OnOffBean> datas) {
        this.mContext = context;
        this.mList = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    public void smartHomeOn(ButtonInterface buttonInterface){
        this.mSmartHomeOn=buttonInterface;
    }
    public void smartHomeOff(ButtonInterface buttonInterface){
        this.mSmartHomeOff=buttonInterface;
    }
    public void smartHomeSet(ButtonInterface buttonInterface){
        this.mSmartHomeSet=buttonInterface;
    }

    public void longItem(LongItem longItem){
        this.mViewClick=longItem;
    }

    /**
          * 按钮点击事件对应的接口
          */
    public interface ButtonInterface{
        public void onclick( View view,int position);
    }

    public interface LongItem{
        public void onLongclick(View view ,int position);
    }





    @Override
    public OnOffViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.on_off_item, parent, false);
        OnOffViewHoler holder = new OnOffViewHoler(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(OnOffViewHoler holder, final int position) {
        OnOffBean bean = mList.get(position);
        holder.onOffType.setText("开关");
        holder.smartHomeName.setText(bean.getName());
        holder.smartHomeImg.setBackgroundResource(R.mipmap.switch_logo);
        //开
        holder.smartHomeOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSmartHomeOn!=null) {
                    mSmartHomeOn.onclick(v,position);
                }
            }
        });
        //关
        holder.smartHomeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSmartHomeOff!=null) {
                    mSmartHomeOff.onclick(v,position);
                }
            }
        });
        //设置
        holder.smartHomeSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSmartHomeSet!=null) {
                    mSmartHomeSet.onclick(v,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class OnOffViewHoler extends RecyclerView.ViewHolder {
        @Bind(R.id.on_off_type)
        TextView onOffType;
        @Bind(R.id.smart_home_name)
        TextView smartHomeName;
        @Bind(R.id.smart_home_on)
        ImageView smartHomeOn;
        @Bind(R.id.smart_home_off)
        ImageView smartHomeOff;
        @Bind(R.id.smart_home_set)
        ImageView smartHomeSet;
        @Bind(R.id.smart_home_img)
        ImageView smartHomeImg;

        public OnOffViewHoler(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mViewClick != null) {
                        mViewClick.onLongclick(v, getLayoutPosition());
                    }
                    return false;
                }
            });

        }

    }
}
