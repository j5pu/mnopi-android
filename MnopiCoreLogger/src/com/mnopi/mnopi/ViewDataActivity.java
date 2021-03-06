package com.mnopi.mnopi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewDataActivity extends Activity{
	
	private Button btnViewQueries;
	private Button btnViewPagesVisited;
    private Button btnViewQueriesDevice;
    private Button btnViewPagesVisitedDevice;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewdata);  
        
        btnViewQueries = (Button) findViewById(R.id.btnViewQueries);
        btnViewPagesVisited = (Button) findViewById(R.id.btnPagesVisited);
        btnViewQueriesDevice = (Button) findViewById(R.id.btnViewQueriesDevice);
        btnViewPagesVisitedDevice = (Button) findViewById(R.id.btnPagesVisitedDevice);
        
        btnViewQueries.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent(ViewDataActivity.this,
						ViewQueriesActivity.class);
				startActivity(intent);
        	}        	
        });
        
        btnViewPagesVisited.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent(ViewDataActivity.this,
        				ViewPagesVisitedActivity.class);
				startActivity(intent);
        	}        	
        });

        btnViewQueriesDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ViewDataActivity.this,
                        ViewQueriesDeviceActivity.class);
                startActivity(intent);
            }
        });

        btnViewPagesVisitedDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ViewDataActivity.this,
                        ViewPagesVisitedDeviceActivity.class);
                startActivity(intent);
            }
        });
       
	}
}
