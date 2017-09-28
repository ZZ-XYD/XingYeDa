package com.xingyeda.ehome.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.bean.HomeBean;

public class DoorListAdapter extends BaseAdapter
{
    private List<HomeBean> mHomeBeans;
    private LayoutInflater inflater;
    public DoorListAdapter(Context context,List<HomeBean> mXiaoqu_List)
    {
        this.mHomeBeans = mXiaoqu_List;
        this.inflater = LayoutInflater.from(context);
    }
    //总共多少条目
    @Override
    public int getCount()
    {
        return mHomeBeans.size();
    }

   //返回某个条目的内容         position:条目的位置    从0开始
    @Override
    public Object getItem(int position)
    {
        return mHomeBeans.get(position);
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
            convertView = inflater.inflate(R.layout.item_listview_door, null);
            
            //将控件试用内部类存储起来
            viewHolder = new ViewHolder();
            viewHolder.imageLogo = (ImageView) convertView.findViewById(R.id.door_adapter_image);
            viewHolder.tvXiaoqu= (TextView) convertView.findViewById(R.id.door_list_xiaoqu);
            viewHolder.tvQishu =  (TextView) convertView.findViewById(R.id.door_list_qishu);
            viewHolder.tvDongshu =  (TextView) convertView.findViewById(R.id.door_list_dongshu);
            viewHolder.tvDoorplate =  (TextView) convertView.findViewById(R.id.door_list_doorplate);
            
            
            
            //将ViewHolder放入这ConverView中
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        
        
        //动态赋值
        HomeBean bean = mHomeBeans.get(position);
//        viewHolder.imageLogo.setImageResource(bean.getmId());
//        viewHolder.imageLogo.setImageResource(R.id.icon);
        viewHolder.tvXiaoqu.setText(bean.getmCommunity());
        viewHolder.tvQishu.setText(bean.getmPeriods());
        viewHolder.tvDongshu.setText(bean.getmUnit());
        viewHolder.tvDoorplate.setText(bean.getmHouseNumber());  
        
        
        return convertView;
    }
    
    class ViewHolder
    {
        //小区logo
        ImageView imageLogo;
        //小区
        TextView tvXiaoqu;
        //期数
        TextView tvQishu;
        //栋数
        TextView tvDongshu;
        //门牌号
        TextView tvDoorplate;
    }
   

}
