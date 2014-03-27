package com.mnopi.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mnopi.data.DataHandler;
import com.mnopi.data.DataHandlerRegistry;
import com.mnopi.data.PageVisitedDataHandler;
import com.mnopi.data.WebSearchDataHandler;


public class DataCollectorService extends IntentService {

	DataHandlerRegistry dataHandlers = null;
	
	public DataCollectorService() {
		super(DataCollectorService.class.getSimpleName());
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		dataHandlers = new DataHandlerRegistry();
		
		WebSearchDataHandler webHandler = new WebSearchDataHandler(getApplicationContext());
		PageVisitedDataHandler pageHandler = new PageVisitedDataHandler(getApplicationContext());
		dataHandlers.bind(webHandler.getKey(), webHandler);
		dataHandlers.bind(pageHandler.getKey(), pageHandler);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		SharedPreferences prefs = getSharedPreferences(
				"MisPreferencias", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		Boolean butDataCollector = prefs.getBoolean("butDataCollector", true);
		if (butDataCollector){
			
			Bundle bundle = intent.getExtras();			
			DataHandler handler = dataHandlers.lookup(bundle.getString("handler_key"));
			if (handler != null) {
				handler.saveData(bundle);
			}

		}
		else {

		}
		
	}

}
