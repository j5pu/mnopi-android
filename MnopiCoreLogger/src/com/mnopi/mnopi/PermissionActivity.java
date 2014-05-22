package com.mnopi.mnopi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mnopi.data.handlers.PageVisitedDataHandler;
import com.mnopi.data.handlers.WebSearchDataHandler;

public class PermissionActivity extends Activity{
	private Switch butPagesVisited;
	private Switch butSearchQueries;
	private Switch butHtmlVisited;
	private TextView txtHtml;
	private Context mContext;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permission);
        
        mContext = this;
        txtHtml = (TextView) findViewById(R.id.textHtml);
        butPagesVisited = (Switch) findViewById(R.id.butPagesVisited);
        butSearchQueries = (Switch) findViewById(R.id.butSearchQueries);
        butHtmlVisited = (Switch) findViewById(R.id.butHtmlVisited);

        if (!butPagesVisited.isChecked()){
        	butHtmlVisited.setVisibility(View.GONE);
        	txtHtml.setVisibility(View.GONE);
        }
        butPagesVisited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                /* Html permission depends on page permission, so disable it */
                if (!isChecked) {
                	if (butHtmlVisited.isChecked()){
                    	Toast.makeText(mContext, R.string.pages_visited_must_be_on, Toast.LENGTH_SHORT).show();
                	}
                    butHtmlVisited.setChecked(false);
                    butHtmlVisited.setVisibility(View.GONE);
                    txtHtml.setVisibility(View.GONE);
                }
                else{
                	butHtmlVisited.setVisibility(View.VISIBLE);
                	txtHtml.setVisibility(View.VISIBLE);
                }

                if (!isChecked) {
                    setPermissionSharedPreference(PageVisitedDataHandler.HANDLER_KEY, false);
                    setPermissionSharedPreference(PageVisitedDataHandler.HTML_VISITED_KEY, false);
                } else {
                    setPermissionSharedPreference(PageVisitedDataHandler.HANDLER_KEY, true);
                }

            }
            
        });
        
        butHtmlVisited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!butPagesVisited.isChecked()){
                	butHtmlVisited.setChecked(false);
                } else {
                    setPermissionSharedPreference(PageVisitedDataHandler.HTML_VISITED_KEY, isChecked);
                }
            }
        });

        butSearchQueries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setPermissionSharedPreference(WebSearchDataHandler.HANDLER_KEY, isChecked);

            }
        });

	}

    /**
     * Sets a permission for a type of data a value of active or inactive
     * @param permission
     * @param isActive
     */
    public void setPermissionSharedPreference(String permission, boolean isActive) {

        SharedPreferences permissions = getSharedPreferences(MnopiApplication.PERMISSIONS_PREFERENCES,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = permissions.edit();

        editor.putBoolean(permission, isActive);
        editor.commit();
    }

	@Override
	public void onStart(){
	    super.onStart();
	    SharedPreferences prefs = getSharedPreferences(MnopiApplication.PERMISSIONS_PREFERENCES,
				Context.MODE_PRIVATE);

	    butPagesVisited.setChecked(prefs.getBoolean(PageVisitedDataHandler.HANDLER_KEY, true));
	    butSearchQueries.setChecked(prefs.getBoolean(WebSearchDataHandler.HANDLER_KEY, true));
	    butHtmlVisited.setChecked(prefs.getBoolean(PageVisitedDataHandler.HTML_VISITED_KEY, true));
	}

}
