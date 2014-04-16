package com.mnopi.mnopi;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ViewQueriesActivity extends Activity{
	
	private ProgressDialog progress;
	private ArrayList<Query> queries;
	private ListView listQueries;
	private QueryAdapter qAdapter;
	private Boolean there_are_more_queries;
	private Boolean queriesIsEmpty;
	private String meta_next;
	private Boolean sendingQueries;
	private Context mContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewqueries);  
	
		queries = new ArrayList<Query>();
		listQueries = (ListView) findViewById(R.id.listQueries);
		qAdapter = new QueryAdapter(this, R.layout.query_item, queries );
		listQueries.setAdapter(qAdapter);
		there_are_more_queries = true;
		queriesIsEmpty = false;
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
	            		if ((Connectivity.isOnline(mContext)) && (!queriesIsEmpty)){
	            			new GetQueries().execute(); 
	            		}
	            		else{
		            		if (!Connectivity.isOnline(mContext)){
		    					Toast toast = Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG);
		    					toast.show();
		    			    }	 
		            		else{
		            			Toast toast = Toast.makeText(mContext, R.string.no_data, Toast.LENGTH_LONG);
		            			toast.show();
		            		}
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
	    		urlString = MnopiApplication.SERVER_ADDRESS + MnopiApplication.SEARCH_QUERY_RESOURCE;
	    	}    
	    	else{
	    		if (meta_next.equals("null")){
	    			there_are_more_queries = false;
	    		}
    			urlString = MnopiApplication.SERVER_ADDRESS + meta_next;
	    	}
	    	if (there_are_more_queries){
		    	HttpResponse response = null;
		        SharedPreferences prefs = getBaseContext().getSharedPreferences(MnopiApplication.APPLICATION_PREFERENCES,
		        		getBaseContext().MODE_PRIVATE);
		        session_token = prefs.getString(MnopiApplication.SESSION_TOKEN, null);
		        
		        try{
		             //HttpClient client = new DefaultHttpClient();
		             HttpClient httpclient = Connectivity.getNewHttpClient();
		             HttpGet getQueries = new HttpGet(urlString);
		             getQueries.setHeader("Content-Type", "application/json");
		             getQueries.setHeader("Session-Token", session_token);
		             
		             response = httpclient.execute(getQueries);
		             String respStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		             
		             Log.i("WE", respStr);
		             JSONObject respJson = new JSONObject(respStr);
		             JSONObject respMetaJson = respJson.getJSONObject("meta");
					 meta_next = respMetaJson.getString("next");
					 queriesIsEmpty = respMetaJson.getInt("total_count") == 0;
					 
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
}

