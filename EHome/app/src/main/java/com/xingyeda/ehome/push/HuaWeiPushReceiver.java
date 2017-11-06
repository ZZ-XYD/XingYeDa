/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2016. All rights reserved.
 * See LICENSE.txt for this sample's licensing information.
 */

package com.xingyeda.ehome.push;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.huawei.hms.support.api.push.PushReceiver;
import com.ldl.dialogshow.animation.BounceEnter.BounceTopEnter;
import com.ldl.dialogshow.animation.SlideExit.SlideBottomExit;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.MaterialDialog;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.Service.HeartbeatService;
import com.xingyeda.ehome.Test;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.base.LitePalUtil;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.bean.ParkBean;
import com.xingyeda.ehome.bean.PushBean;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.ActivityVideo;
import com.xingyeda.ehome.door.DoorFragment;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.tenement.Notice_Activity;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.zhibo.ActivityShareMain;
import com.xingyeda.ehome.zhibo.ActivitySharePlay;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.R.id.message;


/*
 * 接收Push所有消息的广播接收器
 */
public class HuaWeiPushReceiver extends PushReceiver {

    private EHomeApplication mApplication;
    private Context mContext;
    private PushBean bean;

    @Override
    public void onToken(Context context, String token, Bundle extras) {
//        String belongId = extras.getString("belongId");
//        String content = "获得token和belongId成功, token = " + token + ",belongId = " + belongId;
        Map<String, String> params = new HashMap<>();
        params.put("uId", SharedPreUtil.getString(mContext, "userId", ""));
        params.put("regkey", token);
        Looper.prepare();
        OkHttp.get(context, ConnectPath.HUAWEI_PUSH, params);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {


        try {
            mContext = context;
            mApplication = (EHomeApplication) mContext.getApplicationContext();
            if (mApplication.getActivityStack() != null && !mApplication.getActivityStack().isEmpty()) {
                boolean isStart = false;
                boolean isReturn = true;
                for (Activity activity : mApplication.getActivityStack()) {
                    if (activity.getClass().equals(ActivityLogin.class)) {
                        return false;
                    }
                }
                for (Activity activity : mApplication.getActivityStack()) {
                    if (activity.getClass().equals(ActivityHomepage.class)) {
                        isReturn = false;
                    } else if (activity.getClass().equals(ActivityShareMain.class)) {
                        isReturn = false;
                    }
                    isStart = true;
                }
                if (isStart) {
                    if (isReturn) {
                        return false;
                    }
                }
            }
//            String content = "通过msg接收一个push： " + new String(msg, "UTF-8");
            String content = new String(msg, "UTF-8");
            Log.d("HuaWeiPushReceiver", content);
            Gson gson = new Gson();

            if (content != null && !content.equals("")) {
//                ReceivePush rBean = gson.fromJson(content, ReceivePush.class);
//                PushBean bean = rBean.getPushObject();
                bean = gson.fromJson(content, PushBean.class);
                MyLog.i("HWPush信息：" + bean.toString());
                if (mApplication.getmPushMap() != null) {
                    Iterator<Map.Entry<String, Boolean>> entries = mApplication.getmPushMap().entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<String, Boolean> entry = entries.next();
                        LogUtils.i("HWPushmsgId : " + bean.getmMsgId());
                        LogUtils.i("HWPushmapKey : " + entry.getKey());
                        if (bean.getmMsgId().equals(entry.getKey())) {
                            return false;
                        }
                    }
                    mApplication.addPushMap(bean.getmMsgId(), true);
                    if (null == bean) {
                        return false;
                    }
                    if (null == bean.getmType()) {
                        return false;
                    }
                    if (!bean.getmType().equals("8")) {
                        if (null == bean.getRegId()) {
                            return false;
                        }
                    }
                    if (!bean.getmType().equals("3") && !bean.getmType().equals("6") && !bean.getmType().equals("8") && !bean.getmType().equals("11")) {
                        if (bean.getmType().equals("2")) {
                            if (LitePalUtil.getUserInfo() != null) {
                                InformationBase informationBase = new InformationBase(SharedPreUtil.getString(mContext, "userId", ""), bean.getmAdminName(),
                                        bean.getEaddress(), bean.getTitle(), bean.getAlertContent(), bean.getTime(), Integer.valueOf(bean.getSendType()), 0, bean.getPhotograph(), 0, 0);
                                informationBase.save();
                            }
                        } else {
                            InformationBase informationBase = new InformationBase(SharedPreUtil.getString(mContext, "userId", ""), bean.getmAdminName(), bean.getEaddress(), bean.getTitle(),
                                    bean.getAlertContent(), bean.getTime(), Integer.valueOf(bean.getSendType()), 0, null, -1, -1);
                            informationBase.save();
                        }
                    }
                    if (bean != null) {
                        // 拨号
                        if (bean.getmType().equals("2")) {

                            if (SharedPreUtil.getBoolean(mContext, "receivecall")) {
                                try {
                                    Bundle bundle1 = new Bundle();
                                    final SimpleDateFormat sdf = new SimpleDateFormat(
                                            "yyyy-MM-dd HH:mm:ss");
                                    final Date startTime = sdf.parse(bean.getTime());
                                    if (!("".equals(bean.getmUrl())) && null != bean.getmUrl()) {
                                        videoCallBack(context, bean.getmUrl());
                                    }
                                    bundle1.putString("dongshu", bean.getmUtil());
                                    bundle1.putString("eid", bean.getEid());
                                    bundle1.putString("housenum", bean.getHousenum());
                                    bundle1.putString("type", "dial");
                                    bundle1.putString("addressData", bean.getEaddress());
                                    bundle1.putString("jie", bean.getJietong());
                                    bundle1.putString("echo", bean.getmUrl());
                                    bundle1.putString("rtmp", bean.getRtmp());
                                    bundle1.putString("time", bean.getTime());
                                    if (LitePalUtil.getUserInfo() != null) {
                                        bundle1.putString("code", bean.getmCode());
                                        LogUtils.i("呼叫时间 ： " + startTime);
                                        LogUtils.i("服务器当前时间：" + formatTimeInMillis(BaseUtils.getServerTime(mContext)));
                                        if ((BaseUtils.getServerTime(mContext) - startTime.getTime()) <= 10000) {
                                            BaseUtils.startActivities(mContext, ActivityVideo.class, bundle1);

                                        }
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                        } else if (bean.getmType().equals("3")) {
                            LogUtils.i("jpush : 挂断提示");
                            for (Activity activity : mApplication.getActivityStack()) {
                                if (activity.getClass().equals(ActivityVideo.class)) {
                                    SharedPreUtil.put(mContext, "isFinish", true);
                                    mApplication.finishActivity(ActivityVideo.class);
                                    return false;
                                }
                            }
                        } else if (bean.getmType().equals("4")) {
                            LogUtils.i("jpush : 审核通知");
                            Intent mIntent = new Intent(DoorFragment.ACTION_NAME);
                            mIntent.putExtra("yaner", "check");
                            //发送广播
                            mContext.sendBroadcast(mIntent);


                            SharedPreUtil.put(mContext, "isDoor_Upload", true);
                        } else if (bean.getmType().equals("5")) {
                            LogUtils.i("jpush : 异地登录");
                            BaseUtils.showShortToast(mContext, "您的帐号在异地登录!");
                            mApplication.finishAllActivity();
                            mApplication.clearData();
                            mApplication.closemTimer();
                            mContext.stopService(new Intent(mContext,
                                    HeartbeatService.class));
                            BaseUtils.startActivity(mContext, ActivityLogin.class);
                        } else if (bean.getmType().equals("6")) {
                            LogUtils.i("jpush : 其他的接通了通知关掉呼叫界面");
                            for (Activity activity : mApplication.getActivityStack()) {
                                if (activity.getClass().equals(ActivityVideo.class)) {
                                    mApplication.finishActivity(ActivityVideo.class);
                                    return false;
                                }
                            }
                        } else if (bean.getmType().equals("7")) {
                            LogUtils.i("jpush : 物业通知");
                            SharedPreUtil.put(mContext, "isTenement_Upload", true);
                            final NormalDialog dialog = DialogShow.showSelectDialogONE(context, bean.getTitle(), bean.getAlertContent());
                            dialog.setOnBtnClickL(new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {
                                    dialog.dismiss();
                                }
                            });
                        } else if (bean.getmType().equals("8")) {

                            final MaterialDialog dialog = new MaterialDialog(context);
                            dialog.btnNum(2)
                                    .title(bean.getTitle())
                                    .content("\t\t" + bean.getAlertContent())
                                    .btnText(new String[]{"确定", "查看详情"})
                                    .showAnim(new BounceTopEnter())
                                    .dismissAnim(new SlideBottomExit());
//			final MaterialDialog dialog = DialogShow.showMessageDialog(EHomeApplication.getmContext(), bean.getTitle(),"\t\t" + bean.getAlertContent(),2, new String[]{"确定","查看详情"});
                            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                            dialog.setOnBtnClickL(new OnBtnClickL() {
                                // listener
                                @Override
                                public void onBtnClick() {
                                    msgCallBack(bean.getmCode());
                                    dialog.dismiss();
                                }
                            }, new OnBtnClickL() {
                                @Override
                                public void onBtnClick() {
                                    msgCallBack(bean.getmCode());
                                    Bundle bundle = new Bundle();
                                    bundle.putString("title", bean.getTitle());
                                    bundle.putString("content", bean.getAlertContent());
                                    bundle.putString("time", bean.getTime());
                                    bundle.putString("imageList", null);
                                    bundle.putString("bean", "annunciate");
                                    dialog.dismiss();
                                    BaseUtils.startActivities(EHomeApplication.getmContext(), Notice_Activity.class, bundle);

                                }
                            });
                        } else if (bean.getmType().equals("9")) {//停车场出入消息
                            ParkBean parkBean = new ParkBean(SharedPreUtil.getString(mContext, "userId", ""), bean.getTitle(), bean.getmMsg(), bean.getTime(), bean.getPhotograph(), 0);
                            parkBean.save();
                        } else if (bean.getmType().equals("11")) {//聊天室消息
                            String name = bean.getmCode();
                            String contentString = bean.getAlertContent();
                            String time = bean.getTime();
                            Intent msgIntent = new Intent(ActivitySharePlay.ACTION_NAME);
                            msgIntent.putExtra("name", name);
                            msgIntent.putExtra("content", contentString);
                            msgIntent.putExtra("time", time);
                            mContext.sendBroadcast(msgIntent);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
//            String content = "收到延伸通知消息: " + extras.getString(BOUND_KEY.pushMsgKey);
        }
        super.onEvent(context, event, extras);
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
        try {
//            String content = "推动现状： " + (pushState ? "连接" : "断开连接");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void videoCallBack(Context context, String mEcho) {
        Looper.prepare();
        OkHttp.get(context, mEcho, new ConciseStringCallback(context, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }));
    }

    private void msgCallBack(String id) {
        Map<String, String> params = new HashMap<>();
        params.put("id", id);
        OkHttp.get(EHomeApplication.getmContext(), ConnectPath.PUSHMSG_PATH, params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }));
    }

    public static String formatTimeInMillis(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        Date date = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fmt = dateFormat.format(date);

        return fmt;
    }
}
