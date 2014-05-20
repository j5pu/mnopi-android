package com.mnopi.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataProvider extends ContentProvider {

	public static final String AUTHORITY = "com.mnopi.android.contentprovider";
	public static final Uri PAGE_VISITED_URI = Uri.parse("content://" + AUTHORITY + "/pagevisited");
	public static final Uri WEB_SEARCH_URI = Uri.parse("content://" + AUTHORITY + "/websearch");

    private static final String WEB_SEARCHES_TABLE_NAME = "web_searches";
    private static final String VISITED_WEB_PAGES_TABLE_NAME = "visited_web_pages";

    private static final int PAGES_VISITED = 10;
    private static final int PAGES_VISITED_ID = 20;
    private static final int WEB_SEARCHES = 11;
    private static final int WEB_SEARCHES_ID = 21;
    private static final UriMatcher uriMatcher;

    public static final class WebSearch implements BaseColumns{
		private WebSearch() {}
		
		public static final String COL_QUERY = "query";
		public static final String COL_URL = "url";
		public static final String COL_DATE = "date";		
	}
	
	public static final class PageVisited implements BaseColumns{
		private PageVisited() {}
		
		public static final String COL_URL = "url";
		public static final String COL_DATE = "date";
		public static final String COL_HTML_CODE = "html_code";		
	}

    private SQLiteDatabase db = null;
    private DataLogOpenHelper dbHelper = null;

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "pagevisited", PAGES_VISITED);
		uriMatcher.addURI(AUTHORITY, "pagevisited/#", PAGES_VISITED_ID);
		uriMatcher.addURI(AUTHORITY, "websearch", WEB_SEARCHES);
		uriMatcher.addURI(AUTHORITY, "websearch/#", WEB_SEARCHES_ID);
	}

    @Override
    public boolean onCreate() {
        dbHelper = new DataLogOpenHelper(getContext());
        return true;
    }

    @Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String where = selection;
		String tableName = null;
		int count;
		int uriType = uriMatcher.match(uri);
		switch (uriType) {
					
		case PAGES_VISITED:
			tableName = VISITED_WEB_PAGES_TABLE_NAME;
			break;
			
		case PAGES_VISITED_ID:
			tableName = VISITED_WEB_PAGES_TABLE_NAME;
			where = "_id=" + uri.getLastPathSegment();
			break;
			
		case WEB_SEARCHES:
			tableName = WEB_SEARCHES_TABLE_NAME;
			break;
			
		case WEB_SEARCHES_ID:
			tableName = WEB_SEARCHES_TABLE_NAME;
			where = "_id=" + uri.getLastPathSegment();
			break;
			
		default: 
			throw new IllegalArgumentException("Unknown URI: " + uri);
			
		}
		db = dbHelper.getWritableDatabase();
		count = db.delete(tableName, where, selectionArgs);
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long regId = 1;
		String tableName = null;
		Uri contentUri = null;
		int uriType = uriMatcher.match(uri);
		switch (uriType) {
			
            case PAGES_VISITED:
                tableName = VISITED_WEB_PAGES_TABLE_NAME;
                contentUri = PAGE_VISITED_URI;
                break;
			
            case WEB_SEARCHES:
                tableName = WEB_SEARCHES_TABLE_NAME;
                contentUri = WEB_SEARCH_URI;
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        regId = db.insert(tableName, null, values);
        Uri newUri = ContentUris.withAppendedId(contentUri, regId);

		return newUri;
	}

    @Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String where = selection;
		String tableName = null;
		int uriType = uriMatcher.match(uri);
        switch (uriType) {

            case PAGES_VISITED:
                tableName = VISITED_WEB_PAGES_TABLE_NAME;
                break;

            case PAGES_VISITED_ID:
                tableName = VISITED_WEB_PAGES_TABLE_NAME;
                where = "_id=" + uri.getLastPathSegment();
                break;

            case WEB_SEARCHES:
                tableName = WEB_SEARCHES_TABLE_NAME;
                break;

            case WEB_SEARCHES_ID:
                tableName = WEB_SEARCHES_TABLE_NAME;
                where = "_id=" + uri.getLastPathSegment();
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

        }
		db = dbHelper.getWritableDatabase();
		
		Cursor c = db.query(tableName, projection, where,
				selectionArgs, null, null, sortOrder);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String where = selection;
		String tableName = null;
		int count;
		int uriType = uriMatcher.match(uri);
		switch (uriType) {
					
            case PAGES_VISITED:
                tableName = VISITED_WEB_PAGES_TABLE_NAME;
                break;
			
            case PAGES_VISITED_ID:
                tableName = VISITED_WEB_PAGES_TABLE_NAME;
                where = "_id=" + uri.getLastPathSegment();
                break;

            case WEB_SEARCHES:
                tableName = WEB_SEARCHES_TABLE_NAME;
                break;
			
            case WEB_SEARCHES_ID:
                tableName = WEB_SEARCHES_TABLE_NAME;
                where = "_id=" + uri.getLastPathSegment();
                break;
			
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);

		}
		db = dbHelper.getWritableDatabase();
		count = db.update(tableName, values, where, selectionArgs);
		return count;
	}

    @Override
    public String getType(Uri uri) {
        int uriType = uriMatcher.match(uri);
        switch (uriType) {

            case PAGES_VISITED:
                return "vnd.android.cursor.dir/vnd.mnopi." + "pagevisited";

            case PAGES_VISITED_ID:
                return "vnd.android.cursor.item/vnd.mnopi." + "pagevisited";

            case WEB_SEARCHES:
                return "vnd.android.cursor.dir/vnd.mnopi." + "websearch";

            case WEB_SEARCHES_ID:
                return "vnd.android.cursor.item/vnd.mnopi." + "websearch";

            default:
                return null;
        }

    }

}
