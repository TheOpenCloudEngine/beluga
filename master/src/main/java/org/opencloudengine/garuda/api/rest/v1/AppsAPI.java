package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

/**
 * Deploy App API
 *
 * @author Sang Wook, Song
 */
@Path("/v1/apps")
public class AppsAPI {

	private static Logger logger = LoggerFactory.getLogger(AppsAPI.class);

	private static final String UPLOAD_LOCATION_DIR = "/tmp/";

	/**
	 * Create apps.
	 * Docker 이미지를 만들어서 Registry에 등록한다.
	 * <p/>
	 * POST /v1/apps
	 * <p/>
	 * 결과 :
	 * {
	 * "appId": "...",
	 * "status" : "complete",
	 * "type": "create"
	 * }
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createApp(
			@FormDataParam("instanceSize") int instanceSize,
			@FormDataParam("port") int port,
			@FormDataParam("userId") String userId,
			@FormDataParam("file") InputStream is,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		logger.debug("instanceSize = {}", instanceSize);
		logger.debug("port = {}", port);
		logger.debug("userId = {}", userId);
		logger.debug("fileInputStream = {}", is);
		logger.debug("contentDispositionHeader = {}", contentDispositionHeader);

		String fileName = contentDispositionHeader.getFileName();

		File tmpDir = getTempDir(UPLOAD_LOCATION_DIR);
		try {
			File appFilePath = new File(tmpDir, fileName);
			File scriptFilePath = new File(tmpDir, fileName);

			logger.debug("Target app filePath = {}", appFilePath);

			saveFile(is, appFilePath);

			//TODO deploy_php_apache.sh aaa.zip <userId>_<appFile name>_php <registry addr> "php5_apache2"


			//TODO 결과에 appId가 반들시 포함되어야 run, delete 등을 수행할수 있다.
			//TODO appid는 userId-imageId 로 자동생성한다.

			String output = "";

			return Response.ok(output).build();

		} finally {
			if (tmpDir.exists()) {
				tmpDir.delete();
			}

			//TODO is 를 닫아야 하나?

		}

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

	/**
	 * Run App
	 * <p/>
	 * POST /v1/apps/$APP_ID/run
	 * <p/>
	 * 결과 :
	 * {
	 * "appId": "...",
	 * "status" : "in-progress",
	 * "type": "run"
	 * }
	 */
	@GET
	@Path("{appID}/run")
	@Produces(MediaType.TEXT_PLAIN)
	public Response runApp(@PathParam("appId") String appId) {

		/*
		* TODO 짠 코드. 마라톤 디플로이.
		* */

		return Response.ok("").build();
	}

}
