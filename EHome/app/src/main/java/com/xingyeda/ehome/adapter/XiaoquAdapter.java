package com.xingyeda.ehome.adapter;


import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.HomeBean;

public class XiaoquAdapter extends BaseAdapter
{
    private List<HomeBean> mHomeBeans;
    private LayoutInflater inflater;
    private Context mContext;
    private EHomeApplication mApplication;
    
    public XiaoquAdapter(Context context,List<HomeBean> mXiaoqu_List)
    {
        this.mHomeBeans = mXiaoqu_List;
        this.inflater = LayoutInflater.from(context);
        mContext = context;
        mApplication = (EHomeApplication) ((Activity) mContext).getApplication();
    }
    @Override
    public int getCount()
    {
        return mHomeBeans.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mHomeBeans.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_listview_xiaoqu, null);
            
            //将控件试用内部类存储起来
            viewHolder = new ViewHolder(convertView);
            //将ViewHolder放入这ConverView中
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        
        
        //动态赋值
        HomeBean bean = mHomeBeans.get(position);
        if (bean.equals(mApplication.getmCurrentUser().getmXiaoqu())) {
        	viewHolder.mDefaultXiaoqu.setVisibility(View.VISIBLE);
		}
        viewHolder.mXiaoqu.setText(bean.getmCommunity());
        viewHolder.mQishu.setText(bean.getmPeriods());
        viewHolder.mDongshu.setText(bean.getmUnit());
        viewHolder.mDoorplate.setText("\t"+bean.getmHouseNumber());  
        
        
        return convertView;
    }
    
    class ViewHolder
    {
    	@Bind(R.id.xiaoqu)
        TextView mXiaoqu;
    	@Bind(R.id.qishu)
        TextView mQishu;
    	@Bind(R.id.dongshu)
        TextView mDongshu;
    	@Bind(R.id.doorplate)
        TextView mDoorplate;
    	@Bind(R.id.default_xiaoqu)
    	TextView mDefaultXiaoqu;
        
        public ViewHolder(View view) {
    	    ButterKnife.bind(this, view);
    	}
    }
    
   

}
