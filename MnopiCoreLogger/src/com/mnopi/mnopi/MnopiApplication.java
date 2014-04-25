package com.mnopi.mnopi;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mnopi.data.DataHandlerRegistry;
import com.mnopi.data.PageVisitedDataHandler;
import com.mnopi.data.WebSearchDataHandler;

public class MnopiApplication extends Application {

	//TODO: Prepare a list of api endpoints with constants
	public final static String SERVER_ADDRESS = "https://ec2-54-197-231-98.compute-1.amazonaws.com";
	public final static String PAGE_VISITED_RESOURCE = "/api/v1/page_visited/";
	public final static String SEARCH_QUERY_RESOURCE = "/api/v1/search_query/";
	public final static String LOGIN_SERVICE = "/api/v1/user/login/";
	public final static String REGISTRATION_SERVICE = "/api/v1/user/";

    public static final String PERMISSIONS_PREFERENCES = "mnopi_permissions";
    public static final String APPLICATION_PREFERENCES = "mnopi_application";

    public static final String SEND_TO_SERVER_REGISTRY = "send_registry";
    public static final String RECEIVE_FROM_SERVICE_REGISTRY = "receive_registry";

    // General reception permission
    public static final String RECEIVE_IS_ALLOWED = "receive_allowed";

    public static final String SESSION_TOKEN = "session_token";
    public static final String USER_RESOURCE = "user_resource";
    public static final String USERNAME = "user_name";

    // Particular reception and sending permission
    public static final String SEND_PAGE_IS_ALLOWED = "send_page_visited";
    public static final String RECEIVE_PAGE_IS_ALLOWED = "receive_page_visited";
    public static final String RECEIVE_HTML_IS_ALLOWED = "receive_html_visited";
    public static final String SEND_SEARCH_IS_ALLOWED = "send_search_query";
    public static final String RECEIVE_SEARCH_IS_ALLOWED = "receive_search_query";

    public static void initHandlerRegistries(Context context) {

        DataHandlerRegistry sendHandlerRegistry =
                DataHandlerRegistry.getInstance(SEND_TO_SERVER_REGISTRY);
        DataHandlerRegistry receiveHandlerRegistry =
                DataHandlerRegistry.getInstance(RECEIVE_FROM_SERVICE_REGISTRY);

        WebSearchDataHandler searchHandler = new WebSearchDataHandler(context.getApplicationContext());
        PageVisitedDataHandler pageHandler = new PageVisitedDataHandler(context.getApplicationContext());

        SharedPreferences settings = context.getSharedPreferences(
                PERMISSIONS_PREFERENCES, Context.MODE_PRIVATE);

        if (settings.getBoolean(SEND_SEARCH_IS_ALLOWED, true)) {
            sendHandlerRegistry.bind(searchHandler.getKey(), searchHandler);
        }
        if (settings.getBoolean(SEND_PAGE_IS_ALLOWED, true)) {
            sendHandlerRegistry.bind(pageHandler.getKey(), pageHandler);
        }

        pageHandler.setSaveHtmlVisited(settings.getBoolean(RECEIVE_HTML_IS_ALLOWED, true));

        if (settings.getBoolean(RECEIVE_SEARCH_IS_ALLOWED, true)) {
            receiveHandlerRegistry.bind(searchHandler.getKey(), searchHandler);
        }
        if (settings.getBoolean(RECEIVE_PAGE_IS_ALLOWED, true)) {
            receiveHandlerRegistry.bind(pageHandler.getKey(), pageHandler);
        }
	    Log.i("permisosMnopiApplication",settings.getBoolean(RECEIVE_HTML_IS_ALLOWED, false)+" " + settings.getBoolean(RECEIVE_SEARCH_IS_ALLOWED, true) +
	    		" "+ settings.getBoolean(RECEIVE_PAGE_IS_ALLOWED, false));

    }

}
