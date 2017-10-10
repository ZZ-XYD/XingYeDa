package com.xingyeda.ehome.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.ldl.dialogshow.animation.BounceEnter.BounceTopEnter;
import com.ldl.dialogshow.animation.SlideExit.SlideBottomExit;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.MaterialDialog;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
//import com.xiaomi.mipush.sdk.ErrorCode;
//import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.ActivityLogin;
import com.xingyeda.ehome.Service.HeartbeatService;
import com.xingyeda.ehome.Test;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.InformationBase;
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
import com.xingyeda.ehome.util.SharedPreUtil;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.R.attr.entries;
import static android.R.attr.path;


/**
 * Created by LDL on 2017/5/9.
 */

public class MiPushReceiver extends PushMessageReceiver {

    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;
    private EHomeApplication mApplication;
    private Context mContext;
    private PushBean bean;

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        mContext = context;
        mApplication = (EHomeApplication) mContext.getApplicationContext();

        if (mApplication.getActivityStack()!=null) {
            if (!mApplication.getActivityStack().isEmpty()) {
                boolean isStart = false;
                boolean isReturn = true;
                for (Activity activity : mApplication.getActivityStack()) {
                    if (activity.getClass().equals(ActivityLogin.class)) {
                        return ;
                    }
                }
                for (Activity activity : mApplication.getActivityStack()) {
                    if (activity.getClass().equals(ActivityHomepage.class)) {
                        isReturn = false;
                    }
                    isStart = true;
                }
                if (isStart) {
                    if (isReturn) {
                        return ;
                    }
                }
            }
        }


//        String log = context.getString(R.string.recv_passthrough_message, message.getContent());
        String msg = message.getExtra().get("pushObject");
        // LogUtils.i(message);

        Gson gson = new Gson();
        // JSONObject jsonObject = gson.fromJson(message, \);

        if (msg != null&&!msg.equals("")) {

//            BaseUtils.showLongToast(mContext,"mipush");
             bean = gson.fromJson(msg, PushBean.class);
            LogUtils.i("MiPushMsgId   "+bean.getmMsgId());
            LogUtils.i("MiPushMsgId   "+bean.toString());
            if (mApplication.getmPushMap()!=null) {
            Iterator<Map.Entry<String, Boolean>> entries = mApplication.getmPushMap().entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Boolean> entry = entries.next();
                if (bean.getmMsgId().equals(entry.getKey())) {
                    return;
                }
            }
            }
//            Intent mIntent2 = new Intent(JVMaoYanActivity.ACTION_MI);
//            mContext.sendBroadcast(mIntent2);
//
//            Intent mIntent1 = new Intent(JVMaoYanActivity.ACTION_PUSH);
//            mContext.sendBroadcast(mIntent1);
            mApplication.addPushMap(bean.getmMsgId(),true);
//            PushBean bean = rBean.getPushObject();
//            DBManager manager = new DBManager(mContext);
            if (null == bean) {
                return;
            }
            if (null == bean.getmType()) {
                return;
            }
            if (!bean.getmType().equals("8")) {
                if (null == bean.getRegId()) {
                    return ;
                }
            }
            if (!bean.getmType().equals("3") && !bean.getmType().equals("6")) {
                if (bean.getmType().equals("2")) {
                    // mImagePath = getPhotoFileName();
                    if (mApplication.getmCurrentUser() != null) {
//                        mApplication.getmManager().addScore(new InformationBase(Integer.valueOf(mApplication.getmCurrentUser().getmId()), bean.getmAdminName(), bean.getEaddress(),
//                                  bean.getTitle(), bean.getAlertContent(), bean.getTime(), Integer.valueOf(bean.getSendType()), 0, bean.getPhotograph(),0, 0));
                        InformationBase informationBase = new InformationBase(mApplication.getmCurrentUser().getmId(),bean.getmAdminName(),
                                bean.getEaddress(), bean.getTitle(), bean.getAlertContent(), bean.getTime(), Integer.valueOf(bean .getSendType()), 0, bean.getPhotograph(),0, 0);
                        informationBase.save();
                    }
                    // getImagePath(bean.getPhotograph());
                } else {
//                    mApplication.getmManager().addScore(new InformationBase(Integer.valueOf(mApplication.getmCurrentUser().getmId()), bean.getmAdminName(), bean.getEaddress(),
//                            bean.getTitle(), bean.getAlertContent(), bean.getTime(), Integer.valueOf(bean.getSendType()), 0, null, -1, -1));
                    InformationBase informationBase = new InformationBase(mApplication.getmCurrentUser().getmId(),bean.getmAdminName(), bean.getEaddress(), bean.getTitle(),
                            bean.getAlertContent(), bean.getTime(), Integer.valueOf(bean.getSendType()), 0, null, -1, -1);
                    informationBase.save();
                }
            }
            if (bean != null) {
                // 拨号
                if (bean.getmType().equals("2")) {
                    Intent mIntent = new Intent(Test.ACTION_MI1);
                    mContext.sendBroadcast(mIntent);
                    if (SharedPreUtil.getBoolean(mContext, "receivecall")) {
                        try {
                            Bundle bundle = new Bundle();
                            final SimpleDateFormat sdf = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss");
                            final Date startTime = sdf.parse(bean.getTime());
                            if (!("".equals(bean.getmUrl())) && null !=bean.getmUrl()){
                            videoCallBack(bean.getmUrl());
                            }
                            bundle.putString("dongshu", bean.getmUtil());
                            bundle.putString("eid", bean.getEid());
                            bundle.putString("housenum", bean.getHousenum());
                            bundle.putString("type", "dial");
                            bundle.putString("addressData", bean.getEaddress());
                            bundle.putString("jie", bean.getJietong());
                            bundle.putString("echo", bean.getmUrl());
                            bundle.putString("rtmp", bean.getRtmp());
                            bundle.putString("time", bean.getTime());
                            if (mApplication.getmCurrentUser() != null) {
                                bundle.putString("code", bean.getmCode());
                                LogUtils.i("呼叫时间 ： " + startTime);
                                LogUtils.i("服务器当前时间：" + formatTimeInMillis(BaseUtils.getServerTime(mContext)));
                                if ((BaseUtils.getServerTime(mContext) - startTime.getTime()) <= 10000) {
                                    BaseUtils.startActivities(mContext, ActivityVideo.class, bundle);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                } else if (bean.getmType().equals("3")) {
//                    Intent mIntent = new Intent(JVMaoYanActivity.ACTION_MI2);
//                    mContext.sendBroadcast(mIntent);
                    LogUtils.i("jpush : 挂断提示");
                    for (Activity activity : mApplication.getActivityStack()) {
                        if (activity.getClass().equals(ActivityVideo.class)) {
                            SharedPreUtil.put(mContext, "isFinish", true);
                            mApplication.finishActivity(ActivityVideo.class);
                            return;
                        }
                    }
                } else if (bean.getmType().equals("4")) {
//                    Intent mIntent = new Intent(JVMaoYanActivity.ACTION_MI3);
//                    mContext.sendBroadcast(mIntent);
                    LogUtils.i("jpush : 审核通知");
//		    Intent intentBroadcast = new Intent();
//		    intentBroadcast.setAction("check");
//		    mContext.sendBroadcast(intent, null);   //广播发送
//		    Intent intentBroadcast = new Intent("com.xinyeda.check");
//		    mContext.sendBroadcast(intentBroadcast);


                    Intent mIntent = new Intent(DoorFragment.ACTION_NAME);
                    mIntent.putExtra("yaner", "check");
                    //发送广播
                    mContext.sendBroadcast(mIntent);


                    SharedPreUtil.put(mContext, "isDoor_Upload", true);
                } else if (bean.getmType().equals("5")) {
//                    Intent mIntent = new Intent(JVMaoYanActivity.ACTION_MI4);
//                    mContext.sendBroadcast(mIntent);
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
                            return;
                        }
                    }
                } else if (bean.getmType().equals("7")) {
//                    Intent mIntent = new Intent(JVMaoYanActivity.ACTION_MI5);
//                    mContext.sendBroadcast(mIntent);

                    LogUtils.i("jpush : 物业通知");
                    SharedPreUtil.put(mContext, "isTenement_Upload", true);
                    final NormalDialog dialog = DialogShow.showSelectDialogONE(context, bean.getTitle(), bean.getAlertContent());
                    dialog.setOnBtnClickL(new OnBtnClickL() {
                        @Override
                        public void onBtnClick() {
                            dialog.dismiss();
                        }
                    });

                }else if (bean.getmType().equals("8")) {

                    final MaterialDialog dialog = new MaterialDialog(context);
                    dialog.btnNum(2)
                            .title( bean.getTitle())
                            .content("\t\t" + bean.getAlertContent())
                            .btnText(new String[]{"确定","查看详情"})
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
                }
            }



        }

    }



    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
//        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
//                SharedPreUtil.put(context,"State",true+"");
                //注册成功
            } else {
                //注册失败
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
//                SharedPreUtil.put(context,"State",true+"");
                //注册成功
//                log = context.getString(R.string.set_alias_success, mAlias);
            } else {
                //注册失败原因
//                log = context.getString(R.string.set_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
//                log = context.getString(R.string.unset_alias_success, mAlias);
            } else {
//                log = context.getString(R.string.unset_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
//                log = context.getString(R.string.set_account_success, mAccount);
            } else {
//                log = context.getString(R.string.set_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAccount = cmdArg1;
//                log = context.getString(R.string.unset_account_success, mAccount);
            } else {
//                log = context.getString(R.string.unset_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
//                log = context.getString(R.string.subscribe_topic_success, mTopic);
            } else {
//                log = context.getString(R.string.subscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
//                log = context.getString(R.string.unsubscribe_topic_success, mTopic);
            } else {
//                log = context.getString(R.string.unsubscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
//                mEndTime = cmdArg2;
//                log = context.getString(R.string.set_accept_time_success, mStartTime, mEndTime);
            } else {
//                log = context.getString(R.string.set_accept_time_fail, message.getReason());
            }
        } else {
//            log = message.getReason();
        }
    }

    private void videoCallBack(String mEcho) {
        OkHttp.get(mContext,mEcho, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
            @Override
            public void onResponse(JSONObject response) {
            }
        }));
    }
    private void msgCallBack(String id) {
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        OkHttp.get(EHomeApplication.getmContext(), ConnectPath.PUSHMSG_PATH,params, new ConciseStringCallback(mContext, new ConciseCallbackHandler<String>() {
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