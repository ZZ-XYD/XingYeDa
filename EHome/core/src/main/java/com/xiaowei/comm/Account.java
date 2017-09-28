package com.xiaowei.comm;

public class Account {
    private String mSid;
    public static final String BIZ_ACC_VERSION_STRING = "2.2.1";
    public static final int BIZ_ACC_STATUS_OK = 0;
    public static final int BIZ_ACC_STATUS_BUSY = 1;
    public static final int BIZ_ACC_STATUS_CONNECT = 2;
    public static final int BIZ_ACC_STATUS_MSG = 3;
    public static final int BIZ_ACC_STATUS_RID = 4;
    public static final int BIZ_ACC_STATUS_SID = 5;
    public static final int BIZ_ACC_STATUS_IP = 6;
    public static final int BIZ_ACC_STATUS_AUTH = 7;
    public static final int BIZ_ACC_STATUS_AUTH_USR = 8;
    public static final int BIZ_ACC_STATUS_AUTH_PWD = 9;
    public static final int BIZ_ACC_STATUS_VERSION = 10;
    public static final int BIZ_ACC_STATUS_REDIRECT = 11;
    public static final int BIZ_ACC_STATUS_SYSERR = 20;
    public static final int BIZ_ACC_CH = 1;
    public static final int BIZ_ACC_EN = 2;
    public static final int BIZ_ACC_TW = 3;
    public static final int BIZ_ACC_EVENT_CONNECTED = 1;
    public static final int BIZ_ACC_EVENT_DISCONNECT = 2;
    public static final int BIZ_ACC_EVENT_CONNECT_FAILED = 3;
    public static final int BIZ_ACC_EVENT_CONNECT_REFRESH = 4;
    public static final int BIZ_ACC_PUSH_TEXT = 0;//普通报警消息
    public static final int BIZ_ACC_PUSH_SHARE = 1;//设备分享消息
    public static final int BIZ_ACC_PUSH_CANCEL_SHARE = 2;//主账号取消对某个设备的分享
    public static final int BIZ_ACC_PUSH_DEV_ONLINE = 3;//设备上线
    public static final int BIZ_ACC_PUSH_DEV_OFFLINE = 4;//设备离线
    public static final int BIZ_ACC_PUSH_DEV_CSDL = 5;//云存储
    public static final int BIZ_ACC_PUSH_ADS = 6;//2017.4.18新增 广告推送

    static {
        System.loadLibrary("bizacc");
    }

    public static native void init(int paramInt1, int paramInt2, int
            paramInt3, String paramString1, String paramString2, Object
                                           paramObject);

    public static native void term();

    public static native void login(String paramString1, String paramString2,
                                    String paramString3);

    public static native void logout();

    public static native void pushswitch(int paramInt);

    public static native void updatetoken(String paramString);
}
