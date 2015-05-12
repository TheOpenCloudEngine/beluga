//package org.opencloudengine.garuda.api.rest;
//
//import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
//import org.glassfish.jersey.media.multipart.FormDataParam;
//
//import javax.ws.rs.Consumes;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.io.*;
//
///**
// * File Upload API
// *
// * @author Sang Wook, Song
// *
// */
//@Path("/file")
//public class FileAPI {
//
//	private static final String SERVER_UPLOAD_LOCATION_FOLDER = "/tmp";
//
//	/**
//	 * Upload a File
//	 */
//	@POST
//	@Path("/upload")
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	public Response uploadFile(
//			@FormDataParam("file") InputStream fileInputStream,
//			@FormDataParam("file") FormDataContentDisposition contentDispositionHeader) {
//
//		String filePath = SERVER_UPLOAD_LOCATION_FOLDER	+ contentDispositionHeader.getFileName();
//
//		// save the file to the server
//		saveFile(fileInputStream, filePath);
//
//		String output = "File saved to server location : " + filePath;
//
//		return Response.ok(output).build();
//
//	}
//
//	// save uploaded file to a defined location on the server
//	private void saveFile(InputStream uploadedInputStream,
//	                      String serverLocation) {
//
//		try {
//			OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
//			int read = 0;
//			byte[] bytes = new byte[1024];
//
//			outpuStream = new FileOutputStream(new File(serverLocation));
//			while ((read = uploadedInputStream.read(bytes)) != -1) {
//				outpuStream.write(bytes, 0, read);
//			}
//			outpuStream.flush();
//			outpuStream.close();
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
//
//	}
//
//}
