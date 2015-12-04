package org.opencloudengine.garuda.beluga.cloud.openstack.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.features.UserApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.junit.Test;

import java.io.IOException;
import java.util.Set;

/**
 * Created by swsong on 2015. 12. 4..
 */
public class NovaAPITest {

    @Test
    public void test1() throws IOException {
        String endpoint = "http://10.0.1.251:5000/v2.0";
        String tenantName = "demo";
        String userName = "demo";
        String identity = tenantName + ":"  + userName;
        String password = "demopass";
        String provider = "openstack-nova";
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        NovaApi novaApi = ContextBuilder.newBuilder(provider)
                .endpoint(endpoint)
                .credentials(identity, password)
                .modules(modules)
                .buildApi(NovaApi.class);

        Set<String> regions = novaApi.getConfiguredRegions();

        for (String region : regions) {
            ServerApi serverApi = novaApi.getServerApi(region);

            System.out.println("Servers in " + region);

            for (Server server : serverApi.listInDetail().concat()) {
                System.out.println("  " + server);
            }
        }

        Closeables.close(novaApi, true);
    }
}
