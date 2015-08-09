package org.opencloudengine.garuda.cloud;

import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.Settings;
import org.opencloudengine.garuda.service.AbstractService;
import org.opencloudengine.garuda.service.ServiceException;
import org.opencloudengine.garuda.service.common.ServiceManager;

/**
 * Created by swsong on 2015. 7. 15..
 */
public class CloudWatchService extends AbstractService {
    public CloudWatchService(Environment environment, Settings settings, ServiceManager serviceManager) {
        super(environment, settings, serviceManager);
    }

    @Override
    protected boolean doStart() throws ServiceException {
        return false;
    }

    @Override
    protected boolean doStop() throws ServiceException {
        return false;
    }

    @Override
    protected boolean doClose() throws ServiceException {
        return false;
    }
}
