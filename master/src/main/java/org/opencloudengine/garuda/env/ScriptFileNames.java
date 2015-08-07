package org.opencloudengine.garuda.env;

import java.io.File;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class ScriptFileNames {

    /*
    * script/cluster
    * */
    private static final String CLUSTER_SCRIPT_FILE_ROOT = "script/cluster/";
    public static final String CONFIGURE_MESOS_MASTER = "configure_master.sh";
    public static final String CONFIGURE_MESOS_SLAVE = "configure_slave.sh";

    /*
    * script/webapp
    * */
    private static final String WEBAPP_SCRIPT_FILE_ROOT = "script/webapp/";
    public static final String MERGE_IMAGE_FORMAT = "merge_%s_image.sh";

    public static String getClusterScriptPath(Environment env, String path) {
        return env.filePaths().makeRelativePath(CLUSTER_SCRIPT_FILE_ROOT + path).file().getAbsolutePath();
    }

    public static File getClusterScriptFile(Environment env, String path) {
        return env.filePaths().makeRelativePath(CLUSTER_SCRIPT_FILE_ROOT + path).file();
    }

    public static String getMergeWebAppScriptPath(Environment env, String stackType) {
        String fileName = String.format(MERGE_IMAGE_FORMAT, stackType);
        return env.filePaths().makeRelativePath(WEBAPP_SCRIPT_FILE_ROOT + fileName).file().getAbsolutePath();
    }
}
