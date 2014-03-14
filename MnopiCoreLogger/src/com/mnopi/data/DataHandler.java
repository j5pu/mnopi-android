package com.mnopi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * Abstract for data handlers. They control how the data is received,
 * saved and sent
 * @author Alfredo Lainez
 *
 */
public abstract class DataHandler {
	
	protected SQLiteDatabase db = null;
	
	/**
	 * 
	 * @param context application context
	 */
	public DataHandler(Context context){
		DataLogOpenHelper dbHelper = new DataLogOpenHelper(context);
		db = dbHelper.getWritableDatabase(); //TODO: Check if getting the database every time is too time-consuming
	}
	
	/**
	 * Specifies how to save the data handled
	 * @param bundle bundle data
	 */
	abstract public void saveData(Bundle bundle);
	
	/**
	 * Specifies how to send the data to the server
	 */
	abstract public void sendData();

}
