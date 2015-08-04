package org.opencloudengine.garuda.settings;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by swsong on 2015. 7. 20..
 */
public class ClusterDefinition extends PropertyConfig {

    private String iaasProfile;
    private String keyPair;
    private String privateKey;
    private List<Group> groupList;
    private List<RoleDefinition> roleList;

    public ClusterDefinition(File f) {
        super(f);
    }

    public ClusterDefinition(Properties p) {
        super(p);
    }

    public String getIaasProfile() {
        return iaasProfile;
    }

    public void setIaasProfile(String iaasProfile) {
        this.iaasProfile = iaasProfile;
    }

    public String getKeyPair() {
        return keyPair;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public List<RoleDefinition> getRoleList() {
        return roleList;
    }

    @Override
    protected void init(Properties p) {

        iaasProfile= p.getProperty("iaasProfile");
        /*
        * KeyPair
        * */
        keyPair = p.getProperty("keyPair");
        String keyPairFilePath = p.getProperty("keyPairFile");
        if(keyPairFilePath != null) {
            File f = new File(keyPairFilePath);
            if(f.exists()) {
                try {
                    privateKey = FileUtils.readFileToString(f, "utf-8");
                } catch (IOException e) {
                    logger.error("error read private key file", e);
                }
            }else {
                logger.error("private key file not exists");
            }
        }
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
            RoleDefinition roleDef = new RoleDefinition(role, imageId, instanceType, diskSize, group, defaultSize);
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
        private int diskSize;
        private String group;
        private int defaultSize;

        public RoleDefinition(String role, String imageId, String instanceType, int diskSize, String group, int defaultSize) {
            this.role = role;
            this.imageId = imageId;
            this.instanceType = instanceType;
            this.diskSize = diskSize;
            this.group = group;
            this.defaultSize = defaultSize;
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
    }
}
