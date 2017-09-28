package com.xingyeda.ehome.bean;

import java.util.List;

public class QishuData {
    
    private String mId;
    private String mName;
    private List<DongshuData> dongshu;

    public String getmId() {
        return mId;
    }
    public void setmId(String mId) {
        this.mId = mId;
    }
    public String getmName() {
        return mName;
    }
    public void setmName(String mName) {
        this.mName = mName;
    }
    public List<DongshuData> getDongshu() {
        return dongshu;
    }
    public void setDongshu(List<DongshuData> dongshu) {
        this.dongshu = dongshu;
    }
    @Override
    public String toString() {
	return mName;
    }

    
}
