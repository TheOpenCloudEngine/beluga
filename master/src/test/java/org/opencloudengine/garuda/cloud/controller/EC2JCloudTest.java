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
import java.util.ArrayList;
import java.util.List;
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
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.javax.annotation.Nullable;
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

    public EC2JCloudTest(String accessKey, String secretKey) {
        init(accessKey, secretKey);
    }

    private void init(String accessKey, String secretKey) {
        Properties overrides = new Properties();
        context = ContextBuilder.newBuilder("aws-ec2")
                .credentials(accessKey, secretKey)
                .overrides(overrides)
                .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(),
                        new SshjSshClientModule()))
                .buildView(ComputeServiceContext.class);
        compute = context.getComputeService();
    }

    public void initTemplate(String instanceType, String imageId, int volumeSize, String group, String keyPair) {
        TemplateBuilder templateBuilder = compute.templateBuilder();
        templateBuilder.hardwareId(instanceType); //InstanceType.T2_MICRO
        templateBuilder.imageId(imageId); //"ap-northeast-1/ami-936d9d93"

        template = templateBuilder.build();
        // specify your own groups which already have the correct rules applied
        template.getOptions().as(AWSEC2TemplateOptions.class).securityGroups(group).keyPair(keyPair);
        template.getOptions().as(AWSEC2TemplateOptions.class).mapNewVolumeToDeviceName("/dev/sda1", volumeSize, true);
    }

    public Set<? extends NodeMetadata> launchInstance(String prefixId, int scale) throws RunNodesException {
        return compute.createNodesInGroup(prefixId, scale, template);
    }

    public void close() {
        context.close();
    }

    public static void main(String[] args) throws RunNodesException {
        String accessKey = args[0];
        String secretKey = args[1];

        EC2JCloudTest test = new EC2JCloudTest(accessKey, secretKey);
        test.initTemplate(InstanceType.T2_MICRO, "ap-northeast-1/ami-936d9d93", 10, "default", "aws-garuda");
        test.launchInstance("test", 1);
        test.close();
    }

}