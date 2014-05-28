package com.mnopi.utils;

import com.mnopi.data.models.PageVisited;
import com.mnopi.data.models.Query;
import com.mnopi.mnopi.MnopiApplication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
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
     *
     * @param jsonData
     * @param metaResponse empty hashmap, results of "meta" part of api call will be written in here
     * @throws JSONException
     */
    public static void jsonToMap(JSONObject jsonData, HashMap<String, String> metaResponse) throws JSONException {
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
    private static HashMap<String, String> postRequest(String uri,
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
    private static String getRequest(String uri, String sessionToken) throws Exception {
        HttpClient httpClient = Connectivity.getNewHttpClient();
        HttpGet getPages = new HttpGet(uri);
        getPages.setHeader("Content-Type", "application/json");
        getPages.setHeader("Session-Token", sessionToken);

        HttpResponse response = httpClient.execute(getPages);
        String respStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);

        return respStr;
    }

    /**
     *
     * @param sessionToken
     * @param resourceUrl
     * @param metaResponse empty hashmap, results of "meta" part of api call will be written in here
     * @return
     * @throws Exception
     */
    public static ArrayList<Query> getQueries(String sessionToken, String resourceUrl,
                                              HashMap<String, String> metaResponse)
            throws Exception {

        String response = getRequest(resourceUrl, sessionToken);
        JSONObject respJson = new JSONObject(response);

        // Get meta data
        JSONObject respMetaJson = respJson.getJSONObject("meta");
        jsonToMap(respMetaJson, metaResponse);

        // Get pages visited
        JSONArray jsonQueries = respJson.getJSONArray("objects");

        ArrayList<Query> resultQueries = new ArrayList<Query>();
        for (int i = 0; i < jsonQueries.length(); i++) {
            JSONObject queryObject = jsonQueries.getJSONObject(i);
            final String date = queryObject.getString("date");
            final String search_query = queryObject.getString("search_query");
            final String resource_uri = queryObject.getString("resource_uri");
            final String result = queryObject.getString("search_results");
            final String dateFormated = date.substring(0, 10);
            final String hour = date.substring(11, 19);
            resultQueries.add(new Query(resource_uri, search_query
                    , dateFormated, result, hour));
        }
        return resultQueries;
    }

    /**
     * Gets page already visited
     * @param sessionToken
     * @param resourceUrl url with the resource containing pages_visited
     * @param metaResponse empty hashmap, results of "meta" part of api call will be written in here
     * @return
     * @throws Exception
     */
    public static ArrayList<PageVisited> getPagesVisited(String sessionToken, String resourceUrl,
                                                         HashMap<String, String> metaResponse)
            throws Exception {

        String response = getRequest(resourceUrl, sessionToken);
        JSONObject respJson = new JSONObject(response);

        // Get meta data
        JSONObject respMetaJson = respJson.getJSONObject("meta");
        jsonToMap(respMetaJson, metaResponse);

        // Get pages visited
        JSONArray jsonPagesVisited = respJson.getJSONArray("objects");

        ArrayList<PageVisited> resultPagesVisited = new ArrayList<PageVisited>();
        for (int i = 0; i < jsonPagesVisited.length(); i++) {
            JSONObject queryObject = jsonPagesVisited.getJSONObject(i);
            final String date = queryObject.getString("date");
            String url = queryObject.getString("page_visited");
            final String resourceUri = queryObject.getString("resource_uri");

            if(!url.startsWith("http") && !url.startsWith("https")){
                url = "http://" + url;
            }

            URL netUrl = new URL(url);
            String host = netUrl.getHost();
            if(host.startsWith("www")){
                host = host.substring("www".length()+1);
            }
            final String dateFormatted = date.substring(0, 10);
            final String hour = date.substring(11, 19);
            ArrayList<String> categoriesAux = new ArrayList<String>();
            final ArrayList<String> categories = categoriesAux;

            resultPagesVisited.add(new PageVisited(url, host, dateFormatted, hour, resourceUri, categories));
        }

        return resultPagesVisited;

    }

    /**
     *
     * @param sessionToken
     * @param resourceUrl
     * @return
     * @throws Exception
     */
    public static ArrayList<String> getCategories (String sessionToken, String resourceUrl)
            throws Exception{
        String response = getRequest(resourceUrl, sessionToken);
        ArrayList<String> resultCategories = new ArrayList<String>();

        JSONArray respJsonCat = new JSONArray(response);
        if (respJsonCat.length() != 0){
            for (int j=0; j<respJsonCat.length(); j++) {
                String category = respJsonCat.getString(j);
                resultCategories.add(category);
            }
        }
        return resultCategories;
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
        int responseStatCode = Integer.parseInt(response.get(STATUS_CODE));
        if (responseStatCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new UnauthorizedException();
        } else if (Integer.parseInt(response.get(STATUS_CODE)) != HttpStatus.SC_CREATED) {
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
        int responseStatCode = Integer.parseInt(response.get(STATUS_CODE));
        if (responseStatCode == HttpStatus.SC_UNAUTHORIZED){
            throw new UnauthorizedException();
        } else if (responseStatCode != HttpStatus.SC_CREATED) {
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

    /**
     *
     * @param userName
     * @param password
     * @param email
     * @return
     * @throws Exception
     */
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
