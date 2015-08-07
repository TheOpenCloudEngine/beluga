package org.opencloudengine.garuda.common.log;

import org.slf4j.Logger;

/**
 * Created by swsong on 2015. 8. 7..
 */
public class AppInfoOutputStream extends Slf4jOutputStream {

    public AppInfoOutputStream(Logger logger) {
        super(logger);
    }


    @Override
    protected void processLine(String line) {
        logger.info(line);
    }
}
