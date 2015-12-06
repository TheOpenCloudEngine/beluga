package org.opencloudengine.garuda.beluga.cloud;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 26..
 */
public class IaasSpec {

    private int cpu;
    private int memory;
    private int ephemeralDisk;

    public static final String EC2_TYPE = "ec2";
    public static final String OPENSTACK_TYPE = "openstack";

    private static IaasSpec nullIaasSpec = new IaasSpec(0, 0, 0);
    private static Map<String, Map<String, IaasSpec>> iaasSpecMap = new HashMap<>();
    private static Map<String, IaasSpec> awsMap = new HashMap<>();
    private static Map<String, IaasSpec> openstackMap = new HashMap<>();
    static {
        iaasSpecMap.put(EC2_TYPE, awsMap);
        iaasSpecMap.put(OPENSTACK_TYPE, openstackMap);

        awsMap.put("t2.micro", new IaasSpec(1, 1, 0));
        awsMap.put("t2.small", new IaasSpec(1, 2, 0));
        awsMap.put("t2.medium", new IaasSpec(2, 4, 0));
        awsMap.put("t2.large", new IaasSpec(2, 8, 0));

        openstackMap.put("m1.micro", new IaasSpec(1, 1, 0));
        openstackMap.put("m1.small", new IaasSpec(1, 2, 0));
        openstackMap.put("m1.medium", new IaasSpec(2, 4, 0));
        openstackMap.put("m1.large", new IaasSpec(4, 8, 0));
        openstackMap.put("m1.xlarge", new IaasSpec(8, 16, 0));
    }
    public static IaasSpec getSpec(String iaasType, String instanceType) {
        Map<String, IaasSpec> map = iaasSpecMap.get(iaasType);
        if(map == null) {
            return null;
        }

        // unkown.....
        IaasSpec iaasSpec = map.get(instanceType);

        if(iaasSpec == null) {
            return nullIaasSpec;
        }
        return iaasSpec;
    }

    public IaasSpec(int cpu, int memory, int ephemeralDisk) {
        this.cpu = cpu;
        this.memory = memory;
        this.ephemeralDisk = ephemeralDisk;
    }

    public int getCpu() {
        return cpu;
    }

    public int getMemory() {
        return memory;
    }

    public int getEphemeralDisk() {
        return ephemeralDisk;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " cpu[" + cpu + "] memory[" + memory + " GB] ephemeralDisk[" + ephemeralDisk + " GB]";
    }
}
