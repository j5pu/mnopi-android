package com.example.dummy_search;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.dummysearch.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class SendSearches extends Activity{
	private Button btnSend;
	private EditText inputQuerySearch;
	private EditText inputSearchResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_searches);
				
		inputQuerySearch = (EditText) findViewById(R.id.search);
		inputSearchResult = (EditText) findViewById(R.id.result);
		btnSend = (Button) findViewById(R.id.btnSend);
		
		
		
        btnSend.setOnClickListener(new View.OnClickListener() {          
                       
			@Override
			public void onClick(View v) {       	
        	Calendar c = Calendar.getInstance();
        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String formattedDate = df.format(c.getTime());		
			String query_search = inputQuerySearch.getText().toString();
	        String search_result = inputSearchResult.getText().toString();        
			Intent myIntent = new Intent("com.mnopi.services.DataCollectorService");
			myIntent.putExtra("search_query", query_search);
			myIntent.putExtra("search_results", search_result);
			myIntent.putExtra("date", formattedDate);
			myIntent.putExtra("handler_key", "web_search");
			
			startService(myIntent);
			}
        });	
	}
}
