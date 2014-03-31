package com.mnopi.mnopi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class PermissionActivity extends Activity{
	private ToggleButton butPagesVisited;
	private ToggleButton butSearchQueries;
	private ToggleButton butHtmlVisited;
	private Context mContext;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission);
        
        mContext = this;
        butPagesVisited = (ToggleButton) findViewById(R.id.butPagesVisited);
        butSearchQueries = (ToggleButton) findViewById(R.id.butSearchQueries);
        butHtmlVisited = (ToggleButton) findViewById(R.id.butHtmlVisited);
        
        butPagesVisited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked){
                	butHtmlVisited.setChecked(false);
                }
            }
        });
        
        butHtmlVisited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!butPagesVisited.isChecked()){
                	butHtmlVisited.setChecked(false);
                	Toast.makeText(mContext, R.string.pages_visited_must_be_on, Toast.LENGTH_SHORT).show();
                }
            }
        });
                   
	}

	@Override
	public void onStart(){
	    super.onStart();
	    SharedPreferences prefs = getSharedPreferences("MisPreferencias",
				Context.MODE_PRIVATE);
	    Boolean isCheckedButPagesVisited = prefs.getBoolean("butPagesVisited", false);
	    butPagesVisited.setChecked(isCheckedButPagesVisited);
	    Boolean isCheckedButSearchQueries = prefs.getBoolean("butSearchQueries", false);
	    butSearchQueries.setChecked(isCheckedButSearchQueries);
	    Boolean isCheckedButHtmlVisited = prefs.getBoolean("butHtmlVisited", false);
	    butHtmlVisited.setChecked(isCheckedButHtmlVisited);
	}

	@Override
	public void onResume(){
		super.onResume();
		if (!butPagesVisited.isChecked()){
			butHtmlVisited.setChecked(false);
		}
	}
	
	@Override
	public void onStop(){
	    super.onStop();
	    SharedPreferences prefs = getSharedPreferences("MisPreferencias",
				Context.MODE_PRIVATE);
	    SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("butHtmlVisited", butHtmlVisited.isChecked());
		editor.putBoolean("butSearchQueries", butSearchQueries.isChecked());
		editor.putBoolean("butPagesVisited", butPagesVisited.isChecked());
		editor.commit();	
	}
}
