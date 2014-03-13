package com.mnopi.mnopi;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.mnopi.data.DataHandler;
import com.mnopi.data.DataHandlerRegistry;
import com.mnopi.data.WebSearchDataHandler;

public class DummyService extends IntentService {
	
	DataHandlerRegistry dataHandlers = null;
	
	public DummyService() {
		super("DummyService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dataHandlers = new DataHandlerRegistry();
		
		WebSearchDataHandler webHandler = new WebSearchDataHandler(getApplicationContext());
		dataHandlers.bind(webHandler.getKey(), webHandler);
	}

	@Override
	protected void onHandleIntent(Intent intent){
		Bundle bundle = intent.getExtras();
		
		// If the data handler doesn't exist, the storage of the data has been disallowed
		DataHandler handler = dataHandlers.lookup(bundle.getString("handler_key"));
		if (handler != null) {
			handler.saveData(bundle);
		}
	}
}
