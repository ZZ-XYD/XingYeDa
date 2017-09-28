package com.xingyeda.ehome.bean;

import java.util.ArrayList;
import java.util.List;

public class BeanComplainHistory
{
    //"id":19,"title":"24","content":"asf","time":"2016-04-19","uid":1,"state":0
    private String mType;
    private String mTime;
    private String mTitle;
    private String mContent;
    private List<String> mImageList;
    
    //{"obj":[{"fileList":[{"path":"upload/ccc.jpg"},{"path":"upload/ddd.jpg"}],"id":1,"title":"1","content":"klasj","time":"2016-04-15","uid":73,"state":1,"filePath":"upload/ccc.jpg,upload/ddd.jpg"},{"fileList":[{"path":"upload/ccc.jpg"},{"path":"upload/ddd.jpg"}],"id":2,"title":"2","content":"klasj","time":"2016-04-15","uid":73,"state":1,"filePath":"upload/ccc.jpg,upload/ddd.jpg"},{"fileList":[{"path":"upload/ccc.jpg"},{"path":"upload/ddd.jpg"}],"id":3,"title":"3","content":"klasj","time":"2016-04-15","uid":73,"state":1,"filePath":"upload/ccc.jpg,upload/ddd.jpg"},{"id":4,"title":"4","content":"klasj","time":"2016-04-15","uid":73,"state":1},{"id":5,"title":"5","content":"klasj","time":"2016-04-15","uid":73,"state":1},{"id":6,"title":"6","content":"klasj","time":"2016-04-15","uid":73,"state":1},{"id":7,"title":"7","content":"klasj","time":"2016-04-15","uid":73,"state":1},{"id":8,"title":"8","content":"klasj","time":"2016-04-15","uid":73,"state":1},{"id":9,"title":"9","content":"klasj","time":"2016-04-15","uid":73,"state":1},{"id":10,"title":"11","content":"klasj","time":"2016-04-15","uid":73,"state":1},{"id":11,"title":"12","content":"klasj","time":"2016-04-15","uid":73,"state":1},{"id":12,"title":"13","content":"klasj","time":"2016-04-15","uid":73,"state":0},{"id":13,"title":"14","content":"klasj","time":"2016-04-15","uid":73,"state":0},{"id":14,"title":"15","content":"klasj","time":"2016-04-15","uid":73,"state":0},{"id":15,"title":"16","content":"klasj","time":"2016-04-15","uid":73,"state":0},{"id":16,"title":"21","content":"klasj","time":"2016-04-15","uid":73,"state":0},{"id":17,"title":"22","content":"klasj","time":"2016-04-15","uid":73,"state":0},{"id":18,"title":"23","content":"klasj","time":"2016-04-15","uid":73,"state":0},{"id":19,"title":"24","content":"asf","time":"2016-04-19","uid":73,"state":0},{"id":20,"title":"asfasf","content":"asf","time":"2016-05-03","uid":73,"state":0},{"id":21,"title":"asfasf","content":"asf","time":"2016-05-03","uid":73,"state":0}],"status":"200","msg":"操作成功"}
    

    public String getmTime()
    {
        return mTime;
    }

    public String getmType()
    {
        return mType;
    }

    public void setmType(String mType)
    {
        this.mType = mType;
    }

    public void setmTime(String mTime)
    {
        this.mTime = mTime;
    }

    public String getmTitle()
    {
        return mTitle;
    }

    public void setmTitle(String mTitle)
    {
        this.mTitle = mTitle;
    }

    public String getmContent()
    {
        return mContent;
    }

    public void setmContent(String mContent)
    {
        this.mContent = mContent;
    }

    public ArrayList<String> getmImageList()
    {
        return (ArrayList<String>) mImageList;
    }

    public void setmImageList(List<String> mImageList)
    {
        this.mImageList = mImageList;
    }
    
}
