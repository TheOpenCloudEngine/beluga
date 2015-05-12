package org.opencloudengine.garuda.controller.mesos;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.opencloudengine.garuda.controller.mesos.marathon.MarathonApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

/**
 * Created by soul on 15. 4. 14.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
public class MesosServerMonitor implements PathChildrenCacheListener {
	private static final Logger logger = LoggerFactory.getLogger(MesosServerMonitor.class);
	private String targetSystem;
	private PathChildrenCache cache;
	private MesosApiClient mesosApiClient;

	public MesosServerMonitor() {
	}

	public MesosServerMonitor(String targetSystem, MesosApiClient mesosApiClient, PathChildrenCache cache) {
		this.targetSystem = targetSystem;
		this.mesosApiClient = mesosApiClient;
		this.cache = cache;
		this.cache.getListenable().addListener(this);
	}

	@Override
	public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
		switch (event.getType()) {
			case CHILD_ADDED:
				String data = null;
				if (mesosApiClient instanceof MarathonApiClient) {
					data = new String(event.getData().getData());
				} else if (mesosApiClient instanceof MesosApiClient) {
					data = URLEncoder.encode(new String(event.getData().getData()), "UTF-8");
				}
				logger.info("Added {} node - path({}) : data({})", new Object[]{targetSystem, event.getData().getPath(), data});
				mesosApiClient.createNodeConnection(data);
				break;
			case CHILD_UPDATED:
				break;
			case CHILD_REMOVED:
				data = URLEncoder.encode(new String(event.getData().getData()), "UTF-8");
				logger.info("Removed {} node - path({}) : data({})", new Object[]{targetSystem, event.getData().getPath(), data});
				mesosApiClient.closeResource(data);
				break;
			case CONNECTION_SUSPENDED:
			case CONNECTION_RECONNECTED:
			case CONNECTION_LOST:
				break;
			case INITIALIZED:
			default:
				break;
		}

		cache.getListenable().addListener(this);
	}
}
