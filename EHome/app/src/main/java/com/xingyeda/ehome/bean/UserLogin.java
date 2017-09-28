package com.xingyeda.ehome.bean;

import java.io.Serializable;



@SuppressWarnings("serial")
public class UserLogin implements Serializable
{
    private String mUserName;
    private String mUserPwd;
    public String getmUserName()
    {
        return mUserName;
    }
    public void setmUserName(String mUserName)
    {
        this.mUserName = mUserName;
    }
    public String getmUserPwd()
    {
        return mUserPwd;
    }
    public void setmUserPwd(String mUserPwd)
    {
        this.mUserPwd = mUserPwd;
    }
    
}
