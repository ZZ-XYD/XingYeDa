package com.jovision.server.entities;

/**
 * DNS xml文件中domainlist中的内容对应的实体类
 *
 * @author ye.jian
 */

public class DomainEntity {

    private String mName;
    private String mIP;
    private String mUrl;

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getIP() {
        return mIP;
    }

    public void setIP(String IP) {
        mIP = IP;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
