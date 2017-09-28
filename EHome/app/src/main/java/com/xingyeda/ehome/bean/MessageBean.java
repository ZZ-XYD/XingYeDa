package com.xingyeda.ehome.bean;

public class MessageBean
{
    private String mType;//类型
    private String mUrl;//rtmpd的播放流
    private String mUtil;//栋数
    private String mCode;//拨号回调的code
    private String time;//系统push开始时间
    private String eid;//设备id
    private String eaddress;//设备绑定的地址
    private String jie;//接通是调用的地址
    private String alertContent;//内容
    private String title;//标题
    private int type;//消息类型 : 0 --个人消息  , 1--是系统消息
    private String mAdminName;//消息类型 : 0 --个人消息  , 1--是系统消息
    
    
    
    
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getmAdminName() {
        return mAdminName;
    }
    public void setmAdminName(String mAdminName) {
        this.mAdminName = mAdminName;
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
    public String getJie()
    {
        return jie;
    }
    public void setJie(String jie)
    {
        this.jie = jie;
    }
    public String getEaddress()
    {
        return eaddress;
    }
    public void setEaddress(String eaddress)
    {
        this.eaddress = eaddress;
    }
    public String getEid()
    {
        return eid;
    }
    public void setEid(String eid)
    {
        this.eid = eid;
    }
    public String getTime()
    {
        return time;
    }
    public void setTime(String time)
    {
        this.time = time;
    }
    public String getmCode()
    {
        return mCode;
    }
    public void setmCode(String mCode)
    {
        this.mCode = mCode;
    }
    public String getmType()
    {
        return mType;
    }
    public void setmType(String mType)
    {
        this.mType = mType;
    }
    public String getmUrl()
    {
        return mUrl;
    }
    public void setmUrl(String mUrl)
    {
        this.mUrl = mUrl;
    }
    public String getmUtil()
    {
        return mUtil;
    }
    public void setmUtil(String mUtil)
    {
        this.mUtil = mUtil;
    }
    
    
    
}
