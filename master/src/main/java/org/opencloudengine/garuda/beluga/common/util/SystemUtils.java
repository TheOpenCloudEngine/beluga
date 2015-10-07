package org.opencloudengine.garuda.beluga.common.util;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.UnknownHostException;

public class SystemUtils {

    public static final long MEGA_BYTES = 1024 * 1024;

    public static String getPid() {
        try {
            String name = ManagementFactory.getRuntimeMXBean().getName();
            if (name != null) {
                return name.split("@")[0];
            }
        } catch (Throwable ex) {
            // Ignore
        }
        return "????";
    }

    public static String getHostName() {
        try {
            return Inet4Address.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getIpAddr() {
        try {
            return Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return null;
    }
}
