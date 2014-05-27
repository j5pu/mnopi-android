package com.mnopi.mnopi;

import java.util.ArrayList;
import java.util.HashMap;

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.data.adapters.QueryAdapter;
import com.mnopi.data.dialogs.QueryDialog;
import com.mnopi.data.models.Query;
import com.mnopi.utils.Connectivity;
import com.mnopi.utils.ServerApi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
	private Boolean thereAreMoreQueries;
	private Boolean queriesIsEmpty;
	private String meta_next;
	private Boolean sendingQueries;
	private Context mContext;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewqueries);  

        if (savedInstanceState != null){
            queries = savedInstanceState.getParcelableArrayList("queries");
            meta_next = savedInstanceState.getString("meta_next");
        }else{
            queries = new ArrayList<Query>();
        }

		listQueries = (ListView) findViewById(R.id.listQueries);
		qAdapter = new QueryAdapter(this, R.layout.query_item, queries );
		listQueries.setAdapter(qAdapter);
		thereAreMoreQueries = true;
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
	            		&& thereAreMoreQueries) {
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


    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("queries", queries);
        outState.putString("meta_next", meta_next);
    }
	
	
	private class GetQueries extends AsyncTask<Void,Integer,Void> {

        private String sessionToken;
		
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
	    			thereAreMoreQueries = false;
	    		}
    			urlString = MnopiApplication.SERVER_ADDRESS + meta_next;
	    	}
	    	if (thereAreMoreQueries){
                sessionToken = AccountGeneral.blockingGetAuthToken(mContext);
		        
		        try{
                    HashMap<String, String> metaResponse = new HashMap<String, String>();
                    final ArrayList<Query> searchQueries =
                            ServerApi.getQueries(sessionToken, urlString, metaResponse);

                    meta_next = metaResponse.get("next");
                    queriesIsEmpty = Integer.parseInt(metaResponse.get("total_count")) == 0;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Query query : searchQueries) {
                                queries.add(query);
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
			qAdapter.notifyDataSetChanged();	
			sendingQueries = false;	
			
			// if an error occurred show the error
			
	    }		
	}
}

