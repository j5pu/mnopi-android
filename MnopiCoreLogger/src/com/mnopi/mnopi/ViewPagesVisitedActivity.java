package com.mnopi.mnopi;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.data.adapters.PageAdapter;
import com.mnopi.data.dialogs.PageDialog;
import com.mnopi.data.models.PageVisited;
import com.mnopi.utils.Connectivity;
import com.mnopi.utils.ServerApi;

public class ViewPagesVisitedActivity extends Activity{
	
	private ProgressDialog progress;
	private ArrayList<PageVisited> pages;
	private ListView listPages;
	private PageAdapter pAdapter;
	private Boolean thereAreMorePages;
	private Boolean pagesIsEmpty;
	private String meta_next;
	private Boolean sendingPages;
	private Context mContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpages);

        pages = new ArrayList<PageVisited>();
        listPages = (ListView) findViewById(R.id.listPages);
        pAdapter = new PageAdapter(this, R.layout.page_item, pages);
		listPages.setAdapter(pAdapter);
		thereAreMorePages = true;
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
	            		&& thereAreMorePages) {
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

	private class GetPages extends AsyncTask<Void,Integer,Void> {
		 
		private String sessionToken;
		
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
	    	} else {
	    		if (meta_next.equals("null")){
	    			thereAreMorePages = false;
	    		}
    			urlString = MnopiApplication.SERVER_ADDRESS + meta_next;
	    	}

	    	if (thereAreMorePages){
		        sessionToken = AccountGeneral.blockingGetAuthToken(mContext);
		        try{

                    HashMap<String, String> metaResponse = new HashMap<String, String>();
                    final ArrayList<PageVisited> pagesVisited =
                            ServerApi.getPagesVisited(sessionToken, urlString, metaResponse);

                    meta_next = metaResponse.get("next");
                    pagesIsEmpty = Integer.parseInt(metaResponse.get("total_count")) == 0;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (PageVisited page : pagesVisited) {
                                pages.add(page);
                            }
                        }
					});
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
		 
		private String authToken;
		
		@Override
		protected void onPreExecute(){	
		}
		
		@Override
	    protected Void doInBackground(PageVisited... params) {
            final PageVisited page = params[0];
			String urlString = MnopiApplication.SERVER_ADDRESS + page.getResource_uri()
					+ "categories";
            authToken = AccountGeneral.blockingGetAuthToken(mContext);
            try{
                 final ArrayList<String> categories = ServerApi.getCategories(authToken, urlString);
	             page.setCategories(categories);
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
