package com.xingyeda.ehome.zhibo;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LDL on 2017/9/18.
 */

public class CameraAdapter2 extends RecyclerView.Adapter<CameraAdapter2.ViewHolder> {
    private Context mContext;
    private List<Camera> mList;
    private ClickItem mViewClick;

    // 判断布局类型
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOT = 1;

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        @Bind(R.id.camera_image)
        ImageView cameraImage;
        @Bind(R.id.camera_name)
        TextView cameraName;
        public ViewHolder(View view) {
            super(view);
            cardView=(CardView)view;
            ButterKnife.bind(this,view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewClick != null) {
                        mViewClick.onclick(v, getLayoutPosition());
                    }
                }
            });
        }
    } class FootHolder extends CameraAdapter2.ViewHolder {
        public FootHolder(View view) {
            super(view);
        }
    }


    /**
     * 当前位置应该展示的条目布局的类型
     */
    @Override
    public int getItemViewType(int position) {
        // 数据集合最后一行数据之后的那一行就应该加载脚布局
        if (position + 1 == getItemCount()) {
            return TYPE_FOOT;
        } else {
            return TYPE_ITEM;
        }
    }
    public void clickItem(ClickItem clickItem){
        mViewClick=clickItem;
    }
    public interface ClickItem{
        public void onclick(View view, int position);
    }
    public CameraAdapter2(List<Camera> list) {
        this.mList = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null){
            mContext=parent.getContext();
        }
//        View view= LayoutInflater.from(mContext).inflate(R.layout.camera_item,parent,false);
//        return new ViewHolder(view);
        View view = null;
        ViewHolder holder = null;

        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.camera_item,parent, false);
            holder = new ViewHolder(view);

        } else if (viewType == TYPE_FOOT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_foot,parent, false);
            holder = new FootHolder(view);
        } else {
            // 默认的布局及其对应的ViewHolder实例，防止发生意外
            // default view = ...
            // default holder = ...
        }

        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Camera camera=mList.get(position);
        holder.cameraName.setText(camera.getmName());
        ImageLoader.getInstance().displayImage(camera.getmImagePath(), holder.cameraImage);
//        ImageLoader.getInstance().displayImage("http://192.168.10.250:8080/xydServer/download?fileId=435", holder.cameraImage);
    }
    @Override
    public int getItemCount() {
        return mList.size()+1;
    }
}


