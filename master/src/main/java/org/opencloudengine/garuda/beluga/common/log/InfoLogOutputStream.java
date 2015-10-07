package org.opencloudengine.garuda.beluga.common.log;

import org.slf4j.Logger;

/**
 * Created by swsong on 2015. 8. 7..
 */
public class InfoLogOutputStream extends Slf4jOutputStream {

    public InfoLogOutputStream(Logger logger) {
        super(logger);
    }


    @Override
    protected void processLine(String line) {
        logger.info(line);
    }
}
