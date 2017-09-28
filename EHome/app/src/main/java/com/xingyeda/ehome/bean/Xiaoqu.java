package com.xingyeda.ehome.bean;

public class Xiaoqu {
	private String mId;
    private String mName;
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
	@Override
    public String toString()
    {
        return  mName ;
    }
    
}
