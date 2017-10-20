package com.xingyeda.ehome.zhibo;

/**
 * Created by LDL on 2017/10/19.
 */

public class MessageBean {

    private String mName;
    private String mContent;

    public MessageBean() {
    }

    public MessageBean(String mName, String mContent) {
        this.mName = mName;
        this.mContent = mContent;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }
}
