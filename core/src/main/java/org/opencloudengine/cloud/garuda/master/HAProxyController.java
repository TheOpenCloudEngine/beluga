package org.opencloudengine.cloud.garuda.master;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;
import java.util.*;

public class HAProxyController extends AbstractLoadBalancerController {

    private String executablePath;
    private String configuration;
    private int listeningPort = 80;

    public HAProxyController(String executablePath, int listeningPort) {
        this.executablePath = executablePath;
        if (!this.executablePath.endsWith("haproxy")) {
            if (!this.executablePath.endsWith("/")) {
                this.executablePath += "/";
            }
            this.executablePath += "haproxy";
        }

        // Kill all running haproxy processes
        // TODO: this probably isn't good
        try {
            String command = "killall haproxy";
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set listening port
        this.listeningPort = listeningPort;
    }
    @Override
    public void generateConfiguration(Vector<Backend> backends)
            throws Exception {

        // Disable logs (Velocity will cause errors if this isn't done)
        Properties properties = new Properties();
        properties.setProperty("log4j.threshold", "WARN");
        org.apache.log4j.PropertyConfigurator.configure(properties);

        // TODO: ports should be variable.
        int provisionerPort = 8080;

        // Create the strings that are to be inserted

        String newConfiguration = "";
        String backendsString = "";
        String aclString = "";
        String usageString = "";

        // Make a list to work with
        HashMap<String, Vector<Backend>> appsBackends = new HashMap<String, Vector<Backend>>();
        for (Backend backend : backends) {
            for (String appName : backend.getAvailibleAppNames()) {
                if (!appsBackends.containsKey(appName)) {
                    Vector<Backend> b = new Vector<Backend>();
                    b.add(backend);
                    appsBackends.put(appName, b);
                } else {
                    Vector<Backend> b = appsBackends.get(appName);
                    b.add(backend);
                }
            }
        }
        // Create groups
        Set<Vector<Backend>> backendGroups = new HashSet<Vector<Backend>>();
        for (Vector<Backend> b : appsBackends.values()) {
            backendGroups.add(b);
        }
        for (Vector<Backend> b : backendGroups) {
            String backendName = "\tbackend ";
            String servers = "";
            for (Backend backend : b) {
                backendName += backend.getInstanceId();
                servers += "\t\tserver " + backend.getInstanceId() + " "
                        + backend.getPublicDnsName() + ":"
                        + backend.getAppPort() + "\n";
            }
            backendsString += backendName + "\n";
            backendsString += servers + "\n";
        }

        // Create acl's
        for (String appName : appsBackends.keySet()) {
            aclString += "\tacl " + "acl_" + appName + " path_beg " + "/" + appName + "\n";
            aclString += "\tacl " + "acl_docs_" + appName + " path_beg " + "/docs/" + appName + "\n";
        }

        // Create usage strings
        for (String appName : appsBackends.keySet()) {
            String backendName = "";
            for (Backend backend : appsBackends.get(appName)) {
                backendName += backend.getInstanceId();
            }
            usageString += "\tuse_backend " + backendName + " if " + "acl_" + appName + "\n";
            usageString += "\tuse_backend " + backendName + " if " + "acl_docs_" + appName + "\n";
        }

        // Start the VelocityEngine
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class",ClasspathResourceLoader.class.getName());
        ve.init();

        // Open the template
        Template t = ve.getTemplate("org/garuda/garudamaster/haproxy.conf_template", "UTF-8");

        // Insert the strings into the template
        VelocityContext context = new VelocityContext();
        context.put("listen_port", listeningPort);
        context.put("arvue_port", provisionerPort);
        context.put("backends", backendsString);
        context.put("backenduse", usageString);
        context.put("acl", aclString);

        // Create a new string from the template
        StringWriter stringWriter = new StringWriter();
        t.merge(context, stringWriter);
        configuration = stringWriter.toString();

        // Write configuration to file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("/tmp/haproxy.conf"));
            writer.write(configuration);
            writer.close();

            //System.out.println("wrote configuration to file");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void reconfigure() throws Exception {

        // Read pid
        String pid = "";
        BufferedReader reader = new BufferedReader(new FileReader(
                "/tmp/haproxy.pid"));
        String line;
        while ((line = reader.readLine()) != null) {
            pid += line + " ";
        }

        // Perform hot reconfiguration of haproxy
        BufferedReader input = null;
        try {
        	String command = executablePath
        			+ " -f /tmp/haproxy.conf -p /tmp/haproxy.pid -sf " + pid;
        	Runtime r = Runtime.getRuntime();
        	Process p = r.exec(command);
        	InputStream in = p.getInputStream();
        	input = new BufferedReader(new InputStreamReader(p .getInputStream()));
        	// while ((line = input.readLine()) != null) {
        	// System.out.println(line);
        	// }
        	//System.out.println("reconfigured haproxy");
        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println(input);
        	// TODO: something
        }

        // TODO: Check if it the reconfiguration actually happened and throw
        // error if necessary
    }

    @Override
    public void startLoadBalancer() throws Exception {

        // Check for configuration file
        File conf;
        conf = new File("/tmp/haproxy.conf");
        if (!conf.exists()) {
            System.err.println("Could not find haproxy configuration file!");
            throw new Exception();
        }

        // Start haproxy and write pid to /tmp/haproxy.pid
        try {
            String command = executablePath
                    + " -f /tmp/haproxy.conf -p /tmp/haproxy.pid";
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(command);
            System.out.println("started haproxy");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stopLoadBalancer() throws Exception {

        // Read the PID's
        Vector<String> pids = new Vector<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                    "/tmp/haproxy.pid"));
            String pid = null;
            while ((pid = reader.readLine()) != null) {
                pids.add(pid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Kill all haproxy processes
        for (String pid : pids) {
            try {
                String command = "kill -s 9" + pid;
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(command);
                System.out.println("stopped haproxy");
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
