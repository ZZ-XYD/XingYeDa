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
import android.util.Log;
import android.widget.Toast;

import com.xingyeda.ehome.AcivityRegister;
import com.xingyeda.ehome.util.AESUtils;
import com.xingyeda.ehome.zhibo.ActivitySharePlay;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SharePasswordService extends Service {

    String id;

    public SharePasswordService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        timer.schedule(task, 0, 3000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");
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
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
                if (!tasks.isEmpty()) {
                    ComponentName cn = tasks.get(0).topActivity;
                    Log.v("SharePasswordService", cn.getPackageName());
                    if (cn.getPackageName().equals("com.xingyeda.ehome")) {
                        String sharePassword = "";
                        String deSharePassword = "";//解密后的设备号,用户ID,房间号字符串
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cm.hasPrimaryClip()) {
                            if (cm.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                                ClipData.Item item = null;
                                item = cm.getPrimaryClip().getItemAt(0);
                                sharePassword = item.getText().toString();
                                Log.v("SharePassword", sharePassword);

                                String result = sharePassword.substring(sharePassword.indexOf("￥"), sharePassword.lastIndexOf("￥")).toString();
                                Log.v("Result", result);
                                String result1 = result.substring(1).trim().toString();
                                Log.v("Result1", result1);
                                try {
                                    deSharePassword = AESUtils.Decrypt(result1, "1234567890123456");
                                    Log.v("DeSharePassword", deSharePassword);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                String[] info = deSharePassword.split("\\|");

                                String mEquipmentId = info[0];
                                String mId = info[1];
                                String mHouseNumberId = info[2];

                                Log.v("Info", mEquipmentId + " " + mId + " " + mHouseNumberId);

                                if (!mId.equals(id)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("equipmentId", mEquipmentId);
                                    intent.putExtra("uid", mId);
                                    intent.putExtra("roomId", mHouseNumberId);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setClass(SharePasswordService.this, ActivitySharePlay.class);
                                    startActivity(intent);
                                }


                                Uri uri = Uri.parse("http://www.xyd999.com/");
                                ClipData cd = ClipData.newUri(getContentResolver(), "株洲市兴业达科技有限公司", uri);
                                cm.setPrimaryClip(cd);

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
