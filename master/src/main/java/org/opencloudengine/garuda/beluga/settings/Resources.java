package org.opencloudengine.garuda.beluga.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by swsong on 2015. 11. 26..
 */
public class Resources {

    public static Map<String, Resource> resourceMap = new HashMap<>();
    static {
        Map<String, String> mysqlEnv = new HashMap<>();
        mysqlEnv.put("MYSQL_ROOT_PASSWORD", "beluga123:)");
        resourceMap.put("mysql5", new Resource("mysql5", "MySQL 5.7.9", "mysql:5.7.9", 3306, 0.2f, 500f, mysqlEnv));
        resourceMap.put("postgresql9", new Resource("postgresql9", "PostgreSQL 9.4.5", "postgres:9.4.5", 5432, 0.2f, 500f));
        resourceMap.put("oraclexe11g", new Resource("oraclexe11g", "Oracle XE 11g", "wnameless/oracle-xe-11g", 1521, 0.2f, 1000f));
        resourceMap.put("mongodb3", new Resource("mongodb3", "MongoDB 3.0", "mongo:3.0", 27017, 0.2f, 500f));
        resourceMap.put("redis3", new Resource("redis3", "Redis 3.0.5", "redis:3.0.5", 6379, 0.2f, 500f));
        resourceMap.put("sftp", new Resource("sftp", "SFTP", "luzifer/sftp-share", 22, 0.1f, 200f));
        resourceMap.put("ftp", new Resource("ftp", "FTP", "mcreations/ftp", 21, 0.1f, 200f));
    }

    public static Set<String> keys() {
        return resourceMap.keySet();
    }

    public static Resource get(String key) {
        return resourceMap.get(key);
    }

    public static class Resource {
        private String id;
        private String name;
        private String image;
        private int port;
        private float cpus;
        private float mem;
        private Map<String, String> env;

        public Resource(String appId, String appName, String appImage, int port, float cpus, float mem) {
            this(appId, appName, appImage, port, cpus, mem, null);
        }
        public Resource(String appId, String appName, String appImage, int port, float cpus, float mem, Map<String, String> env) {
            this.id = appId;
            this.name = appName;
            this.image = appImage;
            this.port = port;
            this.cpus = cpus;
            this.mem = mem;
            this.env = env;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public int getPort() {
            return port;
        }

        public float getCpus() {
            return cpus;
        }

        public float getMem() {
            return mem;
        }

        public Map<String, String> getEnv() {
            return env;
        }

        public String getHostPropertyKey() {
            return "BELUGA_" + id.toUpperCase() + "_HOST";
        }

        public String getPortPropertyKey() {
            return "BELUGA_" + id.toUpperCase() + "_PORT";
        }
    }
}
