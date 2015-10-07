package org.opencloudengine.garuda.beluga.env;

/**
 * Created by swsong on 2015. 8. 7..
 */
public class ClusterPorts {

    public static final int MARATHON_PORT = 8080;
    public static final int MESOS_PORT = 5050;
    public static final int REGISTRY_PORT = 5000;

    public static final int WAS_PORT = 8080;
    public static final int WEBSERVER_PORT = 8080;

    public static final int PROXY_ADMIN_PORT = 8080; //proxy 서버의 8080으로 접근시 마라톤, 메소스등의 관리도구로 연결한다.
    public static final int PROXY_SERVICE_PORT = 80;
}
