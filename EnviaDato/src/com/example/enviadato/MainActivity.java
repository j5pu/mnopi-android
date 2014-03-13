package com.example.enviadato;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Ejecutamos el servicio que est� en RecibeDato
		Intent myIntent = new Intent("com.example.NombreServicio"); //TODO: REVISAR SI ES IMPLICITO O NO. SI ES IMPLICITO CAMBIARLO A EXPLICIT
		myIntent.putExtra("Prueba", "soy un string");
		startService(myIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
