package org.opencloudengine.garuda.beluga.settings;

import org.opencloudengine.garuda.beluga.cloud.IaasSpec;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by swsong on 2015. 7. 20..
 */
public class ClusterDefinition extends PropertyConfig {

    private String id;
    private String iaasProfile;
    private int timeout;
    private String userId;
    private String keyPair;
    private String keyPairFile;
    private List<Group> groupList;
    private List<RoleDefinition> roleList;
    private String providerType;

    public ClusterDefinition(String id, File f) {
        super(f);
        this.id = id;
    }

    public ClusterDefinition(String id, Properties p) {
        super(p);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public AccessInfo getAccessInfo() {
        return new AccessInfo(userId, keyPairFile, timeout);
    }

    public String getIaasProfile() {
        return iaasProfile;
    }

    public void setIaasProfile(String iaasProfile) {
        this.iaasProfile = iaasProfile;
    }

    public String getUserId() {
        return userId;
    }

    public String getKeyPair() {
        return keyPair;
    }

    public String getKeyPairFile() {
        return keyPairFile;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public List<RoleDefinition> getRoleList() {
        return roleList;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    @Override
    protected void init(Properties p) {

        iaasProfile= p.getProperty("iaasProfile");

        try {
            timeout = Integer.parseInt(p.getProperty("ssh.timeout"));
        } catch (Exception e) {
            timeout = 60;
        }
        /*
        * KeyPair
        * */
        userId = p.getProperty("userId");
        keyPair = p.getProperty("keyPair");
        keyPairFile = p.getProperty("keyPairFile");
//        if(keyPairFilePath != null) {
//            File f = new File(keyPairFilePath);
//            if(f.exists()) {
//                try {
//                    keyPairFile = FileUtils.readFileToString(f, "utf-8");
//                } catch (IOException e) {
//                    logger.error("error read private key file", e);
//                }
//            }else {
//                logger.error("private key file not exists");
//            }
//        }
        /*
        * Security Group
        * */
        groupList = new ArrayList<Group>();
        String groups = p.getProperty("groups");
        String[] groupsList = groups.split(",");
        for(String name : groupsList) {
            int[] ports = groupInboundPorts(p, name);
            groupList.add(new Group(name, ports));
        }

        /*
        * Role
        * */
        roleList = new ArrayList<RoleDefinition>();
        String roleStr = p.getProperty("roles");
        String[] roles = roleStr.split(",");
        for(int i = 0; i < roles.length; i++) {
            String role = roles[i];
            String imageId = imageId(p, role);
            String instanceType = instanceType(p, role);
            int diskSize = diskSize(p, role);
            String group = groupId(p, role);
            int defaultSize = defaultSize(p, role);
            String[] networks = networks(p, role);
            RoleDefinition roleDef = new RoleDefinition(role, imageId, instanceType, diskSize, group, defaultSize, networks);
            roleList.add(roleDef);
        }

    }

    private int[] groupInboundPorts(Properties p, String name) {
        String key = String.format("group.%s.inboundPorts", name);
        String value = p.getProperty(key);
        if(value != null) {
            String[] vs = value.split(",");
            int[] ports = new int[vs.length];
            for (int i = 0; i < vs.length; i++) {
                String portStr = vs[i];
                ports[i] = Integer.parseInt(portStr);
            }
            return ports;
        }
        return null;
    }

    private String imageId(Properties p, String role) {
        return p.getProperty(String.format("%s.imageId", role));
    }

    private String instanceType(Properties p, String role) {
        return p.getProperty(String.format("%s.instanceType", role));
    }

    private int diskSize(Properties p, String role) {
        String key = String.format("%s.diskSize", role);
        String value = p.getProperty(key);
        return Integer.parseInt(value);
    }

    private String groupId(Properties p, String role) {
        return p.getProperty(String.format("%s.group", role));
    }

    private int defaultSize(Properties p, String role) {
        String key = String.format("%s.defaultSize", role);
        String value = p.getProperty(key);
        return Integer.parseInt(value);
    }

    private String[] networks(Properties p, String role) {
        String key = String.format("%s.networks", role);
        String value = p.getProperty(key);
        if(value != null) {
            return value.split(",");
        }
        return new String[0];
    }

    private String region(Properties p, String role) {
        return p.getProperty(String.format("%s.region", role));
    }


    public static class Group {
        private String name;
        private int[] inboundPortList;

        public Group(String name, int[] inboundPortList) {
            this.inboundPortList = inboundPortList;
        }

        public String getName() {
            return name;
        }

        public int[] getInboundPortList() {
            return inboundPortList;
        }
    }

    public static class RoleDefinition {
        private String role;
        private String imageId;
        private String instanceType;
        private IaasSpec iaasSpec;
        private int diskSize;
        private String group;
        private int defaultSize;
        private String[] networks;

        public RoleDefinition(String role, String imageId, String instanceType, int diskSize, String group
                , int defaultSize, String[] networks) {
            this.role = role;
            this.imageId = imageId;
            this.instanceType = instanceType;
            this.diskSize = diskSize;
            this.group = group;
            this.defaultSize = defaultSize;
            this.networks = networks;
        }

        public String getRole() {
            return role;
        }

        public String getImageId() {
            return imageId;
        }

        public String getInstanceType() {
            return instanceType;
        }

        public int getDiskSize() {
            return diskSize;
        }

        public String getGroup() {
            return group;
        }

        public int getDefaultSize() {
            return defaultSize;
        }

        public IaasSpec getIaasSpec() {
            return iaasSpec;
        }

        public void setIaasSpec(IaasSpec iaasSpec) {
            this.iaasSpec = iaasSpec;
        }

        public String[] getNetworks() {
            return networks;
        }

    }
}
