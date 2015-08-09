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
    public static final String PUSH_IMAGE = "push_image_to_registry.sh";

    public static String getClusterScriptPath(Environment env, String path) {
        return env.filePaths().makeRelativePath(CLUSTER_SCRIPT_FILE_ROOT + path).file().getAbsolutePath();
    }

    public static File getClusterScriptFile(Environment env, String path) {
        return env.filePaths().makeRelativePath(CLUSTER_SCRIPT_FILE_ROOT + path).file();
    }

    public static String getMergeWebAppImageScriptPath(Environment env, String stackType) {
        return getWebAppScriptPath(env, String.format(MERGE_IMAGE_FORMAT, stackType));
    }

    public static String getPushImageToRegistryScriptPath(Environment env) {
        return getWebAppScriptPath(env, PUSH_IMAGE);
    }

    public static String getWebAppScriptPath(Environment env, String fileName) {
        File f = env.filePaths().makeRelativePath(WEBAPP_SCRIPT_FILE_ROOT + fileName).file();
        if(!f.canExecute()) {
            f.setExecutable(true);
        }
        return f.getAbsolutePath();
    }
}
