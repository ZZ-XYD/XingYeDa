package com.jovision.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/5/12.
 */

public class Utils {
    //MD5加密方法
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


    /**
     * 跟http请求获取 参数map
     *
     * @param msg
     * @return
     */
    public static HashMap<String, String> genMsgMapFromHttpGet(String msg) {
        HashMap<String, String> map = new HashMap<String, String>();

        if (null == msg || "".equalsIgnoreCase(msg)) {
            return null;
        }
        Matcher matcher = Pattern.compile("([^=&]+)=([^=&]+)").matcher(msg);
        while (matcher.find()) {
            map.put(matcher.group(1), matcher.group(2));
        }
        return map;
    }
}
