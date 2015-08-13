package org.opencloudengine.garuda.api.rest;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.opencloudengine.garuda.api.rest.v1.BaseAPI;
import org.opencloudengine.garuda.env.SettingManager;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by swsong on 2015. 8. 13..
 */
@Path("/")
public class MainAPI extends BaseAPI {
        @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok("pong").build();
    }

    @POST
    @Path("/login")
    public Response login(@FormDataParam("id") String userId, @FormDataParam("password") String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        try {
            byte[] bytePassword = password.getBytes("utf-8");
            String loginPassword = SettingManager.getInstance().getSystemSettings().getString("login.password");
            byte[] digest = MessageDigest.getInstance("md5").digest(bytePassword);
            BigInteger bigInt = new BigInteger(1, digest);
            String hashText = bigInt.toString(16) ;
            while(hashText.length() < 32 ){
                hashText = "0"+hashText;
            }
            logger.debug("md5 {} actual : {}, expected : {}", password, hashText, loginPassword);

            if(hashText.equalsIgnoreCase(loginPassword) && userId.equalsIgnoreCase("admin")) {
                return Response.ok().build();
            }

            return Response.status(Response.Status.UNAUTHORIZED).build();
        }catch(Exception e) {
            logger.error("", e);
            throw e;
        }
    }
}
