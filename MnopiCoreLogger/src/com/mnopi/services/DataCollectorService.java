package com.mnopi.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.mnopi.data.DataHandlerRegistry;
import com.mnopi.mnopi.MnopiApplication;


public class DataCollectorService extends IntentService {

	DataHandlerRegistry dataHandlers = null;
	
	public DataCollectorService() {
		super(DataCollectorService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
        //android.os.Debug.waitForDebugger();
		super.onCreate();

        // Handlers must be recreated if the main activity was destroyed
        if (!DataHandlerRegistry.isUsed()) {
            MnopiApplication.initHandlerRegistries(this);
        }

        dataHandlers = DataHandlerRegistry.getInstance(MnopiApplication.RECEIVE_FROM_SERVICE_REGISTRY);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        dataHandlers.saveData(bundle.getString("handler_key"), bundle);
	}

}
