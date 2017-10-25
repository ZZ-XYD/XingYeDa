package com.xingyeda.ehome.http;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.jovision.JVBase;
import com.ldl.dialogshow.dialog.listener.OnBtnClickL;
import com.ldl.dialogshow.dialog.widget.NormalDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xingyeda.ehome.ActivityExplain;
import com.xingyeda.ehome.ActivityHomepage;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.bean.UserInfo;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.http.okhttp.ConciseCallbackHandler;
import com.xingyeda.ehome.http.okhttp.ConciseStringCallback;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.menu.ActivityChangeInfo;
import com.xingyeda.ehome.util.AppUtils;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.LogcatHelper;
import com.xingyeda.ehome.util.SharedPreUtil;


public class ConnectHttpUtils
{
    public static void loginUtils(JSONObject response, Context context,
            String name, String pwd,Class<?> cls)
    {
//        JVBase.detectionJVId(context,name);
        EHomeApplication mApplication = (EHomeApplication) ((Activity) context)
                .getApplication();
        try
        {

            if (response.getString("status").equals("200"))
            {
                JSONObject userInfo = response.getJSONObject("obj");
                UserInfo info = new UserInfo();
                if (pwd.equals("")) {
                    JVBase.detectionJVId(context, userInfo.getString("id"));
                }else{
                    JVBase.detectionJVId(context,name);
                }
//                JVBase.detectionJVId(context, userInfo.getString("id"));
                info.setmId(userInfo.getString("id"));// 账号id
                info.setmUsername(userInfo.has("username")?userInfo.getString("username"):"");// 用户名
                info.setmPhone(userInfo.has("mobilephone")?userInfo.getString("mobilephone"):"");// 电话号码
                info.setmSex(userInfo.has("sex")?userInfo.getString("sex"):"");// 性别
                info.setmRemarksPhone(userInfo.has("beiyongphone")?userInfo.getString("beiyongphone"):"");// 备用号码
                info.setmEmail(userInfo.has("email")?userInfo.getString("email"):"");// 邮箱
                info.setmName(userInfo.has("name")?userInfo.getString("name"):"");// 呢称
                info.setmHeadPhotoUrl(userInfo.has("img")?userInfo.getString("img"):"");// 头像
                info.setmSNCode(userInfo.has("snCode")?userInfo.getString("snCode"):"");//sncode
                if (userInfo.has("isChecked")?userInfo.getInt("isChecked") == 1:false)
                {
                	SharedPreUtil.put(context, "isChecked", true);
                    HomeBean bean = new HomeBean();
                    bean.setmCommunity(userInfo.has("xiaoquname")?userInfo.getString("xiaoquname"):"");
                    bean.setmCommunityId(userInfo.has("xiaoqu")?userInfo.getString("xiaoqu"):"");
                    bean.setmPeriods(userInfo.has("qishuname")?userInfo.getString("qishuname"):"");
                    bean.setmPeriodsId(userInfo.has("qishu")?userInfo.getString("qishu"):"");
                    bean.setmUnit(userInfo.has("dongshuname")?userInfo.getString("dongshuname"):"");
                    bean.setmUnitId(userInfo.has("dongshu")?userInfo.getString("dongshu"):"");
                    bean.setmHouseNumber(userInfo.has("hname")?userInfo.getString("hname"):"");
                    bean.setmHouseNumberId(userInfo.has("hid")?userInfo.getString("hid"):"");
                    bean.setmIdentityType(userInfo.has("type")?userInfo.getString("type"):"");
                    info.setmXiaoqu(bean);

                    LogUtils.i("默认小区 : "+bean);
                }
                else {
                	SharedPreUtil.put(context, "isChecked", false);
				}

                mApplication.setmCurrentUser(info);
                
                LogUtils.i("userinfo : "+info);

                SharedPreUtil.put(context, "isLogin", false);
                SharedPreUtil.put(context, "userId", userInfo.getString("id"));
                Bundle bundle = new Bundle();
                if (info.getmPhone().equals("")) {
                    bundle.putString("type", "login");
                    bundle.putString("id", info.getmId());
                    BaseUtils.startActivities(context, ActivityChangeInfo.class,bundle);
                }else{
                    if (!SharedPreUtil.getBoolean(context, "isFirstExplain")) {
                        BaseUtils.startActivity(context, cls);
                    }else {
                        bundle.putString("type", "login");
                        BaseUtils.startActivities(context, ActivityExplain.class,bundle);
                    }
                }
                ((Activity)context).finish();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void inspectUpdate(final Context context,final int type)
    {
    	OkHttp.get(context,ConnectPath.VERSIONS_PATH, new ConciseStringCallback(context, new ConciseCallbackHandler<String>() {
			@Override
			public void onResponse(JSONObject response) {
				 try
                 {
                     JSONObject version = response.getJSONObject("obj");
                     final String path = version.getString("filePath");
                     if (Float.valueOf(AppUtils.getVersionCode(context)) < Float
                             .valueOf(version.getString("versionNum")))
                     {
                         ActivityHomepage.isFlHint = false;
                 	boolean isImposd =Boolean.valueOf(version.has("imposed")?version.getString("imposed"):"false");
                 	final NormalDialog dialog ;
                 	if (isImposd) {
                 		
                 		dialog = DialogShow.showSelectDialog(context, "检测到新版本",version.has("note")?version.getString("note"):"", 1, new String[]{"马上更新"});
                 		dialog.setOnBtnClickL(new OnBtnClickL() {

							@Override
							public void onBtnClick() {

                                mProgressDialog = new ProgressDialog(context);
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.setTitle("正在下载");
                                mProgressDialog.setMessage("请稍候...");
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条对话框//样式（水平，旋转）
                                mProgressDialog.setMax((int) length);// 设置进度条的最大值
                                progress = (progress > 0) ? progress : 0;
                                mProgressDialog.setProgress(progress);
                                if (path.contains("xydServer")) {
                                    downloadUpdate(ConnectPath.IP+path,context);
                                }else{
                                    downloadUpdate(path,context);
                                }
                                dialog.dismiss();
							}
						});
                 	}
                 	else {
                 		String note = version.has("note")?version.getString("note"):"";
                 		dialog = DialogShow.showSelectDialog(context, "检测到新版本", note, 2, new String[]{"以后再说","马上更新"});
                 		dialog.setOnBtnClickL(new OnBtnClickL() {
							
							@Override
							public void onBtnClick() {
								dialog.dismiss();
							}
						},new OnBtnClickL() {
							
							@Override
										public void onBtnClick() {
											mProgressDialog = new ProgressDialog(context);
											mProgressDialog.setCancelable(false);
											mProgressDialog.setTitle("正在下载");
											mProgressDialog.setMessage("请稍候...");
											mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条对话框//样式（水平，旋转）
											mProgressDialog.setMax((int) length);// 设置进度条的最大值
											progress = (progress > 0) ? progress : 0;
											mProgressDialog.setProgress(progress);
                                            if (path.contains("xydServer")) {
                                                downloadUpdate(ConnectPath.IP+path,context);
                                            }else{
                                                downloadUpdate(path,context);
                                            }
											dialog.dismiss();

							}
						});
                 	}
                     }
                     else {
                         ActivityHomepage.isFlHint = true;
                         if (type == 1)
                         {
                             BaseUtils.showShortToast(context, "您当前版本已经是最新。");
                         }
                     }
                 }
                 catch (Exception e)
                 {
                     e.printStackTrace();
                 }
			}
		}));
    }

    private static TimeCount mCount;
    private static boolean mIsTimeout = false;
    private static HttpUtils mHttpUtils;
    private static ProgressDialog mProgressDialog;
    private static int progress = 0;
    private static long length;

    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    private static void downloadUpdate(String path, final Context context)
    {
        mCount = new TimeCount(5000, 1000, context);
        mProgressDialog.show();
        mCount.start();
        final String apkPath = LogcatHelper.getPATH_LOGCAT() + "/EHome.apk";
        mHttpUtils = new HttpUtils();
        mHttpUtils.download(path, apkPath, true, true, new RequestCallBack()
        {
            @Override
            public void onFailure(HttpException arg0, String arg1)
            {
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading)
            {
                mIsTimeout = true;
                progress = (int) (((float) current / total) * 100);
                progress = (progress > 0) ? progress : 0;
                mProgressDialog.setProgress(progress);
            }

            @Override
            public void onSuccess(ResponseInfo response)
            {
                mProgressDialog.dismiss();
                String[] command =
                { "chmod", "777", apkPath };
                ProcessBuilder builder = new ProcessBuilder(command);
                try
                {
                    builder.start();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                final File file_apk = (File) response.result;
                
                final NormalDialog dialog = DialogShow.showSelectDialog(context, "是否安装", "新版本已下载完", 2, new String[]{"取消","安装"});
                dialog.setOnBtnClickL(new OnBtnClickL() {
					
					@Override
					public void onBtnClick() {
						dialog.dismiss();
					}
				},new OnBtnClickL() {
					
					@Override
					public void onBtnClick() {
						Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(
                                Uri.fromFile(file_apk),
                                "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        dialog.dismiss();
					}
				});
            }
        });
    }


    public static class TimeCount extends CountDownTimer
    {
        private Context mContext;

        public TimeCount(long millisInFuture, long countDownInterval,
                Context context)
        {
            super(millisInFuture, countDownInterval);
            this.mContext = context;
        }

        @Override
        public void onFinish()
        {// 计时完毕
            if (!mIsTimeout)
            {
            	DialogShow.showHintDialog(mContext, "下载连接超时");
            }
        }

        @Override
        public void onTick(long millisUntilFinished)
        {// 计时过程
        }
    }
}
