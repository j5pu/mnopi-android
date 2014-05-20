package com.mnopi.data;

import android.accounts.Account;
import android.accounts.AuthenticatorException;
import android.content.Context;
import android.content.SyncResult;
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
	protected Context context;
	
	public DataHandler(Context c){
		context = c;
		DataLogOpenHelper dbHelper = new DataLogOpenHelper(context);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * Specifies how to save the data handled
	 * @param bundle bundle data
	 */
	abstract public void saveData(Bundle bundle);
	
	/**
	 * Specifies how to send the data to the server
	 */
	abstract public void sendData(Account account, SyncResult syncResult) throws Exception;
}

