package org.opencloudengine.garuda.common;

import org.opencloudengine.garuda.env.Environment;

import java.io.File;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class ScriptFileNames {

    public static final String SCRIPT_FILE_ROOT = "resources/script/";
    public static final String ConfigureMesosMaster = "provision/configure_master.sh";
    public static final String ConfigureMesosSlave = "provision/configure_slave.sh";

    public static String getAbsolutePath(Environment env, String path) {
        return env.filePaths().makeRelativePath(SCRIPT_FILE_ROOT + path).file().getAbsolutePath();
    }

    public static File getFile(Environment env, String path) {
        return env.filePaths().makeRelativePath(SCRIPT_FILE_ROOT + path).file();
    }
}
