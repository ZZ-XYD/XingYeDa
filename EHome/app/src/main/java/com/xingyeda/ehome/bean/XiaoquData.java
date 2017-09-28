package com.xingyeda.ehome.bean;

import java.util.List;

public class XiaoquData {
    
    private String mId;
    private String mName;
    private List<QishuData> qishu;

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
    public List<QishuData> getQishu() {
        return qishu;
    }
    public void setQishu(List<QishuData> qishu) {
        this.qishu = qishu;
    }
    @Override
    public String toString()
    {
        return  mName ;
    }
    

}
