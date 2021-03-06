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

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.data.DataLogOpenHelper;
import com.mnopi.data.DataProvider;

public class HomeActivity extends Activity{

	private Button btnPermissionConsole;
	private Button btnViewData;
    private TextView txtQueriesNumber;
    private TextView txtPagesNumber;
	private Switch butDataCollector;
	private Context mContext;
    private AccountManager mAccountManager;
    private OnAccountsUpdateListener accountsListener;

    private boolean transitionToLoginStarted = false;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        mContext = this;
        btnPermissionConsole = (Button) findViewById(R.id.btnPermissionConsole);
        btnViewData = (Button) findViewById(R.id.btnViewData);
        butDataCollector = (Switch) findViewById(R.id.butDataCollector);

        mAccountManager = AccountManager.get(this);

        // If the Mnopi account was removed the application must log out
        accountsListener = new OnAccountsUpdateListener() {
            @Override
            public void onAccountsUpdated(Account[] accounts) {
                Account[] mnopiAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                if (mnopiAccounts.length == 0 && !transitionToLoginStarted) {
                    Intent i = new Intent(mContext, PromptLoginActivity.class);
                    startActivity(i);
                    finish();
                    DataProvider.deleteDatabase(mContext);
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
        
        butDataCollector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                SharedPreferences permissions = getSharedPreferences(MnopiApplication.PERMISSIONS_PREFERENCES,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = permissions.edit();
                editor.putBoolean(MnopiApplication.RECEIVE_IS_ALLOWED, isChecked);
                editor.commit();
            }
        });

        /* Timer to know number of records saved */
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
