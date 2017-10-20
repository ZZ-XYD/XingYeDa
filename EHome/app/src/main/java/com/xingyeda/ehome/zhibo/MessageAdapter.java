package com.xingyeda.ehome.zhibo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xingyeda.ehome.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LDL on 2017/9/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<MessageBean> mList;
    private Context mContext;

    public MessageAdapter(List<MessageBean> list) {
        mList = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.zb_itme_text)
        TextView zbItmeText;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.zb_message_itme, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MessageBean bean = mList.get(position);
        holder.zbItmeText.setText(bean.getmName()+":"+bean.getmContent());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

