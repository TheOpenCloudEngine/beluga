package org.opencloudengine.garuda.service;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.exception.GarudaException;
import org.opencloudengine.garuda.service.common.ServiceManager;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class CloudWatchService extends AbstractService {
    public CloudWatchService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws GarudaException {
        return false;
    }

    @Override
    protected boolean doStop() throws GarudaException {
        return false;
    }

    @Override
    protected boolean doClose() throws GarudaException {
        return false;
    }
}
