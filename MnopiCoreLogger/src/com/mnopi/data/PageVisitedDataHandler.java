package com.mnopi.data;

import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mnopi.mnopi.MySSLSocketFactory;
import com.mnopi.mnopi.R;

/**
 * Data handler for visited pages
 * @author alainez
 *
 */
public class PageVisitedDataHandler extends DataHandler {

	private static String HANDLER_KEY = "page_visited";
	
	
	
	public PageVisitedDataHandler(Context context){
		super(context);
	}
	
	@Override
	public void saveData(Bundle bundle) {
		
		String url = bundle.getString("url");
		String htmlCode = bundle.getString("html_code");
		String date = bundle.getString("date");

		ContentValues row = new ContentValues();
		row.put("url", url);
		row.put("html_code", htmlCode);
		row.put("date", date);
		
		db.insert(DataLogOpenHelper.VISITED_WEB_PAGES_TABLE_NAME, null ,row);
	}

	@Override
	public void sendData() {
		new SendPageVisited().execute();
	}
	
	/**
	 * Returns the handler key for looking up in the registry
	 * @return
	 */
	public String getKey() {
		return HANDLER_KEY;
	}
	
	private class SendPageVisited extends AsyncTask<Void, Void, Void>{
		
		private String result = null;
		private String reason;
		private boolean hasResultError;
		private String resultError;
		private boolean anyError;

		@Override
		protected void onPreExecute(){		
			hasResultError = false;
			anyError = false;
		}
		
		@Override
		protected Void doInBackground(Void... params){
			
	        String urlString = "https://ec2-54-197-231-98.compute-1.amazonaws.com/api/v1/page_visited/";
			Cursor cursor = db.query(DataLogOpenHelper.VISITED_WEB_PAGES_TABLE_NAME, null, null, null, null, null, null);
			SharedPreferences prefs = context.getSharedPreferences("MisPreferencias",
					context.MODE_PRIVATE);
			
			if(cursor != null){
				if(cursor.moveToFirst()){
					do {
						HttpEntity resEntity;

				        try{
							String session_token = prefs.getString("session_token", null);
				            HttpClient httpclient = getNewHttpClient();	             
				            HttpPost post = new HttpPost(urlString);
				            post.setHeader("Content-Type", "application/json");
				            post.setHeader("Session-Token", session_token);
				             
				            JSONObject jsonObject = new JSONObject();	
				            
				            String user = prefs.getString("user_resource", null);
				            String url = cursor.getString(0);
							String date = cursor.getString(1);		
							String html_code = cursor.getString(2);
							
							jsonObject.put("user", user);
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
	        
	        return null;
		}
		
		@Override
	    protected void onPostExecute(Void result) {
			
			db.delete(DataLogOpenHelper.VISITED_WEB_PAGES_TABLE_NAME, null, null);
			// show information about the data sent
				if (anyError){
					Toast.makeText(context, R.string.error_occurred, Toast.LENGTH_SHORT).show();
				}else if (!hasResultError){
					Toast.makeText(context, R.string.sent_succesful, Toast.LENGTH_SHORT).show();
					
					//TODO: when POST lists is ready, think about what to delete and how
					db.delete(DataLogOpenHelper.VISITED_WEB_PAGES_TABLE_NAME, null, null);
					
				}else{
					Toast.makeText(context, R.string.error_sending_data, Toast.LENGTH_SHORT).show();
				}
		}
	}
	
	public HttpClient getNewHttpClient() {
	     try {
	            KeyStore trustStore = KeyStore.getInstance(KeyStore
	                    .getDefaultType());
	            trustStore.load(null, null);

	            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	            HttpParams params = new BasicHttpParams();
	            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	            SchemeRegistry registry = new SchemeRegistry();
	            registry.register(new Scheme("http", PlainSocketFactory
	                    .getSocketFactory(), 80));
	            registry.register(new Scheme("https", sf, 443));



	            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
	                    params, registry);

	            return new DefaultHttpClient(ccm, params);
	        } catch (Exception e) {
	            return new DefaultHttpClient();
	        }
	    }
	
}
