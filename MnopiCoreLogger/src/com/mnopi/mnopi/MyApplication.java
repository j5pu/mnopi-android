package com.mnopi.mnopi;

import android.app.Application;

public class MyApplication extends Application {
	
	private String SERVER_ADRESS = "https://ec2-54-197-231-98.compute-1.amazonaws.com";
	private Boolean logged_user = false;
	private String user_name = "";
	private String session_token = "";
	private Boolean butDataCollector = false;
	private Boolean butDataDelivery = false;
	private Boolean butSearchQueries = false;
	private Boolean butHtmlVisited = false;
	private Boolean butPagesVisited = false;
	private String user_resource = "";
	
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
	public Boolean getButDataCollector() {
		return butDataCollector;
	}
	public void setButDataCollector(Boolean butDataCollector) {
		this.butDataCollector = butDataCollector;
	}
	public Boolean getButDataDelivery() {
		return butDataDelivery;
	}
	public void setButDataDelivery(Boolean butDataDelivery) {
		this.butDataDelivery = butDataDelivery;
	}
	public Boolean getButSearchQueries() {
		return butSearchQueries;
	}
	public void setButSearchQueries(Boolean butSearchQueries) {
		this.butSearchQueries = butSearchQueries;
	}
	public Boolean getButHtmlVisited() {
		return butHtmlVisited;
	}
	public void setButHtmlVisited(Boolean butHtmlVisited) {
		this.butHtmlVisited = butHtmlVisited;
	}
	public Boolean getButPagesVisited() {
		return butPagesVisited;
	}
	public void setButPagesVisited(Boolean butPagesVisited) {
		this.butPagesVisited = butPagesVisited;
	}

}
