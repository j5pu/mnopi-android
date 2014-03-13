package com.mnopi.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


public class DataCollectorService extends IntentService {

	public DataCollectorService() {
		super(DataCollectorService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		SharedPreferences prefs = getSharedPreferences(
				"MisPreferencias", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		Boolean butDataCollector = prefs.getBoolean("butDataCollector", true);
		if (butDataCollector){
			Log.d("WE","ME HA LLEGADO EL STRING: " + arg0.getExtras().getString("Prueba"));			
			editor.putString("data", arg0.getExtras().getString("Prueba"));
			editor.commit();
		}
		else {
			editor.putString("data", "");
			editor.commit();
		}
		
	}

}
