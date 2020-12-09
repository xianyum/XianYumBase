package com.base.common.utils;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.MissingResourceException;

/**
 * 只能用于静态方法类中，不适合注入方法中使用
 */
@Component
@Order(-100)
public class PropertiesUtil {

    private static Environment env;
    @Autowired
    public void setEnv(Environment env) {
        PropertiesUtil.env = env;
    }

    /**
     *
     * @param key
     * @return
     */
    public static String getString(String key) {
        try {
            return env.getProperty(key);
        } catch (MissingResourceException e) {
            return null;
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public static String getString(String key, String defaultValue) {
        try {
            String value = env.getProperty(key);
            if (StringUtils.isEmpty(value)) {
                return defaultValue;
            }
            return value;
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @return
     */
    public static int getInt(String key) {
        return Integer.parseInt(env.getProperty(key));
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static int getInt(String key, int defaultValue) {
        String value = env.getProperty(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Integer.parseInt(value);
    }

    /**
     * 根据key获取值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = env.getProperty(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return new Boolean(value);
    }

    /***
     * 是否是线上环境
     * @return
     */
    public static boolean isProduction(){
        return "prod".equals(getString("spring.profiles.active")) || "pred".equals(getString("spring.profiles.active"));
    }
}

