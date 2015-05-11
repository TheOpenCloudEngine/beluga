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

import org.opencloudengine.garuda.common.thread.ThreadPoolFactory;
import org.opencloudengine.garuda.loadbalancer.LoadBalancerExtension;
import org.opencloudengine.garuda.loadbalancer.TopologyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/**
 * HAProxy extension main class.
 */
public class Main {
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	private static ExecutorService executorService;

	public static void main(String[] args) {

		LoadBalancerExtension extension = null;
		try {
			// Configure log4j properties
//			PropertyConfigurator.configure(System.getProperty("log4j.properties.file.path"));

			if (log.isInfoEnabled()) {
				log.info("HAProxy extension started");
			}

            // Add shutdown hook
            final Thread mainThread = Thread.currentThread();
            final LoadBalancerExtension finalExtension = extension;
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        if(finalExtension != null) {
                            log.info("Shutting haproxy instance...");
                            finalExtension.stop();
                        }
                        mainThread.join();
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            });

			executorService = ThreadPoolFactory.getExecutorService("haproxy.extension.thread.pool", 10);
			// Validate runtime parameters
			HAProxyContext.getInstance().validate();
            TopologyProvider topologyProvider = new TopologyProvider();
            HAProxyStatisticsReader statisticsReader = HAProxyContext.getInstance().isCEPStatsPublisherEnabled() ?
                    new HAProxyStatisticsReader(topologyProvider) : null;
            extension = new LoadBalancerExtension(new HAProxy(), statisticsReader, topologyProvider);
			extension.setExecutorService(executorService);
			extension.execute();
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error("", e);
			}
			if (extension != null) {
                log.info("Shutting haproxy instance...");
				extension.stop();
			}
		}
	}
}
