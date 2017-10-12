package com.xingyeda.ehome.Service;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.xingyeda.ehome.AcivityRegister;
import com.xingyeda.ehome.util.AESUtils;
import com.xingyeda.ehome.util.SharedPreUtil;
import com.xingyeda.ehome.zhibo.ActivitySharePlay;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SharePasswordService extends Service {

    private String id;
    private Context mContext = this;
    private ActivityManager am;
    private ClipboardManager cm;

    public SharePasswordService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        timer.schedule(task, 0, 5000);//5秒循环一次
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id = SharedPreUtil.getString(mContext, "userId", "");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler handlerListen = new Handler() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                if (!tasks.isEmpty()) {
                    ComponentName cn = tasks.get(0).topActivity;
                    if (cn.getPackageName().equals("com.xingyeda.ehome")) {
                        String sharePassword = "";
                        String deSharePassword = "";//解密后的设备号,用户ID,房间号字符串
                        if (cm.hasPrimaryClip()) {
                            if (cm.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                                ClipData.Item item = null;
                                item = cm.getPrimaryClip().getItemAt(0);
                                sharePassword = item.getText().toString();
                                if (sharePassword.contains("创享E家")) {
                                    String password = sharePassword.substring(sharePassword.indexOf("￥"), sharePassword.lastIndexOf("￥")).toString();
                                    if (password != null) {
                                        String password_result = password.substring(1).trim().toString();
                                        try {
                                            deSharePassword = AESUtils.Decrypt(password_result, "1234567890123456");
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        String[] info = deSharePassword.split("\\|");

                                        String mEquipmentId = info[0];
                                        String mId = info[1];
                                        String mHouseNumberId = info[2];

                                        if (!mId.equals(id)) {
                                            Intent intent = new Intent();
                                            intent.putExtra("equipmentId", mEquipmentId);
                                            intent.putExtra("uid", id);
                                            intent.putExtra("roomId", mHouseNumberId);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.setClass(SharePasswordService.this, ActivitySharePlay.class);
                                            startActivity(intent);
                                        }
                                    }
                                    Uri uri = Uri.parse("http://www.xyd999.com/");
                                    ClipData cd = ClipData.newUri(getContentResolver(), "株洲市兴业达科技有限公司", uri);
                                    cm.setPrimaryClip(cd);
                                }
                            }
                        }
                    }
                }
            }
            super.handleMessage(msg);
        }
    };

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            handlerListen.sendMessage(message);
        }
    };
}
