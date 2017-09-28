package com.xingyeda.ehome.zhibo;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.ConnectPath;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by LDL on 2017/9/18.
 */

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {
    private Context mContext;
    private List<Camera> mList;
    private ClickItem mViewClick;

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        @Bind(R.id.camera_image)
        ImageView cameraImage;
        @Bind(R.id.camera_name)
        TextView cameraName;

        public ViewHolder(View view) {
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

    public void clickItem(ClickItem clickItem) {
        mViewClick = clickItem;
    }

    public interface ClickItem {
        public void onclick(View view, int position);
    }

    public CameraAdapter(List<Camera> list) {
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.camera_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Camera camera = mList.get(position);
        holder.cameraName.setText(camera.getmName());
        ImageLoader.getInstance().displayImage(camera.getmImagePath(), holder.cameraImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

