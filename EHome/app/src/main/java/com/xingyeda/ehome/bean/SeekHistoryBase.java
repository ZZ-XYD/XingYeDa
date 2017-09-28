package com.xingyeda.ehome.bean;

import org.litepal.crud.DataSupport;

public class SeekHistoryBase extends DataSupport{

    private int mId;
    private String name;
    private String mUserId;
    private String mTime;


    public SeekHistoryBase(String name, String time) {
        this.name = name;
        this.mTime = time;
    }
    public SeekHistoryBase(String name) {
        this.name = name;
    }
    public SeekHistoryBase() {
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return mUserId;
    }

    public void setUid(String uid) {
        this.mUserId = uid;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }
}
