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


import org.opencloudengine.bak.cloud.controller.domain.IaasProvider;
import org.opencloudengine.bak.cloud.controller.domain.MemberContext;

/**
 * All IaaSes that are going to support by Cloud Controller, should extend this abstract class.
 */
public abstract class Iaas {
    /**
     * Reference to the corresponding {@link IaasProvider}
     */
    private IaasProvider iaasProvider;

    public Iaas(IaasProvider iaasProvider) {
        this.setIaasProvider(iaasProvider);
    }

    public IaasProvider getIaasProvider() {
        return iaasProvider;
    }

    /**
     * Set iaas provider.
     *
     * @param iaasProvider
     */
    public void setIaasProvider(IaasProvider iaasProvider) {
        this.iaasProvider = iaasProvider;
    }

    /**
     * Initialize the iaas object.
     */
    public abstract void initialize();

    /**
     * Create vm/container instance.
     *
     * @param memberContext
     * @param payload
     * @return updated memberContext
     */
    public abstract MemberContext startInstance(MemberContext memberContext, byte[] payload);

    /**
     * This will deallocate/release the given IP address back to pool.
     *
     * @param ip public IP address to be released.
     */
    public abstract void releaseAddress(String ip);

    /**
     * Create a new volume in the respective Iaas.
     *
     * @param sizeGB size of the volume in Giga Bytes.
     * @return Id of the created volume.
     */
    public abstract String createVolume(int sizeGB, String snapshotId);


    /**
     * Attach a given volume to an instance at the specified device path.
     *
     * @param instanceId of the instance.
     * @param volumeId   volume id of the volume to be attached.
     * @param deviceName name of the device that the volume would bind to.
     * @return the status of the attachment.
     */
    public abstract String attachVolume(String instanceId, String volumeId, String deviceName);

    /**
     * Detach a given volume from the given instance.
     *
     * @param instanceId of the instance.
     * @param volumeId   volume id of the volume to be detached.
     */
    public abstract void detachVolume(String instanceId, String volumeId);

    /**
     * Delete a given volume.
     *
     * @param volumeId volume id of the volume to be detached.
     */
    public abstract void deleteVolume(String volumeId);

    /**
     * This returns the device of the volume specified by the user. This is depends on IAAS.
     * For an instance /dev/sdf maps to /dev/xvdf in EC2.
     */
    public abstract String getIaasDevice(String device);

    /**
     * Allocates ip addresses to member.
     *
     * @param clusterId
     * @param memberContext
     */
    public abstract void allocateIpAddresses(String clusterId, MemberContext memberContext);

    /**
     * This method provides a way to set payload.
     */
    public abstract void setDynamicPayload(byte[] payload);

    /**
     * Terminate an instance.
     *
     * @param memberContext
     */
    public abstract void terminateInstance(MemberContext memberContext);
}
