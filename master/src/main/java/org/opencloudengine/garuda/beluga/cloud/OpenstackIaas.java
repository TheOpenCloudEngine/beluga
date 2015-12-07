package org.opencloudengine.garuda.beluga.cloud;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.keystone.v2_0.KeystoneApi;
import org.jclouds.openstack.neutron.v2.NeutronApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.RebootType;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

/**
 * Created by swsong on 2015. 8. 4..
 */
public class OpenstackIaas implements Iaas {

    private static final Logger logger = LoggerFactory.getLogger(OpenstackIaas.class);

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private Properties overrides;

    private Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

    public OpenstackIaas(String endpoint, String accessKey, String secretKey, Properties overrides) {
        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        if(overrides != null) {
            this.overrides = overrides;
        } else {
            this.overrides = new Properties();
        }
    }

    @Override
    public List<CommonInstance> launchInstance(InstanceRequest request, String name, int scale, int startIndex) {
        List<String> ids = new ArrayList<>();
        String clusterId = request.getClusterId();
        String imageRef = request.getImageId();
        String flavorRef = request.getInstanceType();
        String[] securityGroups = request.getGroups().toArray(new String[0]);
        String keyPair = request.getKeyPair();
        String[] networks = request.getNetworks();
        CreateServerOptions options = new CreateServerOptions().securityGroupNames(securityGroups).keyPairName(keyPair).networks(networks);

        NovaApi novaApi = openNovaApi();
        String region = novaApi.getConfiguredRegions().iterator().next();
        ServerApi serverApi = novaApi.getServerApi(region);
        int index = startIndex;
        for(int i = 0; i < scale; i++, index++){
            String tagName = null;
            if (index > 1) {
                tagName = String.format("%s/%s-%d", clusterId, name, startIndex);
            } else {
                tagName = String.format("%s/%s", clusterId, name);
            }
            ServerCreated serverCreated = serverApi.create(tagName, imageRef, flavorRef, options);
            logger.debug("{}", serverCreated);
            ids.add(serverCreated.getId());

        }

        /*
        * Active 상태가 될때까지 대기한다
        * */
        List<CommonInstance> newInstances = new ArrayList<>();
        for(String instanceId : ids) {

            boolean isFail = false;
            Server.Status status = Server.Status.BUILD;
            Server server = null;
            while (status != Server.Status.ACTIVE) {
                server = serverApi.get(instanceId);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //ignore
                }
                status = server.getStatus();
                if (status.ordinal() > Server.Status.BUILD.ordinal()) {
                    //문제발생.
                    isFail = true;
                    break;
                }
            }

            if (isFail) {
                logger.error("Server launch Failed! :-( >> {}", server);
            } else {
                CommonInstance instance = new CommonInstance(server);
                logger.info("Server is Active now! :-) >> {}", instance);
                newInstances.add(instance);
            }
        }

        closeApi(novaApi);

        return newInstances;
    }

    @Override
    public void terminateInstance(String id) {

    }

    @Override
    public void updateInstancesInfo(List<CommonInstance> instanceList) {
        NovaApi novaApi = openNovaApi();
        String region = novaApi.getConfiguredRegions().iterator().next();
        ServerApi serverApi = novaApi.getServerApi(region);
        for(CommonInstance instance : instanceList) {

            String instanceId = instance.getInstanceId();
            Server server = serverApi.get(instanceId);
            if(server.getStatus() != Server.Status.ACTIVE) {
                //TODO
            }
            instance.update(server);
        }
        closeApi(novaApi);
    }

    @Override
    public List<CommonInstance> getInstances(Collection<String> instanceList) {
        List<CommonInstance> newInstances = new ArrayList<>();
        NovaApi novaApi = openNovaApi();
        String region = novaApi.getConfiguredRegions().iterator().next();
        ServerApi serverApi = novaApi.getServerApi(region);
        for(String instanceId : instanceList) {
            Server server = serverApi.get(instanceId);
            if(server.getStatus() != Server.Status.ACTIVE) {
                //TODO
            }
            newInstances.add(new CommonInstance(server));
        }
        closeApi(novaApi);
        return newInstances;
    }

    @Override
    public void waitUntilInstancesRunning(Collection<CommonInstance> instanceList) {
        waitUntilInstancesState(instanceList, Server.Status.ACTIVE);
    }

    @Override
    public void waitUntilInstancesStopped(Collection<CommonInstance> instanceList) {
        waitUntilInstancesState(instanceList, Server.Status.SHUTOFF);
    }

    private void waitUntilInstancesState(Collection<CommonInstance> instanceList, Server.Status stateCode) {
        int size = instanceList.size();
        BitSet statusSet = new BitSet(size);

        NovaApi novaApi = openNovaApi();
        String region = novaApi.getConfiguredRegions().iterator().next();
        ServerApi serverApi = novaApi.getServerApi(region);

        while(true) {

            int i = 0;
            for(CommonInstance instance : instanceList) {
                Server server = serverApi.get(instance.getInstanceId());
                Server.Status status = server.getStatus();
                logger.debug("Check Instance status [{}] {}/{}", instance.getInstanceId(), instance.getName(), status);
                //완료되지 않았고, 실행중으로 변했다면.
                if (status == stateCode) {
                    statusSet.set(i);
                }
                i++;
            }

            if(statusSet.cardinality() < size) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                }
            } else {
                break;
            }
        }

        closeApi(novaApi);

    }

    @Override
    public void terminateInstances(Collection<String> instanceIdList) {
        NovaApi novaApi = openNovaApi();
        String region = novaApi.getConfiguredRegions().iterator().next();
        ServerApi serverApi = novaApi.getServerApi(region);
        for(String instanceId : instanceIdList) {
            serverApi.delete(instanceId);
        }
        closeApi(novaApi);
    }

    @Override
    public void stopInstances(Collection<String> instanceIdList) {
        NovaApi novaApi = openNovaApi();
        String region = novaApi.getConfiguredRegions().iterator().next();
        ServerApi serverApi = novaApi.getServerApi(region);
        for(String instanceId : instanceIdList) {
            serverApi.stop(instanceId);
        }
        closeApi(novaApi);
    }

    @Override
    public void startInstances(Collection<String> instanceIdList) {
        NovaApi novaApi = openNovaApi();
        String region = novaApi.getConfiguredRegions().iterator().next();
        ServerApi serverApi = novaApi.getServerApi(region);
        for(String instanceId : instanceIdList) {
            serverApi.start(instanceId);
        }
        closeApi(novaApi);
    }

    @Override
    public void rebootInstances(Collection<String> instanceIdList) {
        NovaApi novaApi = openNovaApi();
        String region = novaApi.getConfiguredRegions().iterator().next();
        ServerApi serverApi = novaApi.getServerApi(region);
        for(String instanceId : instanceIdList) {
            serverApi.reboot(instanceId, RebootType.HARD);
        }
        closeApi(novaApi);
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
