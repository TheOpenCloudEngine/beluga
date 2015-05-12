package org.opencloudengine.garuda.api.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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

}
