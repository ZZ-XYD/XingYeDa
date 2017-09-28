package com.jovision.server.utils;

import android.text.TextUtils;
import android.util.Xml;

import com.jovision.server.entities.DnsXmlEntity;
import com.jovision.server.entities.DomainEntity;
import com.xiaowei.core.rx.retrofit.ObservableProvider;
import com.xiaowei.core.rx.retrofit.exception.ApiException;
import com.xiaowei.core.rx.retrofit.subscriber.DownLoadSubscribe;
import com.xiaowei.core.utils.FileUtils;
import com.xiaowei.core.utils.Logger;
import com.xiaowei.core.utils.Scheme;
import com.xingyeda.ehome.base.EHomeApplication;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/**
 * 针对服务器上xml的一个工具类
 *
 * @author ye.jian
 */

public class DnsXmlUtils {

    // DNS XML地址
    private static Stack<String> mDNSUrls = new Stack<>();
    private static String sPath1 = "http://xwdns1.cloudsee" +
            ".net:8088/ipManage/getaccdns.do";
    private static String sPath2 = "http://xwdns2.cloudsee" +
            ".net:8088/ipManage/getaccdns.do";
    private static String sPath3 = "http://xwdns3.cloudsee" +
            ".net:8088/ipManage/getaccdns.do";

//    private static String sPath1 = "http://xwdns1.cloudsee.net:8088/ipManaget/getaccdns.do";
//    private static String sPath2 = "http://xwdns2.cloudsee.net:8088/ipManaget/getaccdns.do";
//    private static String sPath3 = "http://xwdns3.cloudsee.net:8088/ipManaget/getaccdns.do";

    // DNS文件是否已经下载解析完成
    private static boolean isDnsDownloadAndParseFinish = false;

    // ----------------------------------------------
    // # Web接口地址获取
    // ----------------------------------------------

    /**
     * DNS文件是否正常
     *
     * @return
     */
    public static boolean isDnsNormal() {
        return DnsXmlEntity.getInstance().getResult() == 0;
    }

    /**
     * 获取账号相关接口地址
     *
     * @param shortUrl 接口短地址
     * @return
     */
    public static String getAccountInterface(String shortUrl) {
        StringBuffer sb = new StringBuffer(DnsXmlEntity.getInstance()
                .getAccountBaseUrl());
        if (!TextUtils.isEmpty(shortUrl)) {
            sb.append(shortUrl);
        }
        return sb.toString();
    }

    /**
     * 获取设备相关的接口地址
     *
     * @param shortUrl 接口短地址
     * @return
     */
    public static String getDeviceInterface(String shortUrl) {
        StringBuffer sb = new StringBuffer(DnsXmlEntity.getInstance()
                .getDeviceBaseUrl());
        if (!TextUtils.isEmpty(shortUrl)) {
            sb.append(shortUrl);
        }
        return sb.toString();
    }

    /**
     * 获取报警相关的接口地址
     *
     * @param shortUrl 接口短地址
     * @return
     */
    public static String getAlarmInterface(String shortUrl) {
        StringBuffer sb = new StringBuffer(DnsXmlEntity.getInstance()
                .getAlarmBaseUrl());
        if (!TextUtils.isEmpty(shortUrl)) {
            sb.append(shortUrl);
        }
        return sb.toString();
    }

    /**
     * 获取其它的接口地址
     *
     * @param shortUrl 接口短地址
     * @return
     */
    public static String getOtherInterface(String shortUrl) {
        StringBuffer sb = new StringBuffer(DnsXmlEntity.getInstance()
                .getOtherBaseUrl());
        if (!TextUtils.isEmpty(shortUrl)) {
            sb.append(shortUrl);
        }
        return sb.toString();
    }

    /**
     * 获取云存储的接口地址
     *
     * @param shortUrl 接口短地址
     * @return
     */
    public static String getCloudInterface(String shortUrl) {
        StringBuffer sb = new StringBuffer(DnsXmlEntity.getInstance()
                .getCloudBaseUrl());
        if (!TextUtils.isEmpty(shortUrl)) {
            sb.append(shortUrl);
        }
        return sb.toString();
    }

    // ----------------------------------------------
    // # DNS文件下载/解析
    // ----------------------------------------------

    /**
     * 检查DNS文件(耗时方法)
     * 1.DNS文件如果需要下载会自动下载
     * 2.如果DNS文件需要下载, 当前调用线程会进入等待状态, 直到DNS文件下载/解析完成后唤醒
     */
    public static void checkDnsFile() {
        if (isUpdateLocalDnsFile()) {
            resetDnsData();
            downloadDnsFile();
            waitDnsDownloadAndParse();
        }
    }

    /**
     * 下载DNS文件(xml格式)
     */
    public static void downloadDnsFile() {
        if (mDNSUrls.empty()) {
            return;
        }
        String url = mDNSUrls.pop();
        ObservableProvider.getDefault().download(url, new DownLoadSubscribe
                ("dns.xml") {
            @Override
            public void _onSuccess(File file) {
                Logger.i("download dns xml success.");
                // 解析DNS
                parseDnsFile(file);
                // 唤醒等待DNS下载解析完成的线程
                setDnsInitFinish();
            }

            @Override
            public void _onError(ApiException e) {
                Logger.e("download dns xml error:" + e.getMsg());
                // 用其它的dns地址进行请求
                downloadDnsFile();
                // 下载DNS失败,仍然需要唤醒等待中的线程
                if (mDNSUrls.empty()) {
                    setDnsInitFinish();
                }
            }

            @Override
            public void onProgress(double progress, long downloadSize, long
                    totalSize) {
                Logger.i("dns xml progress:" + progress + " downloadSize:" +
                        downloadSize + " totalSize:" + totalSize);
            }
        });
    }

    /**
     * 重置DNS TODO
     * 1.重置DNS下载/解析标志
     * 2.重新设置DNS获取地址列表
     */
    public static void resetDnsData() {
        Logger.i("start update dns xml.");
        isDnsDownloadAndParseFinish = false;
        mDNSUrls.push(sPath3);
        mDNSUrls.push(sPath2);
        mDNSUrls.push(sPath1);
    }

    /**
     * 手动设置DNS下载/解析完成
     */
    private static void setDnsInitFinish() {
        // 唤醒等待DNS下载解析完成的线程
        synchronized (DnsXmlUtils.class) {
            isDnsDownloadAndParseFinish = true;
            DnsXmlUtils.class.notifyAll();
            Logger.i("notify wait dns download's thread.");
        }
    }

    /**
     * 等待DNS文件下载/解析
     */
    private static void waitDnsDownloadAndParse() {
        synchronized (DnsXmlUtils.class) {
            while (!isDnsDownloadAndParseFinish) {
                try {
                    DnsXmlUtils.class.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    /**
     * 检查DNS文件是否需要更新
     *
     * @return
     */
    public static boolean isUpdateLocalDnsFile() {
        File dnsFile = FileUtils.getFileFromCache(EHomeApplication.getInstance(), "dns.xml");
        //1.文件不存在, 需要更新
        if (!dnsFile.exists()) {
            Logger.e("dns xml is not exist.");
            return true;
        }
        //2.文件中的result状态异常, 需要更新
        int result = DnsXmlEntity.getInstance().getResult();
        //2.1 DNS文件没有解析, 执行DNS解析
        if (result == -9000) {
            parseDnsFile(dnsFile);
        }
        //2.2 DNS文件的结果不正常, 需要更新
        boolean isNormal = DnsXmlEntity.getInstance().getResult() == 0;
        Logger.i("dns xml's result is true/false, result:" + isNormal);
        if (!isNormal) {
            return true;
        }
        //3.DNS文件非今天下载的, 需要更新
        Date lastModified = new Date(dnsFile.lastModified());
        Logger.i("dns xml's date:" + DateUtils.toDate(lastModified));
        Date current = new Date();
        if (!DateUtils.isTheSameDay(lastModified, current)) {
            Logger.e("dns xml is out of date.");
            return true;
        }

        return false;
    }

    /**
     * 解析DNS文件 TODO
     * <p>
     * xml文件内容↓:
     * <?xml version="1.0" encoding="UTF-8"?>
     * <dns>
     * <retcode>0</retcode>
     * <domainlist>
     * <domain name="xwacc.cloudsee.net" port="17009" timeout="1800">
     * <ip>123.57.162.111</ip>
     * </domain>
     * <domain name="accountService">
     * <url>123.57.162.111:8088/accountService</url>
     * </domain>
     * <domain name="alarmService">
     * <url>123.57.162.111:8088/publicService</url>
     * </domain>
     * <domain name="deviceService">
     * <url>123.57.162.111:8088/publicService</url>
     * </domain>
     * <domain name="otherService">
     * <url>123.57.162.111:8088/publicService</url>
     * </domain>
     * </domainlist>
     * </dns>
     */
    private static void parseDnsFile(File file) {
        Logger.i("parse dns xml, file path:" + file.getPath());
        List<DomainEntity> domainList = null;
        DomainEntity domain = null;
        int result = -1;

        try {
            FileInputStream inputStream = new FileInputStream(file);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(inputStream, "UTF-8");

            byte[] sb = new byte[1024];
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        domainList = new ArrayList<>();
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        if (tag.equals("retcode")) {
                            result = Integer.parseInt(parser.nextText());
                        }

                        if ("domain".equals(tag)) {
                            domain = new DomainEntity();
                            domain.setName(parser.getAttributeValue(0));
                        }

                        if (domain != null) {
                            if ("ip".equals(tag)) {
                                domain.setIP(parser.nextText());
                            } else if ("url".equals(tag)) {
                                String url = parser.nextText();
                                if (!url.startsWith("http")) {
                                    url = Scheme.HTTP.wrap(url);
                                    domain.setUrl(url);
                                } else {
                                    domain.setUrl(url);
                                }
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("domain".equals(parser.getName())) {
                            domainList.add(domain);
                            domain = null;
                        }
                        break;
                    default:
                        break;
                }

                event = parser.next();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Logger.i("// +++++++++DNS XML INFO ↓+++++++++");
        Logger.i("->result:" + result);
        // 构筑dns信息对象
        DnsXmlEntity dns = DnsXmlEntity.getInstance();
        dns.setResult(result);
        if (domainList != null) {
            for (DomainEntity item : domainList) {
                if ("accountService".equals(item.getName())) {
                    Logger.i("->accountService:" + item.getUrl());
                    dns.setAccountBaseUrl(item.getUrl());
                } else if ("alarmService".equals(item.getName())) {
                    Logger.i("->alarmService:" + item.getUrl());
                    dns.setAlarmBaseUrl(item.getUrl());
                } else if ("deviceService".equals(item.getName())) {
                    Logger.i("->deviceService:" + item.getUrl());
                    dns.setDeviceBaseUrl(item.getUrl());
                    /*
                      现在xml中没有云存储对应的url(听说是xml中增加后,iPhone那边会崩溃)
                      所以现在云存储使用和设备一样的url
                     */
                    dns.setCloudBaseUrl(item.getUrl());
                } else if ("otherService".equals(item.getName())) {
                    Logger.i("->otherService:" + item.getUrl());
                    dns.setOtherBaseUrl(item.getUrl());
                }
            }
        }
        Logger.i("// +++++++++DNS XML INFO ↑+++++++++");
    }
}
