package com.mnopi.services;

import java.util.ArrayList;

import com.mnopi.data.DataHandler;
import com.mnopi.data.DataHandlerRegistry;
import com.mnopi.data.WebSearchDataHandler;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


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
		dataHandlers.bind(webHandler.getKey(), webHandler);
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
