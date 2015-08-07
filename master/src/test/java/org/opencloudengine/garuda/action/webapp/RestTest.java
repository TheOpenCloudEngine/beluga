package org.opencloudengine.garuda.action.webapp;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

/**
 * Created by swsong on 2015. 8. 7..
 */
public class RestTest {

    @Test
    public void getSampleJson() {
        Client client = ClientBuilder.newClient();
        String target = "http://www.ticketmonster.co.kr";
        String path = "deallist/GetMoreList";

        String response = client.target(target).path(path).request(MediaType.APPLICATION_JSON_TYPE).get(String.class);
        System.out.println("reponse = " + response);
        try {
            JSONObject jsonOutput = new JSONObject(response);
            System.out.println("last_deal_srl = " + jsonOutput.getString("last_deal_srl"));
            System.out.println("deal_list = " + jsonOutput.getJSONArray("deal_list"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        client.close();
    }

    @Test
    public void postNetCatCommand() {
        Client client = ClientBuilder.newClient();
        String target = "http://52.69.164.92:8080/";
        String path = "deallist/GetMoreList";

        String response = client.target(target).path(path).request(MediaType.APPLICATION_JSON_TYPE).get(String.class);



    }
}
