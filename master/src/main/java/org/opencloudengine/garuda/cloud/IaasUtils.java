package org.opencloudengine.garuda.cloud;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swsong on 2015. 8. 5..
 */
public class IaasUtils {
    public static List<String> getIdList(List<CommonInstance> instanceList) {
        List<String> list = new ArrayList<>();
        for(CommonInstance i : instanceList) {
            list.add(i.getInstanceId());
        }
        return list;
    }
}
