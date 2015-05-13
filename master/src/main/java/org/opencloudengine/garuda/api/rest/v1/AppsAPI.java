package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

/**
 * Deploy App API
 *
 * @author Sang Wook, Song
 *
 */
@Path("/v1/apps")
public class AppsAPI {

	private static final String SERVER_UPLOAD_LOCATION_FOLDER = "/tmp";

	/**
	 * Create apps.
	 * Docker 이미지를 만들어서 Registry에 등록한다.
	 *
	 * POST /v1/apps
	 *
	 * 결과 :
	 * {
	 *     "appId": "...",
	 *     "status" : "complete",
	 *     "type": "create"
	 * }
	 * */
	@POST
	@Path("/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response createApp(
			@FormDataParam("instanceSize") int instanceSize,
			@FormDataParam("port") int port,
			@FormDataParam("userId") String userId,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {

		String filePath = SERVER_UPLOAD_LOCATION_FOLDER	+ contentDispositionHeader.getFileName();

		// save the file to the server
		saveFile(fileInputStream, filePath);

		String output = "File saved to server location : " + filePath;

		//TODO deploy_php_apache.sh aaa.zip <userId>_<appFile name>_php <registry addr> "php5_apache2"


		//TODO 결과에 appId가 반들시 포함되어야 run, delete 등을 수행할수 있다.
		//TODO appid는 userId-imageId 로 자동생성한다.

		return Response.ok(output).build();

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

	/**
	 * Run App
	 *
	 * POST /v1/apps/$APP_ID/run
	 *
	 * 결과 :
	 * {
	 *     "appId": "...",
	 *     "status" : "in-progress",
	 *     "type": "run"
	 * }
	 * */
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
