package com.mnopi.mnopi;

import android.app.Application;

public class MnopiApplication extends Application {

    public final static String MNOPI_CLIENT = "android-v1.0b";

	public final static String SERVER_ADDRESS = "https://ec2-54-197-231-98.compute-1.amazonaws.com";
	public final static String PAGE_VISITED_RESOURCE = "/api/v1/page_visited/";
	public final static String SEARCH_QUERY_RESOURCE = "/api/v1/search_query/";
	public final static String LOGIN_SERVICE = "/api/v1/user/login/";
	public final static String REGISTRATION_SERVICE = "/api/v1/user/";

    public static final String PERMISSIONS_PREFERENCES = "mnopi_permissions";

    // General reception permission
    public static final String RECEIVE_IS_ALLOWED = "receive_allowed";

}
