package org.opencloudengine.garuda.beluga.cloud.openstack.api;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.junit.Test;

/**
 * Created by swsong on 2015. 12. 3..
 */
public class CinderAPITest {

    @Test
    public void test1() {
        String endpoint = "http://10.0.1.251:5000/v2.0";
        String tenantName = "demo";
        String userName = "demo";
        String identity = tenantName + ":"  + userName;
        String password = "demopass";
        String provider = "openstack-cinder";//"openstack-keystone";
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        CinderApi cinderApi = ContextBuilder.newBuilder(provider)
                .endpoint(endpoint)
                .credentials(identity, password)
                .modules(modules)
                .buildApi(CinderApi.class);

        for(String region : cinderApi.getConfiguredRegions()) {
            System.out.println(region);
        }
    }

}
