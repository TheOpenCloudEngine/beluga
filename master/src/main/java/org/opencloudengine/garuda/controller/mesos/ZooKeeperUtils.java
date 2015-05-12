package org.opencloudengine.garuda.controller.mesos;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Created by soul on 15. 4. 6.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
public class ZooKeeperUtils {
	private static final Logger logger = LoggerFactory.getLogger(ZooKeeperUtils.class);

	private final static String ZOO_SERVER_PROPERTY = "/data/conf/zoo_servers.properties";
	private final static String ZOO_SERVER_PROPERTY_KEY = "zoo_servers";

	private final static String DEFAULT_SEPARATOR = "/";
	private final static String PARENT_ZNODE = "/garuda/nodes";
	public final static Map<String, String> DEFAULT_ZNODE_MAP;

	static {
		DEFAULT_ZNODE_MAP = new HashMap<>();
		//각 노드들의 데이터를 영속적으로 보관한
		DEFAULT_ZNODE_MAP.put("persistence", PARENT_ZNODE + "/persistence");
		//각 노드들의 znode 생성을 위함. (접속이 끊어지면 znode 삭제됨)
		DEFAULT_ZNODE_MAP.put("ephemeral", PARENT_ZNODE + "/ephemeral");
	}

	public enum NODE_STATUS {
		running, die
	}

	//session timeout 값을 작게 줘야 ephemeral 노드의 삭제가 빠르게 일어나서 감지가 빠르게 된다.
	public final static int SESSION_TIMEOUT = 1000;
	public final static int CONNECTION_TIMEOUT = 3000;
	public final static int RETRY_INTERVAL = 1000;
	public final static int RETRY_CNT = 3;

	public static String getNodePath(String znodeMapKey, boolean useSystemIp) {
		if (useSystemIp) {
			return ZooKeeperUtils.DEFAULT_ZNODE_MAP.get(znodeMapKey) + "/" + SystemUtils.getIpAddr();
		}
		return ZooKeeperUtils.DEFAULT_ZNODE_MAP.get(znodeMapKey) + "/" + System.getenv("hostIp");
	}

	public static String getZooKeeperServers() {
		File file = new File(ZOO_SERVER_PROPERTY);
		Properties prop = new Properties();
		InputStream is = null;

		String servers = null;
		try {
			if (file.exists()) {
				is = new FileInputStream(ZOO_SERVER_PROPERTY);
			} else {
				is = ZooKeeperUtils.class.getResourceAsStream("/com/test/test.properties");
			}
			prop.load(new InputStreamReader(is, "UTF-8"));

			servers = prop.getProperty(ZOO_SERVER_PROPERTY_KEY);
			if (servers == null) {
				throw new Exception(String.format("Zookeeper server don't set up - key:%s", ZOO_SERVER_PROPERTY_KEY));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return servers;
	}

	/**
	 * 각 노들의 ZNode를 생성하기 위한 부모 ZNode를 생성한다.
	 *
	 * @throws Exception
	 */
	public static void makeParentZNode(CuratorFramework client) {
		boolean loop = true;
		int retryCnt = 0;
		try {
			while (loop) {
				switch (client.getState()) {
					case LATENT:    //has not yet been called
						retryCnt++;

						if (retryCnt == 3) {
							throw new Exception("You confirm to establish zookeeper server");
						}
						Thread.sleep(300);
						break;
					case STARTED:   //has been called
						Iterator<String> iter = DEFAULT_ZNODE_MAP.keySet().iterator();
						while (iter.hasNext()) {
							String parentNode = DEFAULT_ZNODE_MAP.get(iter.next());
							String[] pathList = parentNode.split(DEFAULT_SEPARATOR);
							String fullPath = "";

							for (int i = 1; i < pathList.length; i++) {
								fullPath += DEFAULT_SEPARATOR + pathList[i];
								Stat stat = client.checkExists().forPath(fullPath);
								if (stat == null) {
									client.create().withMode(CreateMode.PERSISTENT).forPath(fullPath);
									logger.info("Create paret znode - path:{}", fullPath);
								}
							}
						}
						loop = false;
						break;
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Stat checkExists(CuratorFramework client, String path) {
		try {
			return client.checkExists().forPath(path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String createZNode(CuratorFramework client, String path, String data) {
		try {
			String znode = client.create().withMode(CreateMode.PERSISTENT).forPath(path, data.getBytes());
			logger.info("Create znode - path:{}, data:{}", new Object[]{path, data});
			return znode;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Stat setDataInZNode(CuratorFramework client, String path, String data) {
		try {
			Stat stat = client.setData().forPath(path, data.getBytes());
			logger.info("Set data int znode - path:{}, data:{}", new Object[]{path, data});
			return stat;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] getDataInZNode(CuratorFramework client, String path) {
		try {
			return client.getData().forPath(path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public static void existsWithCreate(CuratorFramework client, String path, String data) {
		Stat stat = checkExists(client, path);
		if (stat == null) {
			createZNode(client, path, data);
		} else {
			setDataInZNode(client, path, data);
		}
	}
}
