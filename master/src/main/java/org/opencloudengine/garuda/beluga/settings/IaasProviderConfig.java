package org.opencloudengine.garuda.beluga.settings;

import org.opencloudengine.garuda.beluga.cloud.IaasProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 20..
 */
public class IaasProviderConfig extends PropertyConfig {

    private Map<String, IaasProvider> profileMap;

    private static final String[] OVERRIDES_PARAMS = {"jclouds.regions"};

    public IaasProviderConfig(Properties p) {
        super(p);
    }

    @Override
    protected void init(Properties p) {

        profileMap = new HashMap<>();
        String profiles = p.getProperty("iaasProfiles");

        if(profiles != null) {
            String[] els = profiles.split(",");

            for(String profile : els) {
                String provider = getAttribute(p, profile, "provider");
                String name = getAttribute(p, profile, "name");
                String accessKey = getAttribute(p, profile, "accessKey");
                String credentialKey = getAttribute(p, profile, "credentialKey");
                String endPoint = getAttribute(p, profile, "endPoint");
                String region = getAttribute(p, profile, "region");
                Properties overrides = new Properties();
                for(String key : OVERRIDES_PARAMS) {
                    String value = getAttribute(p, profile, key);
                    if(value != null && value.trim().length() > 0) {
                        overrides.put(key, value);
                    }
                }
                IaasProvider iaasProvider = new IaasProvider(provider, name, accessKey, credentialKey, endPoint, region, overrides);
                logger.debug("{}", iaasProvider);
                profileMap.put(profile, iaasProvider);
            }
        }

    }

    public Map<String, IaasProvider> getProfileMap() {
        return profileMap;
    }

    public IaasProvider getIaasProvider(String profile) {
        return profileMap.get(profile);
    }

    private String getAttribute(Properties p, String provider, String type) {
        return p.getProperty(String.format("%s.%s", provider, type));
    }

}
