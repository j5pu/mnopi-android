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
import android.widget.ToggleButton;

import com.mnopi.data.DataHandlerRegistry;
import com.mnopi.data.PageVisitedDataHandler;
import com.mnopi.data.WebSearchDataHandler;

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
                    butHtmlVisited.setChecked(false);
                    butHtmlVisited.setVisibility(View.GONE);
                    txtHtml.setVisibility(View.GONE);
                }
                else{
                	butHtmlVisited.setVisibility(View.VISIBLE);
                	txtHtml.setVisibility(View.VISIBLE);
                }


                SharedPreferences permissions = getSharedPreferences(MnopiApplication.PERMISSIONS_PREFERENCES,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = permissions.edit();

                DataHandlerRegistry receiveHandlerRegistry = DataHandlerRegistry.getInstance(
                        MnopiApplication.RECEIVE_FROM_SERVICE_REGISTRY);

                if (!isChecked) {
                    editor.putBoolean(MnopiApplication.RECEIVE_PAGE_IS_ALLOWED, false);
                    receiveHandlerRegistry.unbind(PageVisitedDataHandler.getKey());
                } else {
                    editor.putBoolean(MnopiApplication.RECEIVE_PAGE_IS_ALLOWED, true);

                    /* New handler is added if needed */
                    if (receiveHandlerRegistry.lookup(PageVisitedDataHandler.getKey()) == null) {
                        PageVisitedDataHandler pageHandler = new PageVisitedDataHandler(
                                getApplicationContext());
                        receiveHandlerRegistry.bind(PageVisitedDataHandler.getKey(), pageHandler);
                    }
                }
                editor.commit();
            }
        });
        
        butHtmlVisited.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!butPagesVisited.isChecked()){
                	butHtmlVisited.setChecked(false);
                	Toast.makeText(mContext, R.string.pages_visited_must_be_on, Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences permissions = getSharedPreferences(MnopiApplication.PERMISSIONS_PREFERENCES,
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = permissions.edit();

                    DataHandlerRegistry receiveHandlerRegistry = DataHandlerRegistry.getInstance(
                            MnopiApplication.RECEIVE_FROM_SERVICE_REGISTRY);
                    PageVisitedDataHandler pageHandler = (PageVisitedDataHandler)
                            receiveHandlerRegistry.lookup(PageVisitedDataHandler.getKey());

                    editor.putBoolean(MnopiApplication.RECEIVE_HTML_IS_ALLOWED, isChecked);
                    pageHandler.setSaveHtmlVisited(isChecked);

                    editor.commit();
                }
            }
        });

        butSearchQueries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences permissions = getSharedPreferences(MnopiApplication.PERMISSIONS_PREFERENCES,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = permissions.edit();

                DataHandlerRegistry receiveHandlerRegistry = DataHandlerRegistry.getInstance(
                        MnopiApplication.RECEIVE_FROM_SERVICE_REGISTRY);

                if (!isChecked) {
                    editor.putBoolean(MnopiApplication.RECEIVE_SEARCH_IS_ALLOWED, false);
                    receiveHandlerRegistry.unbind(WebSearchDataHandler.getKey());
                } else {
                    editor.putBoolean(MnopiApplication.RECEIVE_SEARCH_IS_ALLOWED, true);

                    /* New handler is added if needed */
                    if (receiveHandlerRegistry.lookup(WebSearchDataHandler.getKey()) == null) {
                        WebSearchDataHandler searchHandler = new WebSearchDataHandler(
                                getApplicationContext());
                        receiveHandlerRegistry.bind(WebSearchDataHandler.getKey(), searchHandler);
                    }
                }
                editor.commit();
            }
        });

	}

	@Override
	public void onStart(){
	    super.onStart();
	    SharedPreferences prefs = getSharedPreferences(MnopiApplication.PERMISSIONS_PREFERENCES,
				Context.MODE_PRIVATE);

	    butPagesVisited.setChecked(prefs.getBoolean(MnopiApplication.RECEIVE_PAGE_IS_ALLOWED, true));
	    butSearchQueries.setChecked(prefs.getBoolean(MnopiApplication.RECEIVE_SEARCH_IS_ALLOWED, true));
	    butHtmlVisited.setChecked(prefs.getBoolean(MnopiApplication.RECEIVE_HTML_IS_ALLOWED, true));
	}

}
