package com.xingyeda.ehome.bean;

import android.graphics.Bitmap;

public class AdvertisementBean
{
    private String mImagePath;
    private String mAdPath;
    private String mTitle;
    private Bitmap mBitmap;
    
    public AdvertisementBean()
    {
        super();
    }

    
    public AdvertisementBean(String mImagePath, String mAdPath)
    {
        super();
        this.mImagePath = mImagePath;
        this.mAdPath = mAdPath;
    }

    
    public String getmTitle() {
		return mTitle;
	}


	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}


	public Bitmap getmBitmap() {
		return mBitmap;
	}


	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}


	public String getmImagePath()
    {
        return mImagePath;
    }

    public void setmImagePath(String mImagePath)
    {
        this.mImagePath = mImagePath;
    }

    public String getmAdPath()
    {
        return mAdPath;
    }

    public void setmAdPath(String mAdPath)
    {
        this.mAdPath = mAdPath;
    }
    
    
    
}
