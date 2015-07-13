package org.opencloudengine.garuda.cloud.controller.util;

import org.opencloudengine.garuda.cloud.controller.domain.IaasProvider;
import org.opencloudengine.garuda.cloud.controller.exception.InvalidIaasProviderException;
import org.opencloudengine.garuda.cloud.controller.iaases.Iaas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * Created by swsong on 2015. 7. 13..
 */
public class CloudControllerUtil {
    private static final Logger logger = LoggerFactory.getLogger(CloudControllerUtil.class);

    public static Iaas createIaasInstance(IaasProvider iaasProvider)
            throws InvalidIaasProviderException {
        try {
            if (iaasProvider.getClassName() == null) {
                String msg = "You have not specified a class which represents the iaas of type: ["
                        + iaasProvider.getType() + "].";
                logger.error(msg);
                throw new InvalidIaasProviderException(msg);
            }

            Constructor<?> c = Class.forName(iaasProvider.getClassName()).getConstructor(IaasProvider.class);
            Iaas iaas = (Iaas) c.newInstance(iaasProvider);
            return iaas;
        } catch (Exception e) {
            String msg = "Class [" + iaasProvider.getClassName()
                    + "] which represents the iaas of type: ["
                    + iaasProvider.getType() + "] has failed to instantiate.";
            logger.error(msg, e);
            throw new InvalidIaasProviderException(msg, e);
        }
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignore) {
        }

    }
}
