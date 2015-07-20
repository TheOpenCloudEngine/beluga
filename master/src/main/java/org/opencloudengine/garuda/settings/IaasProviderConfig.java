package org.opencloudengine.garuda.settings;

import org.opencloudengine.garuda.cloud.IaasProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 20..
 */
public class IaasProviderConfig extends PropertyConfig {


    private Map<String, IaasProvider> providerMap;

    public IaasProviderConfig(Properties p) {
        super(p);
    }

    @Override
    protected void init(Properties p) {

        providerMap = new HashMap<String, IaasProvider>();
        String providers = p.getProperty("providers");

        if(providers != null) {
            String[] els = providers.split(",");

            for(String provider : els) {
                String type = getAttribute(p, provider, "type");
                String name = getAttribute(p, provider, "name");
                String accessKey = getAttribute(p, provider, "accessKey");
                String credentialKey = getAttribute(p, provider, "credentialKey");
                IaasProvider iaasProvider = new IaasProvider(type, name, accessKey, credentialKey);
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
