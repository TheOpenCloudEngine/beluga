package org.opencloudengine.garuda.mesos.marathon;

import org.junit.Test;
import org.opencloudengine.garuda.mesos.marathon.message.GetApp;
import org.opencloudengine.garuda.mesos.marathon.message.GetApps;
import org.opencloudengine.garuda.mesos.marathon.model.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 9..
 */
public class MarathonAPITest {

    private Logger logger = LoggerFactory.getLogger(MarathonAPITest.class);

    String hostAddress = "52.69.233.61";
    String target = "http://" + hostAddress + ":8080";

    @Test
    public void getApps() {
        Client client = ClientBuilder.newClient();
        String path = "/v2/apps";

        GetApps apps = client.target(target).path(path).request(MediaType.APPLICATION_JSON_TYPE).get(GetApps.class);
        List<App> appList = apps.getApps();
        for(App app : appList) {
            System.out.println(app);
            System.out.println("id : " + app.getId());
            System.out.println("cmd : " + app.getCmd());
        }

        client.close();
    }

    @Test
    public void getApp() {
        String appId = "nc";
        Client client = ClientBuilder.newClient();
        String path = "/v2/apps/"+appId;

        GetApp getApp = client.target(target).path(path).request(MediaType.APPLICATION_JSON_TYPE).get(GetApp.class);
        App app = getApp.getApp();
        System.out.println(app);
        System.out.println("id : " + app.getId());
        System.out.println("cmd : " + app.getCmd());
        logger.info("additional : ", app.getAdditionalProperties());
        client.close();
    }

    @Test
    public void deployApp() {
        String appId = "nc5";
        Client client = ClientBuilder.newClient();
        String path = "/v2/apps";
        String cmd  = "while true; do { echo -e \"HTTP/1.1 200 OK\\r\\n$(date)\\r\\n\\r\\n\"; echo \"Hello~~\"; } | nc -l $PORT; done";
        App app = new App(appId);
        app.setCmd(cmd);
        app.setCpus(0.1f);
        app.setMem(16.0f);
        app.setInstances(3);

        App appResponse = client.target(target).path(path).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(app), App.class);
        System.out.println(appResponse);

        System.out.println("id : " + appResponse.getId());
        System.out.println("cmd : " + appResponse.getCmd());
        logger.info("additional : ", appResponse.getAdditionalProperties());
        client.close();
    }
}
