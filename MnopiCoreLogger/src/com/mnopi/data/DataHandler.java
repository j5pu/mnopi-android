package com.mnopi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public abstract class DataHandler {
	
	protected SQLiteDatabase db = null;
	
	public DataHandler(Context context){
		DataLogOpenHelper dbHelper = new DataLogOpenHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	abstract public void saveData(Bundle bundle);
	
	abstract public void sendData();
	
	// Save data
	// Send data
	// Apply special permissions
	//

}
