package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

/**
 * File Upload API
 *
 * @author Sang Wook, Song
 *
 */
@Path("/v1/deploy")
public class DeployPhpAPI {

	private static final String SERVER_UPLOAD_LOCATION_FOLDER = "/tmp";

	/**
	 * Upload a File
	 */
	@POST
	@Path("php")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("appFile") InputStream fileInputStream,
			@FormDataParam("appFile") FormDataContentDisposition contentDispositionHeader,
			@RequestParam int instanceSize, @RequestParam int port, @RequestParam String userId) {

		String filePath = SERVER_UPLOAD_LOCATION_FOLDER	+ contentDispositionHeader.getFileName();

		// save the file to the server
		saveFile(fileInputStream, filePath);

		String output = "File saved to server location : " + filePath;

		//TODO 1 deploy_php_apache.sh aaa.zip <userId>_<appFile name>_php <registry addr> "php5_apache2"


		//TODO 2. 짠 코드. 마라톤 디플로이.

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

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "Test";
	}

}
