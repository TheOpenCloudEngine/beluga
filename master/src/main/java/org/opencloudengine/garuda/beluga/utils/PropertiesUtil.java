package org.opencloudengine.garuda.beluga.utils;

import java.io.*;
import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 22..
 */
public class PropertiesUtil {

    public static Properties loadProperties(String filepath) throws IOException {
        InputStream is = new FileInputStream(new File(filepath));
        Properties props = new Properties();
        try {
            props.load(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return props;
    }
}
