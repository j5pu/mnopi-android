package com.mnopi.dummy;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mnopi.mnopiapi.PageValidationException;
import com.mnopi.mnopiapi.PageVisitedSender;
import com.mnopi.mnopiapi.SearchSender;
import com.mnopi.mnopiapi.SearchValidationException;


public class SendData extends Activity{
	private Button btnSendSearch;
	private Button btnSendPage;
	private Button btnOpenBrowser;
	
	private EditText inputQuerySearch;
	private EditText inputSearchResult;
	
	private EditText inputUrlVisited;
	private EditText inputHtmlVisited;

    private SearchSender searchSender;
    private PageVisitedSender pageSender;

    private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_data);
        context = this;
				
		inputQuerySearch = (EditText) findViewById(R.id.search);
		inputSearchResult = (EditText) findViewById(R.id.result);
		btnSendSearch = (Button) findViewById(R.id.btnSend);
		
		inputUrlVisited= (EditText) findViewById(R.id.url_page_visited);
		inputHtmlVisited = (EditText) findViewById(R.id.html_page_visited);
		btnSendPage = (Button) findViewById(R.id.btn_send_page_visited);
		btnOpenBrowser = (Button) findViewById(R.id.btn_open_browser);

        searchSender = new SearchSender(this);
        pageSender = new PageVisitedSender(this);
		
        btnSendSearch.setOnClickListener(new View.OnClickListener() {          
                       
			@Override
			public void onClick(View v) {       	
				String querySearch = inputQuerySearch.getText().toString();
				String searchResult = inputSearchResult.getText().toString();

                try {
                    searchSender.send(querySearch, searchResult);
                    Toast.makeText(context, "Search query sent", Toast.LENGTH_SHORT).show();
                } catch (SearchValidationException ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
			}
        });
        
        btnSendPage.setOnClickListener(new View.OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		String urlVisited = inputUrlVisited.getText().toString();
        		String htmlVisited = inputHtmlVisited.getText().toString();

                try {
                    pageSender.send(urlVisited, htmlVisited);
                    Toast.makeText(context, "Page visited sent", Toast.LENGTH_SHORT).show();
                } catch (PageValidationException ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
