package com.mnopi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public abstract class DataHandler {
	
	protected SQLiteDatabase db = null;
	Context context;
	public DataHandler(Context c){
		context = c;
		DataLogOpenHelper dbHelper = new DataLogOpenHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	abstract public void saveData(Bundle bundle);
	
	abstract public void sendData();

	public void sendData(Context c) {
		// TODO Auto-generated method stub
		
	}
	
	// Save data
	// Send data
	// Apply special permissions
	//

}
