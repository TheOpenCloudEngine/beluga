package org.opencloudengine.garuda.beluga.cloud.openstack.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.keystone.v2_0.domain.Role;
import org.jclouds.openstack.keystone.v2_0.features.UserApi;
import org.junit.Test;

/**
 * Created by swsong on 2015. 12. 3..
 */
public class KeystoneApiTest {

    @Test
    public void test1() {
        String endpoint = "http://10.0.1.251:35357/v2.0";
        String tenantName = "admin";
        String userName = "admin";
        String identity = tenantName + ":"  + userName;
        String password = "adminpass";
        String provider = "openstack-keystone";
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        KeystoneApi keystoneApi = ContextBuilder.newBuilder(provider)
                .endpoint(endpoint)
                .credentials(identity, password)
                .modules(modules)
                .buildApi(KeystoneApi.class);

        System.out.println(keystoneApi.getApiMetadata());

        Optional<? extends UserApi> userApiExtension = keystoneApi.getUserApi();
        UserApi userApi = userApiExtension.get();
        for(Role role : userApi.listRolesOfUser("user")) {
            System.out.println(role);
        }
    }
}
