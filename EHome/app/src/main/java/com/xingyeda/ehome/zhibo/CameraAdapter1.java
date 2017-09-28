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

public class CameraAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Camera> mList;
    private ClickItem mViewClick;

    // 判断布局类型
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOT = 1;


    public CameraAdapter1(List<Camera> list) {
        this.mList = list;
    }

    public void clickItem(ClickItem clickItem) {
        mViewClick = clickItem;
    }

    public interface ClickItem {
        public void onclick(View view, int position);
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


    /**
     * 设置各布局，并返回与其对应的ViewHolder实例
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null){
            mContext=parent.getContext();
        }

        View view = null;
        RecyclerView.ViewHolder holder = null;

        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.camera_item,parent, false);
            holder = new CameraAdapter1.ItemHolder(view);

        } else if (viewType == TYPE_FOOT) {
            boolean b =mList.size()>=10;
            if (mList.size()>=10) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_foot,parent, false);
            }else{
                view = LayoutInflater.from(mContext).inflate(R.layout.list_foot_notdatas,parent, false);
            }
            holder = new CameraAdapter1.FootHolder(view);
        } else {
            // 默认的布局及其对应的ViewHolder实例，防止发生意外
            // default view = ...
            // default holder = ...
        }

        return holder;
    }


    /**
     * 填充各布局的控件内容
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemHolder) {
            Camera camera = mList.get(position);
            ((ItemHolder) holder).cameraName.setText(camera.getmName());
            ImageLoader.getInstance().displayImage(camera.getmImagePath(), ((ItemHolder) holder).cameraImage);
        }
    }


    /**
     * 要展示的条目总数(包括所有数据集合和额外添加的头、身、脚条目数量)
     */
    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }


    /****************************************
     * 各布局类型的ViewHolder
     */
    class FootHolder extends RecyclerView.ViewHolder {
        public FootHolder(View view) {
            super(view);
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        @Bind(R.id.camera_image)
        ImageView cameraImage;
        @Bind(R.id.camera_name)
        TextView cameraName;

        public ItemHolder(View view) {
            super(view);
            cardView = (CardView) view;
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewClick != null) {
                        mViewClick.onclick(v, getLayoutPosition());
                    }
                }
            });
        }
    }
}


