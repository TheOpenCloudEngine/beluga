package org.opencloudengine.garuda.api.rest;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

/**
 * Sample
 *
 * @author Sang Wook, Song
 *
 */
@Path("/sample")
public class SampleAPI {

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test() {
		return "Test";
	}

    @POST
    @Path("/fileupload")  //Your Path or URL to call this service
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {
        //Your local disk path where you want to store the file
        String uploadedFileLocation = "/tmp/" + fileDetail.getFileName();
        System.out.println(uploadedFileLocation);
        // save it
        File objFile=new File(uploadedFileLocation);
        if(objFile.exists())
        {
            objFile.delete();

        }

        saveToFile(uploadedInputStream, uploadedFileLocation);

        String output = "File uploaded via Jersey based RESTFul Webservice to: " + uploadedFileLocation;

        return Response.status(200).entity(output).build();

    }
    private void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

        try {
            OutputStream out = null;
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}
