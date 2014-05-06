package com.mnopi.mnopi;

import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.mnopi.adapters.PageAdapter;
import com.mnopi.dialogs.PageDialog;
import com.mnopi.models.PageVisited;
import com.mnopi.utils.Connectivity;

public class ViewPagesVisitedActivity extends Activity{
	
	private ProgressDialog progress;
	private ArrayList<PageVisited> pages;
	private ListView listPages;
	private PageAdapter pAdapter;
	private Boolean there_are_more_pages;
	private Boolean pagesIsEmpty;
	private String meta_next;
	private Boolean sendingPages;
	private Context mContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpages);

        if (savedInstanceState != null){
            pages = savedInstanceState.getParcelableArrayList("pages");
            meta_next = savedInstanceState.getString("meta_next");
        }else{
            pages = new ArrayList<PageVisited>();
        }

        listPages = (ListView) findViewById(R.id.listPages);
        pAdapter = new PageAdapter(this, R.layout.page_item, pages);
		listPages.setAdapter(pAdapter);
		there_are_more_pages = true;
		pagesIsEmpty = false;
		sendingPages = false;
		mContext = this;
		progress = new ProgressDialog(this);
		progress.setTitle(R.string.getData);
		progress.setMessage(getResources().getString(R.string.wait_please));
		progress.setCancelable(false);  
		
		listPages.setOnScrollListener(new OnScrollListener() {
	        @Override
	        public void onScroll(AbsListView view, int firstVisibleItem,
	                int visibleItemCount, int totalItemCount) {
	            // Check if the last view is visible
	            if ((++firstVisibleItem + visibleItemCount > totalItemCount) 
	            		&& there_are_more_pages) {
	            	if (!sendingPages){
	            		if ((Connectivity.isOnline(mContext)) && (!pagesIsEmpty)){
	            			new GetPages().execute(); 
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
	        }
	    });
		
		listPages.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				PageVisited page = pages.get(position);
				if (page.getCategories().size() != 0){
					PageDialog pDialog = new PageDialog(mContext, page);
					pDialog.setTitle(page.getDomain());
					pDialog.show();
				}
				else{
					if (Connectivity.isOnline(mContext)){
	        			new GetCategories().execute(page); 
	        		}
	        		else{
						Toast toast = Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG);
						toast.show();
				    }
				}	
				
			}
			
		});
	}

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("pages", pages);
        outState.putString("meta_next", meta_next);
    }
	
	private class GetPages extends AsyncTask<Void,Integer,Void> {
		 
		private String session_token;
		
		@Override
		protected void onPreExecute(){		
			// Show ProgressDialog
			progress.show();
			sendingPages = true;
		}
		
		@Override
	    protected Void doInBackground(Void... params) {
			
			String urlString = null;
	    	if (pages.size() == 0){
	    		urlString = MnopiApplication.SERVER_ADDRESS + MnopiApplication.PAGE_VISITED_RESOURCE;
	    	}    
	    	else{
	    		if (meta_next.equals("null")){
	    			there_are_more_pages = false;
	    		}
    			urlString = MnopiApplication.SERVER_ADDRESS + meta_next;
	    	}
	    	if (there_are_more_pages){
		    	HttpResponse response = null;
		        SharedPreferences prefs = getBaseContext().getSharedPreferences(MnopiApplication.APPLICATION_PREFERENCES,
		        		getBaseContext().MODE_PRIVATE);
		        session_token = prefs.getString(MnopiApplication.SESSION_TOKEN, null);
		        
		        try{
		             HttpClient httpclient = Connectivity.getNewHttpClient();
		             HttpGet getPages = new HttpGet(urlString);
		             getPages.setHeader("Content-Type", "application/json");
		             getPages.setHeader("Session-Token", session_token);
		             
		             response = httpclient.execute(getPages);
		             String respStr = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		             
		             Log.i("WE", respStr);
		             JSONObject respJson = new JSONObject(respStr);
		             JSONObject respMetaJson = respJson.getJSONObject("meta");
					 meta_next = respMetaJson.getString("next");
					 pagesIsEmpty = respMetaJson.getInt("total_count") == 0;
					 
					 JSONArray queriesObject = respJson.getJSONArray("objects");
					 
					 for (int i = 0; i < queriesObject.length(); i++) {
						 JSONObject queryObject = queriesObject.getJSONObject(i);
						 final String date = queryObject.getString("date");
						 final String url = queryObject.getString("page_visited");
						 final String resource_uri = queryObject.getString("resource_uri");
						 
						 // get domain, dateFormated, hour, categories
						 String uri = url;
						 if(!url.startsWith("http") && !url.startsWith("https")){
						     uri = "http://" + url;
						 }        
						 URL netUrl = new URL(uri);
						 String host = netUrl.getHost();
						 if(host.startsWith("www")){
						    host = host.substring("www".length()+1);
						 }
						 final String domain = host;
						 final String dateFormated = date.substring(0, 10);
						 final String hour = date.substring(11, 19);
						              
		            	 ArrayList<String> categoriesAux = new ArrayList<String>();
			             final ArrayList<String> categories = categoriesAux;
						 runOnUiThread(new Runnable() {
								@Override
								public void run() {					 
									 PageVisited pageAux = new PageVisited(url, domain, dateFormated, hour
											 , resource_uri, categories );
									 pages.add(pageAux);
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
			pAdapter.notifyDataSetChanged();	
			sendingPages = false;	
			
			// if an error occurred show the error
			
	    }		
	}
	
	private class GetCategories extends AsyncTask<PageVisited,Integer,Void> {
		 
		private String session_token;
		
		@Override
		protected void onPreExecute(){	
		}
		
		@Override
	    protected Void doInBackground(PageVisited... params) {
			final PageVisited page = params[0];
			String urlString = MnopiApplication.SERVER_ADDRESS + page.getResource_uri()
					+ "categories";
	    	HttpResponse response = null;
	        SharedPreferences prefs = getBaseContext().getSharedPreferences(MnopiApplication.APPLICATION_PREFERENCES,
	        		getBaseContext().MODE_PRIVATE);
	        session_token = prefs.getString(MnopiApplication.SESSION_TOKEN, null);
	        
	        try{
	             HttpClient httpclient = Connectivity.getNewHttpClient();
	             HttpGet getCategories = new HttpGet(urlString);
	             getCategories.setHeader("Content-Type", "application/json");
	             getCategories.setHeader("Session-Token", session_token);
	             
	             response = httpclient.execute(getCategories);
	             String respStrCategories = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
	             JSONArray respJsonCat = new JSONArray(respStrCategories);
            	 ArrayList<String> categoriesAux = new ArrayList<String>();
	             if (respJsonCat.length() != 0){
	            	 for (int j=0; j<respJsonCat.length(); j++) {
	            		    String category = respJsonCat.getString(j); 
	            		    categoriesAux.add(category);
	            	 }	    	            		    
	             }				 
	             page.setCategories(categoriesAux);
	             runOnUiThread(new Runnable() {
						@Override
						public void run() {	
				 			 PageDialog pDialog = new PageDialog(mContext, page);
							 pDialog.setTitle(page.getDomain());
							 pDialog.show();
						}
	             });		
	        }
	        catch (Exception ex){
	             Log.e("Debug", "error: " + ex.getMessage(), ex);
	        }
			return null;
	      
	    }
						
		@Override
	    protected void onPostExecute(Void result) {	
	    }		
	}

}
