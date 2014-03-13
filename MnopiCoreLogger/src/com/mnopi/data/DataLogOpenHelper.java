package com.mnopi.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataLogOpenHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "mnopi";
	private static final int DATABASE_VERSION = 5;
	
	public static final String WEB_SEARCHES_TABLE_NAME = "web_searches";
	private static final String WEB_SEARCHES_TABLE_CREATE =
			"CREATE TABLE " + WEB_SEARCHES_TABLE_NAME + 
			" (query TEXT," +
			  "url TEXT," +
			  "date TEXT);";
	
	public static final String VISITED_WEB_PAGES_TABLE_NAME = "visited_web_pages";
	private static final String VISITED_WEB_PAGES_TABLE_CREATE =
			"CREATE TABLE " + VISITED_WEB_PAGES_TABLE_NAME +
			" (url TEXT," +
			  "date TEXT," +
			  "html_code TEXT);";
	
	private static final String SQL_DELETE_WEB_SEARCHES =
			"DROP TABLE IF EXISTS " + WEB_SEARCHES_TABLE_NAME + ";";
	
	private static final String SQL_DELETE_WEB_PAGES =
			"DROP TABLE IF EXISTS " + VISITED_WEB_PAGES_TABLE_NAME + ";";
		
	public DataLogOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(WEB_SEARCHES_TABLE_CREATE);
		db.execSQL(VISITED_WEB_PAGES_TABLE_CREATE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		db.execSQL(SQL_DELETE_WEB_PAGES);
		db.execSQL(SQL_DELETE_WEB_SEARCHES);
		onCreate(db);
	}

}
