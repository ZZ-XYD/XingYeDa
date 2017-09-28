package com.jovision;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jovision.Utils.Utils;
import com.jovision.server.AccountServiceImpl;
import com.jovision.server.WebApiImpl;
import com.jovision.server.exception.RequestError;
import com.jovision.server.listener.ResponseListener;

import java.util.Locale;

/**
 * Created by LDL on 2017/5/23.
 */

public class JVBase {
    private static final String ACCESSKEY = "3185A20BEF4942469AA38429AE54EC4D";

    /**
     * 查询已绑定设备
     * @param context
     * @param account  账号号码
     */
    public static void detectionJVId(final Context context , final String account){
    WebApiImpl.getInstance().isAccountExist(account, new ResponseListener<JSONObject>() {

        @Override
        public void onSuccess(JSONObject result) {
            Log.e("webApi", "isAccountExist: onSuccess = "+result);
            boolean isExist;
            try{
                isExist = result.getBoolean("isExist");
                if (isExist) {
//                    Toast.makeText(RegisterActivity.this, "账号已注册", Toast.LENGTH_LONG).show();
                    if (AccountServiceImpl.getInstance().isLogin) {
//                        Toast.makeText(this, "已经登录", Toast.LENGTH_LONG).show();
                        return;
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AccountServiceImpl.getInstance().
                                    login(account, "123456", new ResponseListener() {
                                        @Override
                                        public void onSuccess(Object result) {
//                                            Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show();
                                            Log.e("webApi", "login: onSuccess = "+result);
                                        }

                                        @Override
                                        public void onError(RequestError error) {
//                                            Toast.makeText(context, "登录失败："+error.errmsg, Toast.LENGTH_LONG).show();
                                            Log.e("webApi", "login: onError = "+error.errmsg);
                                        }
                                    });
                        }
                    }).start();
                }else {
//                    Toast.makeText(context, "账号可以使用", Toast.LENGTH_LONG).show();
                    canRegister(context,account);
                }
            }catch (Exception e) {
                e.printStackTrace();
//                Toast.makeText(context, "数据解析错误", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onError(RequestError error) {
            Log.e("webApi", "isAccountExist: onError = "+error.errmsg);
        }
    });
    }

    /**
     * 进行账号注册
     * @param context
     * @param account  账号号码
     */
    public static void canRegister(final Context context, final String account) {
        String pwd1 = "123456";
            String sig = Utils.md5(ACCESSKEY+account+pwd1);
            WebApiImpl.getInstance().registerWithoutValidateCode(account, pwd1,
                    sig, new ResponseListener<JSONObject>(){

                        @Override
                        public void onSuccess(JSONObject result) {
                            Log.e("webApi", "registerWithoutValidateCode: onSuccess = "+result);
                            detectionJVId(context,account);
//                            Toast.makeText(context, "注册成功", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(RequestError error) {
                            Log.e("webApi", "registerWithoutValidateCode: onError = "+error.errmsg);
                            Toast.makeText(context, "注册失败："+error.errmsg, Toast.LENGTH_LONG).show();
                        }
                    });
        }

    /**
     * 添加猫眼设备
     * @param guid 设备号
     * */
    public static void addDev(String guid) {
        String user = "jovetech";
        String pwd = "jovision";
        String name = guid;
        Log.e("addDev", "guid = "+guid+";user = "+user+";pwd = "+pwd+";name = "+name);
        WebApiImpl.getInstance().addDevice(guid, user, pwd, name, new ResponseListener() {
            @Override
            public void onSuccess(Object result) {
                Log.e("webApi", "addDevice: onSuccess = "+result);
//                Toast.makeText(context, "添加成功", Toast.LENGTH_LONG).show();
//                scanDevList();
            }

            @Override
            public void onError(RequestError error) {
                Log.e("webApi", "addDevice: onError = "+error.errmsg);
//                Toast.makeText(context, "添加失败:"+error.errmsg, Toast.LENGTH_LONG).show();
            }
        });
    }
    /**
     * 添加摄像头设备
     * @param guid 设备号
     * */
    public static void addJvDev(String guid) {
        String user = "admin";
        String pwd = "";
        String name = guid;
        Log.e("addDev", "guid = "+guid+";user = "+user+";pwd = "+pwd+";name = "+name);
        WebApiImpl.getInstance().addDevice(guid, user, pwd, name, new ResponseListener() {
            @Override
            public void onSuccess(Object result) {
                Log.e("webApi", "addDevice: onSuccess = "+result);
//                Toast.makeText(context, "添加成功", Toast.LENGTH_LONG).show();
//                scanDevList();
            }

            @Override
            public void onError(RequestError error) {
                Log.e("webApi", "addDevice: onError = "+error.errmsg);
//                Toast.makeText(context, "添加失败:"+error.errmsg, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 删除设备
     * @param guid 设备号
     */
    public static void delDev(String guid) {
        WebApiImpl.getInstance().deleteDevice(guid, new ResponseListener() {
            @Override
            public void onSuccess(Object result) {
                Log.e("webApi", "deleteDevice: onSuccess = "+result);
//                Toast.makeText(JVDevAbout.this, "设备删除成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(RequestError error) {
                Log.e("webApi", "deleteDevice: onError = "+error.errmsg);
//                Toast.makeText(JVDevAbout.this, "删除失败:"+error.errmsg, Toast.LENGTH_LONG).show();
            }
        });
    }



}
