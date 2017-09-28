package com.xingyeda.ehome.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.bean.LifeContentBean;
import com.xingyeda.ehome.http.okhttp.OkHttp;

public class LifeContextAdapter extends BaseAdapter
{
    private List<LifeContentBean> mLifeConetxt;
    private LayoutInflater inflater;
    private Context mContent;
    public LifeContextAdapter(Context context,List<LifeContentBean> list)
    {
        this.mLifeConetxt = list;
        this.inflater = LayoutInflater.from(context);
        mContent =context;
    }
    //总共多少条目
    @Override
    public int getCount()
    {
        return mLifeConetxt.size();
    }

   //返回某个条目的内容         position:条目的位置    从0开始
    @Override
    public Object getItem(int position)
    {
        return mLifeConetxt.get(position);
    }

    //返回某个条目的id
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    //条目长什么样子
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        /**
         * ListView的优化
         * 1.重复利用convertView，减少去将布局文件加载成View的次数（减少IO的次数）
         * 2.不让条目的加载每次都到窗口上找控件
         */
        ViewHolder viewHolder = null;
        if(convertView == null)
        {
            //将布局加载成View
            convertView = inflater.inflate(R.layout.item_life_context, null);
            
            //将控件试用内部类存储起来
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.item_life_image);
            viewHolder.tvTitle= (TextView) convertView.findViewById(R.id.item_life_title);
            viewHolder.tvContext =  (TextView) convertView.findViewById(R.id.item_life_context);
          
            
            viewHolder.image.setScaleType(ScaleType.FIT_XY);
//            OkHttp.getImage(mContent,ConnectPath.IMAGE_PATH+mLifeConetxt.get(position).getmImagePath(),viewHolder.image);
            ImageLoader.getInstance().displayImage(ConnectPath.IMAGE_PATH+mLifeConetxt.get(position).getmImagePath(),viewHolder.image);
            //将ViewHolder放入这ConverView中
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        
        
        //动态赋值
        LifeContentBean bean = mLifeConetxt.get(position);
//        OkHttp.getImage(mContent,ConnectPath.IMAGE_PATH+mLifeConetxt.get(position).getmImagePath(),viewHolder.image);
        ImageLoader.getInstance().displayImage(ConnectPath.IMAGE_PATH+mLifeConetxt.get(position).getmImagePath(),viewHolder.image);
//        viewHolder.imageLogo.setImageResource(R.id.icon);
        viewHolder.tvTitle.setText(bean.getmTitle());
        viewHolder.tvContext.setText(bean.getmContext());
        
        
        return convertView;
    }
    
    class ViewHolder
    {
        ImageView image;
        TextView tvTitle;
        TextView tvContext;
    }
   

}
