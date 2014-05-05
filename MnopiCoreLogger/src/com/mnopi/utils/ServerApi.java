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

    private final static String SIGN_IN_URI = MnopiApplication.SERVER_ADDRESS + MnopiApplication.LOGIN_SERVICE;
    private final static String SIGN_UP_URI = MnopiApplication.SERVER_ADDRESS + MnopiApplication.REGISTRATION_SERVICE;

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
                                                      HashMap<String, String> parameters)
            throws Exception {

        HttpEntity resEntity;
        HttpClient httpclient = Connectivity.getNewHttpClient();

        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-Type", "application/json");

        JSONObject postData = new JSONObject(parameters);

        // StringEntity UTF-8
        StringEntity entity = new StringEntity(postData.toString(), HTTP.UTF_8);
        post.setEntity(entity);

        HttpResponse response = httpclient.execute(post);
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

        response = postRequest(SIGN_IN_URI, signInData);
        if (response.get("result").equals("ERR")) {
            if (response.containsKey("reason")) {
                throw new Exception(response.get("reason")); //TODO: Change
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

        response = postRequest(SIGN_UP_URI, signUpData);
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
