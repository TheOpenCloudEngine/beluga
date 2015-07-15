/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opencloudengine.garuda.cloud.controller;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.aws.ec2.reference.AWSEC2Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.*;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Location;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.predicates.InetSocketAddressConnect;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import org.jclouds.sshj.config.SshjSshClientModule;

/**
 * This the Main class of an Application that demonstrates the use of the Amazon EC2 extensions by
 * creating a small spot server.
 *
 * Usage is: java MainApp accesskeyid secretkey group command where command in create destroy
 */
public class EC2JCloudTest {

    ComputeServiceContext context;
    ComputeService compute;
    Template template;

    public EC2JCloudTest(String accessKey, String secretKey, String group) {

        init(accessKey, secretKey);
    }

    public void init(String accessKey, String secretKey) {
        Properties overrides = new Properties();
        context = ContextBuilder.newBuilder("aws-ec2")
                .credentials(accessKey, secretKey)
                .overrides(overrides)
                .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(),
                        new SshjSshClientModule()))
                .buildView(ComputeServiceContext.class);
        compute = context.getComputeService();
    }

    public void initTemplate(String instanceType, String imageId, String group) {
        TemplateBuilder templateBuilder = compute.templateBuilder();
        templateBuilder.hardwareId(instanceType); //InstanceType.T2_MICRO
        templateBuilder.imageId(imageId); //"ap-northeast-1/ami-936d9d93"
        template = templateBuilder.build();
        // specify your own groups which already have the correct rules applied
        template.getOptions().as(AWSEC2TemplateOptions.class).securityGroups(group);
    }

    public Set<? extends NodeMetadata> launchInstance(String prefixId, int scale) throws RunNodesException {
        return compute.createNodesInGroup(prefixId, scale, template);
    }


    public static void main(String[] args) throws RunNodesException {

//        if (args.length < PARAMETERS)
//            throw new IllegalArgumentException(INVALID_SYNTAX);

        // Args
        String accesskeyid = args[0];
        String secretkey = args[1];
        String group = "default";//args[2];

        // Init
        Properties overrides = new Properties();
// choose only amazon images that are ebs-backed
//        overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY,
//                "owner-id=137112412989;state=available;image-type=machine;root-device-type=ebs");
        ComputeServiceContext context = ContextBuilder.newBuilder("aws-ec2")
                .credentials(accesskeyid, secretkey)
                .overrides(overrides)
                .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(),
                        new SshjSshClientModule()))
                .buildView(ComputeServiceContext.class);

        ComputeService compute = context.getComputeService();
//        ComputeService compute = new ComputeServiceContextFactory().createContext("aws-ec2", accesskeyid, secretkey)
//                .getComputeService();




        // here's an example of the portable api
//        Set<? extends Location> locations = compute.listAssignableLocations();

//        Set<? extends Image> images = compute.listImages();
//for(Image i : images) {
//    String loc = i.getLocation().getId();
//    if(loc.startsWith("ap-northeast-1")) {
//
//        System.out.println(i);
//    }
//    String fa = i.getOperatingSystem().getFamily().value();
//    if(fa.contains("ubuntu")) {
//        System.out.println(" UBUNTU >>>>>>>>>" + i);
//    }
//}
// pick the highest version of the RightScale CentOS template
        TemplateBuilder templateBuilder = compute.templateBuilder();
        templateBuilder.hardwareId(InstanceType.T2_MICRO);
//        templateBuilder.osVersionMatches("14.04");
//        templateBuilder.osFamily(OsFamily.UBUNTU);
        templateBuilder.imageId("ap-northeast-1/ami-936d9d93");
//        templateBuilder.locationId("ap-northeast-1");
        Template template = templateBuilder.build();



// specify your own groups which already have the correct rules applied
        template.getOptions().as(AWSEC2TemplateOptions.class).securityGroups(group);

// specify your own keypair for use in creating nodes
//        template.getOptions().as(AWSEC2TemplateOptions.class).keyPair(keyPair);

// run a couple nodes accessible via group
        Set<? extends NodeMetadata> nodes = compute.createNodesInGroup("webserver", 2, template);

// when you need access to very ec2-specific features, use the provider-specific context
//        AWSEC2Client ec2Client = AWSEC2Client.class.cast(context.getProviderSpecificContext().getApi());

// ex. to get an ip and associate it with a node
//        NodeMetadata node = Iterables.get(nodes, 0);
//        String ip = ec2Client.getElasticIPAddressServices().allocateAddressInRegion(node.getLocation().getId());
//        ec2Client.getElasticIPAddressServices().associateAddressInRegion(node.getLocation().getId(), ip, node.getProviderId());

        context.close();



        // wait up to 60 seconds for ssh to be accessible
//        try {
//            if (command.equals("create")) {
//
////                Template template = compute.templateBuilder().build();
//
//                template.getOptions().as(AWSEC2TemplateOptions.class)
//                        // set the price as 3 cents/hr
//                        .spotPrice(0.03f)
//                                // authorize my ssh key
//                        .authorizePublicKey(
//                                Files.toString(new File(System.getProperty("user.home") + "/.ssh/id_rsa.pub"),
//                                        Charsets.UTF_8));
//
//                System.out.printf(">> running one spot node type(%s) with ami(%s) in group(%s)%n", template.getHardware()
//                        .getProviderId(), template.getImage().getId(), group);
//                // run only a single node
//                NodeMetadata node = Iterables.getOnlyElement(compute.createNodesInGroup(group, 1, template));
//
//                System.out.printf("<< running node(%s)%n", node.getId());
//
//            } else if (command.equals("destroy")) {
//                System.out.printf(">> destroying nodes in group(%s)%n", group);
//                Set<? extends NodeMetadata> destroyed = compute.destroyNodesMatching(NodePredicates.inGroup(group));
//                System.out.printf("<< destroyed(%d)%n", destroyed.size());
//                System.exit(0);
//            } else {
//                System.err.println(INVALID_SYNTAX);
//                System.exit(1);
//            }
//        } catch (RunNodesException e) {
//            System.err.println(e.getMessage());
//            for (NodeMetadata node : e.getNodeErrors().keySet())
//                compute.destroyNode(node.getId());
//            System.exit(1);
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//            System.exit(1);
//        } finally {
//            compute.getContext().close();
//        }

    }

}