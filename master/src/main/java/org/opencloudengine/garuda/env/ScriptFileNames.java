package org.opencloudengine.garuda.env;

import java.io.File;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class ScriptFileNames {

    public static final String SCRIPT_FILE_ROOT = "script/";
    public static final String CONFIGURE_MESOS_MASTER = "cluster/configure_master.sh";
    public static final String CONFIGURE_MESOS_SLAVE = "cluster/configure_slave.sh";

    public static final String MERGE_JAVA_WILDFLY_IMAGE = "webapp/merge_java_wildfly_image.sh";
    public static final String MERGE_PHP_APACHE_IMAGE = "webapp/merge_php_apache_image.sh";

    public static String getAbsolutePath(Environment env, String path) {
        return env.filePaths().makeRelativePath(SCRIPT_FILE_ROOT + path).file().getAbsolutePath();
    }

    public static File getFile(Environment env, String path) {
        return env.filePaths().makeRelativePath(SCRIPT_FILE_ROOT + path).file();
    }
}
