package com.lance.ftp.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropertiesUtil {
    /**
     * 默认的配置文件
     */
    private static String defaultPropName = "ftp.properties";

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);


    public static String getProperty(String key, String defaultValue, String propName) {
        Properties prop = new Properties();
        String value = defaultValue;
        try {
            prop.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(propName),"UTF-8"));
            value = prop.getProperty(key.trim());
            if (StringUtils.isBlank(value)) {
                value = defaultValue;
                System.out.println("defaultValue:" + defaultValue);
            }
        } catch (IOException e) {
            logger.error("配置文件读取异常", e);
        }
        return value;
    }

    public static String getProperty(String key) {
        return getProperty(key, "", defaultPropName);
    }

    public static String getProperty(String key, String defaultValue) {
        return getProperty(key, defaultValue,defaultPropName);
    }

}
