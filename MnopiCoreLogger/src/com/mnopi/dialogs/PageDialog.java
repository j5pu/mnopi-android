package com.mnopi.dialogs;

import java.util.ArrayList;

import com.mnopi.mnopi.R;
import com.mnopi.models.PageVisited;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class PageDialog extends Dialog{

	private PageVisited pageVisited;
	private TextView txtDomain;
	private TextView txtUrl;
	private TextView txtDate;
	private TextView txtHour;
	private TextView txtCategories;
	
	public PageDialog(Context context, PageVisited page) {
		super(context);

		this.pageVisited = page;		
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_dialog);     
        
        txtDomain = (TextView) findViewById(R.id.txtDomain);
        txtUrl = (TextView) findViewById(R.id.txtUrl);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtHour = (TextView) findViewById(R.id.txtHour);
        txtCategories = (TextView) findViewById(R.id.txtCategories);
        
        txtDomain.setText(this.pageVisited.getDomain());
        txtUrl.setText(this.pageVisited.getUrl());
        txtDate.setText(this.pageVisited.getDate());
        txtHour.setText(this.pageVisited.getHour());
        ArrayList<String> categories = this.pageVisited.getCategories();
        String strCategories = "";
        if (!categories.isEmpty()){
        	for (int i=0; i<categories.size(); i++) {
    		    String category = categories.get(i);
    		    if (i == categories.size()-1){
    		    	strCategories = strCategories + category;
    		    }
    		    else {
    		    	strCategories = strCategories + category + ", ";
    		    }
        	}	
        	txtCategories.setText(strCategories);
        }
	}    
}
