package com.xingyeda.ehome.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.bean.BeanMaintainHistory;

public class MaintainAdapter extends BaseAdapter
{
    private List<BeanMaintainHistory> mList;
    private LayoutInflater mInflater;
    
    public MaintainAdapter(Context context,List<BeanMaintainHistory> list){
        this.mList = list;
        this.mInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mList.get(position);
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
         * ListView的优化
         * 1.重复利用convertView，减少去将布局文件加载成View的次数（减少IO的次数）
         * 2.不让条目的加载每次都到窗口上找控件
         */
        ViewHolder viewHolder = null;
        if(convertView == null)
        {
            //将布局加载成View
            convertView = mInflater.inflate(R.layout.history_item, null);
            
            //将控件试用内部类存储起来
            viewHolder = new ViewHolder();
            viewHolder.mTitle = (TextView) convertView.findViewById(R.id.history_item_title);
            viewHolder.mTime= (TextView) convertView.findViewById(R.id.history_item_time);
            viewHolder.mContent =  (TextView) convertView.findViewById(R.id.history_item_content);
            
            
            
            //将ViewHolder放入这ConverView中
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        
        
        //动态赋值
        BeanMaintainHistory bean = mList.get(position);
        viewHolder.mTitle.setText(bean.getmTitle());
        viewHolder.mTime.setText(bean.getmTime());
        viewHolder.mContent.setText(bean.getmContent());
        
        
        return convertView;
    }

    class ViewHolder
    {
        TextView mTitle;//标题
        TextView mTime;//时间
        TextView mContent;//内容
    }
   
}
