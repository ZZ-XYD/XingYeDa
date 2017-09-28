package com.xingyeda.ehome.util;

import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class BaseUtils
{
    private static Intent mIntent;
    private static Toast mToast;
    /**
     * 界面跳转
     * @param context : 要跳转的当前Activity
     * @param cls : 跳转到达的目的Activity
     */
    public static void startActivity(Context context,Class<?> cls)
    {
        startActivities(context,cls, null);
    }
    /**
     *  带参数界面跳转
     * @param context : 要跳转的当前Activity
     * @param cls : 跳转到达的目的Activity
     * @param bdl : 传递的参数Bundle
     */
    public static void startActivities(Context context,Class<?> cls, Bundle bdl)
    {
        if(mIntent==null)
        {
            mIntent = new Intent(context, cls);
        }
        mIntent.setClass(context, cls);
        if(bdl!=null)
        {
            mIntent.putExtras(bdl);
        }
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        context.startActivity(mIntent);
    }
    
    /**
     * 短Toast
     * @param context : 展示的Activity
     * @param msg : 展示内容
     */
    public static void showShortToast(Context context,CharSequence msg)
    {
        if(null==mToast)
        {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setText(msg);
        mToast.show();
    }
    
    /**
     * 短Tosat
     * @param context : 展示的Activity
     * @param resId : 展示内容的ID
     */
    public static void showShortToast(Context context,int resId)
    {
        showShortToast(context,context.getResources().getText(resId));
    }
    /**
     * 长Toast 
     * @param context : 展示的Activity
     * @param msg : 展示内容
     */
    public static void showLongToast(Context context,CharSequence msg)
    {
        if(null==mToast)
        {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setText(msg);
        mToast.show();
    }
    /**
     * 长Toast 
     * @param context : 展示的Activity
     * @param resId : 展示内容的ID
     */
    public static void showLongToast(Context context,int resId)
    {
        showShortToast(context,context.getResources().getText(resId));
    }
    
    public static long getServerTime(Context context){
	Date date = new Date(SharedPreUtil.getLong(context, "time_difference")+System.currentTimeMillis());//获取当前时间
	return (long)date.getTime();
	
	
    }


}
