package org.opencloudengine.garuda.beluga.action.webapp;

import org.junit.Test;
import org.opencloudengine.garuda.beluga.action.ActionStatus;
import org.opencloudengine.garuda.beluga.action.BaseActionTest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 7..
 */
public class DeployWebAppActionTest extends BaseActionTest {

    @Test
    public void deployTest() throws InterruptedException {
        String clusterId = "test-cluster";
        String appId = "webapp-java";
        String webAppFile = new File("production/sampleapp/webapp-java-1.0.war").getAbsolutePath();
        String webAppType = "java7_wildfly8.2";
        List<WebAppContextFile> fileList = new ArrayList<>();
        fileList.add(new WebAppContextFile(webAppFile, "/"));
        //어느 클러스터에 보낼지
        float cpus = 0.2f;
        float memory = 700; //최소 768이다. WAS가 512로 뜨기때문에.
        int scale = 1;

        DeployWebAppActionRequest request = new DeployWebAppActionRequest(clusterId, appId, 1, fileList, webAppType, null, cpus, memory, scale, null);
        DeployWebAppAction action = new DeployWebAppAction(request);
        ActionStatus status = action.getStatus();
        action.run();
        status.waitForDone();
    }
}
