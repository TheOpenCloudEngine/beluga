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

import java.util.concurrent.ExecutorService;

/**
 * Load balancer extension thread for executing load balancer life-cycle according to the topology updates
 * received from the message broker.
 */
public class LoadBalancerExtension {

	private final LoadBalancer loadBalancer;
	private final LoadBalancerStatisticsReader statsReader;
	private final TopologyProvider topologyProvider;

	private boolean loadBalancerStarted;

	private ExecutorService executorService;

	public LoadBalancerExtension(LoadBalancer loadBalancer, LoadBalancerStatisticsReader statsReader, TopologyProvider topologyProvider) {
		this.loadBalancer = loadBalancer;
		this.statsReader = statsReader;
		this.topologyProvider = topologyProvider;
	}

	public void stop() {
		try {
			loadBalancer.stop();
		} catch (Exception ignore) {
		}
	}

	public void setExecutorService(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public void execute() {

	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	/**
	 * Configure and reload load balancer configuration.
	 */
	private void reloadConfiguration() {
		try {
			if (!loadBalancerStarted) {
//				configureAndStart();
			}
			else {
//				configureAndReload();

			}
		} catch (Exception e) {
//			if (logger.isErrorEnabled()) {
//				logger.error("Could not reload load balancer configuration", e);
//			}
		}
	}
}
