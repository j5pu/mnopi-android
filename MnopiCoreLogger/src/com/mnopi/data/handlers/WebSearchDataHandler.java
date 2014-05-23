package com.mnopi.data.handlers;

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.authentication.MnopiAuthenticator;
import com.mnopi.data.DataProvider;
import com.mnopi.data.DataProvider.WebSearch;
import com.mnopi.data.handlers.DataHandler;
import com.mnopi.utils.ServerApi;
import com.mnopi.utils.UnauthorizedException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

/**
 * Data handler for web searches
 * @author alainez
 *
 */
public class WebSearchDataHandler extends DataHandler {
	
	public static final String HANDLER_KEY = "web_search";
    private Context context;

	public WebSearchDataHandler(Context c){
		super(c);
        this.context = c;
	}
	
	@Override
	public void saveData(Bundle bundle) {		
		//Save data to contentProvider
		Uri webSearchUri = DataProvider.WEB_SEARCH_URI;
		ContentResolver cr = context.getContentResolver();
		ContentValues row = new ContentValues();
		String url = bundle.getString("search_results");
        String date = bundle.getString("date");
        String query = bundle.getString("search_query");
        row.put("url", url);
        row.put("date", date);
        row.put("query", query);
        cr.insert(webSearchUri, row);
	}

	@Override
	public void sendData(Account account, SyncResult syncResult) throws Exception {
        // Get data from content provider
        String[] projection = new String[]{
                WebSearch._ID,
                WebSearch.COL_QUERY,
                WebSearch.COL_URL,
                WebSearch.COL_DATE
        };

        Uri webSearchUri = DataProvider.WEB_SEARCH_URI;
        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(webSearchUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            String user = AccountGeneral.getLoggedUserResource(context, account);
            AccountManager mAccountManager = AccountManager.get(context);
            String authToken = mAccountManager.blockingGetAuthToken(account,
                    MnopiAuthenticator.STANDARD_ACCOUNT_TYPE, true);

            do {

                String url = cursor.getString(cursor.getColumnIndex(WebSearch.COL_URL));
                String date = cursor.getString(cursor.getColumnIndex(WebSearch.COL_DATE));
                String query = cursor.getString(cursor.getColumnIndex(WebSearch.COL_QUERY));

                try {

                    HashMap<String, String> response = ServerApi.sendWebSearch(user, date,
                            query, url, authToken);

                    int searchId = cursor.getInt(cursor.getColumnIndex(WebSearch._ID));
                    Uri deleteUri = ContentUris.withAppendedId(DataProvider.WEB_SEARCH_URI, searchId);
                    cr.delete(deleteUri, null, null);

                } catch (UnauthorizedException ex) {
                    syncResult.stats.numAuthExceptions++;
                    throw ex;
                } catch (Exception ex) {
                    // All other problems that indicate that the resource could not be created are
                    // considered authExceptions as this is marked as hard error (shown in account)
                    Log.e("Sync adapter", "Server response: web search resource not created");
                    syncResult.stats.numAuthExceptions++;

                    // Continue synchronization
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
