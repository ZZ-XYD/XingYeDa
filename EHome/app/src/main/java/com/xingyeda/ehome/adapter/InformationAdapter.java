package com.xingyeda.ehome.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.bean.InformationBase;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

@SuppressLint("SimpleDateFormat")
public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.ViewHolder> {


	private List<InformationBase> mList;
	private Context mContext;
	private Delete mDelete;
	private ClickItem mViewClick;

	public InformationAdapter(Context context, List<InformationBase> list) {
		mList = list;
		mContext = context;
    }

	public void delete(Delete delete){
		mDelete=delete;
	}
	public void clickItem(ClickItem clickItem){
		mViewClick=clickItem;
	}

	public interface Delete{
		public void onclick( View view,int position);
	}
	public interface ClickItem{
		public void onclick(View view ,int position);
	}
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_information, parent, false);
		ViewHolder holder = new ViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		InformationBase bean = mList.get(position);

		holder.tvContent.setTextColor(mContext.getResources().getColor(R.color.black));
		holder.tvTitle.setText(bean.getmTitle());
		if (bean.getmZhongWeiType()!=null) {
			String imagePath = bean.getmZhongWeiImage();
			holder.info_img_content.setBackgroundResource(R.mipmap.information_bgm);
			holder.tvContent.setVisibility(View.GONE);
			holder.info_img_content.setVisibility(View.VISIBLE);
			if (imagePath!=null) {
				if (isExist(imagePath)) {
					holder.info_img_content.setImageBitmap(getLoacalBitmap(imagePath));
				}
			}
		}else{
			holder.tvContent.setVisibility(View.VISIBLE);
			holder.info_img_content.setVisibility(View.GONE);
			holder.tvContent.setText("\t\t"+bean.getmContent());
		}
		if (isToday(bean.getmTime())) {
			holder.tvTime.setText(dateFormaterTime.get().format(toDate(bean.getmTime())));
		}
		else {
			holder.tvTime.setText(dateFormaterDate.get().format(toDate(bean.getmTime())));
		}
		if (bean.getmIsExamine() == 1) {
			holder.tvContent.setTextColor(mContext.getResources().getColor(
					R.color.gray));
		}

		holder.delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mDelete!=null) {
					mDelete.onclick(v,position);
				}
			}
		});

	}

	@Override
	public int getItemCount() {
		return mList.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder{
			@Bind(R.id.info_title)
			TextView tvTitle;
			@Bind(R.id.info_time)
			TextView tvTime;
			@Bind(R.id.info_content)
			TextView tvContent;
			@Bind(R.id.delete)
			ImageView delete;
			@Bind(R.id.info_img_content)
			ImageView info_img_content;
		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this,itemView);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mViewClick != null) {
						mViewClick.onclick(v, getLayoutPosition());
					}
				}
			});

		}
	}

	private boolean isExist(String path){
		if (path==null){
			return  false;
		}else{
			File file = new File(path);
			if (file.exists()) {
				return true;
			}
		}
		return false;
	}
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isToday(String date){
		boolean isDate = false;
		Date time = toDate(date);
		Date today = new Date();
		if(time != null){
			String nowDate = dateFormaterDate.get().format(today);
			String timeDate = dateFormaterDate.get().format(time);
			if(nowDate.equals(timeDate)){
				isDate = true;
			}
		}
		return isDate;
	}

	public static Date toDate(String date) {
		try {
			return dateFormater.get().parse(date);
		} catch (Exception e) {
			return null;
		}
	}

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormaterDate = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};
	private final static ThreadLocal<SimpleDateFormat> dateFormaterTime = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("HH:mm:ss");
		}
	};

}
