package com.mnopi.data.dialogs;

import com.mnopi.mnopi.R;
import com.mnopi.data.models.Query;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class QueryDialog extends Dialog {

	private Query query;
	private TextView txtQuery;
	private TextView txtUrl;
	private TextView txtDate;
	private TextView txtHour;
	
	public QueryDialog(Context context, Query query) {
		super(context);
		
		this.query = query;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.query_dialog);     
        
        txtQuery = (TextView) findViewById(R.id.txtQuery);
        txtUrl = (TextView) findViewById(R.id.txtUrl);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtHour = (TextView) findViewById(R.id.txtHour);
        
        txtQuery.setText(this.query.getQuery());
        txtUrl.setText(this.query.getResult());
        txtDate.setText(this.query.getDate());
        txtHour.setText(this.query.getHour());
	}    
	
}
