package com.xingyeda.ehome.bean;


public class BeanComplainAndService
{
    private String mId;//用户id
    private String mType;//1:投诉   2:维修
    private String mData;//投诉或者维修的类型
    private String mContent;//投诉或者维修的内容
    private String mTitle;
    
    public BeanComplainAndService()
    {
        super();
    }
    


    public BeanComplainAndService(String mId, String mType, String mData,
            String mContent, String mTitle)
    {
        super();
        this.mId = mId;
        this.mType = mType;
        this.mData = mData;
        this.mContent = mContent;
        this.mTitle = mTitle;
    }



    public String getmTitle()
    {
        return mTitle;
    }



    public void setmTitle(String mTitle)
    {
        this.mTitle = mTitle;
    }



    public String getmData()
    {
        return mData;
    }


    public void setmData(String mData)
    {
        this.mData = mData;
    }


    public String getmId()
    {
        return mId;
    }
    public void setmId(String mId)
    {
        this.mId = mId;
    }
    public String getmType()
    {
        return mType;
    }
    public void setmType(String mType)
    {
        this.mType = mType;
    }
    public String getmContent()
    {
        return mContent;
    }
    public void setmContent(String mContent)
    {
        this.mContent = mContent;
    }
    @Override
    public String toString()
    {
        return "ComplainAndServiceBean [mId=" + mId + ", mType=" + mType
                + ", mContent=" + mContent + "]";
    }
    
    
}
