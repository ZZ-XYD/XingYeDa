package com.xingyeda.ehome.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.bean.AnnunciateBean;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AnnunciateAdapter extends RecyclerView.Adapter<AnnunciateAdapter.ViewHolder> {

    private List<AnnunciateBean> mList;
    private Context mContext;
    private ClickItem mViewClick;

    public interface ClickItem{
        public void onclick(View view ,int position);
    }
    public void clickItem(ClickItem clickItem){
        mViewClick=clickItem;
    }

    public AnnunciateAdapter(List<AnnunciateBean> list) {
        this.mList = list;
    }

    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_ac_list, parent, false);
        return new AnnunciateAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if ((position%2)!=0) {
        	holder.time.setBackgroundResource(R.drawable.ad_itme);
        	 holder.tvDay.setTextColor(mContext.getResources().getColor(R.color.theme_orange));
             holder.tvMonth.setTextColor(mContext.getResources().getColor(R.color.theme_orange));
             holder.dayText.setTextColor(mContext.getResources().getColor(R.color.theme_orange));
             holder.monthText.setTextColor(mContext.getResources().getColor(R.color.theme_orange));
		}else {
			holder.time.setBackgroundResource(R.color.theme_orange);
			holder.tvDay.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.tvMonth.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.dayText.setTextColor(mContext.getResources().getColor(R.color.white));
			holder.monthText.setTextColor(mContext.getResources().getColor(R.color.white));
		}

        AnnunciateBean Bean = mList.get(position);
        holder.tvTitle.setText(Bean.getmTitle());
        holder.tvContent.setText("\t"+Bean.getmContent());
        holder.tvMonth.setText(getMonth(Bean.getmTime())+"");
        holder.tvDay.setText(getDay(Bean.getmTime())+"");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.tv_ac_title)
        TextView tvTitle;
        @Bind(R.id.tv_ac_content)
        TextView tvContent;
        @Bind(R.id.month)
        TextView tvMonth;
        @Bind(R.id.day)
        TextView tvDay;
        @Bind(R.id.day_text)
        TextView dayText;
        @Bind(R.id.month_text)
        TextView monthText;
        @Bind(R.id.time)
        RelativeLayout time;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
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

//
//    @Override
//    public int getCount() {
//        return mList.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return mList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        if (getCount() == 0) {
//            return null;
//        }
//
//        ViewHolder holder = null;
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.item_ac_list, null);
//
//            holder = new ViewHolder();
////            holder.tvTime = (TextView) convertView
////                    .findViewById(R.id.tv_ac_time);
//            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_ac_title);
//            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_ac_content);
//            holder.tvDay = (TextView) convertView.findViewById(R.id.day);
//            holder.tvMonth = (TextView) convertView.findViewById(R.id.month);
//            holder.time =(RelativeLayout) convertView.findViewById(R.id.time);
//            holder.dayText =(TextView) convertView.findViewById(R.id.day_text);
//            holder.monthText =(TextView) convertView.findViewById(R.id.month_text);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        if ((position%2)!=0) {
//        	holder.time.setBackgroundResource(R.drawable.ad_itme);
//        	 holder.tvDay.setTextColor(mContext.getResources().getColor(R.color.theme_orange));
//             holder.tvMonth.setTextColor(mContext.getResources().getColor(R.color.theme_orange));
//             holder.dayText.setTextColor(mContext.getResources().getColor(R.color.theme_orange));
//             holder.monthText.setTextColor(mContext.getResources().getColor(R.color.theme_orange));
//		}else {
//			holder.time.setBackgroundResource(R.color.theme_orange);
//			holder.tvDay.setTextColor(mContext.getResources().getColor(R.color.white));
//			holder.tvMonth.setTextColor(mContext.getResources().getColor(R.color.white));
//			holder.dayText.setTextColor(mContext.getResources().getColor(R.color.white));
//			holder.monthText.setTextColor(mContext.getResources().getColor(R.color.white));
//		}
//
//        AnnunciateBean Bean = mList.get(position);
//        holder.tvTitle.setText(Bean.getmTitle());
//        holder.tvContent.setText("\t"+Bean.getmContent());
//        holder.tvMonth.setText(getMonth(Bean.getmTime())+"");
//        holder.tvDay.setText(getDay(Bean.getmTime())+"");
//        return convertView;
//    }
//
    @SuppressLint("SimpleDateFormat")
    private int getMonth(String str){

    	SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date=null;
        try {
            date=formatter.parse(str);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar calendar=new GregorianCalendar();
        calendar.setTime(date);

		return calendar.get(Calendar.MONTH)+1;

    }
private int getDay(String str){

    	SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date=null;
        try {
            date=formatter.parse(str);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar calendar=new GregorianCalendar();
        calendar.setTime(date);

		return calendar.get(Calendar.DAY_OF_MONTH);

    }
}
