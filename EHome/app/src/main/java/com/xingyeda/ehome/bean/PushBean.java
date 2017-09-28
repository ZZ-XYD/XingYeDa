package com.xingyeda.ehome.bean;

public class PushBean {
	private String mMsgId;// id
	private String alertContent;// 内容
	private String title;// 标题
	private String mMsg;//消息
	private String regId;//注册id
	private String mType;//类型
	private String mUrl;//收到回调
	private String mUtil;//栋数id
	private String phone;//电话
	private String mCode;//随机码
	private String eid;//设备id
	private String housenum;//设备房号
	private String eaddress;//设备地址
	private String jietong;//接通地址
	private String sendType;//发送类型 0个人 1系统
	private String mAdminName;//管理员
	private String time;//push发送系统时间
	private String rtmp;//rtmp播放地址
	private String photograph;//图片地址


	public String getHousenum() {
		return housenum;
	}

	public void setHousenum(String housenum) {
		this.housenum = housenum;
	}

	public String getmUtil() {
		return mUtil;
	}
	public void setmUtil(String mUtil) {
		this.mUtil = mUtil;
	}
	public String getPhotograph() {
	    return photograph;
	}
	public void setPhotograph(String photograph) {
	    this.photograph = photograph;
	}
	public String getRtmp() {
	    return rtmp;
	}
	public void setRtmp(String rtmp) {
	    this.rtmp = rtmp;
	}
	public String getmMsg() {
	    return mMsg;
	}
	public void setmMsg(String mMsg) {
	    this.mMsg = mMsg;
	}
	public String getTime() {
	    return time;
	}
	public void setTime(String time) {
	    this.time = time;
	}
	public String getAlertContent() {
		return alertContent;
	}
	public void setAlertContent(String alertContent) {
		this.alertContent = alertContent;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}
	public String getmType() {
		return mType;
	}
	public void setmType(String mType) {
		this.mType = mType;
	}
	public String getmUrl() {
		return mUrl;
	}
	public void setmUrl(String mUrl) {
		this.mUrl = mUrl;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getmCode() {
		return mCode;
	}
	public void setmCode(String mCode) {
		this.mCode = mCode;
	}
	public String getEid() {
		return eid;
	}
	public void setEid(String eid) {
		this.eid = eid;
	}
	public String getEaddress() {
		return eaddress;
	}
	public void setEaddress(String eaddress) {
		this.eaddress = eaddress;
	}
	public String getJietong() {
		return jietong;
	}
	public void setJietong(String jietong) {
		this.jietong = jietong;
	}
	public String getSendType() {
		return sendType;
	}
	public void setSendType(String sendType) {
		this.sendType = sendType;
	}
	public String getmAdminName() {
		return mAdminName;
	}
	public void setmAdminName(String mAdminName) {
		this.mAdminName = mAdminName;
	}
	public String getmMsgId() {
		return mMsgId;
	}


	@Override
	public String toString() {
		return "PushBean{" +
				"mMsgId='" + mMsgId + '\'' +
				", alertContent='" + alertContent + '\'' +
				", title='" + title + '\'' +
				", mMsg='" + mMsg + '\'' +
				", regId='" + regId + '\'' +
				", mType='" + mType + '\'' +
				", mUrl='" + mUrl + '\'' +
				", mUtil='" + mUtil + '\'' +
				", phone='" + phone + '\'' +
				", mCode='" + mCode + '\'' +
				", eid='" + eid + '\'' +
				", housenum='" + housenum + '\'' +
				", eaddress='" + eaddress + '\'' +
				", jietong='" + jietong + '\'' +
				", sendType='" + sendType + '\'' +
				", mAdminName='" + mAdminName + '\'' +
				", time='" + time + '\'' +
				", rtmp='" + rtmp + '\'' +
				", photograph='" + photograph + '\'' +
				'}';
	}

	public void setmMsgId(String mMsgId) {
		this.mMsgId = mMsgId;
	}
	
	
}
	