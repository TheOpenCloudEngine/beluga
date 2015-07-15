package org.opencloudengine.cloud.garuda.master;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class SyslogServerInterface {

    private int port;
    private String host;
    private HashMap<String, HashMap<String, Float>> rtt = new HashMap<String, HashMap<String, Float>>();

    public SyslogServerInterface() {

    }

    public void setPort(int port) {
        this.port = port;

    }

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Fetch fresh data from the syslog server and update the internal log
     */
    public void update() {

        rtt = new HashMap<String, HashMap<String, Float>>();

        String data = null;
        data = this.getData();
        if (data == null) {
            //System.out.println("Failed to fetch data from syslog server on: "+ host + ":" + port);
            return;
        }

        // Validate data
        JSONObject serversJSON = null;
        if (data != null) {
            try {
                serversJSON = new JSONObject(data).getJSONObject("servers");
            } catch (JSONException e) {

            }
        }

        // Loop through all servers
        if (JSONObject.getNames(serversJSON) != null) {
            for (String serverName : JSONObject.getNames(serversJSON)) {

                JSONObject server = null;
                try {
                    server = serversJSON.getJSONObject(serverName);
                } catch (JSONException e) {
                }

                // Loop through all apps on server
                if (server != null) {
                    String[] appNames = null;
                    appNames = JSONObject.getNames(server);

                    if (appNames != null) {
                        for (String appName : appNames) {

                            JSONObject app = null;
                            float responseTime = -1;

                            // Get app
                            try {
                                app = server.getJSONObject(appName);
                            } catch (JSONException e) {
                            }

                            // Get response time
                            if (app != null) {
                                try {
                                    responseTime = (float) app
                                            .getDouble("avgResponseTime");
                                } catch (JSONException e) {
                                }
                            }

                            // Update log
                            if (responseTime >= 0) {
                                if (!rtt.containsKey(serverName)) {
                                    rtt.put(serverName,
                                            new HashMap<String, Float>());
                                }
                                rtt.get(serverName).put(appName, responseTime);
                            }
                        }
                    }

                    /*
                    for (String s : rtt.keySet()) {
                        System.out.println(s + ":");
                        for (String a : rtt.get(s).keySet()) {
                            System.out.println("\t" + a + ": "
                                    + rtt.get(s).get(a));
                        }
                    }
                    */
                }
            }

        }
    }

    private String getData() {

        String data = null;

        try {
            data = sendCommand("/status");
        } catch (Exception e) {

        }

        return data;
    }

    private String sendCommand(String command) throws Exception {

        String response = "";

        URL backendServer = new URL("http://" + host + ":" + port + command);

        URLConnection conn = backendServer.openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write("");
        wr.flush();

        // Get the response
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                .getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            if (line != null) {
                response += line;
            }
        }
        wr.close();
        rd.close();

        return response;
    }

    public HashMap<String, Float> getBackendAvgRtts(String backend) {

        if (rtt.containsKey(backend)) {
            return rtt.get(backend);
        }
        return null;
    }

}
