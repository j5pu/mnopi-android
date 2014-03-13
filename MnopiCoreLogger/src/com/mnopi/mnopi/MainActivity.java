package com.mnopi.mnopi;



import com.mnopi.mnopi.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	static String NAME = "admin";
	static String PASS = "1234";
	private TextView lblGotoRegister;
	private Button btnLogin;
	private EditText inputEmail;
	private EditText inputPassword;
	private TextView loginErrorMsg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		inputEmail = (EditText) findViewById(R.id.txtEmail);
		inputPassword = (EditText) findViewById(R.id.txtPass);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		loginErrorMsg = (TextView) findViewById(R.id.login_error);
		lblGotoRegister = (TextView) findViewById(R.id.link_to_register);

		SharedPreferences prefs = getSharedPreferences("MisPreferencias",
				Context.MODE_PRIVATE);
		String name = prefs.getString("name", "");
		String pass = prefs.getString("pass", "");

		// Comprobamos nombre y clave de ususario
		
		 if(name.equals(NAME) && pass.equals(PASS)){ 
			 Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
			 startActivity(intent);
			 finish();
		 }
		 

		btnLogin.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				String email = inputEmail.getText().toString();
				String password = inputPassword.getText().toString();

				if (email.equalsIgnoreCase(NAME)
						&& password.equalsIgnoreCase(PASS)) {
					// Si el usuario escrito es correcto, almacenamos la
					// preferencia y entramos en la app
					SharedPreferences settings = getSharedPreferences(
							"MisPreferencias", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("name", email);
					editor.putString("pass", password);
					// Confirmamos el almacenamiento.
					editor.commit();

					// Entramos en la app
					Intent intent = new Intent(MainActivity.this,
							WelcomeActivity.class);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(getApplicationContext(),
							"El usuario introducido no es correcto",
							Toast.LENGTH_LONG).show();
				}

			}

		});

		lblGotoRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Entramos en el registro
				Intent intent = new Intent(MainActivity.this,
						RegisterActivity.class);
				startActivity(intent);
				finish();
			}
		});

	}

}
