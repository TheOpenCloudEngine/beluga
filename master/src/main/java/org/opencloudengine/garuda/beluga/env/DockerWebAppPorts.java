package org.opencloudengine.garuda.beluga.env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 9..
 */
public class DockerWebAppPorts {

    private static Map<String, List<Integer>> appPortsMap;

    public static final String JAVA7_TOMCAT7 = "java7_tomcat7";
    public static final String JAVA7_WILDFLY8_2 = "java7_wildfly8.2";
    public static final String PHP5_APACHE2 = "php5_apache2";

    static {
        appPortsMap = new HashMap<>();
        /* java7 wildfly8.2 */
        {
            List<Integer> ports = new ArrayList<>();
            ports.add(8080);
            appPortsMap.put(JAVA7_TOMCAT7, ports);
        }
        /* java7 wildfly8.2 */
        {
            List<Integer> ports = new ArrayList<>();
            ports.add(8080);
            appPortsMap.put(JAVA7_WILDFLY8_2, ports);
        }
        /* php5 apache2 */
        {
            List<Integer> ports = new ArrayList<>();
            ports.add(80);
            appPortsMap.put(PHP5_APACHE2, ports);
        }
    }

    public static List<Integer> getPortsByStackId(String stackId) {
        return appPortsMap.get(stackId);
    }
}
