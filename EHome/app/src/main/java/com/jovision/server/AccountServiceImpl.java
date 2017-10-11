package com.jovision.server;

import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.util.LogUtil;

import com.jovision.JniUtil;
import com.jovision.PlayUtil;
import com.jovision.Utils.BackgroundHandler;
import com.jovision.Utils.SimpleTask;
import com.jovision.Utils.TokenUtil;
import com.jovision.account.MaoyanGuestActivity;
import com.jovision.server.exception.CustomCode;
import com.jovision.server.exception.RequestError;
import com.jovision.server.listener.ResponseListener;
import com.jovision.server.utils.CommonUtils;
import com.jovision.server.utils.DnsXmlUtils;
import com.xiaowei.comm.Account;
import com.xiaowei.core.utils.FileUtils;
import com.xiaowei.core.utils.Logger;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.InformationBase;
import com.xingyeda.ehome.menu.ActivityAbout;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.LogcatHelper;
import com.xingyeda.ehome.util.MyLog;
import com.xingyeda.ehome.util.SharedPreUtil;

import java.io.File;

import static android.R.attr.id;
import static com.jovision.AppConsts.SDCARD_PATH;
import static com.xingyeda.ehome.push.JPushReceiver.formatTimeInMillis;


/**
 * 账号库处理逻辑
 *
 * @author ye.jian
 */
public class AccountServiceImpl {

    // ---------------------------------------------
    // #
    // ---------------------------------------------
    private AccountServiceImpl() {
    }

    private static class SingletonLoader {
        private static final AccountServiceImpl INSTANCE = new
                AccountServiceImpl();
    }

    public static AccountServiceImpl getInstance() {
        return SingletonLoader.INSTANCE;
    }

    // -------------------------------------------
    // # 账号服务(此部分的JNI接口全部为异步)↓
    // -------------------------------------------
    // 平台标识
    public static final int BIZ_ACC_ANDROID = 0x10;
    // JNI接口调用超时时间(30秒)
    private static final int TIMEOUT = 30 * 1000;
    // 账号库错误信息在APP中对应的字符串的前缀
    private static final String ERROR_CODE_PREFIX = "lib_error_";
    // 任务锁
    private final Object mTaskLock = new Object();
    // session ID(默认为"",设置成null,个别接口处理会有问题)
    private String mSessionID = "";
    // cloudsee session(云视通端session,例：商城)
    private String mCloudSeeSession;
    // DNS配置是否正常
    private boolean isDnsNormal = false;
    // 具体的任务、超时时间计算任务
    private SimpleTask mTask, mTimeoutTask;
    // 操作是否完成(成功/失败都认为是完成)
    private boolean isTaskFinish = false;
    // 登录结果
    private int mLoginResult = -9999;
    // 推送开关设置的结果
    private int mPushResult;
    // 是否已经登录
    public boolean isLogin;

    public String logAccount = "";

    /**
     * 初始化
     */
    public void init() {
        Logger.i("// ==============================");
        Logger.i("// # INIT");
        Logger.i("// ==============================");
        String dnsPath = FileUtils.getFileFromCache(EHomeApplication
                .getInstance(), "dns.xml").getPath();
        String LOG_ACCOUNT_PATH = Environment.getExternalStorageDirectory().getPath()
                + File.separator + "jovision" + File.separator;
        Account.init(BIZ_ACC_ANDROID, 1, Account.BIZ_ACC_CH, LOG_ACCOUNT_PATH, dnsPath, this);
    }

    /**
     * 释放
     */
    public void release() {
        Logger.i("// ==============================");
        Logger.i("// # RELEASE");
        Logger.i("// ==============================");
        isLogin = false;
        logAccount = "";
        Account.term();
    }

    /**
     * 登录
     *
     * @param userName
     * @param password
     */
    public void login(final String userName, final String password, final
    ResponseListener listener) {
        /*
          检查网络<br/>
          自动登录时,不需要设置回调监听,所以listener为null<br/>
          自动登录时,即使没有网络,也要去执行login流程;否则重新打开网络也不会自动尝试重新登录<br/>
          注:执行login方法以后,账号库会自己控制与服务器的连接/断开等
         */
//        if (!NetWorkUtil.IsNetWorkEnable() && listener != null) {
//            doOnError(listener, new RequestError(CustomCode
//                    .NETWORK_NOT_CONNECTED, CommonUtils.getErrorMsgByCode
//                    (ERROR_CODE_PREFIX, String.valueOf(CustomCode
//                            .NETWORK_NOT_CONNECTED))));
//            return;
//        }

        isTaskFinish = false;
        isDnsNormal = false;

        // 任务
        mTask = new SimpleTask() {
            @Override
            public void doInBackground() {
                // DNS检查
                DnsXmlUtils.checkDnsFile();
                if (DnsXmlUtils.isDnsNormal()) {
                    isDnsNormal = true;
                    Logger.i("start execute login().");
                    mToken = TokenUtil.getToken("LogInfo");
                    Account.login(userName, password, mToken);
                    /*
                       等待任务完成
                       因为账号库中某些方法是异步的并且没有超时机制, 所以我们自己增加超时
                     */
                    waitTaskFinish();
                } else {
                    // 异常, 返回错误信息
                    isDnsNormal = false;
                }
            }

            @Override
            public void onFinish(boolean canceled) {
                if (!canceled) {
                    if (mTimeoutTask != null) {
                        mTimeoutTask.cancel();
                    }
                    mTimeoutTask = null;
                    if (isDnsNormal) {
                        if (mLoginResult == Account.BIZ_ACC_STATUS_OK) {
                            doOnSuccess(listener, "登录成功");
                            logAccount = userName;
                            // 检查Token值
                            if (TextUtils.isEmpty(mToken)) {
                                BackgroundHandler.execute(mUpdateTokenTask);
                            }
                        } else {
                            Logger.e("login error, please retry.");
                            doOnError(listener, new RequestError(mLoginResult,
                                    CommonUtils
                                            .getErrorMsgByCode
                                                    (ERROR_CODE_PREFIX, String
                                                            .valueOf(mLoginResult))));
                        }
                    } else {
                        Logger.e("dns file error, please retry.");
                        doOnError(listener, new RequestError(CustomCode
                                .DNS_ERROR, CommonUtils.getErrorMsgByCode
                                (ERROR_CODE_PREFIX, String.valueOf(CustomCode
                                        .DNS_ERROR))));
                    }
                }
            }

            @Override
            protected void onCancel() {
                // 执行超时被Canceled
                Logger.e("login execute timeout, canceled.");
                mTimeoutTask = null;
                doOnError(listener, new RequestError(CustomCode
                        .EXECUTE_TIMEOUT, CommonUtils.getErrorMsgByCode
                        (ERROR_CODE_PREFIX, String.valueOf(CustomCode
                                .EXECUTE_TIMEOUT))));
            }
        };

        // 计时任务(计算是否超时)
        mTimeoutTask = new SimpleTask() {
            @Override
            public void doInBackground() {
            }

            @Override
            public void onFinish(boolean canceled) {
                if (!canceled) {
                    mTask.cancel();
                    mTask = null;
                }
            }
        };
        SimpleTask.postDelay(mTimeoutTask, TIMEOUT);

        // 执行任务
        BackgroundHandler.execute(mTask);
    }

    /**
     * 注销
     */
    public void logout() {
        Logger.i("execute logout().");
        isLogin = false;
        logAccount = "";
        Account.logout();
    }

    /**
     * @param status 开关状态，0关闭，1开启
     * @return void.
     * @brief 更新推送开关.
     */
    public void pushSwitch(final int status,
                           final ResponseListener
                                   listener) {

        // 检查网络
//        if (!NetWorkUtil.IsNetWorkEnable()) {
//            doOnError(listener, new RequestError(CustomCode
//                    .NETWORK_NOT_CONNECTED, CommonUtils.getErrorMsgByCode
//                    (ERROR_CODE_PREFIX, String.valueOf(CustomCode
//                            .NETWORK_NOT_CONNECTED))));
//            return;
//        }

        isTaskFinish = false;

        // 任务
        mTask = new SimpleTask() {
            @Override
            public void doInBackground() {
                Logger.i("start execute pushswitch().");
                // 执行任务
                Account.pushswitch(status);
                /*
                   等待任务完成
                   因为账号库中某些方法是异步的并且没有超时机制, 所以我们自己增加超时
                 */
                waitTaskFinish();
            }

            @Override
            public void onFinish(boolean canceled) {
                if (!canceled) {
                    mTimeoutTask.cancel();
                    mTimeoutTask = null;
                    if (mPushResult == Account.BIZ_ACC_STATUS_OK) {
                        doOnSuccess(listener, "推送开关操作成功");
                    } else {
                        Logger.e("pushswitch error, please retry.");
                        doOnError(listener, new RequestError(mPushResult,
                                CommonUtils
                                        .getErrorMsgByCode
                                                (ERROR_CODE_PREFIX, String
                                                        .valueOf(mPushResult)
                                                )));
                    }
                }
            }

            @Override
            protected void onCancel() {
                // 执行超时被Canceled
                Logger.e("push switch execute timeout, canceled.");
                mTimeoutTask = null;
                doOnError(listener, new RequestError(CustomCode
                        .EXECUTE_TIMEOUT, CommonUtils.getErrorMsgByCode
                        (ERROR_CODE_PREFIX, String.valueOf(CustomCode
                                .EXECUTE_TIMEOUT))));
            }
        };

        // 计时任务(计算是否超时)
        mTimeoutTask = new SimpleTask() {
            @Override
            public void doInBackground() {
            }

            @Override
            public void onFinish(boolean canceled) {
                if (!canceled) {
                    mTask.cancel();
                    mTask = null;
                }
            }
        };
        SimpleTask.postDelay(mTimeoutTask, TIMEOUT);

        // 执行任务
        BackgroundHandler.execute(mTask);
    }

    // -------------------------------------------
    // # 账号服务对应的服务器回调↓
    // -------------------------------------------

    /**
     * @param type    事件类型.
     * @param payload 事件内容.
     * @return void.
     * @CalledByNative 账号库其它事件回调
     */
    public int OnBizAccEvent(int type, String payload) {

        return 0;
    }

    /**
     * @param version
     * @param status
     * @param session
     * @CalledByNative 登录回调<br/>
     * 注:这个方法在调用登录方法后会调用,另外与服务器连接断开,再连接成功后也会调用.
     */
    public int OnBizAccOnline(int version, int status, String session, String
            cloudseeSession) {
        Logger.i("// +++++++OnBizAccOnline↓+++++++++");
        isLogin = status == 0;
        Logger.i("登录结果:" + isLogin);
        Logger.i("OnBizAccOnline version:" + version + ", session:" +
                session + ", status:" + status);
        Logger.i("// +++++++++++++++++++++++++++++++");

        mLoginResult = status;
        mSessionID = session;
        mCloudSeeSession = cloudseeSession;
        notifyWaitThread();

//        if (isLogin) {
//            // 获取/更新用户信息
//            JVProfileEvent event = new
//                    JVProfileEvent(JVProfileEvent.EVENT_TAG_GET_USERINFO);
//            EventBus.getDefault().post(event);
//
//            // 设置注销标志为false
//            MySharedPreference.putBoolean(JVSharedPreferencesConsts
//                    .LOGOUT_TAG, false);
//        }

        return 0;
    }

    /**
     * @param type    推送类型.
     * @param payload 推送内容.
     * @return void.
     * @CalledByNative 推送回调.
     */
    public int OnBizAccPush(int type, String payload) {
        try {
            JSONObject jobj = new JSONObject(payload);
            if (jobj==null) {
                return 0;
            }
            MyLog.i("中维push："+jobj.toString());

           String aType  = jobj.has("atype")?jobj.getString("atype"):"";
            String id = jobj.has("dguid")?jobj.getString("dguid"):"";
            if (!aType.equals("")) {
                Looper.prepare();
                if ("15".equals(aType)) {
//                    String capturePath = LogcatHelper.getPATH_LOGCAT() + "/"+System.currentTimeMillis() + ".jpg";
//                    PlayUtil.capture(0, capturePath);
                    InformationBase base = new InformationBase();
//                    base.setmImage(capturePath);
                    base.setmUserId(EHomeApplication.getInstance().getmCurrentUser().getmId());
                    base.setmName("猫眼");
                    base.setmTitle("猫眼报警");
                    base.setmContent("");
                    base.setmType(0);
                    base.setImageType(1);
                    base.setmMessage_status(-1);
                    base.setmDoor_status(-1);
                    base.setmImage(jobj.has("apic")?jobj.getString("apic"):"");
                    base.setImageType(8);
                    base.setmZhongWeiId(id);
                    base.setmZhongWeiType("1");
                    base.setmTime(formatTimeInMillis(BaseUtils.getServerTime(EHomeApplication.getmContext())));
                    base.save();
                }
                else if ("14".equals(aType)) {
//                Looper.prepare();
//                    String capturePath = LogcatHelper.getPATH_LOGCAT() + "/"+System.currentTimeMillis() + ".jpg";
//                    PlayUtil.capture(0, capturePath);
                    String time = formatTimeInMillis(BaseUtils.getServerTime(EHomeApplication.getmContext()));
                    InformationBase base = new InformationBase();

//                    base.setmImage(capturePath);
                    base.setmUserId(EHomeApplication.getInstance().getmCurrentUser().getmId());
                    base.setmName("猫眼");
                    base.setmTitle("猫眼门铃提醒");
                    base.setmContent("");
                    base.setmType(0);
                    base.setImageType(1);
                    base.setmTime(time);
                    base.setmMessage_status(-1);
                    base.setmDoor_status(-1);
                    base.setmImage(jobj.has("apic")?jobj.getString("apic"):"");
                    base.setImageType(8);
                    base.setmZhongWeiId(id);
                    base.setmZhongWeiType("1");
                    base.save();
//                String id = jobj.has("dguid")?jobj.getString("dguid"):"";
                if ("".equals(id)) {
                    return 0;
                }
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                bundle.putString("time",time);
                BaseUtils.startActivities(EHomeApplication.getmContext(), MaoyanGuestActivity.class,bundle);
                }
                else if ("7".equals(aType)){
//                else{
                    String time = formatTimeInMillis(BaseUtils.getServerTime(EHomeApplication.getmContext()));
                    InformationBase base = new InformationBase();
                    base.setmUserId(EHomeApplication.getInstance().getmCurrentUser().getmId());
                    base.setmName("摄像头");
                    base.setmTitle("摄像头报警提醒");
                    base.setmContent("");
                    base.setmType(0);
                    base.setImageType(1);
                    base.setmTime(time);
                    base.setmMessage_status(-1);
                    base.setmDoor_status(-1);
                    base.setmImage(jobj.has("apic")?jobj.getString("apic"):"");
                    base.setImageType(8);
                    base.setmZhongWeiId(id);
                    base.setmZhongWeiType("2");
                    base.save();
                }
//                Looper.loop();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param status 服务器错误码.
     * @param sw     开关状态(这个不管用, 一直返回0)
     * @return void.
     * @CalledByNative 推送开关设置回调.
     */
    public int OnBizAccPushSwitch(int status, int sw) {
        Logger.i("OnBizAccPushSwitch status:" + status + ", sw:" + sw);
        mPushResult = status;
        notifyWaitThread();
        return 0;
    }

    /**
     * @param session 服务器返回的session.
     * @return void.
     * @CalledByNative SESSION更新.
     */
    public int OnBizAccSession(String session) {
        Logger.i("OnBizAccSession session:" + session);
        mSessionID = session;
        return 0;
    }

    /**
     * @param status 服务器错误码.
     * @return 0 ok -1 failed.
     * @CalledByNative token设置回调.(更新token以后)
     */
    public int OnBizAccUpdateToken(int status) {
        Logger.i("OnBizAccUpdateToken status:" + status);
        return 0;
    }

    /**
     * @param platform   异地登录平台.
     * @param remoteaddr 异地登录地址.
     * @param remoteport 异地登录端口.
     * @return void.
     * @CalledByNative 异地登录回调.
     */
    public int OnBizAccRemoteLogin(int platform, String remoteaddr,
                                   int remoteport) {
        return 0;
    }

    // -------------------------------------------
    // # 扩展方法↓
    // -------------------------------------------

    /**
     * 获取Session ID
     */
    public String getSession() {
        return mSessionID;
    }

    /**
     * 获取cloudsee的session
     *
     * @return
     */
    public String getCloudSeeSession() {
        return mCloudSeeSession;
    }

    public int getLoginResult() {
        return mLoginResult;
    }

    /**
     * 唤醒等待线程
     */
    private void notifyWaitThread() {
        // 唤醒等待DNS下载解析完成的线程
        synchronized (mTaskLock) {
            isTaskFinish = true;
            mTaskLock.notifyAll();
            Logger.i("notify wait thread.");
        }
    }


    /**
     * 等待操作完成
     */
    private void waitTaskFinish() {
        synchronized (mTaskLock) {
            while (!isTaskFinish) {
                try {
                    mTaskLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // -------------------------------------------------------
    // ## 处理监听回调
    // -------------------------------------------------------
    private void doOnSuccess(ResponseListener listener, String msg) {
        if (listener != null) {
            listener.onSuccess(msg);
        }
    }

    private void doOnError(ResponseListener listener, RequestError error) {
        if (listener != null) {
            listener.onError(error);
        }
    }

    // -------------------------------------------------------
    // ## Token 设置/更新操作
    // -------------------------------------------------------
    private String mToken;
    // 更新Token任务
    private SimpleTask mUpdateTokenTask = new SimpleTask() {
        @Override
        public void doInBackground() {
            Logger.i("wait read token.");
            mToken = TokenUtil.waitReadToken();
        }

        @Override
        public void onFinish(boolean canceled) {
            if (!canceled) {
                if (!TextUtils.isEmpty(mToken)) {
                    Logger.i("read token success, try update.");
                    Account.updatetoken(mToken);
                } else {
                    Logger.e("wait read token, result:failed");
                }
            } else {
                Logger.e("wait read token canceled.");
            }
        }
    };
}
