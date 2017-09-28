package com.xingyeda.ehome.adapter;

import java.util.List;

import okhttp3.Call;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ldl.imageloader.core.ImageLoader;
import com.xingyeda.ehome.R;
import com.xingyeda.ehome.http.okhttp.OkHttp;

public class UploadImageAdapter extends BaseAdapter{

    LayoutInflater mInflater;
    private List<String> mImageList;
    private Context mContext;
    public UploadImageAdapter(Context context,List<String> list)
    {
        this.mImageList = list;
        mContext = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return mImageList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mImageList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
            View view = mInflater.inflate(R.layout.upload_image_adapter, null);
            
            final ImageView image = (ImageView) view.findViewById(R.id.uploadImage);
//            ImageLoader.getInstance().displayImage(mImageList.get(position),image);
            if (!mImageList.get(position).equals("")) {
				
//            	OkHttp.getImage(mContext, mImageList.get(position), image);
                ImageLoader.getInstance().displayImage(mImageList.get(position),image);
           /* OkHttpUtils
            .get()//
            .url(mImageList.get(position))//
            .build()//
            .execute(new BitmapCallback()
            {

                @Override
                public void onResponse(Bitmap bitmap)
                {
                    image.setImageBitmap(bitmap);
                }

		@Override
		public void onError(Call call, Exception e) {
		    
		}

            });*/
            
            }
        return view;
    }
    
}