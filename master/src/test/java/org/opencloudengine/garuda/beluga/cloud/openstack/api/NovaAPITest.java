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
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.junit.Test;
import org.opencloudengine.garuda.beluga.cloud.CommonInstance;

import java.io.IOException;
import java.util.Set;

/**
 * Created by swsong on 2015. 12. 4..
 */
public class NovaApiTest {
    String endpoint = "http://10.0.1.251:5000/v2.0";
    String tenantName = "demo";
    String userName = "demo";
    String identity = tenantName + ":"  + userName;
    String password = "demopass";
    String provider = "openstack-nova";

    @Test
    public void testList() throws IOException {

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

    @Test
    public void testLaunch() throws IOException {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        NovaApi novaApi = ContextBuilder.newBuilder(provider)
                .endpoint(endpoint)
                .credentials(identity, password)
                .modules(modules)
                .buildApi(NovaApi.class);

        String region = novaApi.getConfiguredRegions().iterator().next();
        ServerApi serverApi = novaApi.getServerApi(region);
        String serverName = "test-" + System.currentTimeMillis();
        String imageRef = "44c83f44-f95d-41f1-9d14-06639de892fa";//ubuntu14_04_3
        String flavorRef = "2";
        String securityGroup = "default";
        String keyPair = "beluga-openstack";
        String network = "ff2e5579-2cfa-4a67-832f-4fc2a6085de9";//demo-net
        CreateServerOptions options = new CreateServerOptions().securityGroupNames(securityGroup).keyPairName(keyPair).networks(network);
        ServerCreated serverCreated = serverApi.create(serverName, imageRef, flavorRef, options);
        System.out.println("### " + serverCreated.getId() + " / " + serverCreated.getName());

        String serverId = serverCreated.getId();

        boolean isFail = false;
        Server.Status status = Server.Status.BUILD;
        Server server = null;
        while(status != Server.Status.ACTIVE) {
            server = serverApi.get(serverId);
            System.out.println(server.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            status = server.getStatus();
            System.out.println("### status : " + status + ", " + status.ordinal());
            if(status.ordinal() > Server.Status.BUILD.ordinal() ) {
                //문제발생.
                isFail = true;
                break;
            }
        }

        if(isFail) {
            System.out.println("Server launch Failed! :-(");
        } else {
            CommonInstance instance = new CommonInstance(server);
            System.out.println("Server is Active now! :-)");
            System.out.println(instance);
        }

        Closeables.close(novaApi, true);
    }


    @Test
    public void testRegion() throws IOException {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        NovaApi novaApi = ContextBuilder.newBuilder(provider)
                .endpoint(endpoint)
                .credentials(identity, password)
                .modules(modules)
                .buildApi(NovaApi.class);

        System.out.println(novaApi.getConfiguredRegions());
    }

    }
