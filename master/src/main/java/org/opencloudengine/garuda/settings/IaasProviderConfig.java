package org.opencloudengine.garuda.settings;

import org.opencloudengine.garuda.cloud.IaasProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 20..
 */
public class IaasProviderConfig extends PropertyConfig {

    private Map<String, IaasProvider> providerMap;

    private static final String[] OVERRIDES_PARAMS = {"jclouds.regions"};

    public IaasProviderConfig(Properties p) {
        super(p);
    }

    @Override
    protected void init(Properties p) {

        providerMap = new HashMap<>();
        String types = p.getProperty("iaasTypes");

        if(types != null) {
            String[] els = types.split(",");

            for(String type : els) {
                Properties overrides = new Properties();
                String provider = getAttribute(p, type, "provider");
                String name = getAttribute(p, type, "name");
                String accessKey = getAttribute(p, type, "accessKey");
                String credentialKey = getAttribute(p, type, "credentialKey");
                for(String key : OVERRIDES_PARAMS) {
                    String value = getAttribute(p, type, key);
                    if(value != null && value.trim().length() > 0) {
                        overrides.put(key, value);
                    }
                }
                IaasProvider iaasProvider = new IaasProvider(provider, name, accessKey, credentialKey, overrides);
                logger.debug("{}", iaasProvider);
                providerMap.put(type, iaasProvider);
            }
        }

    }

    public Map<String, IaasProvider> getProviderMap() {
        return providerMap;
    }

    private String getAttribute(Properties p, String provider, String type) {
        return p.getProperty(String.format("%s.%s", provider, type));
    }

}
