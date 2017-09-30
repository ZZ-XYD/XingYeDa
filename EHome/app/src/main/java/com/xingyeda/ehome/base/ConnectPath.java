package com.xingyeda.ehome.base;


import static android.os.FileObserver.CREATE;

public class ConnectPath
{
    //Ip地址
    public static final String IP = "http://192.168.10.250:8080/";
//    public static final String IP = "http://service.xyd999.com:8080/";

    //IP路径
//    public static final String IP_PATH = IP+"intefaces/servlet/";
//	public static final String IP_PATH2 = IP+"intefaces/";
    public static final String IP_PATH = IP+"xydServer/servlet/";
	public static final String IP_PATH2 = IP+"xydServer/";
    
    //版本更新查询接口
    public static final String VERSIONS_PATH = IP+"xydServer/servlet/" + "versionUpdate";

    //欢迎图片
    public static final String GUIDE_PATH = IP_PATH + "getPrologue";
    //欢迎图片
    public static final String CREATE_KEY = IP_PATH + "createKey";
    //login图片
    public static final String LOGINIMAGE_PATH = IP_PATH + "getBGI?type=bgi";
    
    //登录路径m 
    public static final String LOGIN_PATH = IP_PATH+"userLogin";
    //心跳协议
    public static final String KEEPLIVE_PATH = IP_PATH+"KeepLive";
    //注册路径
    public static final String REGISTER_PATH = IP_PATH+"userReg";
    
    //验证码获取
    public static final String SECURITY_PATH = IP_PATH+"generateCode";
//    //注册提交验证
//    public static final String VERIFY_PATH = IP_PATH+"verificationSms";
//    
    //忘记密码，设置新密码
    public static final String NEWPWD_PATH = IP_PATH+"findPwd";
    //忘记密码，设置新密码
    public static final String CHANGEPWD_PATH = IP_PATH+"editPwd";
   //退出登录
    public static final String REPETITIONLOGIN_PATH = IP_PATH+"loginOut";
    //获取服务器的系统时间
    public static final String SYSTEMTIME_PATH = IP_PATH+"getCuTime?type=long";
    //通知服务器重启监控视频
    public static final String RESTARTRTMP_PATH = IP_PATH+"notificationRestart";
    //通知消息计数
    public static final String PUSHMSG_PATH = IP_PATH+"clicknum";
    
    
    
    
    //开门路径
    public static final String OPENDOOR_PATH = IP_PATH+"openDoor";
    //关门路径
    public static final String CLOSEDOOR_PATH = IP_PATH+"clientCancel";
    //关门路径
        public static final String MENUHINT_PATH = IP_PATH+"getEquipmentAnnouncement";
    
    //小区  
    public static final String XIAOQU_PATH = IP_PATH+"xiaoqu";
    //期数  
    public static final String QISHU_PATH = IP_PATH+"qishu";
    //栋数
    public static final String DONGSHU_PATH = IP_PATH+"dongshu";
    //用户绑定房子
    public static final String BIND_PATH = IP_PATH+"userBind";
    //用户解除绑定房子
    public static final String CLEARBIND_PATH = IP_PATH+"clearUser";
    //返回用户所有房子
    public static final String RETURN_HOUSE_PATH = IP_PATH+"myproperty";
    //物业通告
    public static final String ANNUNCIATE_PATH = IP_PATH + "getAnnouncement";
    //物业投诉和维修
    public static final String COMPLAINANDSERVICE_PATH = IP_PATH + "shujuzidian";
//    //物业投诉提交
//    public static final String COMPLAINADD_PATH = IP_PATH + "xydServer/servlet/addtousu";
//    //物业维修提交
//    public static final String SERVICEADD_PATH = IP_PATH + "addweixiu";
    //数据提交
    public static final String UPLOAD_PATH = IP_PATH + "upload";
    //上传头像
    public static final String UPLOADHEAD_PATH = IP_PATH + "updateUserImg";
    //建议
    public static final String ADDTOUSU_PATH = IP_PATH + "addtousu";
    //建议
    public static final String ADDWEIXIU_PATH = IP_PATH + "addweixiu";
    
    //投诉记录
    public static final String GETCOMPLAIN_PATH = IP_PATH + "gettuosu";
    //维修记录
    public static final String GETSERVICE_PATH = IP_PATH + "getWeixiu";
    
    //修改信息
    public static final String MODIFICATION_PATH = IP_PATH + "updateUser";
    //修改信息
    public static final String MESSAGEPICTURE_PATH = IP_PATH + "getBosResource";
    
    
    //商圈的种类
    public static final String LIFETAG_PATH = IP_PATH + "lable";
    
    //商圈
    public static final String LIFECIRCLE_PATH = IP_PATH + "getShangQuan";
    
    
    
    //图片path
    public static final String IMAGE_PATH = IP_PATH2+"download/";
    
    
    //修改默认小区
    public static final String CHANGEXIAOQU_PATH = IP_PATH + "updateDefault";

    
    
    
    
    
//    //物业投诉接口
//    public static final String PROPERTY_COMLAINTS_PATH = IP_PATH+"";
    //广告接口
    public static final String ADVERTISEMENT_PATH = IP_PATH+"guangao?flag=select&type=img";
    //视频接口
    public static final String VIDEO_PATH = IP_PATH+"rtmp";
    //视频接通回调
    public static final String VIDEOCALL_BACK_PATH = IP_PATH+"echo";
    //视频未接通挂断通知客户端
    public static final String NOTIFICATION_CLIENT_PATH = IP_PATH+"tz/client";
    //设备再次呼叫
    public static final String BUSY_PATH = IP_PATH+"busy";
    //便民服务号码
    public static final String CONVENIENCE_PATH = IP_PATH+"serviceCall";
    //获取设置状态
    public static final String GETSETUP_PATH = IP_PATH+"getsetup";
    //修改设置状态
    public static final String SETUP_PATH = IP_PATH+"setUp";
    
    
    
    
    
    //添加摄像头
    public static final String ADD_CAMERA = IP_PATH+"addCamera";
    //添加开关
    public static final String QUERY_ON_OFF = IP_PATH+"getCameraByTypeAndUser";
    //添加摄像头
    public static final String HUAWEI_PUSH = IP_PATH+"updateUserRegKey";
//    //修改摄像头呢称
//    public static final String UPDATE_CAMERA = IP_PATH+"addCamera";


    /**
     * 停车场
     */
    //查询岗亭
    public static final String QUERY_SENTRY = IP_PATH+"getParkingLotByXiaoqu";
    //车辆绑定停车场
    public static final String BIND_SENTRY = IP_PATH+"addCarParkingLot";
    //锁车
    public static final String LOCK_CAR = IP_PATH+"lockCar";
    //解锁
    public static final String DEBLOCKING_CAR = IP_PATH+"unlock";
    //月卡
    public static final String CAR_MONTH_CARD = IP_PATH+"monthCard";


    /**
     * 分享
     */
    //分享摄像头
    public static final String SHARE_CAMERA = IP_PATH+"createShare";
    //获取分享
    public static final String GET_SHARE_CAMERA = IP_PATH+"searchRoom";
    //进入直播间通知
    public static final String CAMERA_ROOM_ENTER = IP_PATH+"enter";
    //退出直播间通知
    public static final String CAMERA_ROOM_EXIT = IP_PATH+"exit";
    //发送消息
    public static final String CAMERA_SEND_MESSAGE = IP_PATH+"receiveMsg";
    //我分享的设备
    public static final String CAMERA_MY_SHARE = IP_PATH+"myroom";
    //删除我分享的设备
    public static final String CAMERA_DELETE_SHARE = IP_PATH+"deleteShare";
    //修改我分享的设备
    public static final String CAMERA_UPDATE_SHARE = IP_PATH+"shareUpdate";
    //上传图片
    public static final String CAMERA_UPDATE_IMAGE = IP_PATH+"updateRoomImg";


}
