package com.mnopi.mnopi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewDataActivity extends Activity{
	
	private Button btnViewQueries;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewdata);  
        
        btnViewQueries = (Button) findViewById(R.id.btnViewQueries);
        
        btnViewQueries.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent(ViewDataActivity.this,
						ViewQueriesActivity.class);
				startActivity(intent);
        	}        	
        });
        
	}
}
