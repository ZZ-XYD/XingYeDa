package com.xingyeda.ehome.bean;

import org.litepal.crud.DataSupport;

public class HomeBean extends DataSupport {
    //小区logo
    private String mId;
    private String state;//用户是否冻结
    //小区
    private String mCommunityId;//小区id
    private String mCommunity;//小区名字
    //期数
    private String mPeriodsId;//期数id
    private String mPeriods;//期数名字
    //单元
    private String mUnitId;//栋数id
    private String mUnit;//栋数名字
    //门牌号
    private String mHouseNumberId;
    private String mHouseNumber;
    //设备id
    private String mEquipmentId;
    //设备版本
    private String mVersions;//forAlice: 新的下位机，windows: 旧版本
    
    //默认小区
    private String mIsDefault;
    //身份类型
    private String mIdentityType;
    
    private String mType;//设备类型   1：门禁    2：普通摄像头       3：摇头机    4：猫眼  5:停车场

    private String mCameraId;//摄像头id

    private String mCameraName;//摄像头呢称

    private String mParkName;
    private String mParkId;
    private String mParkNickName;
    private String mParkTruckSpace;
    private String mParkLock;

    private String mYunNumber;//云通讯设备
    private String mPhone;//电话
    private String mBase;//


    public HomeBean() {
    }

    public HomeBean(String mParkName, String mParkId, String mParkNickName) {
        this.mParkName = mParkName;
        this.mParkId = mParkId;
        this.mParkNickName = mParkNickName;
    }

    public String getmVersions() {
        return mVersions;
    }

    public void setmVersions(String mVersions) {
        this.mVersions = mVersions;
    }

    public String getmBase() {
        return mBase;
    }

    public void setmBase(String mBase) {
        this.mBase = mBase;
    }

    public String getmYunNumber() {
        return mYunNumber;
    }

    public void setmYunNumber(String mYunNumber) {
        this.mYunNumber = mYunNumber;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmParkLock() {
        return mParkLock;
    }

    public void setmParkLock(String mParkLock) {
        this.mParkLock = mParkLock;
    }

    public String getmParkTruckSpace() {
        return mParkTruckSpace;
    }

    public void setmParkTruckSpace(String mParkTruckSpace) {
        this.mParkTruckSpace = mParkTruckSpace;
    }

    public String getmParkNickName() {
        return mParkNickName;
    }

    public void setmParkNickName(String mParkNickName) {
        this.mParkNickName = mParkNickName;
    }

    public String getmParkName() {
        return mParkName;
    }

    public void setmParkName(String mParkName) {
        this.mParkName = mParkName;
    }

    public String getmParkId() {
        return mParkId;
    }

    public void setmParkId(String mParkId) {
        this.mParkId = mParkId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getmCameraId() {
		return mCameraId;
	}
	public void setmCameraId(String mCameraId) {
		this.mCameraId = mCameraId;
	}
	public String getmCameraName() {
		return mCameraName;
	}
	public void setmCameraName(String mCameraName) {
		this.mCameraName = mCameraName;
	}
	public String getmType() {
		return mType;
	}
	public void setmType(String mType) {
		this.mType = mType;
	}
	public String getmIdentityType()
    {
        return mIdentityType;
    }
    public void setmIdentityType(String mIdentityType)
    {
        this.mIdentityType = mIdentityType;
    }
    public String getmId()
    {
        return mId;
    }
    public void setmId(String mId)
    {
        this.mId = mId;
    }
    public String getmHouseNumber()
    {
        return mHouseNumber;
    }
    public void setmHouseNumber(String mHouseNumber)
    {
        this.mHouseNumber = mHouseNumber;
    }
    public String getmCommunity()
    {
        return mCommunity;
    }
    public void setmCommunity(String mCommunity)
    {
        this.mCommunity = mCommunity;
    }
    public String getmPeriods()
    {
        return mPeriods;
    }
    public void setmPeriods(String mPeriods)
    {
        this.mPeriods = mPeriods;
    }
    public String getmUnit()
    {
        return mUnit;
    }
    public void setmUnit(String mUnit)
    {
        this.mUnit = mUnit;
    }
    public String getmCommunityId()
    {
        return mCommunityId;
    }
    public void setmCommunityId(String mCommunityId)
    {
        this.mCommunityId = mCommunityId;
    }
    public String getmPeriodsId()
    {
        return mPeriodsId;
    }
    public void setmPeriodsId(String mPeriodsId)
    {
        this.mPeriodsId = mPeriodsId;
    }
    public String getmUnitId()
    {
        return mUnitId;
    }
    public void setmUnitId(String mUnitId)
    {
        this.mUnitId = mUnitId;
    }
    public String getmEquipmentId()
    {
        return mEquipmentId;
    }
    public void setmEquipmentId(String mEquipmentId)
    {
        this.mEquipmentId = mEquipmentId;
    }
    public String getmHouseNumberId()
    {
        return mHouseNumberId;
    }
    public void setmHouseNumberId(String mHouseNumberId)
    {
        this.mHouseNumberId = mHouseNumberId;
    }
  
    public String getmIsDefault()
    {
        return mIsDefault;
    }
    public void setmIsDefault(String isDefault)
    {
        this.mIsDefault = isDefault;
    }
//    @Override
//  	public String toString() {
//  		return mCommunity+mPeriods+mUnit+mHouseNumber;
//  	}
    
    
}
