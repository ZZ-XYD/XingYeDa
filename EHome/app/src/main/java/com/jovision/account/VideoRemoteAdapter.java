package com.jovision.account;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jovision.play.RemoteVideo;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.OnOffAdaper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VideoRemoteAdapter extends RecyclerView.Adapter<VideoRemoteAdapter.ViewHolder> {

    List<RemoteVideo> mList;
    private Context mContext;
    private ItemClick mViewClick;
//    private String mData;

    public void longItem(ItemClick itemClick){
        this.mViewClick=itemClick;
    }

    public interface ItemClick{
        public void itemClick(View view ,int position);
    }

    public VideoRemoteAdapter(Context context, List<RemoteVideo> datas) {
        this.mContext = context;
        this.mList = datas;
//        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_remotevideo, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RemoteVideo bean = mList.get(position);
        holder.videoTime.setText(bean.year+"-"+bean.month+"-"+bean.day);
        holder.videodate.setText(bean.remoteDate);
        if ("A".equalsIgnoreCase(bean.remoteKind)) {
            holder.videodisk.setText( R.string.video_alarm);
        } else if ("M".equalsIgnoreCase(bean.remoteKind)) {
            holder.videodisk.setText( R.string.video_motion);
        } else if ("T".equalsIgnoreCase(bean.remoteKind)) {
            holder.videodisk.setText( R.string.video_time);
        } else {
            holder.videodisk.setText( R.string.video_normal);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.videodate)
        TextView videodate;
        @Bind(R.id.time)
        TextView videoTime;
        @Bind(R.id.videodisk)
        TextView videodisk;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mViewClick != null) {
                        mViewClick.itemClick(v, getLayoutPosition());
                    }
                }
            });
        }
    }
}
