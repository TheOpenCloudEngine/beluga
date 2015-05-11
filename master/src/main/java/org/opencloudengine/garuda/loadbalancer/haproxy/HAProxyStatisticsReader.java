/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.opencloudengine.garuda.loadbalancer.haproxy;

import org.opencloudengine.garuda.common.util.CommandUtils;
import org.opencloudengine.garuda.loadbalancer.LoadBalancerStatisticsReader;
import org.opencloudengine.garuda.loadbalancer.TopologyProvider;
import org.opencloudengine.garuda.loadbalancer.domain.Cluster;
import org.opencloudengine.garuda.loadbalancer.domain.Member;
import org.opencloudengine.garuda.loadbalancer.domain.Port;
import org.opencloudengine.garuda.loadbalancer.domain.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HAProxy statistics reader.
 */
public class HAProxyStatisticsReader implements LoadBalancerStatisticsReader {

	private static final Logger log = LoggerFactory.getLogger(HAProxyStatisticsReader.class);

    private String scriptsPath;
    private String statsSocketFilePath;
    private TopologyProvider topologyProvider;

    public HAProxyStatisticsReader(TopologyProvider topologyProvider) {
        this.scriptsPath = HAProxyContext.getInstance().getScriptsPath();
        this.statsSocketFilePath = HAProxyContext.getInstance().getStatsSocketFilePath();
        this.topologyProvider = topologyProvider;
    }

    @Override
    public int getInFlightRequestCount(String clusterId) {
        String frontendId, backendId, command, output;
        String[] array;
        int totalWeight, weight;

        for (Service service : topologyProvider.getTopology().getServices()) {
            for (Cluster cluster : service.getClusters()) {
                if (cluster.getClusterId().equals(clusterId)) {
                    totalWeight = 0;
                    if ((service.getPorts() == null) || (service.getPorts().size() == 0)) {
                        throw new RuntimeException(String.format("No ports found in service: %s", service.getServiceName()));
                    }

                    for (Port port : service.getPorts()) {
                        for(String hostname : cluster.getHostNames()) {
                            backendId = hostname+"-http-members";
                            for (Member member : cluster.getMembers()) {
                                // echo "get weight <backend>/<server>" | socat stdio <stats-socket>
                                command = String.format("%s/get-weight.sh %s %s %s", scriptsPath, backendId, member.getMemberId(), statsSocketFilePath);
                                try {
                                    output = CommandUtils.executeCommand(command);
                                    if ((output != null) && (output.length() > 0)) {
                                        array = output.split(" ");
                                        if ((array != null) && (array.length > 0)) {
                                            weight = Integer.parseInt(array[0]);
                                            if (log.isDebugEnabled()) {
                                                log.debug("Member weight found: [cluster] {} [member] {} [weight] {}", member.getClusterId(), member.getMemberId(), weight);
                                            }
                                            totalWeight += weight;
                                        }
                                    }
                                } catch (IOException e) {
                                    if (log.isErrorEnabled()) {
                                        log.error("", e);
                                    }
                                }
                            }
                        }
                    }
                    if (log.isInfoEnabled()) {
                        log.info("Cluster weight found: [cluster] {} [weight] {}", cluster.getClusterId(), totalWeight);
                    }
                    return totalWeight;
                }
            }
        }
        return 0;
    }

    @Override
    public int getServedRequestCount(String clusterId) {
        return 0;
    }

    @Override
    public int getActiveInstancesCount(Cluster cluster) {
        return 0;
    }
}
