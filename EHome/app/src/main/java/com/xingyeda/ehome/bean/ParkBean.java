package com.xingyeda.ehome.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by LDL on 2017/9/27.
 */

public class ParkBean extends DataSupport {

    private int id;
    private String mUserId;//用户id
    private String mTitle;//标题
    private String mContent;//内容
    private String mTime;//时间
    private String mPicture;//图片
    private int mIsExamine;//是否查看 : 0 -- 未查看, 1 -- 查看
    private boolean isChecked;

    public ParkBean(String mUserId, String mTitle, String mContent, String mTime, String mPicture, int mIsExamine) {
        this.mUserId = mUserId;
        this.mTitle = mTitle;
        this.mContent = mContent;
        this.mTime = mTime;
        this.mPicture = mPicture;
        this.mIsExamine = mIsExamine;
    }

    public ParkBean() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmPicture() {
        return mPicture;
    }

    public void setmPicture(String mPicture) {
        this.mPicture = mPicture;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public int getmIsExamine() {
        return mIsExamine;
    }

    public void setmIsExamine(int mIsExamine) {
        this.mIsExamine = mIsExamine;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
