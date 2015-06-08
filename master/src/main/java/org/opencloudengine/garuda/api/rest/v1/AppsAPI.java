package org.opencloudengine.garuda.api.rest.v1;

import org.apache.commons.io.FileUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.common.util.CommandUtils;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Container;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.CreateApp;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.Docker;
import org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req.PortMapping;
import org.opencloudengine.garuda.env.Environment;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.env.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Deploy App API
 *
 * @author Sang Wook, Song
 */
@Path("/v1/apps")
public class AppsAPI {

	private static Logger logger = LoggerFactory.getLogger(AppsAPI.class);

	private static final String UPLOAD_LOCATION_DIR = "/tmp/";
    private static final String REGISTER_ADDR = "128.199.80.104:5000/";
    private static final String MARATHON_ADDR = "http://10.132.37.106:8080";

	/**
	 * Create apps.
	 * Docker 이미지를 만들어서 Registry에 등록한다.
	 *
	 * <p/>
	 * POST /v1/apps
	 * <p/>
	 * 결과 :
	 * {
	 * "appId": "...",
	 * "status" : "complete",
	 * "type": "create"
	 * }
	 *
	 * @param stack app을 올릴 환경 stack 이름을 받는다. 이 이름을 바탕으로 deploy_<stack>.sh 스크립트를 실행한다.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createApp(
			@FormDataParam("stack") String stack,
			@FormDataParam("port") int port,
			@FormDataParam("userId") String userId,
			@FormDataParam("file") InputStream is,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		logger.debug("port = {}", port);
		logger.debug("userId = {}", userId);
		logger.debug("fileInputStream = {}", is);
		logger.debug("contentDispositionHeader = {}", contentDispositionHeader);

		String fileName = contentDispositionHeader.getFileName();

		File workDir = getTempDir(UPLOAD_LOCATION_DIR);
		try {

			Environment env = SettingManager.getInstance().getEnvironment();
			Settings settings = SettingManager.getInstance().getSystemSettings();
			String scriptPath = settings.getString("resources.script.path");
			logger.debug("### scriptPath > {}", new File(env.home(), scriptPath));

			File appFilePath = new File(workDir, fileName);

			/*
			* Script file
			* */
			String scriptFileName = "deploy_" + stack + ".sh";
			File sourceScriptFilePath = new File(new File(env.home(), scriptPath), scriptFileName);
			File targetScriptFilePath = new File(workDir, scriptFileName);

			logger.debug("sourceScriptFilePath > {}", sourceScriptFilePath.getAbsolutePath());
			FileUtils.copyFile(sourceScriptFilePath, targetScriptFilePath);

			/*
			* App file
			* */

 			String normalizedFileName = fileName.replaceAll("[._]", "-");
 			String newImageName = String.format("%s_%s", userId, normalizedFileName);

			logger.debug("Target app filePath = {}", appFilePath);

			saveFile(is, appFilePath);

            Map<String, String> envParameters = new HashMap<>();
            envParameters.put("registry_address", settings.getString("registry.address"));
            envParameters.put("marathon_address", settings.getString("marathon-master.address"));

			//실행 deploy_php_apache.sh <작업디렉토리> <App파일명> <Docker 이미지명>
			String output = CommandUtils.executeCommand(new String[]{"/bin/bash", targetScriptFilePath.getAbsolutePath(), workDir.getAbsolutePath(), fileName, newImageName}, envParameters);


			//TODO 결과에 appId가 반들시 포함되어야 run, delete 등을 수행할수 있다.
			//TODO appid는 userId-imageId 로 자동생성한다.


			return Response.ok(output).build();

		} catch (Exception e) {
			logger.error("", e);
		} finally {
//			if (workDir.exists()) {
//				workDir.delete();
//			}

			//TODO is 를 닫아야 하나?

		}
		return Response.serverError().build();
	}

    @GET
    @Path("/")
    public Response getAppList() {

        //TODO

        return null;
    }


    @GET
    @Path("/{appId}")
    public Response getAppInfo(@PathParam("appId") String appId){

        //TODO

        return null;
    }

    @DELETE
    @Path("/{appId}")
    public Response deleteApp(@PathParam("appId") String appId){

        //TODO

        return null;
    }


    /**
     * Run App
     * <p/>
     * POST /v1/apps/$APP_ID/actions -d '{
     *     type: "run"
     * }'
     * <p/>
     * 결과 :
     * {
     * "appId": "...",
     * "status" : "in-progress",
     * "type": "run"
     * }
     */
    @GET
    @Path("{appId}/actions")
    public Response doActions(@PathParam("appId") String appId) {

        //TODO type에 따라서 run, stop 을 수행한다.

        //TODO run일 경우 instanceSize 를 받아서 해당 갯수만큼 실행시켜준다.

		/*
		* TODO 짠 코드. 마라톤 디플로이.
		* */

        int instanceSize = 2;
        int port = 0;
        String newImage = null;

        try {
            this.deployApp(instanceSize, port, newImage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return Response.ok("").build();
    }

	/*
	* nanoTime을 사용하여 unique한 디렉토리를 만들어준다.
	* */
	private File getTempDir(String filePath) {

		File tempRootDirFile = new File(filePath);
		if (!tempRootDirFile.exists()) {
			tempRootDirFile.mkdirs();
		}

		String tempString = System.nanoTime() + "";
		File f = new File(tempRootDirFile, tempString);

		if (!f.exists()) {
			f.mkdir();
		}
		return f;
	}

	private void saveFile(InputStream is, File targetFilePath) {

		OutputStream os = null;
		try {
			int read = 0;
			byte[] bytes = new byte[1024];

			os = new FileOutputStream(targetFilePath);
			while ((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			os.flush();
		} catch (IOException e) {
			logger.error("error while save app file.", e);
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				//ignore
			}
		}

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



}
