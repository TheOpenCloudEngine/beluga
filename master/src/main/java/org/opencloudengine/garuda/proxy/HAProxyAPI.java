package org.opencloudengine.garuda.proxy;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.service.common.ServiceManager;

import java.io.*;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class HAProxyAPI {

    private static final String RESTART_COMMAND = "sudo haproxy -f /etc/haproxy/haproxy.cfg -p /var/run/haproxy.pid -sf $(cat /var/run/haproxy.pid)";

    private String templateFilePath;

    public HAProxyAPI(Environment environment, String domain) {
        templateFilePath = environment.filePaths().path("conf", "haproxy.cfg.template").file().getAbsolutePath();
    }

    public void onChangeTopology(String clusterId) {
        VelocityContext context = new VelocityContext();
        ClusterService clusterService = ServiceManager.getInstance().getService(ClusterService.class);
        Map<String, Object> data = null;
        //TODO topology구성도로 context에 값을 넣어준다.
        clusterService.getClusterTopology(clusterId);

        //TODO 임시파일.
        File toFile = new File("");

        VelocityEngine engine = new VelocityEngine();
        engine.init();

        Template template = engine.getTemplate(templateFilePath, "UTF-8");
        StringWriter stringWriter = new StringWriter();

        template.merge(context, stringWriter);
        String configurationString = stringWriter.toString();


        //TODO file에 슬것인가. 아님 ssh로 바로 보낼것인가.. onchangetopology에서 동기화문제는 없겠다..
        // garuda가 전담을 하니..

        BufferedWriter writer = null;
        try {
            if(!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }

            writer = new BufferedWriter(new FileWriter(toFile));
            writer.write(configurationString);
        } catch (Throwable t) {
            //logger.error("", t);
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException ignore) {
                }
            }

        }

    }



    public void onChangeApp(String clusterId) {


    }


    private void restartProxy() {

    }

}