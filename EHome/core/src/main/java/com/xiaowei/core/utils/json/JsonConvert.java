package com.xiaowei.core.utils.json;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.xiaowei.core.utils.json.AbsConvert;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Json解析处理
 *
 * @author ye.jian
 */
public class JsonConvert<T> extends AbsConvert<T> {

    private String mDataName = null;

    @Override
    public T parseData(String result) {
        T t = null;
        try {
            Type trueType = ((ParameterizedType) this.getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
            Gson gson = new Gson();
            if (!TextUtils.isEmpty(mDataName)) {
                JSONObject jsonObject = new JSONObject(result);
                t = gson.fromJson(jsonObject.getString(mDataName), trueType);
            } else {
                t = gson.fromJson(result, trueType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 在数据中要获取的name（当数据类型为集合时）
     *
     * @return data_name
     */
    public void setDataName(String dataName) {
        mDataName = dataName;
    }
}