package com.base.config;

import com.base.common.exception.SoException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhangwei
 * @date 2019/4/18 15:27
 */
public class BaseProperties extends PropertyPlaceholderConfigurer {
    private static Map<String, Object> ctxPropertiesMap = new HashMap();
    private static ConfigurableConversionService conversionService = new DefaultConversionService();
    private static AtomicBoolean loadFlog = new AtomicBoolean(true);
    private static final String APP_PROPERTIES = "applicationConfigurationProperties";
    private static final String CLASS_PATH_RESOURCE = "class path resource";

    public BaseProperties() {
    }

    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        if (loadFlog.get()) {
            loadData(props);
        }

    }

    public static void loadData(Properties props) throws BeansException {
        ctxPropertiesMap = new HashMap();
        Iterator var1 = props.keySet().iterator();

        while(var1.hasNext()) {
            Object key = var1.next();
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            ctxPropertiesMap.put(keyStr, value);
        }

    }

    public static Object getProperty(String key) {
        return ctxPropertiesMap.get(key);
    }

    public static String getString(String key) {
        return (String)ctxPropertiesMap.get(key);
    }

    public static Map<String, Object> getAll() {
        return ctxPropertiesMap;
    }

    public static boolean containsProperty(String key) {
        return ctxPropertiesMap.containsKey(key);
    }

    public static boolean setProperty(String key, String value) {
        if (key != null && value != null) {
            ctxPropertiesMap.put(key, value);
            return true;
        } else {
            return false;
        }
    }

    public static String getProperty(String key, String defaultValue) {
        Object value = ctxPropertiesMap.get(key);
        return value == null ? defaultValue : (String)value;
    }

    public static <T> T getProperty(String key, Class<T> targetType) {
        Object value = ctxPropertiesMap.get(key);
        return value == null ? null : conversionService.convert(value, targetType);
    }

    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        Object value = ctxPropertiesMap.get(key);
        return value == null ? defaultValue : conversionService.convert(value, targetType);
    }

    public static void loadData(Environment event) {
        if (loadFlog.get()) {
            ConfigurableEnvironment environment = (ConfigurableEnvironment)event;
            Iterator iter = environment.getPropertySources().iterator();

            while(true) {
                while(true) {
                    PropertySource ps;
                    String name;
                    do {
                        if (!iter.hasNext()) {
                            loadFlog.set(false);
                            return;
                        }

                        ps = (PropertySource)iter.next();
                        name = ps.getName();
                    } while(name == null);

                    if ("applicationConfigurationProperties".equals(name)) {
                        EnumerablePropertySource<?> eps = (EnumerablePropertySource)ps;
                        Map<String, Object> data = new HashMap();
                        String[] var7 = eps.getPropertyNames();
                        int var8 = var7.length;

                        for(int var9 = 0; var9 < var8; ++var9) {
                            String key = var7[var9];
                            data.put(key, eps.getProperty(key));
                        }

                        ctxPropertiesMap = data;
                    } else if (name.startsWith("class path resource")) {
                        try {
                            String propertiesName = name.substring(name.indexOf("[") + 1, name.lastIndexOf("]"));
                            Properties properties = PropertiesLoaderUtils.loadAllProperties(propertiesName);
                            loadData(properties);
                        } catch (IOException var11) {
                            throw new SoException(var11.getMessage());
                        }
                    }
                }
            }
        }
    }
}
