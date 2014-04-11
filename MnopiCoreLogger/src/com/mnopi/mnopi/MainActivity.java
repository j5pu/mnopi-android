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

import com.mnopi.utils.Connectivity;

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
	
	private TextView lblGotoRegister;
	private Button btnLogin;
	private EditText inputUser;
	private EditText inputPassword;
	private TextView loginErrorMsg;
	private boolean has_name_error;
	private boolean has_result_error;
	private boolean login_error = false;
	private boolean any_error;
	private ProgressDialog progress;
	private String result = null;
	private String reason;
	private String name_error;
	private String result_error;
	private String loginMessageError;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContext = this;
		//Layout
		inputUser = (EditText) findViewById(R.id.txtUser);
		inputPassword = (EditText) findViewById(R.id.txtPass);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);
		lblGotoRegister = (TextView) findViewById(R.id.link_to_register);

		
		//Global vars and sharedpreferences
		SharedPreferences prefs = getSharedPreferences(MnopiApplication.APPLICATION_PREFERENCES,
				Context.MODE_PRIVATE);

		String session_token = prefs.getString(MnopiApplication.SESSION_TOKEN, null);

		// Check if user is logged
		if(session_token != null) {
			 Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
			 startActivity(intent);
			 finish();
		 }

		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (Connectivity.isOnline(mContext)){
					if (checkFields()){
						new LogInUser().execute();
					}
				}
				else{
					Toast toast = Toast.makeText(mContext, R.string.no_connection, Toast.LENGTH_LONG);
					toast.show();
			    }	
			}
		});

		lblGotoRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// register
				Intent intent = new Intent(MainActivity.this,
						RegisterActivity.class);
				startActivity(intent);
			}
		});
		progress = new ProgressDialog(this);
		progress.setTitle(R.string.login);
		progress.setMessage(getResources().getString(R.string.wait_please));
		progress.setCancelable(false);
	}
	
	private class LogInUser extends AsyncTask<Void,Integer,Void> {
		 
		private String username;
		private String session_token;
		private String user_resource;
		
		@Override
		protected void onPreExecute(){
			has_name_error = false;
			has_result_error = false;
			login_error = false;
			any_error = false;
			
			// Show ProgressDialog
			progress.show();
		}
		
		@Override
	    protected Void doInBackground(Void... params) {
	 
	    	HttpEntity resEntity;
	        String urlString = MnopiApplication.SERVER_ADDRESS + "/api/v1/user/login/";
	        
	        try{
	             HttpClient httpclient = Connectivity.getNewHttpClient();
	             HttpPost post = new HttpPost(urlString);
	             post.setHeader("Content-Type", "application/json");
	             
	             JSONObject dato = new JSONObject();	   
	             
	             String username = inputUser.getText().toString();
	             String password = inputPassword.getText().toString();
	             String cliente = "android-v1.0a";
             
	             dato.put("username", username);
	             dato.put("key", password);
	             dato.put("client", cliente);

	             // StringEntity UTF-8 
	             StringEntity entity = new StringEntity(dato.toString(), HTTP.UTF_8);
	             post.setEntity(entity);

	             HttpResponse response = httpclient.execute(post);
	             resEntity = response.getEntity();
	             final String response_str = EntityUtils.toString(resEntity);
	             	         
	             if (resEntity != null) {
	                 Log.i("LOGIN","login: " + response_str);
	                 // get JSON from server
	                 JSONObject respJSON = new JSONObject(response_str);
	                 
	                 // check the result field
	                 result = respJSON.getString("result");  
	                 
	                 // if is OK get session_token
	                 if (result.equals("OK")){
	                	// user_uri = respJSON.getString("user_uri");
	                	 session_token = respJSON.getString("session_token");
	                	 user_resource = respJSON.getString("user_resource");
	                 }
	                 // if is ERROR get reason
	                 else if (result.equals("ERR")){
	                	 if (respJSON.has("reason")){
		                	 reason = respJSON.getString("reason");
		                		 result_error = reason;
		                		 has_result_error = true;
	                	 }
	                	 // an error occurred without error message: show error
	                	 else{
	                		 JSONObject jsonMessageError = new JSONObject(respJSON.getString("reason"));
	                		 loginMessageError = jsonMessageError.getString("error_message");
	                		 login_error = true;
	                	 }    	
	                 }
	             }
	        }
	        catch (Exception ex){
	             Log.e("Debug", "error: " + ex.getMessage(), ex);
	             any_error = true;
	        }
			return null;
	      
	    }
				

		@Override
	    protected void onPostExecute(Void result) {
			
			// hide ProgressDialog
			if (progress.isShowing()) {
		        progress.dismiss();
		    }
			
			// if an error occurred show the error
			if (any_error){
				Toast.makeText(getBaseContext(), R.string.error_occurred, Toast.LENGTH_SHORT).show();
			}
			else{
				 if (!login_error){
					if (has_result_error){
						loginErrorMsg.setText(result_error);
					}else{
                        // login is ok
						SharedPreferences prefs = getSharedPreferences(MnopiApplication.APPLICATION_PREFERENCES,
								Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						// save session_token and user_resource
			        	editor.putString(MnopiApplication.SESSION_TOKEN, session_token);
			        	editor.putString(MnopiApplication.USER_RESOURCE, user_resource);
			        	// save username
			        	editor.putString(MnopiApplication.USERNAME, username);
			        	editor.commit();
			        	Toast.makeText(getBaseContext(), R.string.login_succesful, Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
						startActivity(intent);	
			        	finish();
					}	
				}
				
				// any error occurred: show message
				else{
		        	Toast.makeText(getBaseContext(), loginMessageError, Toast.LENGTH_SHORT)
					.show();
				}
			}
	    }		
	}

	/**  TODO
	 * check all fields
	 * @return True if all fields are right. False if one of them aren't.
	 */
	private boolean checkFields(){
		boolean allCorrect = true ;
		
		inputUser.setError(null);
		inputPassword.setError(null);
		
		// username minimum 4 char
		if (inputUser.getText().toString().length() < 4){
			inputUser.setError(getResources().getString(R.string.minimum4));
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

		// Password minimum 4 char
		if (inputPassword.getText().toString().length() < 4){
			inputPassword.setError(getResources().getString(R.string.minimum4));
			allCorrect = false;
		}
		
		// Password blank
		if (inputPassword.getText().toString().length() == 0){
			inputPassword.setError(getResources().getString(R.string.field_required));
			allCorrect = false;
		}

		return allCorrect;
	}
	
}
