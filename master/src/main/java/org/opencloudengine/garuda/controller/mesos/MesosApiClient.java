package org.opencloudengine.garuda.controller.mesos;

/**
 * Created by soul on 15. 4. 16.
 *
 * @author Seong Jong, Jeon
 * @since 1.0
 */
public interface MesosApiClient {
	void createNodeConnection(String host);

	void closeResource(String host);
}
