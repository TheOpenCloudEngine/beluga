package org.opencloudengine.garuda.beluga.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 20..
 */
public abstract class PropertyConfig {
    protected final Logger logger = LoggerFactory.getLogger(PropertyConfig.class);

    private Properties p;

    public PropertyConfig(File f) {
        try {
            p = loadProperties(f);
        } catch (IOException e) {
            logger.error("error load config file", e);
        }
        init(p);
    }

    public PropertyConfig(Properties p) {
        this.p = p;
        init(p);
    }

    protected abstract void init(Properties p);

    private Properties loadProperties(File f) throws IOException {
        Properties p = new Properties();
        InputStream is = new FileInputStream(f);

        try {
            p.load(is);
        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        }
        return p;
    }


}
