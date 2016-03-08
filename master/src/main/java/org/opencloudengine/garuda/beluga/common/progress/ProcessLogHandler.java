package org.opencloudengine.garuda.beluga.common.progress;

import org.opencloudengine.garuda.beluga.common.log.LogOutputStream;
import org.slf4j.Logger;

/**
 * Created by uengine on 2016. 3. 8..
 */
public class ProcessLogHandler extends LogOutputStream {

    private Logger log;

    public ProcessLogHandler(Logger log) {
        this.log = log;
    }

    @Override
    protected void processLine(String line) {
        log.info(line);
    }
}
