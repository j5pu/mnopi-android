package com.mnopi.mnopi;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.mnopi.data.handlers.PageVisitedDataHandler;
import com.mnopi.data.handlers.WebSearchDataHandler;

public class MnopiApplication extends Application {

    public final static String MNOPI_CLIENT = "android-v1.0b";

	public final static String SERVER_ADDRESS = "https://ec2-54-197-231-98.compute-1.amazonaws.com";
	public final static String PAGE_VISITED_RESOURCE = "/api/v1/page_visited/";
	public final static String SEARCH_QUERY_RESOURCE = "/api/v1/search_query/";
	public final static String LOGIN_SERVICE = "/api/v1/user/login/";
	public final static String REGISTRATION_SERVICE = "/api/v1/user/";

    public static final String PERMISSIONS_PREFERENCES = "mnopi_permissions";
    public static final String APPLICATION_PREFERENCES = "mnopi_application";

    public static final String SESSION_TOKEN = "session_token";
    public static final String USER_RESOURCE = "user_resource";
    public static final String USERNAME = "user_name";

    // General reception permission
    public static final String RECEIVE_IS_ALLOWED = "receive_allowed";

}
