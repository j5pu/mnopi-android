package com.mnopi.mnopi;

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.authentication.MnopiAuthenticator;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * Init class for the application. Automatically tries to recover a user token, which opens
 * the login screen if needed
 */
public class PromptLoginActivity extends Activity {

    private AccountManager mAccountManager;
    private Activity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(this);
        mActivity = this;

        getToken();
	}

    private void getToken() {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(AccountGeneral.ACCOUNT_TYPE,
                MnopiAuthenticator.STANDARD_ACCOUNT_TYPE, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        if (future.isCancelled()){
                            // User pressed back button on login screen
                            mActivity.finish();
                            return;
                        }
                        try {
                            Bundle bundle = future.getResult();
                            final String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                            Log.d("mnopi", "GetTokenForAccount Bundle is " + bundle);

                            Intent intent = new Intent(PromptLoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            mActivity.finish();
                        } catch (Exception e) {
                            mActivity.finish();
                        }
                    }
                }
        , null);
    }
}