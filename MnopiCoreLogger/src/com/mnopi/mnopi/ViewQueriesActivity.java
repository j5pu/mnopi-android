package com.mnopi.mnopi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import org.json.JSONArray;
import org.json.JSONObject;

import com.mnopi.adapters.QueryAdapter;
import com.mnopi.dialogs.QueryDialog;
import com.mnopi.models.Query;
import com.mnopi.utils.Connectivity;



import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewQueriesActivity extends Activity{
	
	private MyApplication myApplication;
	private ProgressDialog progress;
	private ArrayList<Query> queries;
	private ListView listQueries;
	private QueryAdapter qAdapter;
	private Boolean there_are_more_queries;
	private String meta_next;
	private Boolean sendingQueries;
	private Context mContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewqueries);  
	
		myApplication = ((MyApplication) this.getApplication());
		queries = new ArrayList<Query>();
		listQueries = (ListView) findViewById(R.id.listQueries);
		qAdapter = new QueryAdapter(this, R.layout.query_item, queries );
		listQueries.setAdapter(qAdapter);
		there_are_more_queries = true;
		sendingQueries = false;
		mContext = this;
		progress = new ProgressDialog(this);
		progress.setTitle(R.string.getData);
		progress.setMessage(getResources().getString(R.string.wait_please));
		progress.setCancelable(false);  
		
		listQueries.setOnScrollListener(new OnScrollListener() {
	        @Override
	        public void onScroll(AbsListView view, int firstVisibleItem,
	                int visibleItemCount, int totalItemCount) {
	            // Check if the last view is visible
	            if ((++firstVisibleItem + visibleItemCount > totalItemCount) 
	            		&& there_are_more_queries) {
	            	if (!sendingQueries){
	            		if (Connectivity.isOnline(mContext)){
	            			new GetQueries().execute(); 
	            		}
	            		else{
	    					Toast toast = Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG);
	    					toast.show();
	    			    }	     			    		
	            	}     		
	            }
	        }
	        @Override
	        public void onScrollStateChanged(AbsListView view, int scrollState) {
	            // TODO Auto-generated method stub
	        }
	    });
		
		listQueries.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Query query = queries.get(position);
				QueryDialog qDialog = new QueryDialog(mContext, query);
				qDialog.setTitle(query.getQuery());
				qDialog.show();
			}
			
		});

	}
	
	
	private class GetQueries extends AsyncTask<Void,Integer,Void> {
		 
		private String session_token;
		
		@Override
		protected void onPreExecute(){		
			// Show ProgressDialog
			progress.show();
			sendingQueries = true;
		}
		
		@Override
	    protected Void doInBackground(Void... params) {
			
			String urlString = null;
	    	if (queries.size() == 0){
	    		urlString = myApplication.getSERVER_ADRESS() + "/api/v1/search_query/";
	    	}    
	    	else{
	    		if (meta_next.equals("null")){
	    			there_are_more_queries = false;
	    		}
    			urlString = myApplication.getSERVER_ADRESS() + meta_next;
	    	}
	    	HttpResponse response = null;
	        SharedPreferences prefs = getBaseContext().getSharedPreferences("MisPreferencias",
	        		getBaseContext().MODE_PRIVATE);
	        session_token = prefs.getString("session_token", null);
	        
	        try{
	             //HttpClient client = new DefaultHttpClient();
	             HttpClient httpclient = getNewHttpClient();	             
	             HttpGet getQueries = new HttpGet(urlString);
	             getQueries.setHeader("Content-Type", "application/json");
	             getQueries.setHeader("Session-Token", session_token);
	             
	             response = httpclient.execute(getQueries);
	             String respStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
	             
	             Log.i("WE", respStr);
	             JSONObject respJson = new JSONObject(respStr);
	             JSONObject respMetaJson = respJson.getJSONObject("meta");
				 meta_next = respMetaJson.getString("next");
				 
				 JSONArray queriesObject = respJson.getJSONArray("objects");
				 
				 for (int i = 0; i < queriesObject.length(); i++) {
					 JSONObject queryObject = queriesObject.getJSONObject(i);
					 final String date = queryObject.getString("date");
					 final String search_query = queryObject.getString("search_query");
					 final String resource_uri = queryObject.getString("resource_uri");
					 final String result = queryObject.getString("search_results");
					 final String dateFormated = date.substring(0, 10);
					 final String hour = date.substring(11, 19);
					 runOnUiThread(new Runnable() {
							@Override
							public void run() {					 
								 Query queryAux = new Query(resource_uri, search_query
										 , dateFormated, result, hour);
								 queries.add(queryAux);
							}
					 });		
				 }
	        }
	        catch (Exception ex){
	             Log.e("Debug", "error: " + ex.getMessage(), ex);
	        }
			return null;
	      
	    }
						
		@Override
	    protected void onPostExecute(Void result) {
			
			// hide ProgressDialog
			if (progress.isShowing()) {
		        progress.dismiss();
		    }
			qAdapter.notifyDataSetChanged();	
			sendingQueries = false;	
			
			// if an error occurred show the error
			
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

