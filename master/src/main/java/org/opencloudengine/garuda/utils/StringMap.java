package org.opencloudengine.garuda.utils;

import java.util.HashMap;

/**
 * Created by swsong on 2015. 8. 8..
 */
public class StringMap extends HashMap<String, String> {

    public StringMap() {
       super();
    }

    public StringMap(int size) {
        super(size);
    }

    public StringMap with(String key, String value) {
        super.put(key, value);
        return this;
    }
}
