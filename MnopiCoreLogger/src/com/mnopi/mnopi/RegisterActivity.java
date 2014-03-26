package com.mnopi.mnopi;

import java.security.KeyStore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
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

public class RegisterActivity extends Activity {

    private TextView lblGotoLogin;
    private Button btnRegister;
    private EditText inputUser;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPassword2;
    private TextView registerErrorMsg; 
	private Context mContext;
	private MyApplication myApplication;
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
        lblGotoLogin = (TextView) findViewById(R.id.link_to_login);
        registerErrorMsg = (TextView) findViewById(R.id.register_error);
        
        myApplication = ((MyApplication) this.getApplication());
		SharedPreferences prefs = getSharedPreferences("MisPreferencias",
				Context.MODE_PRIVATE);

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
        
        lblGotoLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);
				startActivity(intent);				
			}
		});
        progress = new ProgressDialog(this);
		progress.setTitle(R.string.signing_up);
		progress.setMessage(getResources().getString(R.string.wait_please));
		progress.setCancelable(false);
    }
    
    private class SignUpUser extends AsyncTask<Void,Integer,Void> {
    	@Override
		protected void onPreExecute(){   		
			has_result_error = false;
			any_error = false;			
			// Show ProgressDialog
			progress.show();
		}
		
		@Override
	    protected Void doInBackground(Void... params) {
			
			HttpEntity resEntity;
	        String urlString = myApplication.getSERVER_ADRESS() + "/api/v1/user/";
	        
	        try{
	             HttpClient httpclient = getNewHttpClient();	             
	             HttpPost post = new HttpPost(urlString);
	             post.setHeader("Content-Type", "application/json");
	             
	             JSONObject dato = new JSONObject();	   
	             
	             String username = inputUser.getText().toString();
	             String password = inputPassword.getText().toString();
	             String email = inputEmail.getText().toString();
            
	             dato.put("username", username);
	             dato.put("password", password);
	             dato.put("email", email);

	             // StringEntity UTF-8 
	             StringEntity entity = new StringEntity(dato.toString(), HTTP.UTF_8);
	             post.setEntity(entity);

	             HttpResponse response = httpclient.execute(post);
	             resEntity = response.getEntity();
	             final String response_str = EntityUtils.toString(resEntity);
	             int status = response.getStatusLine().getStatusCode();	         
	             if (resEntity != null) {
	            	 if (status != 201){				   
	                	 JSONObject respJSON = new JSONObject(response_str);
		                 // check the result field
		                 result = respJSON.getString("result"); 
		                 if (result.equals("ERR")){
		                	 if (respJSON.has("reason")){
			                	 	reason = respJSON.getString("reason");
			                	 	result_error = respJSON.getString("erroneous_parameters");
			                	    has_result_error = true;
					                Log.i("Sign up","Error " + reason + ": " + result_error);
		                	 }
		                 }
	                 }else{
	                	 Log.i("Sign up", "OK");
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
			// show information about the sign up result
			if (any_error){
				Toast.makeText(mContext, R.string.error_signing_up, Toast.LENGTH_SHORT).show();
			}else if (!has_result_error){
				Toast.makeText(mContext, R.string.sign_up_succesful, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(mContext, result_error, Toast.LENGTH_SHORT).show();
			}
		}
    }
 
    public HttpClient getNewHttpClient() {
	     try {
	            KeyStore trustStore = KeyStore.getInstance(KeyStore
	                    .getDefaultType());
	            trustStore.load(null, null);

	            MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	            sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	            HttpParams params = new BasicHttpParams();
	            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	            SchemeRegistry registry = new SchemeRegistry();
	            registry.register(new Scheme("http", PlainSocketFactory
	                    .getSocketFactory(), 80));
	            registry.register(new Scheme("https", sf, 443));



	            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
	                    params, registry);

	            return new DefaultHttpClient(ccm, params);
	        } catch (Exception e) {
	            return new DefaultHttpClient();
	        }
	    }
	
	

	// ---------------------------------------------------------------------------------------------------------
	// ----------------------             MÉTODOS PRIVADOS             -----------------------------------------
	// ---------------------------------------------------------------------------------------------------------
		
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
		if (user.matches("^.*[^a-zA-Z0-9 ].*$")){
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
}

