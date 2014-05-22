package com.mnopi.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.mnopi.data.handlers.PageVisitedDataHandler;
import com.mnopi.data.handlers.WebSearchDataHandler;
import com.mnopi.mnopi.MnopiApplication;


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
        } catch (Exception ex) {
            Log.e("Sync adapter", "Error sending data sync adapter");
        }

    }
}
