package com.xingyeda.ehome.bean;

import java.util.List;

public class LifeContentBean
{
    private String mImagePath;
    private String mTitle;
    private String mContext;
    private String mConnectPath;
    private String mType;
    private List<LifeBean> mBean;
    
    
    public List<LifeBean> getmBean()
    {
        return mBean;
    }
    public void setmBean(List<LifeBean> mBean)
    {
        this.mBean = mBean;
    }
    public String getmType()
    {
        return mType;
    }
    public void setmType(String mType)
    {
        this.mType = mType;
    }
    public LifeContentBean()
    {
        super();
    }
    public LifeContentBean(String mTitle)
    {
        super();
        this.mTitle = mTitle;
    }
    public String getmConnectPath()
    {
        return mConnectPath;
    }
    public void setmConnectPath(String mConnectPath)
    {
        this.mConnectPath = mConnectPath;
    }
    public String getmImagePath()
    {
        return mImagePath;
    }
    public void setmImagePath(String mImagePath)
    {
        this.mImagePath = mImagePath;
    }
    public String getmTitle()
    {
        return mTitle;
    }
    public void setmTitle(String mTitle)
    {
        this.mTitle = mTitle;
    }
    public String getmContext()
    {
        return mContext;
    }
    public void setmContext(String mContext)
    {
        this.mContext = mContext;
    }
    
    
}
