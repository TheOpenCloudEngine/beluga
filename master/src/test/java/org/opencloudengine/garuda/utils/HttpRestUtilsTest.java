package org.opencloudengine.garuda.utils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 6..
 */
public class HttpRestUtilsTest {

    @Test
    public void test() {
        Client client = HttpRestUtils.createClient();

        String url = "http://52.69.244.248:5000/v1/repositories/php5_apache2/tags";
        WebResource webResource = client.resource(url);
        Map result = webResource.accept(MediaType.APPLICATION_JSON).get(Map.class);
        System.out.println(result.getClass().getName());
    }
}
