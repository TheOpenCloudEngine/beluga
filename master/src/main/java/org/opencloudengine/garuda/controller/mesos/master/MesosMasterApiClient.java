package org.opencloudengine.garuda.controller.mesos.master;

import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.opencloudengine.garuda.controller.component.PropertyComponent;
import org.opencloudengine.garuda.controller.mesos.*;
import org.opencloudengine.garuda.controller.mesos.master.model.state.Slafe;
import org.opencloudengine.garuda.controller.mesos.master.model.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by soul on 15. 4. 16.
 * - http://10.0.1.161:5050/state.json
 * - http://10.0.1.161:5050/help
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
@Component("mesosMasterApiClient")
public class MesosMasterApiClient implements MesosApiClient {
	private static final Logger logger = LoggerFactory.getLogger(MesosMasterApiClient.class);

	@Autowired
	private MesosApiManager mesosApiManager;
	@Autowired
	private PropertyComponent propertyComponent;

	//Web Client (Mesos Master Leader에 요청을 해야 모든 데이터를 가져올 수 있다.)
	private String leaderHost;
	private Map<String, Client> webClientMap = new ConcurrentHashMap<>();
	private Map<String, WebTarget> webResourceMap = new ConcurrentHashMap<>();

	//zookeeper monitor setting
	private PathChildrenCache cache;
	private final String MESOS_MASTER_ZNODE = "/mesos";

	@PostConstruct
	public void init() throws Exception {
		//marathon node monitoring
		cache = new PathChildrenCache(mesosApiManager.getClient(), MESOS_MASTER_ZNODE, true);
		cache.start();
		new MesosServerMonitor("mesos master", this, cache);
	}

	@Override
	public void createNodeConnection(String nodeData) {
		if (nodeData == null || "".equals(nodeData)) {
			return;
		}
		//Mesos Master가 설치된 도메인으로 접속 (내부 아이피인 경우 hosts에 도메인 설정)
		String[] splitData = nodeData.split("\\*%12");
		String[] splitIp = splitData[0].split("%3A");
		String ip = splitIp[0].split("%40")[1];
		String port = splitIp[1];
		String domain = splitData[1];

		String apiUri = String.format("http://%s:%s", domain, port);
		if (webResourceMap.get(ip) == null) {
//			WebResourceConfigBuilder builder = new WebResourceConfigBuilder();
//			builder.withMaxTotalConnections(mesosApiManager.MAX_CONNECTION)
//				.withReadTimeout(mesosApiManager.READ_TIMEOUT)
//				.withUri(apiUri);
//
//			WebResourceConfig webResourceConfig = builder.build();
//			Client webClient = WebResourceClient.getWebClient(webResourceConfig);
//			WebTarget webResource = webClient.target(webResourceConfig.getUri());
//
//			webClientMap.put(ip, webClient);
//			webResourceMap.put(ip, webResource);
//			logger.info("Connect to mesos master node - {}, available nodes:{}", new Object[]{apiUri, webResourceMap.size()});
		}
		checkLeader();
	}

	@Override
	public void closeResource(String host) {
		if (host.equals(leaderHost)) {
			leaderHost = null;
			checkLeader();
		}

		Client webClient = webClientMap.remove(host);
		webResourceMap.remove(host);

		if (webClient != null) {
			webClient.close();
			logger.info("Disconnect to mesos master node - {}, available nodes:{}", new Object[]{host, webResourceMap.size()});
		}
	}

	private void checkLeader() {
		if (leaderHost == null || "".equals(leaderHost)) {
			leaderHost = searchMasterLeader();
		}
	}

	private WebTarget getWebResource(boolean isLeader) {
		if (webResourceMap == null || webResourceMap.size() == 0) {
			throw new RuntimeException("Not exists web resource");
		}

		if (isLeader) {
			return webResourceMap.get(leaderHost);
		}

		Iterator<String> iter = webResourceMap.keySet().iterator();
		return webResourceMap.get(iter.next());
	}

	/**
	 * Mesos Master 들중 Leader 노드를 조회한다.
	 *
	 * @return
	 */
	public String searchMasterLeader() {
		WebTarget resource = getWebResource(false).path("/state.json");

		State state = resource.request()
			.accept(MediaType.APPLICATION_JSON)
			.get(new GenericType<State>() {
			});

		if (state.getLeader() != null && !"".equals(state.getLeader())) {
			String[] split = state.getLeader().split("@");
			return split[1];
		}

		return null;
	}

	public Map<String, Integer> searchMastersHealthCheck() {
		Map<String, Integer> result = new HashMap<>();

		WebTarget resource = null;
		Iterator<String> iter = webResourceMap.keySet().iterator();
		while (iter.hasNext()) {
			String host = iter.next();
			resource = webResourceMap.get(host).path("/master/health");
			result.put(host, resource.request().get().getStatus());
		}

		return result;
	}

	public State searchMasterLeaderStatus() {
		WebTarget resource = getWebResource(true).path("/state.json");

		return resource.request()
			.accept(MediaType.APPLICATION_JSON)
			.get(new GenericType<State>() {
			});
	}

	/**
	 * Mesos Slave의 자원등 클러스터의 현재 상태 값을 조회한다.
	 *
	 * @return
	 */
	public List<Slafe> searchSlavesResource() {
		WebTarget resource = getWebResource(true).path("/master/slaves");

		State state = resource.request()
			.accept(MediaType.APPLICATION_JSON)
			.get(new GenericType<State>() {
			});
		return state.getSlaves();
	}

	/**
	 * 이건 사용하지 말고 marathon을 이용할 것!
	 *
	 * @param frameworkId
	 * @return
	 */
	@Deprecated
	public int shutdownFramework(String frameworkId) {
		Form form = new Form("frameworkId", frameworkId);
		WebTarget resource = getWebResource(true).path("/master/shutdown");
		return resource.request().post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE)).getStatus();
	}
}
