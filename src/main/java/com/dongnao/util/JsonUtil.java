package com.dongnao.util;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * Json-lib的封装类，实现json字符串与json对象之间的转换
 * 网友johncon提供稍作修改
 * 
 *
 */
public class JsonUtil {
    private static Logger LOG = LoggerFactory.getLogger(JsonUtil.class);
    
    /**
     * 字符串是否非空
     * 
     * @param str
     *            str
     * @return boolean
     */
    public static final boolean isNotBlank(Object str) {
        return !isBlank(str);
    }
    
    /**
     * 字符串是否为空
     * 
     * @param str
     *            str
     * @return boolean
     */
    public static final boolean isBlank(Object str) {
        if (str != null) {
            String s = str.toString();
            return "".equals(s) || "{}".equals(s) || "[]".equals(s)
                    || "null".equals(s);
        }
        else {
            return true;
        }
    }
    
    /**
     * 判断List是否为null或者空
     * 
     * @param str
     * @return
     */
    public static boolean isListEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }
    
    public static String getString(JSONObject jo, String name) {
        
        if (!jo.containsKey(name)) {
            return "";
        }
        
        return jo.getString(name);
    }
    
    public static Long getLong(JSONObject jo, String name) {
        
        if (!jo.containsKey(name)) {
            return 0L;
        }
        
        return jo.getLong(name);
    }
    
    public static Integer getInteger(JSONObject jo, String name) {
        
        if (!jo.containsKey(name)) {
            return 0;
        }
        
        return jo.getInteger(name);
    }
    
    public static BigDecimal getBigDecimal(JSONObject jo, String name) {
        
        if (!jo.containsKey(name)) {
            return new BigDecimal(0.0);
        }
        
        return jo.getBigDecimal(name);
    }
    
}
