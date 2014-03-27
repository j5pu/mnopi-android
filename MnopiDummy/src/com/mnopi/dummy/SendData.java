package com.mnopi.dummy;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;



public class SendData extends Activity{
	private Button btnSendSearch;
	private Button btnSendPage;
	private Button btnOpenBrowser;
	
	private EditText inputQuerySearch;
	private EditText inputSearchResult;
	
	private EditText inputUrlVisited;
	private EditText inputHtmlVisited;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_data);
				
		inputQuerySearch = (EditText) findViewById(R.id.search);
		inputSearchResult = (EditText) findViewById(R.id.result);
		btnSendSearch = (Button) findViewById(R.id.btnSend);
		
		inputUrlVisited= (EditText) findViewById(R.id.url_page_visited);
		inputHtmlVisited = (EditText) findViewById(R.id.html_page_visited);
		btnSendPage = (Button) findViewById(R.id.btn_send_page_visited);
		btnOpenBrowser = (Button) findViewById(R.id.btn_open_browser);
		
        btnSendSearch.setOnClickListener(new View.OnClickListener() {          
                       
			@Override
			public void onClick(View v) {       	
				Calendar c = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				String formattedDate = df.format(c.getTime());		
				String querySearch = inputQuerySearch.getText().toString();
				String searchResult = inputSearchResult.getText().toString();        
				
				Intent searchIntent = new Intent("com.mnopi.services.DataCollectorService");
				searchIntent.putExtra("search_query", querySearch);
				searchIntent.putExtra("search_results", searchResult);
				searchIntent.putExtra("date", formattedDate);
				searchIntent.putExtra("handler_key", "web_search");

				startService(searchIntent);
			}
        });
        
        btnSendPage.setOnClickListener(new View.OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		Calendar c = Calendar.getInstance();
        		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        		
        		String formattedDate = df.format(c.getTime());
        		String urlVisited = inputUrlVisited.getText().toString();
        		String htmlVisited = inputHtmlVisited.getText().toString();
        		
        		Intent pageIntent = new Intent("com.mnopi.services.DataCollectorService");
        		pageIntent.putExtra("url", urlVisited);
        		pageIntent.putExtra("html_code", htmlVisited);
        		pageIntent.putExtra("date", formattedDate);
        		pageIntent.putExtra("handler_key", "page_visited");
        		
        		startService(pageIntent);
        	}
        });
        
        btnOpenBrowser.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(SendData.this, WebActivity.class);
				startActivity(i);
			}

        });
        
	}
}
