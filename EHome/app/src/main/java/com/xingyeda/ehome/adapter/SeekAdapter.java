package com.xingyeda.ehome.adapter;

import java.util.List;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.DoorAdapter.ViewHolder;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.bean.Xiaoqu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SeekAdapter extends BaseAdapter{
	

	 private List<Xiaoqu> mList;
	    private LayoutInflater inflater;
	    public SeekAdapter(Context context,List<Xiaoqu> List)
	    {
	        this.mList = List;
	        this.inflater = LayoutInflater.from(context);
	    }
	    //总共多少条目
	    @Override
	    public int getCount()
	    {
	        return mList.size();
	    }

	   //返回某个条目的内容         position:条目的位置    从0开始
	    @Override
	    public Object getItem(int position)
	    {
	        return mList.get(position);
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
	        ViewHolder viewHolder = null;
	        if(convertView == null)
	        {
	            //将布局加载成View
	            convertView = inflater.inflate(R.layout.item_listview_seek, null);
	            
	            //将控件试用内部类存储起来
	            viewHolder = new ViewHolder();
	            viewHolder.tvXiaoqu =  (TextView) convertView.findViewById(R.id.seek_item_text);
	            
	            
	            
	            //将ViewHolder放入这ConverView中
	            convertView.setTag(viewHolder);
	        }
	        else
	        {
	            viewHolder = (ViewHolder) convertView.getTag();
	        }
	        
	        
	        
	        //动态赋值
	        Xiaoqu bean = mList.get(position);
	        viewHolder.tvXiaoqu.setText(bean.getmName());  
	        
	        
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
