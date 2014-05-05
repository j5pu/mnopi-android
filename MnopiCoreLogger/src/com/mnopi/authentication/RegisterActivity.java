package com.mnopi.authentication;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;

import com.mnopi.mnopi.MnopiApplication;
import com.mnopi.mnopi.R;
import com.mnopi.utils.Connectivity;
import com.mnopi.utils.ServerApi;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    private Button btnRegister;
    private EditText inputUser;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPassword2;
	private Context mContext;
	private ProgressDialog progress;
	private String result = null;
	private boolean has_result_error;
	private String result_error;
	private boolean any_error;
	private String reason;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        mContext = this;
        inputUser = (EditText) findViewById(R.id.txtUserName);
        inputEmail = (EditText) findViewById(R.id.txtEmail);
        inputPassword = (EditText) findViewById(R.id.txtPass);
        inputPassword2 = (EditText) findViewById(R.id.txtPass2);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (Connectivity.isOnline(mContext)){
					if (checkFields()){
						new SignUpUser().execute();
					}
				}
				else{
					Toast toast = Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG);
					toast.show();
			    }
            }
        });

    }
    
    
    private class SignUpUser extends AsyncTask<Void,Integer,Intent> {

        private String username = inputUser.getText().toString();
        private String password = inputPassword.getText().toString();
        private String email = inputEmail.getText().toString();

        @Override
        protected void onPreExecute() {
            // Show ProgressDialog
            progress = new ProgressDialog(mContext);
            progress.setTitle(R.string.signing_up);
            progress.setMessage(getResources().getString(R.string.wait_please));
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Intent doInBackground(Void... params) {

            HttpEntity resEntity;

            Bundle respData = new Bundle();
            try {
                HashMap<String, String> response =
                        ServerApi.userSignUp(username, password, email);

                HashMap<String, String> loginResponse =
                        ServerApi.userSignIn(username, password, MnopiApplication.MNOPI_CLIENT);

                respData.putString(AccountManager.KEY_ACCOUNT_NAME, username);
                respData.putString(AccountManager.KEY_AUTHTOKEN, loginResponse.get("session_token"));
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
                if (resultError.equalsIgnoreCase("{\"username\":\"Username already exists\"}")) {
                    inputUser.setError(getResources().getString(R.string.username_already_exists));
                } else if (resultError.equalsIgnoreCase("{\"email\":\"Not an email address\"}")) {
                    inputEmail.setError(getResources().getString(R.string.invalid_email));
                } else if (resultError.equalsIgnoreCase("{\"email\":\"Email already registered\"}")) {
                    inputEmail.setError(getResources().getString(R.string.email_already_registered));
                } else {
                    Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext, R.string.sign_up_succesful, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

	/**  
	 * check all fields
	 * @return True if all fields are right. False if one of them aren't.
	 */
	private boolean checkFields(){
		boolean all_correct = true ;
		
		inputUser.setError(null);
		inputPassword.setError(null);
		inputPassword2.setError(null);
		inputEmail.setError(null);
		String pass = inputPassword.getText().toString();
		String pass2 = inputPassword2.getText().toString();
		String user = inputUser.getText().toString();
		String email = inputEmail.getText().toString();
		
		// username without spaces
		Pattern pattern = Pattern.compile("\\s");
		Matcher m = pattern.matcher(user);
		if (m.find()){
			inputUser.setError(getResources().getString(R.string.username_alphanumeric));
			all_correct = false;
		}		
		// username alphanumeric
		if (user.matches("^.*[^a-zA-Z0-9�� ].*$")){
			inputUser.setError(getResources().getString(R.string.username_alphanumeric));
			all_correct = false;
		}
		// username between 1 and 20 char
		if ((user.length() < 1) || (user.length() > 20)){
			inputUser.setError(getResources().getString(R.string.username_lengh));
			all_correct = false;
		}

		// Password between 6 and 40 char
		if ((pass.length() < 6) || (pass.length() > 40)){
			inputPassword.setError(getResources().getString(R.string.pass_length));
			all_correct = false;
		}
		
		// Email valid
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern pattern2 = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern2.matcher(email);
		if ((email.length() < 1) || (pass.length() > 80)){
			inputEmail.setError(getResources().getString(R.string.email_length));
			all_correct = false;
		}
		if (!matcher.matches()){
			inputEmail.setError(getResources().getString(R.string.invalid_email));
			all_correct = false;
		}
		
		// Password must be equals RepeatPassword
		if (!pass.equalsIgnoreCase(pass2)){
			inputPassword2.setError(getResources().getString(R.string.different_pass));
			all_correct = false;
		}
		
		// All fields required
		if (user.length() == 0){
			inputUser.setError(getResources().getString(R.string.field_required));
			all_correct = false;
		}
		if (email.length() == 0){
			inputEmail.setError(getResources().getString(R.string.field_required));
			all_correct = false;
		}
		if (pass.length() == 0){
			inputPassword.setError(getResources().getString(R.string.field_required));
			all_correct = false;
		}
		if (pass2.length() == 0){
			inputPassword2.setError(getResources().getString(R.string.field_required));
			all_correct = false;
		}

		return all_correct;
	}

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}

