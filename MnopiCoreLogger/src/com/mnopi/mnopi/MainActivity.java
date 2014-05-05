package com.mnopi.mnopi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.authentication.MnopiAuthenticator;
import com.mnopi.authentication.RegisterActivity;
import com.mnopi.utils.Connectivity;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private AccountManager mAccountManager;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mAccountManager = AccountManager.get(this);

	}

    @Override
    public void onStart(){
        super.onStart();

        getTokenForAccountCreateIfNeeded();
    }

    private void getTokenForAccountCreateIfNeeded() {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(AccountGeneral.ACCOUNT_TYPE,
                MnopiAuthenticator.STANDARD_ACCOUNT_TYPE, null, this, null, null,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bnd = null;
                        try {
                            bnd = future.getResult();
                            final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
//                          Toast.makeText(mContext, (authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL", Toast.LENGTH_LONG).show();
                            Log.d("udinic", "GetTokenForAccount Bundle is " + bnd);

                            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        , null);
    }
}
