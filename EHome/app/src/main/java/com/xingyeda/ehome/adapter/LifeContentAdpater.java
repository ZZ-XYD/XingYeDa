package com.xingyeda.ehome.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.LifeBean;
import com.xingyeda.ehome.http.okhttp.OkHttp;

public class LifeContentAdpater extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<LifeBean> mLifeTag;
    private Context mContent;

    public LifeContentAdpater(Context context, List<LifeBean> lifeTag)
    {
        this.mInflater = LayoutInflater.from(context);
        this.mLifeTag = lifeTag;
        mContent = context;
    }

    private int clickTemp = -1;

    // 标识选择的Item
    public void setSeclection(int position)
    {
        clickTemp = position;
    }

    @Override
    public int getCount()
    {
        return mLifeTag.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mLifeTag.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        /**
         * ListView的优化 1.重复利用convertView，减少去将布局文件加载成View的次数（减少IO的次数）
         * 2.不让条目的加载每次都到窗口上找控件
         */
        ViewHolder viewHolder = null;
        
        
        if (convertView == null)
        {
            // 将布局加载成View
            convertView = mInflater.inflate(R.layout.item_life, null);

            // 将控件试用内部类存储起来
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView
                    .findViewById(R.id.life_item_image);
            viewHolder.tvName = (TextView) convertView
                    .findViewById(R.id.life_item_text);
//            OkHttp.getImage(mContent,ConnectPath.IMAGE_PATH + mLifeTag.get(position).getmPath(),viewHolder.image);
            ImageLoader.getInstance().displayImage(ConnectPath.IMAGE_PATH + mLifeTag.get(position).getmPath(), viewHolder.image);
            // 将ViewHolder放入这ConverView中
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 动态赋值
        LifeBean bean = mLifeTag.get(position);
        viewHolder.tvName.setText(bean.getmName());
        if (clickTemp == position) {
            viewHolder.image.setBackgroundResource(R.mipmap.circumscribe);
        }
        else {
            viewHolder.image.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    class ViewHolder
    {
        // tag的图片
        ImageView image;
        // tag的内容
        TextView tvName;
    }

}
