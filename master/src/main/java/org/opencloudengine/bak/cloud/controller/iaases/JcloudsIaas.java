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

import com.google.common.net.InetAddresses;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.Template;
import org.opencloudengine.bak.cloud.controller.domain.IaasProvider;
import org.opencloudengine.bak.cloud.controller.domain.InstanceMetadata;
import org.opencloudengine.bak.cloud.controller.domain.MemberContext;
import org.opencloudengine.bak.cloud.controller.exception.CloudControllerException;
import org.opencloudengine.bak.cloud.controller.exception.InvalidIaasProviderException;
import org.opencloudengine.bak.cloud.controller.util.CloudControllerConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An abstraction for defining jclouds IaaS features.
 */
public abstract class JcloudsIaas extends Iaas {

    private static final Log log = LogFactory.getLog(JcloudsIaas.class);

    public JcloudsIaas(IaasProvider iaasProvider) {
        super(iaasProvider);
    }

    public abstract void buildComputeServiceAndTemplate();

    /**
     * Builds only the jclouds {@link Template}
     */
    public abstract void buildTemplate();

    /**
     * This method should create a Key Pair corresponds to a given public key in the respective region having the name given.
     * Also should override the value of the key pair in the {@link Template} of this IaaS.
     *
     * @param region      region that the key pair will get created.
     * @param keyPairName name of the key pair. NOTE: Jclouds adds a prefix : <code>jclouds#</code>
     * @param publicKey   public key, from which the key pair will be created.
     * @return whether the key pair creation is successful or not.
     */
    public abstract boolean createKeyPairFromPublicKey(String region, String keyPairName, String publicKey);

    /**
     * This will obtain an IP addresses from the allocated list and associate that IP with this node.
     *
     * @param node Node to be associated with an IP.
     * @return return list of associated public IPs.
     */
    public abstract List<String> associateAddresses(NodeMetadata node);

    /**
     * This will obtain a predefined IP address and associate that IP with this node, if ip is already in use allocate ip from pool
     * (through associateAddress())
     *
     * @param node Node to be associated with an IP.
     * @return associated public IP.
     * @ip preallocated floating Ip
     */
    public abstract String associatePredefinedAddress(NodeMetadata node, String ip);

    @Override
    public void initialize() {
        try {
            JcloudsIaasUtil.buildComputeServiceAndTemplate(getIaasProvider());
        } catch (InvalidIaasProviderException e) {
            log.error("Could not initialize jclouds IaaS", e);
        }
    }

    @Override
    public MemberContext startInstance(MemberContext memberContext, byte[] payload) {
        // generate the group id from domain name and sub domain name.
        // Should have lower-case ASCII letters, numbers, or dashes.
        // Should have a length between 3-15

        this.setDynamicPayload(payload);
        String clusterId = memberContext.getClusterId();
        String str = clusterId.length() > 10 ? clusterId.substring(0, 10) : clusterId.substring(0, clusterId.length());
        String group = str.replaceAll("[^a-z0-9-]", "");

        try {
            ComputeService computeService = getIaasProvider().getComputeService();
            Template template = getIaasProvider().getTemplate();

            if (template == null) {
                String msg = "Could not start an instance, jclouds template is null for iaas provider [type]: " +
                        getIaasProvider().getType();
                log.error(msg);
                throw new InvalidIaasProviderException(msg);
            }

            if (log.isDebugEnabled()) {
                log.debug("Cloud controller is delegating request to start an instance for "
                        + memberContext + " to jclouds");
            }
            // create and start a node
            Set<? extends NodeMetadata> nodeMetadataSet = computeService.createNodesInGroup(group, 1, template);
            NodeMetadata nodeMetadata = nodeMetadataSet.iterator().next();
            if (log.isDebugEnabled()) {
                log.debug("Cloud controller received a response for the request to start "
                        + memberContext + " from Jclouds layer.");
            }

            if (nodeMetadata == null) {
                String msg = "Null response received for instance start-up request to Jclouds.\n"
                        + memberContext.toString();
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            memberContext.setInstanceId(nodeMetadata.getId());
            memberContext.setInstanceMetadata(createInstanceMetadata(nodeMetadata));
        } catch (Exception e) {
            String msg = "Failed to start an instance. " + memberContext.toString() + " Cause: " + e.getMessage();
            log.error(msg, e);
            throw new IllegalStateException(msg, e);
        }
        return memberContext;
    }

    protected InstanceMetadata createInstanceMetadata(NodeMetadata nodeMetadata) {
        InstanceMetadata instanceMetadata = new InstanceMetadata();
        instanceMetadata.setHostname(nodeMetadata.getHostname());
        instanceMetadata.setImageId(nodeMetadata.getImageId());
        instanceMetadata.setLoginPort(nodeMetadata.getLoginPort());
        if (nodeMetadata.getHardware() != null) {
            instanceMetadata.setHypervisor(nodeMetadata.getHardware().getHypervisor());
            instanceMetadata.setRam(nodeMetadata.getHardware().getRam());
        }
        if (nodeMetadata.getOperatingSystem() != null) {
            instanceMetadata.setOperatingSystemName(nodeMetadata.getOperatingSystem().getName());
            instanceMetadata.setOperatingSystemVersion(nodeMetadata.getOperatingSystem().getVersion());
            instanceMetadata.setOperatingSystem64bit(nodeMetadata.getOperatingSystem().is64Bit());
        }
        return instanceMetadata;
    }

    @Override
    public void allocateIpAddresses(String clusterId, MemberContext memberContext) {
        try {
            if (log.isInfoEnabled()) {
                log.info(String.format("Allocating IP addresses for member: [cartridge-type] %s [member-id] %s",
                        memberContext.getClusterId(), memberContext.getInstanceId()));
            }

            ComputeService computeService = getIaasProvider().getComputeService();
            NodeMetadata nodeMetadata = computeService.getNodeMetadata(memberContext.getInstanceId());
            if (nodeMetadata == null) {
                String message = "Node metadata not found: [node-id] " + memberContext.getInstanceId();
                log.error(message);
                throw new CloudControllerException(message);
            }

            String autoAssignIpProp = getIaasProvider().getProperty(CloudControllerConstants.AUTO_ASSIGN_IP_PROPERTY);
            String preDefinedPublicIp = getIaasProvider().getProperty(CloudControllerConstants.FLOATING_IP_PROPERTY);
            List<String> associatedIPs = new ArrayList<String>();

            // default behavior is autoIpAssign=false
            if ((autoAssignIpProp == null) || ((autoAssignIpProp != null) && autoAssignIpProp.equals("false"))) {

                if (StringUtils.isNotBlank(preDefinedPublicIp)) {
                    // Allocate predefined public ip
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Allocating predefined public IP address: " +
                                        "[cartridge-type] %s [member-id] %s [pre-defined-ip] %s",
                                memberContext.getClusterId(), memberContext.getInstanceId(),
                                preDefinedPublicIp));
                    }

                    if (!InetAddresses.isInetAddress(preDefinedPublicIp)) {
                        String msg = String.format("Predefined public IP address is not valid: " +
                                       "[pre-defined-ip] %s", preDefinedPublicIp);
                        log.error(msg);
                        throw new CloudControllerException(msg);
                    }

                    String allocatedIp = associatePredefinedAddress(nodeMetadata, preDefinedPublicIp);
                    if ((StringUtils.isBlank(allocatedIp)) || (!preDefinedPublicIp.equals(allocatedIp))) {
                        String msg = String.format("Could not allocate predefined public IP address: " +
                                        "[cluster-id] %s [instance-id] %s " +
                                        "[pre-defined-ip] %s [allocated-ip] %s",
                                memberContext.getClusterId(), memberContext.getInstanceId(),
                                preDefinedPublicIp, allocatedIp);
                        log.error(msg);
                        throw new CloudControllerException(msg);
                    }
                    associatedIPs.add(allocatedIp);
                } else {
                    // Allocate dynamic public ip addresses
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Allocating dynamic public IP addresses: " +
                                "[cluster-id] %s [instance-id] %s " +
                                memberContext.getClusterId(), memberContext.getInstanceId()));
                    }

                    associatedIPs = associateAddresses(nodeMetadata);

                    // checking for null and empty is enough. If there are elements in this list, they are valid IPs
                    // because we are validating before putting into the list
                    if (associatedIPs == null || associatedIPs.isEmpty()) {
                        String msg = String.format("Could not allocate dynamic public IP addresses: " +
                                        "[cluster-id] %s [instance-id] %s",
                                memberContext.getClusterId(), memberContext.getInstanceId(),
                                preDefinedPublicIp);
                        log.error(msg);
                        throw new CloudControllerException(msg);
                    }
                }

                memberContext.setAllocatedIPs(associatedIPs.toArray(new String[associatedIPs.size()]));
                log.info(String.format("IP addresses allocated to member: [cluster-id] %s [instance-id] %s " +
                                "[allocated-ip-addresses] %s ", memberContext.getClusterId(), memberContext.getInstanceId(),
                        memberContext.getAllocatedIPs()));

                // build the node with the new ip
                nodeMetadata = NodeMetadataBuilder.fromNodeMetadata(nodeMetadata).publicAddresses(associatedIPs).build();
            }


            // public IPs
            Set<String> publicIPAddresses = nodeMetadata.getPublicAddresses();
            if (publicIPAddresses != null && !publicIPAddresses.isEmpty()) {
                memberContext.setPublicIPs(publicIPAddresses.toArray(new String[publicIPAddresses.size()]));
                //TODO set a flag in cartridge definition to specify the default public IP or the interface
                memberContext.setDefaultPublicIP(publicIPAddresses.iterator().next());
                log.info("Retrieving public IP addresses: " + memberContext.toString());
            } else {
                memberContext.setPublicIPs(new String[0]);
            }

            // private IPs
            Set<String> privateIPAddresses = nodeMetadata.getPrivateAddresses();
            if (privateIPAddresses != null && !privateIPAddresses.isEmpty()) {
                memberContext.setPrivateIPs(privateIPAddresses.toArray(new String[privateIPAddresses.size()]));
                //TODO set a flag in cartridge definition to specify the default private IP or the interface
                memberContext.setDefaultPrivateIP(privateIPAddresses.iterator().next());
                log.info("Retrieving private IP addresses " + memberContext.toString());
            } else {
                memberContext.setPrivateIPs(new String[0]);
            }

            if (log.isDebugEnabled()) {
                log.debug("IP allocation process ended for " + memberContext);
            }
        } catch (Exception e) {
            String msg = String.format("Error occurred while allocating ip addresses: [cluster-id] %s [instance-id] %s"
                    , memberContext.getClusterId(), memberContext.getInstanceId());
            log.error(msg, e);
            throw new CloudControllerException(msg, e);
        }
    }

    public void terminateInstance(MemberContext memberContext) {
        String nodeId = memberContext.getInstanceId();

        // if no matching node id can be found.
        if (nodeId == null) {
            String msg = String.format("Member termination failed, could not find node id in member context: " +
                            "[cartridge-type] %s [member-id] %s");

            // Execute member termination post process
//            CloudControllerServiceUtil.executeMemberTerminationPostProcess(memberContext);
            log.error(msg);
            return;
        }

        // Terminate the actual member instance
        destroyNode(nodeId, memberContext);
    }

    /**
     * Terminate member instance via jclouds API
     *
     * @param memberContext
     * @param nodeId
     * @return will return the IaaSProvider
     */
    private void destroyNode(String nodeId, MemberContext memberContext) {
        // Detach volumes if any

        detachVolume(memberContext);

        // Destroy the node via jclouds
        getIaasProvider().getComputeService().destroyNode(nodeId);

        // releasing all allocated IPs
        if (memberContext.getAllocatedIPs() != null) {
            for (String allocatedIP : memberContext.getAllocatedIPs()) {
                releaseAddress(allocatedIP);
            }
        }
    }

    private void detachVolume(MemberContext ctxt) {
        String clusterId = ctxt.getClusterId();
        //TODO
    }


    public NodeMetadata findNodeMetadata(String nodeId) {
        ComputeService computeService = getIaasProvider().getComputeService();
        return computeService.getNodeMetadata(nodeId);
    }
}
