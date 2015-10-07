package org.opencloudengine.garuda.beluga.cloud;

import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.env.Settings;
import org.opencloudengine.garuda.beluga.service.AbstractService;
import org.opencloudengine.garuda.beluga.service.ServiceException;
import org.opencloudengine.garuda.beluga.service.common.ServiceManager;

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
