package org.opencloudengine.garuda.api.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencloudengine.garuda.api.model.Human;
import org.opencloudengine.garuda.common.util.JsonUtils;

import javax.ws.rs.*;
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
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Human> getAllParties(String json) throws Exception {
		List<Human> list = new ArrayList<Human>();
		list.add(new Human(1,  "Mr. Hong", 20));
		list.add(new Human(2, "Mr. Kim", 30));
		list.add(new Human(3, "Mr. Lee", 40));

        System.out.println("## json > " + json);
        return list;
	}

    @POST
    @Path("post")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Human> postJson(String json) throws Exception {
        List<Human> list = new ArrayList<Human>();
        list.add(new Human(1,  "Mr. Hong", 20));
        list.add(new Human(2, "Mr. Kim", 30));
        list.add(new Human(3, "Mr. Lee", 40));

        JsonNode rootNode = JsonUtils.toJsonNode(json);
        if(rootNode.isArray()) {
            for(JsonNode n : rootNode) {
                System.out.println("A : " + n.path("age").asInt());
                System.out.println("N : " + n.path("name").asText());
            }
        }
//        rootNode.as
        System.out.println(rootNode);
        return list;
    }

}
