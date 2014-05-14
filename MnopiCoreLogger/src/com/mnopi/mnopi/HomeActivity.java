package com.mnopi.mnopi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.data.DataHandlerRegistry;
import com.mnopi.data.DataLogOpenHelper;
import com.mnopi.utils.Connectivity;

public class HomeActivity extends Activity{
	
	private Button btnSendImmediately;
	private Button btnPermissionConsole;
	private Button btnViewData;
    private TextView txtQueriesNumber;
    private TextView txtPagesNumber;
	private Switch butDataCollector;
	private Context mContext;
    private AccountManager mAccountManager;
    private OnAccountsUpdateListener accountsListener;

    private boolean transitionToLoginStarted = false;

    public static final String AUTHORITY = "com.mnopi.android.contentprovider";
    public static final String ACCOUNT_TYPE = "com.mnopi.auth";
    ContentResolver mResolver;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        if (!DataHandlerRegistry.isUsed()) {
            MnopiApplication.initHandlerRegistries(this);
        }

        mContext = this;
        btnSendImmediately = (Button) findViewById(R.id.btnSendImmediately);
        btnPermissionConsole = (Button) findViewById(R.id.btnPermissionConsole);
        btnViewData = (Button) findViewById(R.id.btnViewData);
        butDataCollector = (Switch) findViewById(R.id.butDataCollector);

        mAccountManager = AccountManager.get(this);
        Account account = mAccountManager.getAccountsByType(ACCOUNT_TYPE)[0];
        Bundle bundle = new Bundle(1);
        ContentResolver.setSyncAutomatically(account, AUTHORITY, false);
        ContentResolver.addPeriodicSync(account, AUTHORITY, bundle, 60);
        // If the Mnopi account was removed the application must log out
        accountsListener = new OnAccountsUpdateListener() {
            @Override
            public void onAccountsUpdated(Account[] accounts) {
                Account[] mnopiAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                if (mnopiAccounts.length == 0 && !transitionToLoginStarted) {
                    Intent i = new Intent(mContext, PromptLoginActivity.class);
                    startActivity(i);
                    finish();
                    // Avoid listener to be called before the activity is actually destroyed
                    transitionToLoginStarted = true;
                }
                return;
            }
        };

        mAccountManager.addOnAccountsUpdatedListener(accountsListener, null, true);

        btnPermissionConsole.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent(HomeActivity.this,
						PermissionActivity.class);
				startActivity(intent);
        	}        	
        });
        
        btnViewData.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		Intent intent = new Intent(HomeActivity.this,
						ViewDataActivity.class);
				startActivity(intent);
        	}        	
        });
        
        btnSendImmediately.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		if (Connectivity.isOnline(mContext)){
                    DataHandlerRegistry sendRegistry =
                            DataHandlerRegistry.getInstance(MnopiApplication.SEND_TO_SERVER_REGISTRY);
        			try {
                        sendRegistry.sendAll(AccountGeneral.getAccount(mContext));
                    } catch (Exception ex) {
                        Toast toast = Toast.makeText(mContext, "Error sending data", Toast.LENGTH_LONG);
                        toast.show();
                    }
        		}
        		else{
                    Toast toast = Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG);
					toast.show();
			    }
        	}
        });

        butDataCollector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                DataHandlerRegistry receiveHandlerRegistry = DataHandlerRegistry.getInstance(
                        MnopiApplication.RECEIVE_FROM_SERVICE_REGISTRY);
                receiveHandlerRegistry.setEnabled(isChecked);

                SharedPreferences permissions = getSharedPreferences(MnopiApplication.PERMISSIONS_PREFERENCES,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = permissions.edit();
                editor.putBoolean(MnopiApplication.RECEIVE_IS_ALLOWED, isChecked);
                editor.commit();
            }
        });

        /* Timer to know number of records saved */
        //TODO: chapuza temporal

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                txtQueriesNumber = (TextView) findViewById(R.id.search_queries_number);
                                txtPagesNumber = (TextView) findViewById(R.id.pages_visited_number);

                                DataLogOpenHelper dbHelper = new DataLogOpenHelper(mContext);
                                SQLiteDatabase db = dbHelper.getReadableDatabase();

                                int queryNumber = 0;
                                int pagesNumber = 0;

                                Cursor cursor = db.rawQuery("select * from web_searches", null);
                                queryNumber = cursor.getCount();
                                cursor.close();

                                cursor = db.rawQuery("select * from visited_web_pages", null);
                                pagesNumber = cursor.getCount();
                                cursor.close();	
                                txtQueriesNumber.setText("Number of queries saved: " + queryNumber);
                                txtPagesNumber.setText("Number of pages saved: " + pagesNumber);
                                db.close();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    public void clearSessionToken() {
        SharedPreferences settings = getSharedPreferences(
                MnopiApplication.APPLICATION_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(MnopiApplication.SESSION_TOKEN, null);
        editor.commit();
    }

    public void resetHandlers() {
        SharedPreferences permissions = getSharedPreferences(
                MnopiApplication.PERMISSIONS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = permissions.edit();
        editor.clear();
        editor.commit();

        DataHandlerRegistry.clearRegistries();
    }

    private void removeMnopiAccount(){
        AccountManager mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        // If we are logged there must be exactly one account
        mAccountManager.removeAccount(accounts[0], null, null);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.action_logout:
            clearSessionToken();
            resetHandlers();
            removeMnopiAccount();
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onStart(){
	    super.onStart();
	    SharedPreferences prefs = getSharedPreferences(MnopiApplication.PERMISSIONS_PREFERENCES,
				Context.MODE_PRIVATE);
	    butDataCollector.setChecked(prefs.getBoolean(MnopiApplication.RECEIVE_IS_ALLOWED, true));
	}

    @Override
    public void onDestroy(){
        super.onDestroy();

        mAccountManager.removeOnAccountsUpdatedListener(accountsListener);
    }
	
}
