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
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.bean.SeekHistoryBase;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.id.list;

/**
 * Created by LDL on 2017/9/18.
 */

public class SeekAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    private Context mContext;
    private List<SeekHistoryBase> mList;
    private ClickItem mViewClick;
    private LongClickItem mLongClick;
    private Delete mDelete;

//    // 判断布局类型
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOT = 1;


    public SeekAdapter(List<SeekHistoryBase> list) {
        this.mList = list;
    }

    public void clickItem(ClickItem clickItem) {
        mViewClick = clickItem;
    }

    public interface ClickItem {
        public void onclick(View view, int position);
    }

    public void longClickItem(LongClickItem clickItem) {
        mLongClick = clickItem;
    }
    public interface LongClickItem {
        public void longClickItem(View view, int position);
    }

    public void delete(Delete delete) {
        mDelete = delete;
    }

    public interface Delete {
        public void onclick();
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
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_history_seek,parent, false);
//        RecyclerView.ViewHolder  holder = new SeekAdapter.ItemHolder(view);

        View view = null;
        RecyclerView.ViewHolder holder = null;

        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_history_seek,parent, false);
            holder = new SeekAdapter.ItemHolder(view);

        } else if (viewType == TYPE_FOOT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_history_foot,parent, false);
            holder = new SeekAdapter.FootHolder(view);
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
            SeekHistoryBase base = mList.get(position);
            ((ItemHolder) holder).cameraName.setText(base.getName());
        }
    }


    /**
     * 要展示的条目总数(包括所有数据集合和额外添加的头、身、脚条目数量)
     */
    @Override
    public int getItemCount() {
        if (mList!=null&&!mList.isEmpty()) {
            return mList.size()+1;
        }else{
            return 0;
        }
    }


    /****************************************
     * 各布局类型的ViewHolder
     */
    class FootHolder extends RecyclerView.ViewHolder {
        public FootHolder(View view) {
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewClick != null) {
                        mDelete.onclick();
                    }
                }
            });
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.seek_item_text)
        TextView cameraName;

        public ItemHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewClick != null) {
                        mViewClick.onclick(v, getLayoutPosition());
                    }
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mLongClick!=null) {
                        mLongClick.longClickItem(v, getLayoutPosition());
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}


