/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.opencloudengine.bak.cloud.controller.iaases;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jclouds.compute.ComputeService;
import org.opencloudengine.bak.cloud.controller.domain.IaasProvider;
import org.opencloudengine.bak.cloud.controller.exception.InvalidIaasProviderException;
import org.opencloudengine.bak.cloud.controller.util.ComputeServiceBuilderUtil;

/**
 * jclouds IaaS utility methods.
 */
public class JcloudsIaasUtil {
    private static final Log log = LogFactory.getLog(JcloudsIaasUtil.class);

    public static void buildComputeServiceAndTemplate(IaasProvider iaasProvider) throws InvalidIaasProviderException {
        if (iaasProvider.getImage() != null) {
            buildComputeServiceAndTemplateFromImage(iaasProvider);
        } else {
            buildDefaultComputeService(iaasProvider);
        }
    }

    private static void buildComputeServiceAndTemplateFromImage(IaasProvider iaasProvider) throws InvalidIaasProviderException {
        try {
            JcloudsIaas iaas = (JcloudsIaas) iaasProvider.getIaas();
            iaas.buildComputeServiceAndTemplate();
        } catch (Exception e) {
            String msg = "Could not build iaas of type: " + iaasProvider.getType();
            log.error(msg, e);
            throw new InvalidIaasProviderException(msg, e);
        }
    }

    private static void buildDefaultComputeService(IaasProvider iaasProvider) throws InvalidIaasProviderException {
        try {
            ComputeService computeService = ComputeServiceBuilderUtil.buildDefaultComputeService(iaasProvider);
            iaasProvider.setComputeService(computeService);
        } catch (Exception e) {
            String msg = "Unable to build the jclouds object for iaas "
                    + "of type: " + iaasProvider.getType();
            log.error(msg, e);
            throw new InvalidIaasProviderException(msg, e);
        }
    }
}
