package com.mnopi.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.mnopi.authentication.AccountGeneral;
import com.mnopi.authentication.MnopiAuthenticator;
import com.mnopi.data.handlers.PageVisitedDataHandler;
import com.mnopi.data.handlers.WebSearchDataHandler;
import com.mnopi.utils.UnauthorizedException;


public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String TAG = "SyncAdapter";
    private final AccountManager mAccountManager;
    ContentResolver mContentResolver;
    Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mAccountManager = AccountManager.get(context);
        mContentResolver = context.getContentResolver();

    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContext = context;
        mAccountManager = AccountManager.get(context);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");

        // Load all handlers and send their information
        PageVisitedDataHandler pageHandler = new PageVisitedDataHandler(mContext);
        WebSearchDataHandler searchHandler = new WebSearchDataHandler(mContext);

        try {
            pageHandler.sendData(account, syncResult);
            searchHandler.sendData(account, syncResult);
        } catch (UnauthorizedException ex) {
            String authToken = null;
            try {
                authToken = mAccountManager.blockingGetAuthToken(account,
                        MnopiAuthenticator.STANDARD_ACCOUNT_TYPE, true);
            } catch (Exception ex2) {
            }
            mAccountManager.invalidateAuthToken(AccountGeneral.ACCOUNT_TYPE, authToken);

            // Once invalidated, calling again will prompt Android authentication warning
            try {
                authToken = mAccountManager.blockingGetAuthToken(account,
                        MnopiAuthenticator.STANDARD_ACCOUNT_TYPE, true);
            } catch (Exception ex2) {
            }
        } catch (Exception ex) {
            Log.e("Sync adapter", "Error sending data sync adapter");
        }

    }
}
