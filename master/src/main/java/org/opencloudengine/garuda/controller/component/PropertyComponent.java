package org.opencloudengine.garuda.controller.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 현재 filter에 설정된 값들을 여기 한곳에서 가져와서 사용한다.
 * 추후에 환경변수에서 가져오거나 하는 경우는 @PostConstruct 에서 불러와서 셋팅해사 사용한다.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
@Component
public class PropertyComponent {
	@Value("${fileupload.temp.dir}")
	public final String fileUploadTempDir = null;

	/**
	 * ************************************************
	 * VM
	 * *************************************************
	 */
	@Value("${vm.userName}")
	public final String vmUserName = null;
	@Value("${vm.pwdOrKeyPath}")
	public final String vmPwdOrKeyPath = null;
	@Value("${vm.haproxy.loc}")
	public final String vmHaProxyLoc = null;
	@Value("${vm.connection.type}")
	public final SshClient.LoginTypeType vmConnectionType = null;

	/**
	 * ************************************************
	 * HAProxy
	 * *************************************************
	 */
	@Value("${haproxy.mode}")
	public final String haproxyMode = null;
	@Value("${haproxy.front.host}")
	public final String haproxyFrontHost = null;
	@Value("${haproxy.temp.file.loc}")
	public final String haproxyTempFileLoc = null;
	@Value("${haproxy.conf.loc}")
	public final String haproxyConfLoc = null;
	@Value("${haproxy.pid.loc}")
	public final String haproxyPidLoc = null;

	/**
	 * ************************************************
	 * Docker
	 * *************************************************
	 */
	@Value("${docker.registry.host}")
	public final String registryHost = null;
	@Value("${docker.registry.port}")
	public final String registryPort = null;
	@Value("${docker.use.ssl.registry}")
	public final Boolean useSslRegistry = null;
	@Value("${docker.ssl.registry.user}")
	public final String sslRegistryUser = null;
	@Value("${docker.ssl.registry.passwd}")
	public final String sslRegistryPwd = null;

	@Value("${docker.image.host}")
	public final String dockerImageHost = null;
	@Value("${docker.port}")
	public final String dockerPort = null;
	@Value("${docker.nodelist}")
	public final String[] nodeList = null;
	@Value("${docker.temp.file.loc}")
	public final String dockerTempFileLoc = null;

	/**
	 * ************************************************
	 * Zookeeper
	 * *************************************************
	 */
	@Value("${zookeeper.nodes}")
	public final String zookeeperNodes = null;

	@PostConstruct
	public void init() {

	}
}
