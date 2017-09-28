package com.xingyeda.ehome.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.bean.LifeBean;

public class LifeAdpater extends BaseAdapter
{
    private LayoutInflater mInflater;
    private List<LifeBean> mLifeTag;
    private Context mContext;
    private int mHeight;

    public LifeAdpater(Context context, List<LifeBean> lifeTag , int height)
    {
        this.mInflater = LayoutInflater.from(context);
        this.mLifeTag = lifeTag;
        mContext = context;
        mHeight = height;
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
            viewHolder = new ViewHolder(convertView);
            // 将ViewHolder放入这ConverView中
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 动态赋值
        
        LifeBean bean = mLifeTag.get(position);
//        viewHolder.mImage.setText(bean.getmName());
//        if (bean.getmPath()!=null && !bean.getmPath().equals("")) {
//        OkHttp.getImage(mContext,bean.getmPath() , viewHolder.mImage);
//        }
        viewHolder.mImage.setImageBitmap(bean.getmImage());
        viewHolder.mName.setText(bean.getmName());
        convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,  mHeight ));
        return convertView;	
    }

    class ViewHolder
    {
    	@Bind(R.id.life_item_image)
        ImageView mImage;
    	@Bind(R.id.life_item_text)
        TextView mName;
        
         ViewHolder(View view) {
    	    ButterKnife.bind(this, view);
    	}
    }

}
