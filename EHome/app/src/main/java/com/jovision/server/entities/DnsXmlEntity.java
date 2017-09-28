package com.jovision.server.entities;

/**
 * DNS xml文件配置信息(单例)
 * 不应该直接使用这个单例，通过DNSUtils来操作
 *
 * @author ye.jian
 */

public class DnsXmlEntity {
    // 结果(设置个默认值, 这个值我们认为还没有解析DNS文件)
    private int mResult = -9000;
    // 账号BaseUrl
    private String mAccountBaseUrl = "";
    // 报警BaseUrl
    private String mAlarmBaseUrl = "";
    // 设备BaseUrl
    private String mDeviceBaseUrl = "";
    // 其它的
    private String mOtherBaseUrl = "";
    // 云存储
    private String mCloudBaseUrl = "";

    public int getResult() {
        return mResult;
    }

    public void setResult(int result) {
        mResult = result;
    }

    public String getAccountBaseUrl() {
        return mAccountBaseUrl;
    }

    public void setAccountBaseUrl(String accountBaseUrl) {
        mAccountBaseUrl = accountBaseUrl;
    }

    public String getAlarmBaseUrl() {
        return mAlarmBaseUrl;
    }

    public void setAlarmBaseUrl(String alarmBaseUrl) {
        mAlarmBaseUrl = alarmBaseUrl;
    }

    public String getDeviceBaseUrl() {
        return mDeviceBaseUrl;
    }

    public void setDeviceBaseUrl(String deviceBaseUrl) {
        mDeviceBaseUrl = deviceBaseUrl;
    }

    public String getOtherBaseUrl() {
        return mOtherBaseUrl;
    }

    public void setOtherBaseUrl(String otherBaseUrl) {
        mOtherBaseUrl = otherBaseUrl;
    }

    public String getCloudBaseUrl() {
        return mCloudBaseUrl;
    }

    public void setCloudBaseUrl(String cloudBaseUrl) {
        mCloudBaseUrl = cloudBaseUrl;
    }

    // ---------------------------------------------
    // #单例
    // ---------------------------------------------
    private DnsXmlEntity() {
    }

    private static class SingletonLoader {
        private static final DnsXmlEntity INSTANCE = new DnsXmlEntity();
    }

    public static DnsXmlEntity getInstance() {
        return SingletonLoader.INSTANCE;
    }
}
