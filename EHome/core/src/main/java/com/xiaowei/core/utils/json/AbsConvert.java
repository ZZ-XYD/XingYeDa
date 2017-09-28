package com.xiaowei.core.utils.json;

/**
 * 解析处理
 *
 * @author ye.jian
 */
public abstract class AbsConvert<T> {

    abstract T parseData(String result);
}
