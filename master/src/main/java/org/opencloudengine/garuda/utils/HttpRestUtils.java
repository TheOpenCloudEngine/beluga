package org.opencloudengine.garuda.utils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache4.ApacheHttpClient4Handler;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;


/**
 * Created by swsong on 2015. 8. 6..
 */
public class HttpRestUtils {

    public static Client createClient() {
        HttpClient apacheClient = HttpClientBuilder.create().build();
        Client client = new Client(new ApacheHttpClient4Handler(apacheClient, new BasicCookieStore(), true));
        return client;
    }
}
