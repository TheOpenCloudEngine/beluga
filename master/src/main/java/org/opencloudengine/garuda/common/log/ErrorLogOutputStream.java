package org.opencloudengine.garuda.common.log;

import org.slf4j.Logger;

/**
 * Created by swsong on 2015. 8. 7..
 */
public class ErrorLogOutputStream extends Slf4jOutputStream {

    public ErrorLogOutputStream(Logger logger) {
        super(logger);
    }


    @Override
    protected void processLine(String line) {
        logger.error(line);
    }
}
