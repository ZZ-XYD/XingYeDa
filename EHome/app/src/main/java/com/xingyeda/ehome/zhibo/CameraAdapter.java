package com.xingyeda.ehome.zhibo;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.LitePalUtil;
import com.xingyeda.ehome.util.AESUtils;
import com.xingyeda.ehome.util.SharedPreUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by LDL on 2017/9/18.
 */

public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.ViewHolder> {
    private Context mContext;
    private List<Camera> mList;
    private ClickItem mViewClick;
    private String mUserName;
    private String enSharePassword;

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        @Bind(R.id.camera_image)
        ImageView cameraImage;
        @Bind(R.id.camera_name)
        TextView cameraName;
        @Bind(R.id.camera_share)
        TextView cameraShare;

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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Camera camera = mList.get(position);
        holder.cameraName.setText(camera.getmName());
        holder.cameraShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserName = LitePalUtil.getUserInfo().getmUsername();
                String mUID = SharedPreUtil.getString(mContext, "userId", "");
                String mEquipmentId = camera.getmEquipmentId();
                String mRoomId = camera.getmRoomId();
                if (mUserName != null && mUID != null && mEquipmentId != null && mRoomId != null) {
                    String sharePassword = mEquipmentId + "|" + mUID + "|" + mRoomId;
                    try {
                        enSharePassword = AESUtils.Encrypt(sharePassword, "1234567890123456");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showShare();
                }
            }
        });
        ImageLoader.getInstance().displayImage(camera.getmImagePath(), holder.cameraImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();//关闭sso授权
//        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段0
        oks.setText("content", "【来自" + mUserName + "的直播观看邀请】,复制这条信息￥" + enSharePassword + "￥后打开创享E家!");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(mContext);
    }
}

