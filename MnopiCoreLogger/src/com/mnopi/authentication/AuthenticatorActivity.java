package com.mnopi.authentication;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mnopi.mnopi.MnopiApplication;
import com.mnopi.mnopi.PromptLoginActivity;
import com.mnopi.mnopi.R;
import com.mnopi.utils.Connectivity;
import com.mnopi.utils.ServerApi;

import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



public class AuthenticatorActivity extends AccountAuthenticatorActivity{

    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    private final int REQ_SIGNUP = 1;

    private TextView lblGotoRegister;
    private Button btnLogin;
    private EditText inputUser;
    private EditText inputPassword;
    private TextView loginErrorMsg;
    private ProgressDialog progress;

    private Context mContext;
    private AccountManager mAccountManager;
    private OnAccountsUpdateListener accountsListener;

    private boolean transitionToHomeStarted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);
        mAccountManager = AccountManager.get(getBaseContext());

        mContext = this;
        //Layout
        inputUser = (EditText) findViewById(R.id.txtUser);
        inputPassword = (EditText) findViewById(R.id.txtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        loginErrorMsg = (TextView) findViewById(R.id.login_error);
        lblGotoRegister = (TextView) findViewById(R.id.link_to_register);

        // If the Mnopi account was added in settings while we were in the login screen
        accountsListener = new OnAccountsUpdateListener() {
            @Override
            public void onAccountsUpdated(Account[] accounts) {
                Account[] mnopiAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
                if (mnopiAccounts.length == 1 && !transitionToHomeStarted) {
                    Intent result = new Intent();
                    Bundle b = new Bundle();
                    result.putExtras(b);

                    // Cancel login: next time the app is loaded the token will be present
                    setAccountAuthenticatorResult(null);
                    setResult(RESULT_OK, result);
                    finish();

                    // Avoid listener to be called before the activity is actually destroyed
                    transitionToHomeStarted = true;
                }
                return;
            }
        };

        mAccountManager.addOnAccountsUpdatedListener(accountsListener, null, true);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (Connectivity.isOnline(mContext)){
                    if (checkFields()){
                        new LogInUser().execute();
                    }
                } else {
                    Toast toast = Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        lblGotoRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivityForResult(intent, REQ_SIGNUP);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private class LogInUser extends AsyncTask<Void,Integer,Intent> {

        private String username = inputUser.getText().toString();
        private String password = inputPassword.getText().toString();

        @Override
        protected void onPreExecute(){
            // Show ProgressDialog
            progress = new ProgressDialog(mContext);
            progress.setTitle(R.string.login);
            progress.setMessage(getResources().getString(R.string.wait_please));
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Intent doInBackground(Void... params) {

            Bundle respData = new Bundle();
            try {
                HashMap<String, String> response =
                        ServerApi.userSignIn(username, password, MnopiApplication.MNOPI_CLIENT);

                respData.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                respData.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountGeneral.ACCOUNT_TYPE);
                respData.putString(AccountManager.KEY_AUTHTOKEN, response.get("session_token"));
                respData.putString(MnopiAuthenticator.KEY_USER_RESOURCE, response.get("user_resource"));
            } catch (Exception e) {
                respData.putString(MnopiAuthenticator.KEY_ERROR_MESSAGE, e.getMessage());
            }

            Intent result = new Intent();
            result.putExtras(respData);
            return result;
        }

        @Override
        protected void onPostExecute(Intent intent) {

            if (progress.isShowing()) {
                progress.dismiss();
            }

            if (intent.hasExtra(MnopiAuthenticator.KEY_ERROR_MESSAGE)) {
                String resultError = intent.getStringExtra(MnopiAuthenticator.KEY_ERROR_MESSAGE);
                if (resultError.equalsIgnoreCase("INCORRECT_USER_PASSWORD")){
                    loginErrorMsg.setText(R.string.incorrect_user_password);
                }else if (resultError.equalsIgnoreCase("UNEXPECTED_SESSION")){
                    loginErrorMsg.setText(R.string.unexpected_session);
                }
                else {
                    loginErrorMsg.setText(resultError);
                }
            } else {
                finishLogin(intent);
            }
        }

    }

    private void finishLogin(Intent intent) {

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        final Account account = new Account(accountName, AccountGeneral.ACCOUNT_TYPE);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

            Bundle accountExtraInfo = new Bundle();
            accountExtraInfo.putString(MnopiAuthenticator.KEY_USER_RESOURCE,
                    intent.getStringExtra(MnopiAuthenticator.KEY_USER_RESOURCE));

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, null, accountExtraInfo);
            mAccountManager.setAuthToken(account, MnopiAuthenticator.STANDARD_ACCOUNT_TYPE, authtoken);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }


    /**
     * check all fields
     * @return True if all fields are right. False if one of them aren't.
     */
    private boolean checkFields(){
        boolean allCorrect = true ;

        inputUser.setError(null);
        inputPassword.setError(null);

        // username minimum 1 char
        if (inputUser.getText().toString().length() < 1){
            inputUser.setError(getResources().getString(R.string.minimum1));
            allCorrect = false;
        }

        // username without spaces
        Pattern pattern = Pattern.compile("\\s");
        Matcher m = pattern.matcher(inputUser.getText().toString());
        if (m.find()){
            inputUser.setError(getResources().getString(R.string.username_not_accepted));
            allCorrect = false;
        }

        // username blank
        if (inputUser.getText().toString().length() == 0){
            inputUser.setError(getResources().getString(R.string.field_required));
            allCorrect = false;
        }

        // Password minimum 1 char
        if (inputPassword.getText().toString().length() < 1){
            inputPassword.setError(getResources().getString(R.string.minimum1));
            allCorrect = false;
        }

        // Password blank
        if (inputPassword.getText().toString().length() == 0){
            inputPassword.setError(getResources().getString(R.string.field_required));
            allCorrect = false;
        }

        return allCorrect;
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        Bundle b = new Bundle();
        result.putExtras(b);

        setAccountAuthenticatorResult(null);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        mAccountManager.removeOnAccountsUpdatedListener(accountsListener);
    }
}
