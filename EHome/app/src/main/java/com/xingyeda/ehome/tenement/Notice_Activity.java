package com.xingyeda.ehome.tenement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.xingyeda.ehome.R;
import com.xingyeda.ehome.adapter.UploadImageAdapter;
import com.xingyeda.ehome.base.BaseActivity;

import static android.R.id.list;
import static com.xingyeda.ehome.R.id.uploadImage;

public class Notice_Activity extends BaseActivity
{
   @Bind(R.id.RT_notice_title)
     TextView mHeader;
   @Bind(R.id.RT_notice_time)
     TextView mTime;//时间
//   @Bind(R.id.RT_notice_day)
//    TextView mDay;//时间
//   @Bind(R.id.RT_notice_content)
//     TextView mContent;//内容
   @Bind(R.id.RT_notice_back)
     TextView mBack;//返回
   @Bind(R.id.notice_title)
    TextView mTitle;
   @Bind(R.id.RT_notice_list)
     ListView mList_Image;
    
    private String mType;
    private  ArrayList<String> mList;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_activity);
        ButterKnife.bind(this);
        mList = new ArrayList<>();
//        mContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        //初始化
        this.init();
        //数据加载
        this.event();
    }
   

     //初始化
    private void init()
    {
        
        this.mType = getIntent().getExtras().getString("bean");
        
        
        
    }
        @OnClick(R.id.RT_notice_back)
        public void onClick(View v)
        {
                Notice_Activity.this.finish();

        }
    
    //数据加载
    private void event()
    {
        if (mType.equals("tousu"))
        {
                mHeader.setText(R.string.complains_records);
        }
        else if (mType.equals("weixiutype"))
        {
            mHeader.setText(R.string.maintenance_record);
        }
        else if (mType.equals("annunciate"))
        {
            mHeader.setText(R.string.village_notice);
        }
        mTitle.setText(getIntent().getExtras().getString("title"));
//        mDay.setText(getDay(getIntent().getExtras().getString("time")));
        mTime.setText(getTime(getIntent().getExtras().getString("time")));
//        mContent.setText("\t\t"+getIntent().getExtras().getString("content"));
        mList.add("\t\t"+getIntent().getExtras().getString("content"));
        if (getIntent().getExtras().getStringArrayList("imageList")!=null)
        {
            ArrayList<String> strList = getIntent().getExtras().getStringArrayList("imageList");
            for (String str : strList) {
                mList.add(str);
            }
        }
        uploadImage(mList);
    }

    private void uploadImage(ArrayList<String> list)
    {
        
        UploadImageAdapter adapter = new UploadImageAdapter(Notice_Activity.this, list);
        mList_Image.setAdapter(adapter);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
    @SuppressLint("SimpleDateFormat") private String getTime(String time){
    	 DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
         Date date=null;
         try {
             date=formatter.parse(time);
              
         } catch (Exception e) {
             e.printStackTrace();
         }
         GregorianCalendar calendar=new GregorianCalendar();
         calendar.setTime(date);
		return calendar.get(Calendar.YEAR)+"年"+(calendar.get(Calendar.MONTH)+1)+"月"+calendar.get(Calendar.DAY_OF_MONTH)+"日";
    	
    }
    private String getDay(String str){
    	
    	SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date=null;
        try {
            date=formatter.parse(str);
             
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar calendar=new GregorianCalendar();
        calendar.setTime(date);

		return calendar.get(Calendar.DAY_OF_MONTH)+"日";
    	
    }
    
 
}
