package com.xingyeda.ehome.base;

import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.bean.UserInfo;
import com.xingyeda.ehome.util.SharedPreUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import static com.alipay.sdk.authjs.a.b;

/**
 * Created by LDL on 2017/10/26.
 */

public class LitePalUtil {

    /**
     * 保存与修改UserInfo
     * @param info
     */
    public static void setUserInfo(UserInfo info){
        List<UserInfo> list = DataSupport.where("mId = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId")).find(UserInfo.class);
        if (list != null && !list.isEmpty()) {
//            DataSupport.updateAll(UserInfo.class, info, "mUserId = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"));
            info.updateAll("mId = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"));
        }else{
            info.save();
        }
    }

    /**
     *
     * @return 获取UserInfo
     */
    public static UserInfo getUserInfo(){
        List<UserInfo> list = DataSupport.where("mId = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId")).find(UserInfo.class);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }else{
            return null;
        }
    }

    /**
     * 删除UserInfo
     *
     */
    public static void deleteUserInfo(){
        DataSupport.deleteAll(UserInfo.class,"mId = ?",SharedPreUtil.getString(EHomeApplication.getmContext(), "userId") );
    }

    /**
     * 默认小区的保存与刷新
     * @param bean
     */
    public static void setHomeBean(HomeBean bean){
        List<HomeBean> list = DataSupport.where("mId = ? and mIsDefault = ? and mHouseNumber = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),"1",bean.getmHouseNumber()).find(HomeBean.class);
        if (list != null && !list.isEmpty()) {
            bean.updateAll("mId = ? and mIsDefault = ? and mHouseNumber = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),"1",bean.getmHouseNumber());
        }else{
            bean.save();
        }
    }
    /**
     * 默认小区的保存与刷新
     * @param bean
     */
    public static void updateHomeBean(HomeBean bean){
        List<HomeBean> list = DataSupport.where("mId = ? and mIsDefault = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),"1").find(HomeBean.class);
        if (list != null && !list.isEmpty()) {
            HomeBean homeBean = list.get(0);
            homeBean.setmIsDefault("0");
            homeBean.updateAll("mId = ? and mEquipmentId = ? and mHouseNumber = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),homeBean.getmEquipmentId(),homeBean.getmHouseNumber());
            bean.setmIsDefault("1");
            bean.updateAll("mId = ? and mEquipmentId = ? and mHouseNumber = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),bean.getmEquipmentId(),bean.getmHouseNumber());
        }else{
            bean.save();
        }
    }

    /**
     * 获取默认小区
     * @return
     */
    public static HomeBean getHomeBean(){
        List<HomeBean> list = DataSupport.where("mId = ? and mIsDefault = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),"1").find(HomeBean.class);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }else{
            return null;
        }
    }

    /**
     * 删除默认小区
     */
    public static void deleteHomeBean(){
        DataSupport.deleteAll(HomeBean.class,"mId = ? and mIsDefault = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),"1" );
    }
    /**
     * 添加与修改相机list
     * @param bean
     */
    public static void addCameraList(HomeBean bean){
        List<HomeBean> list = DataSupport.where("mId = ? and mCameraId = ?",SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),bean.getmCameraId()).find(HomeBean.class);
        if (list != null && !list.isEmpty()) {
            bean.updateAll("mId = ? and mCameraId = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),bean.getmCameraId());
        }else{
            bean.save();
        }
    }
    /**
     * 添加与修改停车场list
     * @param bean
     */
    public static void addParkList(HomeBean bean){
        List<HomeBean> list = DataSupport.where("mId = ? and mParkId = ?",SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),bean.getmParkId()).find(HomeBean.class);
        if (list != null && !list.isEmpty()) {
            bean.updateAll("mId = ? and mParkId = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),bean.getmParkId());
        }else{
            bean.save();
        }
    }

    /**
     * 添加与修改小区list
     * @param bean
     */
    public static void addHomeList(HomeBean bean){
        List<HomeBean> list = DataSupport.where("mId = ? and mEquipmentId = ? and mHouseNumber = ?",SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),bean.getmEquipmentId(),bean.getmHouseNumber()).find(HomeBean.class);
        if (list != null && !list.isEmpty()) {
            bean.updateAll("mId = ? and mEquipmentId = ? and mHouseNumber = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),bean.getmEquipmentId(),bean.getmHouseNumber());
        }else{
            bean.save();
        }
    }
    /**
     * 获取设备list
     * @return
     */
    public static List<HomeBean> getHomeList(){
        List<HomeBean> list = DataSupport.where("mId = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId")).order("mType asc").find(HomeBean.class);
//        List<HomeBean> list = DataSupport.findAll(HomeBean.class);
        if (list != null && !list.isEmpty()) {
            return list;
        }else{
            return null;
        }
    }
    /**
     * 获取小区list
     * @return
     */
    public static List<HomeBean> getCommunityList(){
        List<HomeBean> list = DataSupport.where("mId = ? and mType = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),"1").find(HomeBean.class);
        if (list != null && !list.isEmpty()) {
            return list;
        }else{
            return null;
        }
    }
    /**
     * 删除小区
     */
    public static void deleteHomeList(HomeBean bean){
        DataSupport.deleteAll(HomeBean.class,"mId = ? and mEquipmentId = ? and mHouseNumber = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"),bean.getmEquipmentId(),bean.getmHouseNumber());
    }

    /**
     * 删除所有小区
     */
    public static void deleteHomeListAll(){
        DataSupport.deleteAll(HomeBean.class,"mId = ?", SharedPreUtil.getString(EHomeApplication.getmContext(), "userId"));
    }



}
