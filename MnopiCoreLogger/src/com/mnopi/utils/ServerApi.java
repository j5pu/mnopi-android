package com.mnopi.utils;

import com.mnopi.mnopi.MnopiApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by alfredo on 28/04/14.
 */
public class ServerApi {

    private final static String SIGN_IN_URI = MnopiApplication.SERVER_ADDRESS +
            MnopiApplication.LOGIN_SERVICE;
    private final static String SIGN_UP_URI = MnopiApplication.SERVER_ADDRESS +
            MnopiApplication.REGISTRATION_SERVICE;
    private final static String SEND_PAGE_VISITED_URI = MnopiApplication.SERVER_ADDRESS +
            MnopiApplication.PAGE_VISITED_RESOURCE;
    private final static String SEND_WEB_SEARCH_URI = MnopiApplication.SERVER_ADDRESS +
            MnopiApplication.SEARCH_QUERY_RESOURCE;

    public final static String STATUS_CODE = "status_code";

    /**
     * Converts JsonObject to HashMap
     * @param jsonData
     * @return HashMap with json values
     * @throws JSONException
     */
    public static HashMap<String, String> jsonToMap(JSONObject jsonData) throws JSONException {
        Iterator<String> keyItr = jsonData.keys();
        HashMap<String, String> outMap = new HashMap<String, String>();
        while(keyItr.hasNext()) {
            String key = keyItr.next();
            outMap.put(key, jsonData.getString(key));
        }
        return outMap;
    }

    /**
     * Performs a POST request to the given URI, transforming the parameters map to JSON.
     * @param uri
     * @param parameters
     * @return Map of key-value obtained from a JSON response. It contains the http status code
     * under the key STATUS_CODE
     * @throws Exception
     */
    public static HashMap<String, String> postRequest(String uri,
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
     * Saves a new search resource in the server
     * @param userResource
     * @param date
     * @param searchQuery
     * @param searchResults
     * @param sessionToken
     * @return key-value response
     * @throws Exception
     */
    public static HashMap<String, String> sendWebSearch(String userResource, String date,
                                                        String searchQuery, String searchResults,
                                                        String sessionToken)
            throws Exception {

        HashMap<String, String> searchData = new HashMap<String, String>();
        HashMap<String, String> response;

        searchData.put("user", userResource);
        searchData.put("search_query", searchQuery);
        searchData.put("search_results", searchResults);
        searchData.put("date", date);

        response = postRequest(SEND_WEB_SEARCH_URI, searchData, sessionToken);
        if (Integer.parseInt(response.get(STATUS_CODE)) != HttpStatus.SC_CREATED) {
            if (response.get("result").equals("ERR")) {
                if (response.containsKey("reason")) {
                    throw new Exception(response.get("reason"));
                } else {
                    throw new Exception("Undefined error");
                }
            }
        }
        return response;

    }

    /**
     * Saves a new page visited resource in the server
     * @param userResource
     * @param url
     * @param date
     * @param htmlCode
     * @param sessionToken
     * @return key-value response
     * @throws Exception
     */
    public static HashMap<String, String> sendPageVisited(String userResource, String url, String date,
                                                          String htmlCode, String sessionToken)
            throws Exception {

        HashMap<String, String> pageVisitedData = new HashMap<String, String>();
        HashMap<String, String> response;

        pageVisitedData.put("user", userResource);
        pageVisitedData.put("url", url);
        pageVisitedData.put("html_code", htmlCode);
        pageVisitedData.put("date", date);

        response = postRequest(SEND_PAGE_VISITED_URI, pageVisitedData, sessionToken);
        if (Integer.parseInt(response.get(STATUS_CODE)) != HttpStatus.SC_CREATED) {
            if (response.get("result").equals("ERR")) {
                if (response.containsKey("reason")) {
                    throw new Exception(response.get("reason"));
                } else {
                    throw new Exception("Undefined error");
                }
            }
        }
        return response;

    }
    
    /**
     *
     * @param userName
     * @param password
     * @param client String with the name of the client performing the sign in operation
     * @return Response key-values
     * @throws Exception if there was an error, including an API error
     */
    public static HashMap<String, String> userSignIn(String userName, String password,
                                                     String client)
            throws Exception {

        HashMap<String, String> signInData = new HashMap<String, String>();
        HashMap<String, String> response;

        signInData.put("username", userName);
        signInData.put("key", password);
        signInData.put("client", client);

        response = postRequest(SIGN_IN_URI, signInData, null);
        if (response.get("result").equals("ERR")) {
            if (response.containsKey("reason")) {
                throw new Exception(response.get("reason"));
            } else {
                throw new Exception("Undefined error");
            }
        }

        return response;
    }

    public static HashMap<String, String> userSignUp(String userName, String password,
                                                     String email)
        throws Exception {

        HashMap<String, String> signUpData = new HashMap<String, String>();
        HashMap<String, String> response;

        signUpData.put("username", userName);
        signUpData.put("password", password);
        signUpData.put("email", email);

        response = postRequest(SIGN_UP_URI, signUpData, null);
        if (Integer.parseInt(response.get(STATUS_CODE)) != HttpStatus.SC_CREATED){
            if (response.containsKey("reason") && response.containsKey("erroneous_parameters")) {
                throw new Exception(response.get("erroneous_parameters"));
            } else {
                throw new Exception("Undefined error");
            }
        }

        return response;
    }

}
