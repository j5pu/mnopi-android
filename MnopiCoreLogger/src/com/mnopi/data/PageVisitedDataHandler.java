package com.mnopi.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.authentication.MnopiAuthenticator;
import com.mnopi.data.DataProvider.PageVisited;
import com.mnopi.utils.ServerApi;

import java.util.HashMap;

/**
 * Data handler for visited pages
 * @author alainez
 *
 */
public class PageVisitedDataHandler extends DataHandler {

	private static String HANDLER_KEY = "page_visited";

    private boolean saveHtmlVisited = true;

    private Context context;

	public PageVisitedDataHandler(Context context){
        super(context);
        this.context = context;
	}
	
	@Override
	public void saveData(Bundle bundle) {
		Uri pageVisitedUri = DataProvider.PAGE_VISITED_URI;
		ContentResolver cr = context.getContentResolver();
		ContentValues row = new ContentValues();
		String url = bundle.getString("url");
        String date = bundle.getString("date");
        if (saveHtmlVisited) {
            String htmlCode = bundle.getString("html_code");
            row.put("html_code", htmlCode);
        }
        row.put("url", url);
        row.put("date", date);
        cr.insert(pageVisitedUri, row);
	}

	@Override
	public void sendData(Account account) throws Exception {

        // Get data from content provider
        String[] projection = new String[]{
                PageVisited._ID,
                PageVisited.COL_URL,
                PageVisited.COL_DATE,
                PageVisited.COL_HTML_CODE
        };

        Uri pageVisitedUri = DataProvider.PAGE_VISITED_URI;
        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(pageVisitedUri, projection, null, null, null);

        if (cursor != null && cursor.moveToFirst()){
            do {

                String user = AccountGeneral.getLoggedUserResource(context, account);
                String url = cursor.getString(cursor.getColumnIndex(PageVisited.COL_URL));
                String date = cursor.getString(cursor.getColumnIndex(PageVisited.COL_DATE));
                String htmlCode = cursor.getString(cursor.getColumnIndex(PageVisited.COL_HTML_CODE));

                try {
                    AccountManager mAccountManager = AccountManager.get(context);
                    String authToken = mAccountManager.blockingGetAuthToken(account,
                            MnopiAuthenticator.STANDARD_ACCOUNT_TYPE, true);

                    HashMap<String, String> response = ServerApi.sendPageVisited(user,
                            url, date, htmlCode, authToken);

                    int pageId = cursor.getInt(cursor.getColumnIndex(DataProvider.PageVisited._ID));
                    Uri deleteUri = ContentUris.withAppendedId(DataProvider.PAGE_VISITED_URI, pageId);
                    cr.delete(deleteUri, null, null);

                } catch (AuthenticatorException ex) {
                    throw ex;
                } catch (Exception ex){
                    // Continue sending other records
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public void setSaveHtmlVisited(boolean saveHtmlVisited) {
        this.saveHtmlVisited = saveHtmlVisited;
    }

	/**
	 * Returns the handler key for looking up in the registry
	 * @return
	 */
	public static String getKey() {
		return HANDLER_KEY;
	}

}
