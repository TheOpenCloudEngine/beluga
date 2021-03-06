package org.opencloudengine.garuda.beluga.mesos.marathon;

import com.google.gson.Gson;
import org.junit.Test;
import org.opencloudengine.garuda.beluga.mesos.marathon.model.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 9..
 */
public class MarathonNativeRestTest {

    private Logger logger = LoggerFactory.getLogger(MarathonNativeRestTest.class);

    String hostAddress = "54.254.221.131";
    String target = "http://" + hostAddress + ":8080";

    @Test
    public void getApps() {
        Client client = ClientBuilder.newClient();
        String path = "/v2/apps";

        String response = client.target(target).path(path).request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        logger.debug("{}", response);

        client.close();
    }

    @Test
    public void getApp() {
        String appId = "nc";
        Client client = ClientBuilder.newClient();
        String path = "/v2/apps/"+appId;

        String response = client.target(target).path(path).request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        logger.debug("{}", response);
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

        String response = client.target(target).path(path).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(app), String.class);
        logger.debug("{}", response);
        client.close();
    }

    @Test
    public void deployAppWithEnv() {
        String appId = "nc5";
        Client client = ClientBuilder.newClient();
        String path = "/v2/apps";
        String cmd  = "while true; do { echo -e \"HTTP/1.1 200 OK\\r\\n$(date)\\r\\n\\r\\n\"; echo \"Hello~~\"; } | nc -l $PORT; done";
        App app = new App(appId);
        app.setCmd(cmd);
        app.setCpus(0.1f);
        app.setMem(16.0f);
        app.setInstances(1);
        Map<String, String> env = new HashMap();
        env.put("MYSQL_ROOT_PASSWORD", "1111");
        app.setEnv(env);
        Gson gson = new Gson();
        String jsonString = gson.toJson(app);
        logger.debug(jsonString);
        String response = client.target(target).path(path).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(jsonString),String.class);
        logger.debug("{}", response);
        client.close();
    }
}
