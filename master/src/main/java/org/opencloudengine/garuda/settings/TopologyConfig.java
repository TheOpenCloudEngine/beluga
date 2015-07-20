package org.opencloudengine.garuda.settings;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 20..
 */
public class TopologyConfig extends PropertyConfig {


    public TopologyConfig(File f) {
        super(f);
    }

    public TopologyConfig(Properties p) {
        super(p);
    }

    @Override
    protected void init(Properties p) {


    }

}
