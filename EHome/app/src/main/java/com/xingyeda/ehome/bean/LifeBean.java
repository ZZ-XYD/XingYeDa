package com.xingyeda.ehome.bean;

import android.graphics.Bitmap;

public class LifeBean
{

    private String mId;
    private String mName;
    private String mPath;
    private String mType;
    private String mContent;
    private Bitmap mImage;
    
    public LifeBean(String mId, String mName, String mPath) {
		super();
		this.mId = mId;
		this.mName = mName;
		this.mPath = mPath;
	}
    
	public LifeBean() {
		super();
	}

	public String getmId()
    {
        return mId;
    }
    public void setmId(String mId)
    {
        this.mId = mId;
    }
    public String getmName()
    {
        return mName;
    }
    public void setmName(String mName)
    {
        this.mName = mName;
    }
    public String getmPath()
    {
        return mPath;
    }
    public void setmPath(String mPath)
    {
        this.mPath = mPath;
    }

	public String getmType() {
		return mType;
	}

	public void setmType(String mType) {
		this.mType = mType;
	}

	public String getmContent() {
		return mContent;
	}

	public void setmContent(String mContent) {
		this.mContent = mContent;
	}

	public Bitmap getmImage() {
		return mImage;
	}

	public void setmImage(Bitmap mImage) {
		this.mImage = mImage;
	}
	
    
    
   
    
  
   
    
    
}
