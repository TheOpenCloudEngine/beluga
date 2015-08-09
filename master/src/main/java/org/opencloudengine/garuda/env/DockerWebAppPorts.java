package org.opencloudengine.garuda.env;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 9..
 */
public class DockerWebAppPorts {

    private static Map<String, Integer[]> appPortsMap;

    public static final String JAVA7_WILDFLY8_2 = "java7_wildfly8.2";
    public static final String PHP5_APACHE2 = "php5_apache2";
    static {
        appPortsMap = new HashMap<>();
        appPortsMap.put(JAVA7_WILDFLY8_2, new Integer[] {8080, 80});
        appPortsMap.put(PHP5_APACHE2, new Integer[] {80});
    }

    public static Integer[] getPortsByStackId(String stackId) {
        return appPortsMap.get(stackId);
    }
}
