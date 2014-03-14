package com.mnopi.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

/**
 * Data handler for web searches
 * @author alainez
 *
 */
public class WebSearchDataHandler extends DataHandler {

	// Constant key for the handler in the registry
	private static String HANDLER_KEY = "web_search"; 
	
	public WebSearchDataHandler(Context context){
		super(context);
	}
	
	@Override
	public void saveData(Bundle bundle) {
		
		String url = bundle.getString("url");
		String query = bundle.getString("query");
		String date = bundle.getString("date"); //TODO: Treat date as date instead of string
		
		ContentValues row = new ContentValues();
		row.put("url", url);
		row.put("query", query);
		row.put("date", date);
		
		db.insert(DataLogOpenHelper.WEB_SEARCHES_TABLE_NAME, null ,row);
	}

	@Override
	public void sendData() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Returns the handler key for looking up in the registry
	 * @return handler key
	 */
	public String getKey() {
		return HANDLER_KEY;
	}
	
}
