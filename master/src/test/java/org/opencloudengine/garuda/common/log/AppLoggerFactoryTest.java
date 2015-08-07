package org.opencloudengine.garuda.common.log;

import org.junit.Test;
import org.slf4j.Logger;

import java.io.File;

/**
 * Created by swsong on 2015. 8. 7..
 */
public class AppLoggerFactoryTest {
    @Test
    public void test() {
        File home = new File("/tmp");
        String appId = "app1";

        System.out.println("#### Create logger !!");
        AppLoggerFactory.createLogger(home, appId, "server1");

        System.out.println("#### Get!!");
        Logger logger = AppLoggerFactory.getLogger(appId);

        for(int k = 0; k < 10; k++) {
            for (int i = 0; i < 10000; i++) {
                logger.info("out > {}", System.currentTimeMillis());
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
