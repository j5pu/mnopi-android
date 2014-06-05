package com.mnopi.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.data.handlers.DataHandler;
import com.mnopi.data.handlers.PageVisitedDataHandler;
import com.mnopi.data.handlers.WebSearchDataHandler;
import com.mnopi.mnopi.MnopiApplication;


public class DataCollectorService extends IntentService {

	public DataCollectorService() {
		super(DataCollectorService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();

        // Only save if account is allowed and general permission is set
        if (isAllowed(MnopiApplication.RECEIVE_IS_ALLOWED) && AccountGeneral.isLogged(this)){
            String handlerKey = bundle.getString("handler_key");
            DataHandler handler = null;

            if (handlerKey.equals(WebSearchDataHandler.HANDLER_KEY)) {
                if (isAllowed(handlerKey)) {
                    handler = new WebSearchDataHandler(this);
                }
            } else if (handlerKey.equals(PageVisitedDataHandler.HANDLER_KEY)) {
                if (isAllowed(handlerKey)) {
                    handler = new PageVisitedDataHandler(this);

                    ((PageVisitedDataHandler) handler).setSaveHtmlVisited(
                            isAllowed(PageVisitedDataHandler.HTML_VISITED_KEY));
                }
            }

            if (handler != null) {
                handler.saveData(bundle);
            }
        }
	}
    public boolean isAllowed(String permission) {
        SharedPreferences settings = this.getSharedPreferences(
                MnopiApplication.PERMISSIONS_PREFERENCES, Context.MODE_PRIVATE);

        return settings.getBoolean(permission, true);
    }

}
