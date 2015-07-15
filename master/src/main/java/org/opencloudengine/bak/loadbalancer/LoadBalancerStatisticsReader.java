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

package org.opencloudengine.bak.loadbalancer;


import org.opencloudengine.bak.loadbalancer.domain.Cluster;

/**
 * Load balancer statistics reader interface.
 */
public interface LoadBalancerStatisticsReader {

    /**
     * Returns in-flight request count of sliding window configured.
     * @param clusterId
     */
    int getInFlightRequestCount(String clusterId);

    int getActiveInstancesCount(Cluster cluster);


    /**
     * Returns the number of requests served since the last time this method was called.
     * @param clusterId
     * @return
     */
    int getServedRequestCount(String clusterId);
}
