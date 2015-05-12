package org.opencloudengine.garuda.api.rest;

import org.opencloudengine.garuda.api.model.Human;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Human API
 *
 * @author Sang Wook, Song
 *
 */
@Path("/human")
public class HumanAPI {

	@GET
	@Path("list")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Human> getAllParties() throws Exception {
		List<Human> list = new ArrayList<Human>();
		list.add(new Human(1,  "Mr. Hong", 20));
		list.add(new Human(2, "Mr. Kim", 30));
		list.add(new Human(3, "Mr. Lee", 40));
		return list;
	}


}
