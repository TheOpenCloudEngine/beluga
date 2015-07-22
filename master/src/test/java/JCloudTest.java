//import com.google.common.collect.ImmutableSet;
//import com.google.common.collect.Iterables;
//import com.google.inject.Module;
//import com.sun.org.apache.xpath.internal.NodeSet;
//import org.jclouds.ContextBuilder;
//import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
//import org.jclouds.compute.ComputeService;
//import org.jclouds.compute.ComputeServiceContext;
//import org.jclouds.compute.RunNodesException;
//import org.jclouds.compute.domain.*;
//import org.jclouds.domain.Location;
//import org.jclouds.logging.log4j.config.Log4JLoggingModule;
//import org.jclouds.rest.config.SetCaller;
//import org.jclouds.sshj.config.SshjSshClientModule;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//import java.util.Set;
//
///**
//* Created by swsong on 2015. 7. 10..
//*/
//public class JCloudTest {
//
////    @Test
//    public void testAWS() throws IOException, RunNodesException {
//// get a context with ec2 that offers the portable ComputeService API
//        Properties props = new Properties();
//        InputStream is = this.getClass().getClassLoader().getResourceAsStream("aws-credencial.properties");
//        props.load(is);
//        is.close();
//
//        ComputeServiceContext context = ContextBuilder.newBuilder("aws-ec2")
//                .credentials(props.getProperty("accesskeyid"), props.getProperty("secretkey"))
//                .modules(ImmutableSet.<Module>of(new Log4JLoggingModule(),
//                        new SshjSshClientModule()))
//                .buildView(ComputeServiceContext.class);
//
//        ComputeService client = context.getComputeService();
//
//        for (ComputeMetadata node : client.listNodes()) {
//            node.getId(); // how does jclouds address this in a global scope
//            node.getProviderId(); // how does the provider api address this in a specific scope
//            node.getName(); // if the node is named, what is it?
//            node.getLocation(); // where in the world is the node
//        }
//
//        String group = "group1";
//        NodeSet nodes = client.createNodesInGroup(group, 2, template);
//
//
//
//        // here's an example of the portable api
//        Set<? extends Location> locations =
//                context.getComputeService().listAssignableLocations();
//
//        Set<? extends Image> images = context.getComputeService().listImages();
//
//        // pick the highest version of the RightScale CentOS template
////        Template template = context.getComputeService().templateBuilder().osFamily(OsFamily.UBUNTU).build();
//        Template template = client.templateBuilder()
//                .locationId()
//                .osDescriptionMatches(".*Ubuntu 12.04.*")
//                .hardwareId(regionAndId.slashEncode())
//                .build();
//
//        // specify your own groups which already have the correct rules applied
//        String group1 = "default";
//        template.getOptions().as(AWSEC2TemplateOptions.class).securityGroups(group1);
//
//        // specify your own keypair for use in creating nodes
//        String keyPair = "aws-garuda";
//        template.getOptions().as(AWSEC2TemplateOptions.class).keyPair(keyPair);
//
//        // run a couple nodes accessible via group
//        Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("webserver", 2, template);
//
//        // when you need access to very ec2-specific features, use the provider-specific context
//        AWSEC2Client ec2Client = AWSEC2Client.class.cast(context.getProviderSpecificContext().getApi());
//
//        // ex. to get an ip and associate it with a node
//        NodeMetadata node = Iterables.get(nodes, 0);
//        String ip = ec2Client.getElasticIPAddressServices().allocateAddressInRegion(node.getLocation().getId());
//        ec2Client.getElasticIPAddressServices().associateAddressInRegion(node.getLocation().getId(), ip, node.getProviderId());
//
//        context.close();
//    }
//}
