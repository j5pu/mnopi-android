package com.mnopi.mnopi;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.mnopi.data.DataHandlerRegistry;
import com.mnopi.data.PageVisitedDataHandler;
import com.mnopi.data.WebSearchDataHandler;

public class MyApplication extends Application {

    //TODO: change name to my application
	//TODO: Prepare a list of api endpoints with constants
	private final static String SERVER_ADRESS = "https://ec2-54-197-231-98.compute-1.amazonaws.com";

    private Boolean logged_user = false;
	private String user_name = "";
	private String session_token = "";
	private String user_resource = "";

    public static final String PERMISSIONS_PREFERENCES = "mnopi_permissions";
    public static final String APPLICATION_PREFERENCES = "mnopi_application";

    public static final String SEND_TO_SERVER_REGISTRY = "send_registry";
    public static final String RECEIVE_FROM_SERVICE_REGISTRY = "receive_registry";

    // General reception permission
    public static final String RECEIVE_IS_ALLOWED = "receive_allowed";

    // Particular reception and sending permission
    public static final String SEND_PAGE_IS_ALLOWED = "send_page_visited";
    public static final String RECEIVE_PAGE_IS_ALLOWED = "receive_page_visited";
    public static final String RECEIVE_HTML_IS_ALLOWED = "send_page_visited";
    public static final String SEND_SEARCH_IS_ALLOWED = "send_search_query";
    public static final String RECEIVE_SEARCH_IS_ALLOWED = "receive_search_query";

    public static void initHandlerRegistries(Context context) {

        DataHandlerRegistry sendHandlerRegistry =
                DataHandlerRegistry.getInstance(MyApplication.SEND_TO_SERVER_REGISTRY);
        DataHandlerRegistry receiveHandlerRegistry =
                DataHandlerRegistry.getInstance(MyApplication.RECEIVE_FROM_SERVICE_REGISTRY);

        WebSearchDataHandler searchHandler = new WebSearchDataHandler(context.getApplicationContext());
        PageVisitedDataHandler pageHandler = new PageVisitedDataHandler(context.getApplicationContext());

        SharedPreferences settings = context.getSharedPreferences(
                MyApplication.PERMISSIONS_PREFERENCES, Context.MODE_PRIVATE);

        if (settings.getBoolean(SEND_SEARCH_IS_ALLOWED, true)) {
            sendHandlerRegistry.bind(searchHandler.getKey(), searchHandler);
        }
        if (settings.getBoolean(SEND_PAGE_IS_ALLOWED, true)) {
            sendHandlerRegistry.bind(pageHandler.getKey(), pageHandler);
        }
        if (settings.getBoolean(RECEIVE_HTML_IS_ALLOWED, true)) {
            pageHandler.setSaveHtmlVisited(true);
        }
        if (settings.getBoolean(RECEIVE_SEARCH_IS_ALLOWED, true)) {
            receiveHandlerRegistry.bind(searchHandler.getKey(), searchHandler);
        }
        if (settings.getBoolean(RECEIVE_PAGE_IS_ALLOWED, true)) {
            receiveHandlerRegistry.bind(pageHandler.getKey(), pageHandler);
        }

    }

	public String getUser_resource() {
		return user_resource;
	}
	public void setUser_resource(String user_resource) {
		this.user_resource = user_resource;
	}
	public String getSERVER_ADRESS() {
		return SERVER_ADRESS;
	}
	public Boolean getLogged_user() {
		return logged_user;
	}
	public void setLogged_user(Boolean logged_user) {
		this.logged_user = logged_user;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getSession_token() {
		return session_token;
	}
	public void setSession_token(String session_token) {
		this.session_token = session_token;
	}

}
