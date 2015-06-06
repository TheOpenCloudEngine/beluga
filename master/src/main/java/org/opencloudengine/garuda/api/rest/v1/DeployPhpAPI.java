package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.common.util.CommandUtils;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Container;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.CreateApp;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Docker;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.PortMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * File Upload API
 *
 * @author Sang Wook, Song
 */
@Path("/v1/deploy/php")
public class DeployPhpAPI {

    private static final String SERVER_UPLOAD_LOCATION_FOLDER = "/tmp";
    private static final String REGISTER_ADDR = "128.199.80.104:5000/";
    private static final String MARATHON_ADDR = "http://10.132.37.106:8080";

    /**
     * Upload a File
     */
    @POST
    @Path("/upload")
    public String uploadFile(
            @FormDataParam("inputStream") InputStream fileInputStream,
            @FormDataParam("formDataContentDisposition") FormDataContentDisposition contentDispositionHeader,
            @FormParam("int") int instanceSize, @FormParam("int") int port, @FormParam("string") String userId) throws IOException {

        String filePath = SERVER_UPLOAD_LOCATION_FOLDER + contentDispositionHeader.getFileName();
        String baseImage = "ubuntu14.04x64_php5_apachex";
        String newImage = userId + "_" + filePath + "_php";

        // save the file to the server
        saveFile(fileInputStream, filePath);

        String output = "File saved to server location : " + filePath;

        //TODO 1 deploy_php_apache.sh aaa.zip <userId>_<appFile name>_php <registry addr> "php5_apache2"
        String command = String.format("sh production/resources/script/deploy_php_apache.sh %s %s %s %s", filePath, newImage, REGISTER_ADDR, baseImage);
        CommandUtils.executeCommand(command);

        //TODO 2. 짠 코드. 마라톤 디플로이.
        try {
            this.deployApp(instanceSize, port, newImage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toString()    ;

    }

    public void deployApp(int instanceSize, int port, String newImage) throws InterruptedException, URISyntaxException, IOException {
        CreateApp createApp = this.getDefaultApp(instanceSize, port, newImage);

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(MARATHON_ADDR).path("/v2/apps");

        target.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(createApp));
    }

    private CreateApp getDefaultApp(int instanceSize, int port, String newImage) {
        List<PortMapping> portMappings = new ArrayList<>();
        PortMapping portMapping = new PortMapping();
        portMapping.setContainerPort(port);
        portMapping.setServicePort(83);
        portMappings.add(portMapping);

        Docker docker = new Docker();
        docker.setImage(String.format("%s ", REGISTER_ADDR + newImage));
        docker.setNetwork("BRIDGE");
        docker.setPrivileged(true);
        docker.setPortMappings(portMappings);

        Container container = new Container();
        container.setDocker(docker);
        container.setType("DOCKER");

        CreateApp createApp = new CreateApp();
        createApp.setId(newImage);
        createApp.setContainer(container);
        createApp.setInstances(instanceSize);
        createApp.setCpus(0.5f);
        createApp.setMem(128f);

        return createApp;
    }


    // save uploaded file to a defined location on the server
    private void saveFile(InputStream uploadedInputStream,
                          String serverLocation) {

        try {
            OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            outpuStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outpuStream.write(bytes, 0, read);
            }
            outpuStream.flush();
            outpuStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

}
