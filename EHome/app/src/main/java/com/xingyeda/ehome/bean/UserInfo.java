package com.xingyeda.ehome.bean;

import java.util.List;

import android.graphics.Bitmap;


public class UserInfo
{
    //用户ID
    private String mId;

    //头像地址
    private String mHeadPhotoUrl;
    //头像
    private Bitmap mHeadPhoto;
    //用户名
    private String mUsername;
    //手机号码
    private String mPhone;
    //呢称
    private String mName;
    //邮箱
    private String mEmail;
    //性别
    private String mSex;
    //默认小区
    private HomeBean mXiaoqu;
    //备用号码
    private String mRemarksPhone;
    //绑定的所有小区
    private List<HomeBean> mXiaoquList;
    //SN
    private String mSNCode;

    private  boolean mCameraAdd;


    public boolean ismCameraAdd() {
        return mCameraAdd;
    }

    public void setmCameraAdd(boolean mCameraAdd) {
        this.mCameraAdd = mCameraAdd;
    }
    
    
    
    
//    //小区
//    private String mCommunity;
//    //期数
//    private String mPeriods;
//    //单元
//    private String mUnit;
//    //门号
//    private String mDoorNumber;
//    //身份 :1 业主   2 租客  3 家属
//    private String mIdentity;
//    //是否审核
//    private String mIsChecked;
//    //用户的状态
//    private String mState;




    public UserInfo()
    {
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



    public Bitmap getmHeadPhoto()
    {
        return mHeadPhoto;
    }



    public void setmHeadPhoto(Bitmap mHeadPhoto)
    {
        this.mHeadPhoto = mHeadPhoto;
    }


    public String getmHeadPhotoUrl()
    {
        return mHeadPhotoUrl;
    }


    public void setmHeadPhotoUrl(String mHeadPhotoUrl)
    {
        this.mHeadPhotoUrl = mHeadPhotoUrl;
    }


    public String getmUsername()
    {
        return mUsername;
    }



    public void setmUsername(String mUsername)
    {
        this.mUsername = mUsername;
    }



    public String getmPhone()
    {
        return mPhone;
    }



    public void setmPhone(String mPhone)
    {
        this.mPhone = mPhone;
    }



    public String getmName()
    {
        return mName;
    }



    public void setmName(String mName)
    {
        this.mName = mName;
    }



    public String getmEmail()
    {
        return mEmail;
    }



    public void setmEmail(String mEmail)
    {
        this.mEmail = mEmail;
    }



    public String getmSex()
    {
        return mSex;
    }



    public void setmSex(String mSex)
    {
        this.mSex = mSex;
    }



    public HomeBean getmXiaoqu()
    {
        return mXiaoqu;
    }



    public void setmXiaoqu(HomeBean mXiaoqu)
    {
        this.mXiaoqu = mXiaoqu;
    }



    public String getmRemarksPhone()
    {
        return mRemarksPhone;
    }



    public void setmRemarksPhone(String mRemarksPhone)
    {
        this.mRemarksPhone = mRemarksPhone;
    }


    public List<HomeBean> getmXiaoquList()
    {
        return mXiaoquList;
    }


    public void setmXiaoquList(List<HomeBean> mXiaoqu_List)
    {
        this.mXiaoquList = mXiaoqu_List;
    }
    


    public String getmSNCode() {
		return mSNCode;
	}


	public void setmSNCode(String mSNCode) {
		this.mSNCode = mSNCode;
	}


	@Override
    public String toString()
    {
        return "UserInfo [mId=" + mId + ", mHeadPhoto=" + mHeadPhoto
                + ", mUsername=" + mUsername + ", mPhone=" + mPhone
                + ", mName=" + mName + ", mEmail=" + mEmail + ", mSex=" + mSex
                + ", mXiaoqu=" + mXiaoqu + ", mRemarksPhone=" + mRemarksPhone
                + ", mXiaoquList=" + mXiaoquList + "]";
    }
   
    
   
    
    

}
