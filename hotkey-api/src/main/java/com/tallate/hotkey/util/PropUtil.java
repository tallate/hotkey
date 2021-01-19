package com.tallate.hotkey.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropUtil {

    public static String get(String filePath, String propKey) throws IOException {
        Properties properties = new Properties();
        InputStream is = PropUtil.class.getClassLoader().
                getResourceAsStream(filePath);
        properties.load(is);
        Object obj = properties.get(propKey);
        return null == obj ? "" : (String) obj;
    }

}