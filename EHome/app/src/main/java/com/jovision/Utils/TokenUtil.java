package com.jovision.Utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xingyeda.ehome.base.EHomeApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 离线服务用到的Token值操作类
 *
 * @author ye.jian
 */
public class TokenUtil {
    private static final String TAG = "TokenUtil";
    private static final String TOKEN_FILENAME = "token";
    // 尝试更新时间间隔
    private static int mRetryInterval = 1000;
    // 尝试更新递增间隔
    private static int mRetryIncrease = 1000;

    /**
     * 将获取到的token值写到文件中
     *
     * @param context
     * @param token
     */
    public static void writeToken(Context context, String token) {
        FileOutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(TOKEN_FILENAME, Context
                    .MODE_PRIVATE);
            outputStream.write(token.getBytes());
            Log.v(TAG, "write token success.");
        } catch (FileNotFoundException e) {
            Log.v(TAG, "write token failed.");
            e.printStackTrace();
        } catch (IOException e) {
            Log.v(TAG, "write token failed.");
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取Token
     * 首先从SharedPreference中获取, 其次从文件中获取
     *
     * @param tag 区分是谁调用的这个方法
     * @return
     */
    public static String getToken(String tag) {
        // Token值
        String token = TokenUtil.readTokenFromFile();
        Log.v(TAG, "[" + tag + "] read token from File:" + token);
        return token;
    }

    /**
     * 等待获取token
     * 离线服务中获取token成功后会将token值写入到文件中
     */
    public static String waitReadToken() {
        // 默认重新尝试调用更新Token方法为1秒后(每次递增+1秒: 1 2 3 4秒)
        mRetryInterval = mRetryIncrease;

        String token = "";

        // 如果token值不存在, 等待...
        while (TextUtils.isEmpty(token)) {
            try {
                Thread.sleep(mRetryInterval);
            } catch (InterruptedException e) {
                // 线程被中断后跳出
                break;
            }
            Log.v(TAG, "wait token...");
            // 从文件中读取token
            token = readTokenFromFile();
            if (!TextUtils.isEmpty(token)) {
                Log.v(TAG, "wait read token success");
            }

            mRetryInterval += mRetryIncrease;
        }

        return token;
    }

    /**
     * 从文件中读取token
     *
     * @return
     */
    private static String readTokenFromFile() {
        File tokenFile = new File(EHomeApplication.getInstance().getFilesDir(),
                TOKEN_FILENAME);
        String token = "";
        if (tokenFile.exists()) {
            try {
                FileInputStream fin = EHomeApplication.getInstance()
                        .openFileInput(TOKEN_FILENAME);
                int length = fin.available();
                byte[] buffer = new byte[length];
                fin.read(buffer);
//                token = EncodingUtils.getString(buffer, "UTF-8");
                token = new String(buffer, "UTF-8");
                fin.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return token;
    }
}