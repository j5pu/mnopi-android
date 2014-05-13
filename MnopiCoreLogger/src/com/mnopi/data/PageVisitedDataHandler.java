package com.mnopi.data;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.authentication.MnopiAuthenticator;
import com.mnopi.data.DataProvider.PageVisited;
import com.mnopi.mnopi.MnopiApplication;
import com.mnopi.mnopi.R;
import com.mnopi.utils.Connectivity;

/**
 * Data handler for visited pages
 * @author alainez
 *
 */
public class PageVisitedDataHandler extends DataHandler {

	private static String HANDLER_KEY = "page_visited";

    private boolean saveHtmlVisited = true;

    private Context context;

	public PageVisitedDataHandler(Context context){
        super(context);
        this.context = context;
	}
	
	@Override
	public void saveData(Bundle bundle) {
		//Guardar mediante contentProvider
		Uri pageVisitedUri = DataProvider.PAGE_VISITED_URI;
		ContentResolver cr = context.getContentResolver();
		ContentValues row = new ContentValues();
		String url = bundle.getString("url");
        String date = bundle.getString("date");
        if (saveHtmlVisited) {
            String htmlCode = bundle.getString("html_code");
            row.put("html_code", htmlCode);
        }
        row.put("url", url);
        row.put("date", date);
        cr.insert(pageVisitedUri, row);
		//Guardar mediante contentProvider
		
//        ContentValues row = new ContentValues();
//        String url = bundle.getString("url");
//        String date = bundle.getString("date");
//        if (saveHtmlVisited) {
//            String htmlCode = bundle.getString("html_code");
//            row.put("html_code", htmlCode);
//        }
//        row.put("url", url);
//        row.put("date", date);
//			
//        db.insert(DataLogOpenHelper.VISITED_WEB_PAGES_TABLE_NAME, null ,row);
	}

	@Override
	public void sendData() {
		new SendPageVisited().execute();
	}

    public void setSaveHtmlVisited(boolean saveHtmlVisited) {
        this.saveHtmlVisited = saveHtmlVisited;
    }

	/**
	 * Returns the handler key for looking up in the registry
	 * @return
	 */
	public static String getKey() {
		return HANDLER_KEY;
	}
	
	private class SendPageVisited extends AsyncTask<Void, Void, Void>{
		
		private String result = null;
		private String reason;
		private boolean hasResultError;
		private String resultError;
		private boolean anyError;
		private boolean dataExists;

		@Override
		protected void onPreExecute(){		
			hasResultError = false;
			anyError = false;
			dataExists = false;
		}
		
		@Override
		protected Void doInBackground(Void... params){

			/* get data from content provider
			 */
			String[] projection = new String[]{
				PageVisited.COL_URL,
				PageVisited.COL_DATE,
				PageVisited.COL_HTML_CODE
			};
			Uri pageVisitedUri = DataProvider.PAGE_VISITED_URI;
			ContentResolver cr = context.getContentResolver();
			
			Cursor cursor = cr.query(pageVisitedUri, projection, null, null, null);
			
	        String urlString = MnopiApplication.SERVER_ADDRESS + MnopiApplication.PAGE_VISITED_RESOURCE;
			SharedPreferences prefs = context.getSharedPreferences(
                    MnopiApplication.APPLICATION_PREFERENCES, context.MODE_PRIVATE);
			
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
				             
				            JSONObject jsonObject = new JSONObject();

                            AccountManager mAccountManager = AccountManager.get(context);
                            Account[] mnopiAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                            // Only one account
                            String userResource = mAccountManager.getUserData(mnopiAccounts[0], MnopiAuthenticator.KEY_USER_RESOURCE);

                            //String userResource = prefs.getString(MnopiApplication.USER_RESOURCE, null);
				            String url = cursor.getString(cursor.getColumnIndex ("url"));
							String date = cursor.getString(cursor.getColumnIndex ("date"));		
							String html_code = cursor.getString(cursor.getColumnIndex ("html_code"));
							
							jsonObject.put("user", userResource);
							jsonObject.put("url", url);
							jsonObject.put("html_code", html_code);
							jsonObject.put("date", date);
							
							StringEntity entity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
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
											hasResultError = true;
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
			
            if (anyError) {
                Toast.makeText(context, R.string.error_occurred, Toast.LENGTH_SHORT).show();
            } else {
                //TODO: Delete only the sent elements
                db.delete(DataLogOpenHelper.VISITED_WEB_PAGES_TABLE_NAME, null, null);
            }
		}
	}

}
