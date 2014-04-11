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
import com.mnopi.mnopi.MyApplication;


public class DataCollectorService extends IntentService {

	DataHandlerRegistry dataHandlers = null;
	
	public DataCollectorService() {
		super(DataCollectorService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
        //android.os.Debug.waitForDebugger();
		super.onCreate();
        dataHandlers = DataHandlerRegistry.getInstance(MyApplication.RECEIVE_FROM_SERVICE_REGISTRY);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        dataHandlers.saveData(bundle.getString("handler_key"), bundle);
	}

}
