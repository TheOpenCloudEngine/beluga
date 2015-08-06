package org.opencloudengine.garuda.api.rest.v1;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.cluster.TestActionRequest;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.HashMap;

@Path("/v1/test")
public class TestAPI extends BaseAPI {

    public TestAPI() {
        super();
    }

    @POST
    @Path("/testAction")
    public Response testAction() {
        try {
            logger.debug("actionService() = {}", actionService());
            ActionStatus actionStatus = actionService().request(new TestActionRequest("Good!"));

            while (!actionStatus.checkDone()) {
                logger.info("{}", actionStatus.toString());

                Thread.sleep(1000);
            }
            return Response.ok(actionStatus.getResult()).build();
        }catch(Throwable t) {
            t.printStackTrace();
            return Response.ok(t).build();
        }
    }
    @GET
    @Path("/testAction")
    public Response testActionGet() throws Exception {
        return Response.ok(1).build();
    }


    @POST
    @Path("/")
    public Response test2(@FormDataParam("step") Integer step, @QueryParam("a")Integer a, @FormDataParam("a")Integer b) throws Exception {
        logger.debug("Original step ={}", step);
        logger.debug("Original a ={}", a);
        logger.debug("Original b ={}", b);
        return Response.ok().build();
    }

    /**
     * Test
     */
    @GET
    @Path("/")
    public Response test(@QueryParam("step") Integer step) throws Exception {
        logger.debug("Original step ={}", step);

        ActionStatus status =new ActionStatus("1111111", "test");

        if(step != null) {
            status.registerStep("step1");
            status.registerStep("step2");
            status.registerStep("step3");

            switch (step) {
                case 0:
                    status.setInQueue();
                    break;
                case 1:
                    status.setStart();
                    break;
                case 2:
                    status.setStart();
                    status.walkStep();
                    break;
                case 3:
                    status.setStart();
                    status.walkStep();
                    status.walkStep();
                    break;
                case 4:
                    status.walkStep();
                    status.walkStep();
                    status.setError(new RuntimeException("error while processing."));
                    break;
                case 5:
                    status.walkStep();
                    status.walkStep();
                    status.setComplete();
                    break;
                case 6:
                    status.walkStep();
                    status.walkStep();
                    status.walkStep();
                    status.setComplete();
                    break;
                case 7:
                    status.walkStep();
                    status.walkStep();
                    status.walkStep();
                    HashMap<String, String> result = new HashMap<>();
                    result.put("name", "sang");
                    result.put("company", "fastcat");
                    status.setResult(result);
                    status.setComplete();
                    break;
            }

        }
        return Response.ok(status).build();
    }


}
