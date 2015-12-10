package org.opencloudengine.garuda.beluga.cloud.watcher;

import org.junit.Test;
import org.opencloudengine.garuda.beluga.env.Environment;
import org.opencloudengine.garuda.beluga.env.SettingManager;
import org.opencloudengine.garuda.beluga.utils.JsonUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 12. 10..
 */
public class AutoScaleRuleFileTest {

    @Test
    public void save() {
        try {
            AutoScaleRule rule = new AutoScaleRule();
            rule.setInUse(true);
            rule.setScaleOutWorkLoad(70);
            rule.setScaleOutTimeInMin(5);
            rule.setScaleInWorkLoad(30);
            rule.setScaleInTimeInMin(3);
            Map<String, AutoScaleRule> ruleMap = new HashMap<>();
            ruleMap.put("abc", rule);
            ruleMap.put("mysql", rule);
            String autoScaleRuleString = JsonUtil.object2String(ruleMap);
            System.out.println(autoScaleRuleString);

            String clusterId = "good";
            Environment env = new Environment("/tmp");
            new SettingManager(env).asSingleton();
            SettingManager.getInstance().storeAutoScaleRule(clusterId, autoScaleRuleString);
        } catch (IOException e) {
            e.printStackTrace();;
        }
    }
}
