package com.mnopi.data;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.authentication.MnopiAuthenticator;
import com.mnopi.data.DataProvider.PageVisited;
import com.mnopi.data.DataProvider.WebSearch;
import com.mnopi.mnopi.MnopiApplication;
import com.mnopi.mnopi.R;
import com.mnopi.utils.Connectivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Data handler for web searches
 * @author alainez
 *
 */
public class WebSearchDataHandler extends DataHandler {
	
	private static String HANDLER_KEY = "web_search";
    private Context context;

	public WebSearchDataHandler(Context c){
		super(c);
        this.context = c;
	}
	
	@Override
	public void saveData(Bundle bundle) {		
		//Save data to contentProvider
		Uri webSearchUri = DataProvider.WEB_SEARCH_URI;
		ContentResolver cr = context.getContentResolver();
		ContentValues row = new ContentValues();
		String url = bundle.getString("search_results");
        String date = bundle.getString("date");
        String query = bundle.getString("search_query");
        row.put("url", url);
        row.put("date", date);
        row.put("query", query);
        cr.insert(webSearchUri, row);

	}

	@Override
	public void sendData() {
		new SendWebSearch().execute();
	}
	
	/**
	 * Returns the handler key for looking up in the registry
	 * @return handler key
	 */
	public static String getKey() {
		return HANDLER_KEY;
	}
	
	private class SendWebSearch extends AsyncTask<Void, Void, Void>{
		
		private String result = null;
		private String reason;
		private String resultError;
		private boolean anyError;
		private boolean dataExists;

		@Override
		protected void onPreExecute(){		
			anyError = false;
			dataExists = false;
		}
		
		@Override
		protected Void doInBackground(Void... params){

			/* get data from content provider
			 */
			String[] projection = new String[]{
				WebSearch.COL_QUERY,
				WebSearch.COL_URL,
				WebSearch.COL_DATE
			};
			Uri webSearchUri = DataProvider.WEB_SEARCH_URI;
			ContentResolver cr = context.getContentResolver();
			
			Cursor cursor = cr.query(webSearchUri, projection, null, null, null);
			
	        String urlString = MnopiApplication.SERVER_ADDRESS + MnopiApplication.SEARCH_QUERY_RESOURCE;
			SharedPreferences prefs = context.getSharedPreferences(MnopiApplication.APPLICATION_PREFERENCES,
					context.MODE_PRIVATE);
			
			if(cursor != null){
				if(cursor.moveToFirst()){
					do {
						HttpEntity resEntity;

				        try{
							String session_token = prefs.getString(MnopiApplication.SESSION_TOKEN, null);
				            HttpClient httpclient = Connectivity.getNewHttpClient();
				            HttpPost post = new HttpPost(urlString);
				            post.setHeader("Content-Type", "application/json");
				            post.setHeader("Session-Token", session_token);
				             
				            JSONObject dato = new JSONObject();

                            AccountManager mAccountManager = AccountManager.get(context);
                            Account[] mnopiAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                            // Only one account
                            String userResource = mAccountManager.getUserData(mnopiAccounts[0], MnopiAuthenticator.KEY_USER_RESOURCE);
				            
				            //String user = prefs.getString(MnopiApplication.USER_RESOURCE, null);
				            String query = cursor.getString(cursor.getColumnIndex ("query"));
							String url = cursor.getString(cursor.getColumnIndex ("url"));		
							String date = cursor.getString(cursor.getColumnIndex ("date"));
							
							dato.put("user", userResource);
							dato.put("search_query", query);
							dato.put("search_results", url);
							dato.put("date", date);
							
							StringEntity entity = new StringEntity(dato.toString(), HTTP.UTF_8);
				             post.setEntity(entity);

				             HttpResponse response = httpclient.execute(post);
				             resEntity = response.getEntity();
				             final String response_str = EntityUtils.toString(resEntity);
				             int status = response.getStatusLine().getStatusCode();
				             if (resEntity != null) {
				                 if (status != 201){				   
				                	 JSONObject respJSON = new JSONObject(response_str);
					                 // check the result field
					                 result = respJSON.getString("result"); 
					                 if (result.equals("ERR")){
					                	 if (respJSON.has("reason")){
						                	 	reason = respJSON.getString("reason");
						                	 	resultError = respJSON.getString("erroneous_parameters");
								                Log.i("Send data","Error " + reason + ": " + resultError);
					                	 }
					                 }
				                 }else{
				                	 Log.i("Send data", "OK");
				                	 dataExists = true;
				                 }
				                 			                 
				             }
				        }
				        catch (Exception ex){
				             Log.e("Debug", "error: " + ex.getMessage(), ex);
				             anyError = true;
				        }
				        
				    } while (cursor.moveToNext());
				}
			}
            cursor.close();
	        return null;
		}
		
		@Override
	    protected void onPostExecute(Void result) {
            if (anyError){
                Toast.makeText(context, R.string.error_occurred, Toast.LENGTH_SHORT).show();
            } else {
                db.delete(DataLogOpenHelper.WEB_SEARCHES_TABLE_NAME, null, null);
            }
		}
	}

}
