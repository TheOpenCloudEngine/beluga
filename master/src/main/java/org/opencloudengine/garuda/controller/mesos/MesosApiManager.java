package org.opencloudengine.garuda.controller.mesos;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.opencloudengine.garuda.controller.component.PropertyComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * Created by soul on 15. 4. 16.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
@Component
public class MesosApiManager {
	@Autowired
	private PropertyComponent propertyComponent;
	private CuratorFramework client;
	public final int READ_TIMEOUT = 6000;
	public final int MAX_CONNECTION = 10;

	@PostConstruct
	public void init() throws InterruptedException {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(ZooKeeperUtils.RETRY_INTERVAL, ZooKeeperUtils.RETRY_CNT);
		client = CuratorFrameworkFactory.newClient(
			propertyComponent.zookeeperNodes
			, ZooKeeperUtils.SESSION_TIMEOUT
			, ZooKeeperUtils.CONNECTION_TIMEOUT
			, retryPolicy);
		client.start();

		client.blockUntilConnected(5, TimeUnit.SECONDS);
	}

	@PreDestroy
	public void destroy() {
		if (client != null) {
//			IOUtils.closeQuietly(client);
		}
	}

	public CuratorFramework getClient() {
		return client;
	}
}
