package org.opencloudengine.garuda.beluga.cloud;

import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class OpenstackIaas implements Iaas {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private Properties overrides;

    private Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

    public OpenstackIaas(String endpoint, String accessKey, String secretKey, Properties overrides) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.overrides = overrides;
    }

    @Override
    public List<CommonInstance> launchInstance(InstanceRequest request, String name, int scale, int startIndex) {
        String clusterId = request.getClusterId();
        String region = "RegionOne";
        //String region = request.getRegion();
        ServerApi serverApi = openNovaApi().getServerApi(region);
        String imageRef = request.getImageId(); //"44c83f44-f95d-41f1-9d14-06639de892fa";//ubuntu14_04_3
        String flavorRef = request.getInstanceType(); //3 = m1.medium
        String[] securityGroups = request.getGroups().toArray(new String[0]);
        String keyPair = request.getKeyPair();
        String networks = "ff2e5579-2cfa-4a67-832f-4fc2a6085de9";//demo-net
//      Set<String> netwoks = request.getNetworks();
        CreateServerOptions options = new CreateServerOptions().securityGroupNames(securityGroups).keyPairName(keyPair).networks(networks);
        int index = startIndex;
        for(int i = 0; i < scale; i++, index++){
            String tagName = null;
            if (index > 1) {
                tagName = String.format("%s/%s-%d", clusterId, name, startIndex);
            } else {
                tagName = String.format("%s/%s", clusterId, name);
            }
            ServerCreated serverCreated = serverApi.create(tagName, imageRef, flavorRef, options);
            System.out.println(serverCreated);
        }


        return null;
    }

    @Override
    public void terminateInstance(String id) {

    }

    @Override
    public void updateInstancesInfo(List<CommonInstance> instanceList) {

    }

    @Override
    public List<CommonInstance> getInstances(Collection<String> instanceList) {
        return null;
    }

    @Override
    public void waitUntilInstancesRunning(Collection<CommonInstance> instanceList) {

    }

    @Override
    public void waitUntilInstancesStopped(Collection<CommonInstance> instanceList) {

    }

    @Override
    public void terminateInstances(Collection<String> instanceIdList) {

    }

    @Override
    public void stopInstances(Collection<String> instanceIdList) {

    }

    @Override
    public void startInstances(Collection<String> instanceIdList) {

    }

    @Override
    public void rebootInstances(Collection<String> instanceIdList) {

    }

    @Override
    public String provider() {
        return IaasProvider.OPENSTACK_TYPE;
    }

    @Override
    public void close() {

    }

    public NovaApi openNovaApi(){
        return ContextBuilder.newBuilder("openstack-nova")
                .endpoint(endpoint)
                .overrides(overrides)
                .credentials(accessKey, secretKey)
                .modules(modules)
                .buildApi(NovaApi.class);
    }

    public NeutronApi openNeutronApi(){
        return ContextBuilder.newBuilder("openstack-neutron")
                .endpoint(endpoint)
                .overrides(overrides)
                .credentials(accessKey, secretKey)
                .modules(modules)
                .buildApi(NeutronApi.class);
    }

    public KeystoneApi openKeystoneApi(){
        return ContextBuilder.newBuilder("openstack-keystone")
                .endpoint(endpoint)
                .overrides(overrides)
                .credentials(accessKey, secretKey)
                .modules(modules)
                .buildApi(KeystoneApi.class);
    }

    public CinderApi openCinderApi(){
        return  ContextBuilder.newBuilder("openstack-cinder")
                .endpoint(endpoint)
                .overrides(overrides)
                .credentials(accessKey, secretKey)
                .modules(modules)
                .buildApi(CinderApi.class);
    }

    public void closeApi(Closeable api) {
        try {
            Closeables.close(api, true);
        } catch (IOException e) {
            //
        }
    }
}
