package org.opencloudengine.garuda.proxy;

/**
 * Created by swsong on 2015. 8. 10..
 */
public class Proxy {

    private static final String RESTART_COMMAND = "sudo haproxy -f /etc/haproxy/haproxy.cfg -p /var/run/haproxy.pid -sf $(cat /var/run/haproxy.pid)";


    public void restartCommad() {

    }

}