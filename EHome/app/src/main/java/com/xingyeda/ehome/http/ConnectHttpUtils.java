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
import com.xingyeda.ehome.HomepageHttp;
import com.xingyeda.ehome.base.ConnectPath;
import com.xingyeda.ehome.base.EHomeApplication;
import com.xingyeda.ehome.bean.HomeBean;
import com.xingyeda.ehome.bean.UserInfo;
import com.xingyeda.ehome.dialog.DialogShow;
import com.xingyeda.ehome.door.DoorFragment;
import com.xingyeda.ehome.http.okhttp.BaseStringCallback;
import com.xingyeda.ehome.http.okhttp.CallbackHandler;
import com.xingyeda.ehome.http.okhttp.OkHttp;
import com.xingyeda.ehome.util.AppUtils;
import com.xingyeda.ehome.util.BaseUtils;
import com.xingyeda.ehome.util.LogUtils;
import com.xingyeda.ehome.util.LogcatHelper;
import com.xingyeda.ehome.util.SharedPreUtil;

import static android.R.string.ok;

public class ConnectHttpUtils
{
    public static void loginUtils(JSONObject response, Context context,
            String name, String pwd,Class<?> cls)
    {
        JVBase.detectionJVId(context,name);
        EHomeApplication mApplication = (EHomeApplication) ((Activity) context)
                .getApplication();
        try
        {

            if (response.getString("status").equals("200"))
            {
                JSONObject userInfo = response.getJSONObject("obj");
                UserInfo info = new UserInfo();
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
                if (!SharedPreUtil.getBoolean(context, "isFirstExplain")) {
                    BaseUtils.startActivity(context, cls);
		}else {
			Bundle bundle = new Bundle();
			bundle.putString("type", "login");
		    BaseUtils.startActivities(context, ActivityExplain.class,bundle);
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
    	OkHttp.get(context,ConnectPath.VERSIONS_PATH, new BaseStringCallback(context, new CallbackHandler<String>() {
			
			@Override
			public void parameterError(JSONObject response) {
			}
			
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
//                 	DoorFragment.isFlHint = !(Float.valueOf(AppUtils.getVersionName(context)) < Float.valueOf(version.getString("versionNum")));
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
                                downloadUpdate(ConnectPath.IP+path,context);
                                dialog.dismiss();
							}
						});
//                         DialogUtils.getDialog(context, R.drawable.ic_launcher,
//                                         "检测到新版本", version.has("note")?version.getString("note"):"")
//                                 .setPositiveButton(
//                                         "马上更新",
//                                         new DialogInterface.OnClickListener()
//                                         {
//
//                                             @Override
//                                             public void onClick(
//                                                     DialogInterface dialog,
//                                                     int which)
//                                             {
//                                                 mProgressDialog = new ProgressDialog(
//                                                         context);
//                                                 mProgressDialog
//                                                         .setCancelable(false);
//                                                 mProgressDialog
//                                                         .setTitle("正在下载");
//                                                 mProgressDialog
//                                                         .setMessage("请稍候...");
//                                                 mProgressDialog
//                                                         .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条对话框//样式（水平，旋转）
//                                                 mProgressDialog
//                                                         .setMax((int) length);// 设置进度条的最大值
//                                                 progress = (progress > 0) ? progress
//                                                         : 0;
//                                                 mProgressDialog
//                                                         .setProgress(progress);
//                                                 downloadUpdate(ConnectPath.UPDATE_PATH+path,
//                                                         context);
//                                                 dialog.dismiss();
//
//                                             }
//                                         }).show();
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
											downloadUpdate(ConnectPath.IP+ path, context);
											dialog.dismiss();

							}
						});
//                 		final NormalDialog dialog = DialogShow.showSelectDialog(context, "检测到新版本",version.has("note")?version.getString("note"):"", 2, new String[]{"马上更新","以后再说"});
//                 		dialog.setOnBtnClickL(new OnBtnClickL() {
//							
//							@Override
//							public void onBtnClick() {
////								mProgressDialog = new ProgressDialog(context);
////                                mProgressDialog.setCancelable(false);
////                                mProgressDialog.setTitle("正在下载");
////                                mProgressDialog.setMessage("请稍候...");
////                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条对话框//样式（水平，旋转）
////                                mProgressDialog.setMax((int) length);// 设置进度条的最大值
////                                progress = (progress > 0) ? progress : 0;
////                                mProgressDialog.setProgress(progress);
////                                downloadUpdate(ConnectPath.UPDATE_PATH+path,context);
//                                dialog.dismiss();
//							}
//						},new OnBtnClickL() {
//							
//							@Override
//							public void onBtnClick() {
//								dialog.dismiss();
//							}
//						});
                 		
                 		
//                 	    DialogUtils.getDialog(context, R.drawable.ic_launcher,
//                                     "检测到新版本", version.has("note")?version.getString("note"):"")
//                             .setPositiveButton(
//                                     "马上更新",
//                                     new DialogInterface.OnClickListener()
//                                     {
//
//                                         @Override
//                                         public void onClick(
//                                                 DialogInterface dialog,
//                                                 int which)
//                                         {
//                                             mProgressDialog = new ProgressDialog(
//                                                     context);
//                                             mProgressDialog
//                                                     .setCancelable(false);
//                                             mProgressDialog
//                                                     .setTitle("正在下载");
//                                             mProgressDialog
//                                                     .setMessage("请稍候...");
//                                             mProgressDialog
//                                                     .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条对话框//样式（水平，旋转）
//                                             mProgressDialog
//                                                     .setMax((int) length);// 设置进度条的最大值
//                                             progress = (progress > 0) ? progress
//                                                     : 0;
//                                             mProgressDialog
//                                                     .setProgress(progress);
//                                             downloadUpdate(ConnectPath.UPDATE_PATH+path,
//                                                     context);
//                                             dialog.dismiss();
//
//                                         }
//                                     })
//                                 .setNegativeButton(
//                                         "以后再说",
//                                         new DialogInterface.OnClickListener()
//                                         {
//
//                                             @Override
//                                             public void onClick(
//                                                     DialogInterface dialog,
//                                                     int which)
//                                             {
//                                                 dialog.dismiss();
//                                             }
//                                         }).show();
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
			
			@Override
			public void onFailure() {
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
//	File dir = context.getDir("EHome", Context.MODE_PRIVATE
//                | Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
        final String apkPath = LogcatHelper.getPATH_LOGCAT() + "/EHome.apk";
//        final String apkPath = dir + "/EHome.apk";

//        String s ="http://xyd-img.bj.bcebos.com/apks/EHome.apk?authorization=bce-auth-v1%2F5545ade9ae624e799ff37fa4d785b0e0%2F2017-04-01T06%3A54%3A18Z%2F-1%2Fhost%2F13b40654e7d5e7c7a56adc7590f1611020e0c7a1b3f9edcc976ebaaa62e44e66";
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
//                DialogUtils
//                        .getDialog(context, R.drawable.ic_launcher, "新版本已下载完",
//                                "是否安装")
//                        .setPositiveButton("安装",
//                                new DialogInterface.OnClickListener()
//                                {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                            int which)
//                                    {
//                                        Intent intent = new Intent();
//                                        intent.setAction(Intent.ACTION_VIEW);
//                                        intent.setDataAndType(
//                                                Uri.fromFile(file_apk),
//                                                "application/vnd.android.package-archive");
//                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        context.startActivity(intent);
//                                    }
//                                })
//                        .setNegativeButton("取消",
//                                new DialogInterface.OnClickListener()
//                                {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                            int which)
//                                    {
//
//                                    }
//                                }).show();

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
//                DialogUtils.getHintDialog(mContext, "下载连接超时");
            }
        }

        @Override
        public void onTick(long millisUntilFinished)
        {// 计时过程
        }
    }

    
//    public static Intent getShortcutToDesktopIntent(Context context,Class<?> cls) {  
//        Intent intent = new Intent();   
//        intent.setClass(context, cls);    
//       /*以下两句是为了在卸载应用的时候同时删除桌面快捷方式*/  
//        intent.setAction("android.intent.action.MAIN");    
//        intent.addCategory("android.intent.category.LAUNCHER");    
//         
//        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");  
//        // 不允许重建  
//        shortcut.putExtra("duplicate", false);  
//        // 设置名字  
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,"一键开门");  
//        // 设置图标  
//        Parcelable icon = Intent.ShortcutIconResource.fromContext(context,
//                R.drawable.open_the_door); // 获取快捷键的图标
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,icon);  
//        // 设置意图和快捷方式关联程序  
//        intent.putExtra("type", "openDoor");
//        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);
//        SharedPreUtil.put(context, "oneKeyOpenDoor", true);
// 
//        return shortcut;  
// 
//    } 
//    public static void deleteShortCut(Context context,Class<?> cls)  
//    {  
//       Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");    
//       //快捷方式的名称    
//       shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,"一键开门");    
//       /**删除和创建需要对应才能找到快捷方式并成功删除**/  
//       Intent intent = new Intent();   
//       intent.setClass(context, cls);    
//       intent.setAction("android.intent.action.MAIN");    
//       intent.addCategory("android.intent.category.LAUNCHER");    
//         
//       shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent); 
//       SharedPreUtil.put(context, "oneKeyOpenDoor", false);
//       context.sendBroadcast(shortcut);            
//    }  
}
