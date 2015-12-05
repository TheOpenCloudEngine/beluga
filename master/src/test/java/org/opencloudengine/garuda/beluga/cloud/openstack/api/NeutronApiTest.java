package org.opencloudengine.garuda.beluga.cloud.openstack.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.neutron.v2.domain.FloatingIP;
import org.jclouds.openstack.neutron.v2.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by swsong on 2015. 12. 4..
 */
public class NeutronApiTest {


    String endpoint = "http://10.0.1.251:5000/v2.0";
    String tenantName = "demo";
    String userName = "demo";
    String identity = tenantName + ":"  + userName;
    String password = "demopass";
    String provider = "openstack-neutron";


    @Test
    public void testAssociateFloatingIP() throws IOException {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
        NeutronApi neutronApi = ContextBuilder.newBuilder(provider)
                .endpoint(endpoint)
                .credentials(identity, password)
                .modules(modules)
                .buildApi(NeutronApi.class);

        String region = "RegionOne";
        Optional<FloatingIPApi> floatingIPApiOptional =  neutronApi.getFloatingIPApi(region);
        FloatingIPApi floatingIPApi = floatingIPApiOptional.get();
        for(FloatingIP ip : floatingIPApi.list().concat()) {
            System.out.println(ip);
        }

//        floatingIPApi.

        Closeables.close(neutronApi, true);
    }
}
