package com.xingyeda.ehome.wifiOnOff;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aochn.cat110appsdk.Cat110SDKActivity;
import com.xingyeda.ehome.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Cat110SDKActivity {

    private static final String TAG = "cat110sdkexample";

    private boolean mIsLogined = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((EditText)findViewById(R.id.editOemId)).setText("");
        ((EditText)findViewById(R.id.editOemId)).setMaxHeight(40);
        ((EditText)findViewById(R.id.editUsername)).setText("");
        ((EditText)findViewById(R.id.editPassword)).setText("");

        ((EditText)findViewById(R.id.editWifiPassword)).setText("");

        ((Button)findViewById(R.id.btnQuit)).setVisibility(View.GONE);

        setEventListener(new OnEventListener() {
            public void onConnectionStatusChanged(int status,
                                                  String reason,
                                                  int refuseReason,
                                                  long flag) {
                if (CONNECTION_STATUS_LOGIN_SUCCESS == status) {
                    mIsLogined = true;
                    //((Button)findViewById(R.id.btnLogin)).setText("Logout");
                    Log.i(TAG, "登陆成功");
                } else {
                    mIsLogined = false;
                    Log.i(TAG, "登陆失败");
                    //((Button)findViewById(R.id.btnLogin)).setText("Login");
                }
                Log.i(TAG, "连接状态在改变 status:" + status + " reason:" + reason + " refuseReason:" + refuseReason + " flag:" + flag);
            }
            public void onRefreshLoginName(String loginName) {
                Log.i(TAG, "刷新登录名 loginName:" + loginName + "");
            }
            public void onRefreshDeviceList() {
                Log.i(TAG, "刷新设备列表 deviceCount:" + getDeviceCount());
                int i;
                for (i = 0; i < getDeviceCount(); ++i) {
                    Device item = getDeviceItem(i);
                    Log.i(TAG, "刷新设备列表 device[" + i + "]:" + item.pid);
                }
            }
            //1147209389
            public void onRefreshMsgList() {
                Log.i(TAG, "刷新信息列表");
            }
            public void onRefreshConfigList() {
                Log.i(TAG, "刷新信息列表");
//                ((EditText)findViewById(R.id.editPid)).setText("" + findFirstOnlinePid());
            }
            public void onRefreshMsgCount() {
                Log.i(TAG, "刷新信息计数");
            }
            public void onRefreshConfigItem(long pid, String configName) {
                Log.i(TAG, "更新配置项 pid:" + pid + " configName:" + configName);
                if (configName.equals("_online")) {
//                    ((EditText)findViewById(R.id.editPid)).setText("" + findFirstOnlinePid());
                }
            }
            public void onInitComplete() {
                Log.i(TAG, "初始化完成");
            }
            public void onLocalDataPrepared() {
                Log.i(TAG, "本地数据准备");
            }
            public void onQuit() {
                Log.i(TAG, "onQuit");
                ((Button)findViewById(R.id.btnQuit)).setVisibility(View.GONE);
            }
        });

        tryLastLogin(new OnTryLastLoginResultListener() {
            public void onResult(int result, boolean hasTriedLastLogin) {
                Log.i(TAG, "onResult result:" + result + " 已经最后一次登录:" + hasTriedLastLogin);
                if (0 == result && hasTriedLastLogin) {
                    ((Button)findViewById(R.id.btnQuit)).setVisibility(View.VISIBLE);
                }
            }
        });

        ((Button)findViewById(R.id.btnLogin)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((Button)findViewById(R.id.btnQuit)).setVisibility(View.VISIBLE);
//            	String oemCert = ((EditText)findViewById(R.id.editOemId)).getText().toString();
//            	String username = ((EditText)findViewById(R.id.editUsername)).getText().toString();
//            	String password = ((EditText)findViewById(R.id.editPassword)).getText().toString();
                String oemCert = "1684ea8f2b44aa8e233b019f3e7e190056402e77d22a420fc1111d1395b79820d3412fff71bd4981e3265d758b94bd42cdee8d9c141ffce2167a0cae7897ab59eb7606442e20d180b00e13b43305e7a815a36f9e3cfc02018e4f4000a6b7876d7b24fabf9e796ca8b70473d71f7dd6380d11fdbe08c4f500fbf7425af47ce0c2";
                String username = "15116039587";
                String password = "Ab123456";
                String captcha = "";
                Log.i(TAG, "login username:" + username + "");
                login(oemCert, username, password, captcha);
//            	loginAsGuest(oemCert);//1147209389
            }
        });
        ((Button)findViewById(R.id.register)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendAccountRegister("18711018824", "", new Cat110SDKActivity.OnSendAccountRegisterResultListener() {
                    @Override
                    public void onResult(int result, long queryId , int durationSecs, int captchaPhase) {
                        Log.d("test", result+"   "+queryId +"   "+durationSecs+"   "+captchaPhase+"");
                        submitAccountRegister("18711018824", "Ab123456", queryId, "123456", new Cat110SDKActivity.OnSubmitAccountRegisterResultListener() {
                            @Override
                            public void onResult(int result, long newUid) {
                                Log.i(TAG, "register :" + result + "    "+ newUid);

                            }
                        });
                    }

                });
            }
        });

        ((Button)findViewById(R.id.btnQueryMsg)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Log.i(TAG, "queryMsgList..");
            	queryMsgList(0, 100, new OnQueryMsgListResultListener() {
            		public void onResult(int msgCount, MsgItem msgList[]) {
            			if (-1 == msgCount) {
            				Log.i(TAG, "查询信息列表失败");
            			} else {
            				Log.i(TAG, "查询信息列表成功, 信息数量:" + msgCount);
            				int i;
            				for (i = 0; i < msgCount; ++i) {
            					Log.i(TAG, "信息列表[" + i + "]: content:" + msgList[i].content + " msgId:" + msgList[i].msgId + " pid:" + msgList[i].pid + " text:" + msgList[i].content + " postTime:" + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(msgList[i].postTime * 1000L)));
            				}
            			}
            		}
            	});
            }
        });

        ((Button)findViewById(R.id.btnQuit)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                quit();
            }
        });

        ((Button)findViewById(R.id.btnOn)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pid = Integer.parseInt(((EditText)findViewById(R.id.editPid)).getText().toString());
                if (pid > 0) {
                    Log.i(TAG, "Turn on pid(" + pid + ") online:" + findConfigValue(pid, "_online") + "..");
                    Device destDevice = findDeviceByPid(pid);
                    sendCommandToDevice(destDevice.pid, destDevice.nativeIndex,
                            "wifi_pluge_control",
                            "\"1\"",
                            new OnSendCommandToDeviceResultListener() {
                                public void onResult(int result) {
                                    Log.i(TAG, "设备发送命令的结果:" + result);
                                }
                            });
                }
            }
        });

        ((Button)findViewById(R.id.btnOff)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                long pid =  Long.parseLong(((EditText)findViewById(R.id.editPid)).getText().toString());
                if (pid > 0) {
                    Log.i(TAG, "Turn off pid(" + pid + ") online:" + findConfigValue(pid, "_online") + "..");
                    Device destDevice = findDeviceByPid(pid);
                    sendCommandToDevice(destDevice.pid, destDevice.nativeIndex,
                            "wifi_pluge_control",
                            "\"0\"",
                            new OnSendCommandToDeviceResultListener() {
                                public void onResult(int result) {
                                    Log.i(TAG, "设备发送命令的结果:" + result);
                                }
                            });
                }
            }
        });

        ((Button)findViewById(R.id.btnBindDevice)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//            	String wifiPassword = ((EditText)findViewById(R.id.editWifiPassword)).getText().toString();
//        		Log.i(TAG, "Bind device wifi password:" + wifiPassword);
//                String wifiPassword = "22393818";
                Log.i(TAG, "绑定设备的无线网络密码:");
//                Log.i(TAG, "绑定设备的无线网络密码:" + wifiPassword);
//                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    startBind("xyd", "22393818",
                            new OnBindStatusListener() {
                                public void onCountdown(int remainSeconds) {
                                    ((TextView)findViewById(R.id.txtBindStatus)).setText("onCountdown remainSeconds:" + remainSeconds);
                                    Log.i(TAG, "倒计时读秒:" + remainSeconds);
                                }
                                public void onDeviceConnectedWithRouter() {
                                    ((TextView)findViewById(R.id.txtBindStatus)).setText("设备路由器");
                                    Log.i(TAG, "设备路由器");
                                }
                                public void onBindResult(int result) {
                                    ((TextView)findViewById(R.id.txtBindStatus)).setText("绑定结果:" + result);
                                    Log.i(TAG, "绑定结果:" + result);
                                }
                            });
            }
        });

        ((Button)findViewById(R.id.btnDeleteBind)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int pid = Integer.parseInt(((EditText)findViewById(R.id.editPid)).getText().toString());
                if (pid > 0) {
                    Device item = findDeviceByPid(pid);
                    Log.i(TAG, "删除绑定pid:" + pid + " nativeIndex:" + item.nativeIndex);
                    deleteBind(pid, item.nativeIndex,
                            new OnSimpleResultListener() {
                                public void onResult(int result) {
                                    Log.i(TAG, "结果:" + result);
                                }
                            });
                }
            }
        });

        ((Button)findViewById(R.id.btnToggleDisableOfflineNotifySetting)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String oldValue = findConfigValue(0, "disable_offline_notify");//禁用离线通知
                Log.i(TAG, "禁用离线通知oldvalue切换:" + oldValue);
                updateConfig(0, "disable_offline_notify", oldValue.equals("1") ? "0" : "1",
                        new OnSimpleResultListener() {
                            public void onResult(int result) {
                                Log.i(TAG, "结果:" + result);
                            }
                        });
            }
        });
    }

    long findFirstOnlinePid() {
        int i;
        for (i = 0; i < getConfigCount(); ++i) {
            Config item = getConfigItem(i);
            if (item.configName.equals("_online") &&
                    item.configValue.equals("1") &&
                    null != findDeviceByPid(item.pid)) {
                return item.pid;
            }
        }
        return 0;
    }

    // TODO: Use hashmap, don't use this O(N) function to search device.
    //使用hashmap,不要使用这个O(N)函数来搜索设备
    Device findDeviceByPid(long pid) {
        int i;
        for (i = 0; i < getDeviceCount(); ++i) {
            Device item = getDeviceItem(i);
            if (item.pid == pid) {
                return item;
            }
        }
        return null;
    }

    /** Called when the activity is about to become visible. */
    @Override
    public void onStart() {
        super.onStart();
    }

    /** Called when the activity has become visible. */
    @Override
    public void onResume() {
        super.onResume();
    }

    /** Called when another activity is taking focus. */
    @Override
    public void onPause() {
        super.onPause();
    }

    /** Called when the activity is no longer visible. */
    @Override
    public void onStop() {
        super.onStop();
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
//        quit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quit();
            finish();
        }
        return false;
    }
}