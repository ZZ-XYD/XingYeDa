package com.jovision;

import android.os.Environment;

import com.xingyeda.ehome.util.LogcatHelper;

import java.io.File;

/**
 * Created by juyang on 16/3/23.
 */
public class AppConsts {

    /***************************************
     * 路径
     *************************************************/
//    public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator;
    public static final String SDCARD_PATH = LogcatHelper.getPATH_LOGCAT()+ File.separator;
    public static final String APP_PATH = SDCARD_PATH + "PlaySDK" + File.separator;
    public static final String LOG_PATH = APP_PATH + "Log" + File.separator;
    public static final String CAPTURE_PATH = APP_PATH + "capture" + File.separator;
    public static final String VIDEO_PATH = APP_PATH + "video" + File.separator;
    public static final String DOWNLOAD_PATH = APP_PATH + "download" + File.separator;


    public static final String IMAGE_JPG_KIND = ".jpg";
    public static final String VIDEO_MP4_KIND = ".mp4";



    //设备端文件存放路径
    public static final String DEV_VIDEO_PATH = "/rec/00/VIDEO/";
    public static final String DEV_IMAGE_PATH = "/rec/00/IMAGE/";



    /***************************************
     * String formatter
     *************************************/
    //发送声波数据格式
    public static final String SOUND_WAVE_FORMATTER = "%s;%s;";
    //远程回放数据查询格式
    public static final String REMOTE_SEARCH_FORMATTER = "%04d%02d%02d000000%04d%02d%02d000000";


    public static final int CALL_CONNECT_CHANGE = 0xA1;
    public static final int CALL_NORMAL_DATA = 0xA2;
    public static final int CALL_CHECK_RESULT = 0xA3;
    public static final int CALL_CHAT_DATA = 0xA4;
    public static final int CALL_TEXT_DATA = 0xA5;
    public static final int CALL_DOWNLOAD = 0xA6;
    public static final int CALL_PLAY_DATA = 0xA7;
    public static final int CALL_LAN_SEARCH = 0xA8;
    public static final int CALL_FRAME_I_REPORT = 0xA9;
    public static final int CALL_STAT_REPORT = 0xAA;
    public static final int CALL_GOT_SCREENSHOT = 0xAB;
    public static final int CALL_PLAY_DOOMED = 0xAC;
    public static final int CALL_PLAY_AUDIO = 0xAD;
    public static final int CALL_QUERY_DEVICE = 0xAE;
    public static final int CALL_HDEC_TYPE = 0xAF;

    public static final int DEVICE_TYPE_UNKOWN = -1;
    public static final int DEVICE_TYPE_DVR = 0x01;
    public static final int DEVICE_TYPE_950 = 0x02;
    public static final int DEVICE_TYPE_951 = 0x03;
    public static final int DEVICE_TYPE_IPC = 0x04;
    public static final int DEVICE_TYPE_NVR = 0x05;

    public static final int JAE_ENCODER_SAMR = 0x00;
    public static final int JAE_ENCODER_ALAW = 0x01;
    public static final int JAE_ENCODER_ULAW = 0x02;

    public static final int TEXT_REMOTE_CONFIG = 0x01;
    public static final int TEXT_AP = 0x02;
    public static final int TEXT_GET_STREAM = 0x03;

    public static final int FLAG_WIFI_CONFIG = 0x01;
    public static final int FLAG_WIFI_AP = 0x02;
    public static final int FLAG_BPS_CONFIG = 0x03;
    public static final int FLAG_CONFIG_SCCUESS = 0x04;
    public static final int FLAG_CONFIG_FAILED = 0x05;
    public static final int FLAG_CONFIG_ING = 0x06;
    public static final int FLAG_SET_PARAM = 0x07;
    public static final int FLAG_GPIN_ADD = 0x10;
    public static final int FLAG_GPIN_SET = 0x11;
    public static final int FLAG_GPIN_SELECT = 0x12;
    public static final int FLAG_GPIN_DEL = 0x13;

    public static final int EX_WIFI_CONFIG = 0x0A;

    public static final int ARG1_PLAY_BAD = 0x01;

    public static final int DOWNLOAD_REQUEST = 0x20;
    public static final int DOWNLOAD_START = 0x21;
    public static final int DOWNLOAD_FINISHED = 0x22;
    public static final int DOWNLOAD_ERROR = 0x23;
    public static final int DOWNLOAD_STOP = 0x24;
    public static final int DOWNLOAD_TIMEOUT = 0x76;

    public static final int BAD_STATUS_NOOP = 0x00;
    public static final int BAD_STATUS_OMX = 0x01;
    public static final int BAD_STATUS_FFMPEG = 0x02;
    public static final int BAD_STATUS_OPENGL = 0x03;
    public static final int BAD_STATUS_AUDIO = 0x04;
    public static final int BAD_STATUS_DECODE = 0x05;
    public static final int PLAYBACK_DONE = 0x06;
    public static final int HDEC_BUFFERING = 0x07;

    public static final int BAD_SCREENSHOT_NOOP = 0x00;
    public static final int BAD_SCREENSHOT_INIT = 0x01;
    public static final int BAD_SCREENSHOT_CONV = 0x02;
    public static final int BAD_SCREENSHOT_OPEN = 0x03;

    public static final String IPC_DEFAULT_USER = "jwifiApuser";
    public static final String IPC_DEFAULT_PWD = "^!^@#&1a**U";
    public static final String IPC_DEFAULT_IP = "10.10.0.1";
    public static final int IPC_DEFAULT_PORT = 9101;


    // 视频播放状态
    public static final int TAG_PLAY_CONNECTING = 1;// 连接中
    public static final int TAG_PLAY_CONNECTTED = 2;// 已连接
    public static final int TAG_PLAY_DIS_CONNECTTED = 3;// 断开
    public static final int TAG_PLAY_CONNECTING_BUFFER = 4;// 连接成功，正在缓冲数据。。。
    public static final int TAG_PLAY_STATUS_UNKNOWN = 5;// 未知状态
    public static final int TAG_PLAY_BUFFERING = 6;// 已连接，正在缓冲进度
    public static final int TAG_PLAY_BUFFERED = 7;// 已连接，缓冲成功


    /******************************
     * 设备设置
     ********************************/
    public static final String TAG_MSG = "msg";
    public static final String TAG_FLAG = "flag";

    public static final String TAG_PACKET_TYPE = "packet_type";
    public static final String TAG_EXTEND_TYPE = "extend_type";
    public static final String TAG_EXTEND_MSG = "extend_msg";
    public static final String TAG_EXTEND_ARG1 = "extend_arg1";
    public static final String TAG_EXTEND_ARG2 = "extend_arg2";
    // 主控版本
    public static final String TAG_VERSION = "Version";
    // 添加第三方报警设备
    public static final String TAG_ADD_THIRD_ALRAM_DEV = "addThirdAlarmDev";
    public static final int RC_GPIN_BIND_PTZ = 0x00; // 外设报警联动报警设置回调
    public static final int RC_GPIN_ADD = 0x10; // 外设报警添加
    public static final int RC_GPIN_SET = 0x11; // 外设报警设置
    public static final int RC_GPIN_SECLECT = 0x12; // 外设报警查询
    public static final int RC_GPIN_DEL = 0x13; // 外设报警查询
    public static final int RC_GPIN_SET_SWITCH = 0x14; // 外设报警设置开关(只内部使用)
    public static final int RC_GPIN_SET_SWITCH_TIMEOUT = 0x15; // 外设报警设置开关
    public static final int RC_GPIN_BIND_PTZPRE = 0X18; // 门磁关联预置点
    // 录像存储模式 0: 停止录像 1: 手动录像 2. 报警录像
    public static final String TAG_STORAGEMODE = "storageMode";
    public static final String FORMATTER_STORAGE_MODE = "storageMode=%d;";
    public static final int STORAGEMODE_NULL = 0;// 停止录像
    public static final int STORAGEMODE_NORMAL = 1;// 手动录像
    public static final int STORAGEMODE_ALARM = 2;// 报警录像
    // 视频方向(小维之前的老协议)
    public static final String FORMATTER_EFFECT = "effect_flag=%d;";
    public static final String TAG_EFFECT = "effect_flag";
    public static final int SCREEN_NORMAL = 0;// 0(正),4(反)
    public static final int SCREEN_OVERTURN = 4;// 0(正),4(反)
    public static final int SCREEN_MIRROR = 2;// 0(正),2(镜像)
    // 视频方向(小维之后的新协议)图像反转模式：0（未反转），1（90度反转），2（180度反转），3（270度反转）
    public static final String TAG_ROTATE = "rotate";
    public static final String FORMATTER_ROTATE = "rotate=%d;";
    public static final int TAG_ROTATE_0 = 0;
    public static final int TAG_ROTATE_90 = 1;
    public static final int TAG_ROTATE_180 = 2;
    public static final int TAG_ROTATE_270 = 3;
    // 网络校时 通用开关 0：未开启， 1：已开启
    public static final String TAG_BSNTP = "bSntp";
    public static final String FORMATTER_BSNTP = "bSntp=%d;";
    // 时间格式
    public static final String TAG_TIME_FORMAT = "nTimeFormat";
    // MM/DD/YYYY
    public static final int TIME_TYPE_0 = 0;
    // YYYY-MM-DD
    public static final int TIME_TYPE_1 = 1;
    // DD/MM/YYYY
    public static final int TIME_TYPE_2 = 2;
    // 时区字段
    public static final String TIME_ZONE = "timezone";
    public static final String FORMATTER_TIME_ZONE = "timezone=%d;bSntp=1";
    // 对讲模式：1（单向）2（双向）3（单双向）
    public static final String TAG_CHATMODE = "chatMode";
    public static final int TAG_CHATMODE_S = 1;//单向
    public static final int TAG_CHATMODE_D = 2;//双向
    public static final int TAG_CHATMODE_SAD = 3;//单双向切换
    // 对讲标志位
    public static final String TAG_MOVESPEED = "moveSpeed";
    // 设置码流
    public static final int TYPE_SET_PARAM = 0x03;
    public static final String TAG_STREAM = "MobileQuality";
    public static final String FORMATTER_CHANGE_STREAM = "MobileQuality=%d;";
    // 设置主控发不发音频
    public static final String FORMATTER_CHANGE_AUDIO_STATE = "MoRecordOrMonitor=%d;";
    // 手动录像 bRecEnable
    public static final String TAG_BRECENABLE = "bRecEnable";

    // 手动录像 bRecEnable
    public static final String FORMATTER_BRECENABLE = "bRecEnable=%d;";

    // 报警录像 bRecAlarmEnable
    public static final String TAG_BRECALARMENABLE = "bRecAlarmEnable";
    // 老家用字段MobileCH 2：家用 其他非家用
    public static final String TAG_MOBILECH = "MobileCH";
    public static final int MOBILECH_HOME = 2;
    // ModeByMicStatus插耳机变双向对讲，拔掉耳机单向对讲（但是此逻辑不用了） 1是可以，0是不行
    public static final String TAG_MODEBYMICSTATUS = "ModeByMicStatus";
    // sd卡管理 1:有SD卡 0：没有SD卡
    public static final String TAG_NSTORAGE = "nStorage";

    // new sd卡管理 1:有SD卡 0：没有SD卡
    public static final String TAG_DISK = "disk_exist";

    public static final int NSTORAGE_HAS_SDCARD = 1;
    public static final int NSTORAGE_NO_SDCARD = 0;
    // SD卡总容量
    public static final String TAG_NTOTALSIZE = "nTotalSize";
    // SD卡剩余容量
    public static final String TAG_NUSEDSIZE = "nUsedSize";
    // SD卡状态 nStatus: 0:未发现SD卡 1：未格式化 2：存储卡已满 3：录像中... 4：准备就绪
    public static final String TAG_NSTATUS = "nStatus";
    // 移动侦测灵敏度
    public static final String TAG_NMDSENSITIVITY = "nMDSensitivity";
    public static final String FORMATTER_NMDSENSITIVITY = "nMDSensitivity=%d;";
    // 设置设备语言 0:中文 1：英文
    public static final String FORMATTER_SET_DEV_LANGUAGE = "nLanguage=%d;";

    // 设备安全防护状态
    public static final String TAG_SET_DEV_SAFE_STATE = "bAlarmEnable";
    // 设备安全防护状态 通用开关 0：关闭 1：打开
    public static final String FORMATTER_SET_DEV_SAFE_STATE = "bAlarmEnable=%d;";
    // 移动侦测开关
    public static final String TAG_SET_MDENABLE_STATE = "bMDEnable";
    // 移动侦测开关 通用开关 0：关闭 1：打开
    public static final String FORMATTER_SET_MDENABLE_STATE = "bMDEnable=%d;";
    // 婴儿啼哭报警功能
    public static final String TAG_BBCENABLE = "bBCEnable";
    // 设备报警声音开关
    public static final String TAG_SET_ALARM_SOUND = "bAlarmSound";
    // 设备报警声音开关 通用开关 0：关闭 1：打开
    public static final String FORMATTER_SET_ALARM_SOUND = "bAlarmSound=%d;";
    // 邮件报警开关
    public static final String TAG_SEND_ALARM_EMAIL = "nMDOutEMail";
    // 邮件报警参数设置(手机端自定义)
    public static final String TAG_PARAM_ALARM_EMAIL = "paramAlarmEMail";
    public static final String FORMATTER_ALARM_SEND_TEST_EMAIL = "acMailSender=%s;"// 邮件发送者
            + "acSMTPServer=%s;"// 邮件服务器地址
            + "acSMTPUser=%s;"// 用户名
            + "acSMTPPasswd=%s;"// 密码
            + "acSMTPort=%d;"// 发送邮件端口
            + "acSMTPCrypto=%s;"// 邮件加密方式（none/ssl/tls）
            + "acReceiver0=%s;"// 收件人1
            + "acReceiver1=%s;"// 收件人2
            + "acReceicer2=%s;"// 收件人3
            + "acReciever3=%s;";// 收件人4
    public static final String FORMATTER_ALARM_EMAIL_SET = "alarmTime1=%s;"// 报警时间段-%s
            + "nAlarmDelay=%d;"// 报警间隔
            + "bAlarmSound=%d;"// 报警声音开关
            + "acMailSender=%s;"// 邮件发送者
            + "acSMTPServer=%s;"// 邮件服务器地址
            + "acSMTPUser=%s;"// 用户名
            + "acSMTPPasswd=%s;"// 密码
            + "vmsServerIp=%s;"// vms服务器IP地址
            + "vmsServerPort=%d;"// vms服务器端口
            + "acSMTPort=%d;"// 发送邮件端口
            + "acSMTPCrypto=%s;"// 邮件加密方式（none/ssl/tls）
            + "acReceiver0=%s;"// 收件人1
            + "acReceiver1=%s;"// 收件人2
            + "acReceicer2=%s;"// 收件人3
            + "acReciever3=%s;";// 收件人4
    // 报警时间段
    public static final String TAG_ALARM_TIME = "alarmTime1";
    // 报警间隔
    public static final String TAG_ALARM_DELAY = "nAlarmDelay";
    // vms服务器IP地址
    public static final String TAG_VMS_SERVER_IP = "vmsServerIp";
    // vms服务器端口
    public static final String TAG_VMS_SERVER_PORT = "vmsServerPort";
    // 邮件发送者
    public static final String TAG_ACMAILSENDER = "acMailSender";
    // 邮件服务器地址
    public static final String TAG_ACSMTPSERVER = "acSMTPServer";
    // 用户名
    public static final String TAG_ACSMTPUSER = "acSMTPUser";
    // 密码
    public static final String TAG_ACSMTPPASSWD = "acSMTPPasswd";
    // 发送邮件端口
    public static final String TAG_ACSMTPORT = "acSMTPPort";
    // 邮件加密方式（none/ssl/tls）
    public static final String TAG_ACSMTPCRYPTO = "acSMTPCrypto";
    // 收件人0
    public static final String TAG_ACRECEIVER0 = "acReceiver0";
    // 收件人1
    public static final String TAG_ACRECEIVER1 = "acReceiver1";
    // 收件人2
    public static final String TAG_ACRECEIVER2 = "acReceicer2";
    // 收件人3
    public static final String TAG_ACRECEIVER3 = "acReciever3";
    // 设备邮件报警开关 通用开关 0：关闭 1：打开
    public static final String FORMATTER_SEND_MAIL = "nMDOutEMail=%d;";
    // 报警时间段字段
    public static final String TAG_SET_ALARM_TIME = "alarmTime0";
    // 报警时间段设置
    public static final String FORMATTER_SET_ALARM_TIME = "alarmTime0=%s;";
    // 全天报警
    public static final String ALARM_TIME_ALL_DAY = "00:00:00-23:59:59";
    // 报警时间段时间格式化
    public static final String FORMATTER_ALARM_TIME = "%s:00-%s:00";
    // 通用开关
    public static final int SWITCH_CLOSE = 0;
    public static final int SWITCH_OPEN = 1;
    public static final int TYPE_EX_UPDATE = 0x01;
    public static final int TYPE_EX_SENSOR = 0x02;
    public static final int TYPE_EX_STORAGE_SWITCH = 0x07;
    public static final int TYPE_EX_SET_DHCP = 0x09;
    public static final int COUNT_EX_UPDATE = 0x01;
    public static final int COUNT_EX_NETWORK = 0x02;
    public static final int COUNT_EX_STORAGE = 0x03;
    public static final int COUNT_EX_SENSOR = 0x08;
    /* MTU设置 */
    public static final int MTU_700 = 700;
    public static final int MTU_1400 = 1400;// 路由器可以设置的最大值是1472
    /* 日志前缀 */
    public static final String LOG_PREFIX_ACTIVITY = "xiaowei_activity_";
    public static final String LOG_PREFIX_FRAGMENT = "xiaowei_fragment_";
    /* 平台标识 */
    public static final int BIZ_ACC_ANDROID = 0x10;
    /******************************
     * 添加第三方报警设备
     ********************************/
    // 报警设备昵称设置,报警开关设置
    public static final String FORMATTER_SET_THIRD_ALARM_DEV = "type=%d;guid=%d;name=%s;enable=%d;";
    // 添加第三方报警设备
    public static final String FORMATTER_ADD_THIRD_ALARM_DEV = "type=%d;";
    // 删除第三方报警设备
    public static final String FORMATTER_DELETE_THIRD_ALARM_DEV = "type=%d;guid=%d;";


    /******************************
     * 飞行器新增
     ********************************/
    public static final int RC_EX_IVP = 0x0f;
    public static final int EX_IVP_ASD_SUBMIT = 0x22;

    public static final String FORMATTER_DBALARM = "bEnableDBDetect=%d;"+// 分贝报警使能 0,1
            "DBThreahold=%d;"+// 绝对分贝报警门限 0~100
            "DBRealtiveThreahold=%d;"+// 相对分贝报警门限 0~100
            "bAsdEnableRecord=%d;"+// 使能报警录像 0,1
            "bAsdOutClient=%d;"+// 使能发送至分控 0,1
            "bAsdOutEMail=%d;";// 使能报警邮件 0,1

    public static final String TAG_RECFILELENGTH = "RecFileLength";
    public static final String FORMATTER_RECFILELENGTH = "RecFileLength=%d;";

    public static final String TAG_UARTBAUT = "uartbaut";
    public static final String FORMATTER_UARTBAUT = "uartbaut=%d;";





}
