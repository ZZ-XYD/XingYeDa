package com.xingyeda.ehome.bean;

import org.litepal.crud.DataSupport;

public class InformationBase extends DataSupport{
    
    private int mId;
    private String mUserId;//用户id
    private String mName;//消息发起者
    private String mReceiver;//消息接受者
    private String mTitle;//标题
    private String mContent;//内容
    private String mTime;//时间
    private int mType;//消息类型 : 0 -- 个人消息 , 1 -- 系统消息 
    private int mIsExamine;//是否查看 : 0 -- 未查看, 1 -- 查看

    private String mImage;//照片地址
    private int imageType;//照片类型
    private int mMessage_status;//是否接通 : 0 -- 未接通, 1 -- 接通
    private int mDoor_status;//是否开门 : 0 -- 未开门, 1 -- 开门 
    private String mZhongWeiId;//中维id
    private String mZhongWeiType;//类型
    private String mZhongWeiImage;

    private boolean isChecked;
    private boolean isShow;
    
 
    
    public InformationBase(String mUserId, String mName, String mReceiver,
	    String mTitle, String mContent, String mTime, int mType,
	    int mIsExamine, String mImage, int mMessage_status, int mDoor_status) {
	super();
	this.mUserId = mUserId;
	this.mName = mName;
	this.mReceiver = mReceiver;
	this.mTitle = mTitle;
	this.mContent = mContent;
	this.mTime = mTime;
	this.mType = mType;
	this.mIsExamine = mIsExamine;
	this.mImage = mImage;
	this.mMessage_status = mMessage_status;
	this.mDoor_status = mDoor_status;
    }



    public InformationBase(int mId, String mUserId, String mName,
	    String mReceiver, String mTitle, String mContent, String mTime,
	    int mType, int mIsExamine, String mImage, int mMessage_status,
	    int mDoor_status) {
	super();
	this.mId = mId;
	this.mUserId = mUserId;
	this.mName = mName;
	this.mReceiver = mReceiver;
	this.mTitle = mTitle;
	this.mContent = mContent;
	this.mTime = mTime;
	this.mType = mType;
	this.mIsExamine = mIsExamine;
	this.mImage = mImage;
	this.mMessage_status = mMessage_status;
	this.mDoor_status = mDoor_status;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getmReceiver() {
        return mReceiver;
    }



    public void setmReceiver(String mReceiver) {
        this.mReceiver = mReceiver;
    }



    public String getmImage() {
        return mImage;
    }



    public void setmImage(String mImage) {
        this.mImage = mImage;
    }



    public int getmMessage_status() {
        return mMessage_status;
    }



    public void setmMessage_status(int mMessage_status) {
        this.mMessage_status = mMessage_status;
    }



    public int getmDoor_status() {
        return mDoor_status;
    }



    public void setmDoor_status(int mDoor_status) {
        this.mDoor_status = mDoor_status;
    }



    public String getmUserId() {
        return mUserId;
    }


    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }


    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public InformationBase() {
	super();
    }
    public String getmName() {
        return mName;
    }
    public void setmName(String mName) {
        this.mName = mName;
    }
    public String getmTitle() {
        return mTitle;
    }
    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    public String getmContent() {
        return mContent;
    }
    public void setmContent(String mContent) {
        this.mContent = mContent;
    }
    public String getmTime() {
        return mTime;
    }
    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
    public int getmType() {
        return mType;
    }
    public void setmType(int mType) {
        this.mType = mType;
    }
    public int getmIsExamine() {
        return mIsExamine;
    }
    public void setmIsExamine(int mIsExamine) {
        this.mIsExamine = mIsExamine;
    }



	public boolean isChecked() {
		return isChecked;
	}



	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	   public boolean isShow() {
	        return isShow;
	    }
	    public void setShow(boolean isShow) {
	        this.isShow = isShow;
	    }

    public String getmZhongWeiId() {
        return mZhongWeiId;
    }

    public void setmZhongWeiId(String mZhongWeiId) {
        this.mZhongWeiId = mZhongWeiId;
    }

    public String getmZhongWeiType() {
        return mZhongWeiType;
    }

    public void setmZhongWeiType(String mZhongWeiType) {
        this.mZhongWeiType = mZhongWeiType;
    }

    public String getmZhongWeiImage() {
        return mZhongWeiImage;
    }

    public void setmZhongWeiImage(String mZhongWeiImage) {
        this.mZhongWeiImage = mZhongWeiImage;
    }
}
