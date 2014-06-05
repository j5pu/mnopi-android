package com.mnopi.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by alainez on 2/06/14.
 */
public class ServerUtils {

    public final static String STATUS_CODE = "status_code";

    /**
     * Converts JsonObject to HashMap
     * @param jsonData
     * @return HashMap with json values
     * @throws JSONException
     */
    static HashMap<String, String> jsonToMap(JSONObject jsonData) throws JSONException {
        Iterator<String> keyItr = jsonData.keys();
        HashMap<String, String> outMap = new HashMap<String, String>();
        while(keyItr.hasNext()) {
            String key = keyItr.next();
            outMap.put(key, jsonData.getString(key));
        }
        return outMap;
    }

    /**
     *
     * @param jsonData
     * @param metaResponse empty hashmap, results of "meta" part of api call will be written in here
     * @throws JSONException
     */
    static void jsonToMap(JSONObject jsonData, HashMap<String, String> metaResponse) throws JSONException {
        Iterator<String> keyItr = jsonData.keys();
        while(keyItr.hasNext()) {
            String key = keyItr.next();
            metaResponse.put(key, jsonData.getString(key));
        }
    }

    /**
     * Performs a POST request to the given URI, transforming the parameters map to JSON.
     * @param uri
     * @param parameters
     * @return Map of key-value obtained from a JSON response. It contains the http status code
     * under the key STATUS_CODE
     * @throws Exception
     */
     static HashMap<String, String> postRequest(String uri,
                                                       HashMap<String, String> parameters,
                                                       String sessionToken)
            throws Exception {

        HttpEntity resEntity;
        HttpClient httpClient = Connectivity.getNewHttpClient();

        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-Type", "application/json");
        if (sessionToken != null) {
            post.setHeader("Session-Token", sessionToken);
        }

        JSONObject postData = new JSONObject(parameters);

        // StringEntity UTF-8
        StringEntity entity = new StringEntity(postData.toString(), HTTP.UTF_8);
        post.setEntity(entity);

        HttpResponse response = httpClient.execute(post);
        resEntity = response.getEntity();
        final String responseStr = EntityUtils.toString(resEntity);

        HashMap resultMap;
        try {
            JSONObject respJSON = new JSONObject(responseStr);
            resultMap = jsonToMap(respJSON);
        } catch (JSONException ex) {
            resultMap = new HashMap<String, String>();
        }
        resultMap.put(STATUS_CODE, Integer.toString(response.getStatusLine().getStatusCode()));

        return resultMap;

    }

    /**
     *
     * @param uri
     * @param sessionToken
     * @return
     * @throws Exception
     */
    static String getRequest(String uri, String sessionToken) throws Exception {
        HttpClient httpClient = Connectivity.getNewHttpClient();
        HttpGet getPages = new HttpGet(uri);
        getPages.setHeader("Content-Type", "application/json");
        getPages.setHeader("Session-Token", sessionToken);

        HttpResponse response = httpClient.execute(getPages);
        String respStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        return respStr;
    }

}
