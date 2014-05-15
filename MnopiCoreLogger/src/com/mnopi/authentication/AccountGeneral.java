package com.mnopi.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class AccountGeneral {
    /**
     * Account type id
     */
    public static final String ACCOUNT_TYPE = "com.mnopi.auth";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "mnopi";

    /**
     * Returns logged account. A user must be logged.
     * @param context
     * @return
     */
    public static Account getAccount(Context context) {
        AccountManager mAccountManager = AccountManager.get(context);
        Account[] mnopiAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        return mnopiAccounts[0];
    }

    /**
     * Returns logged user resoruce. A user must be logged
     * @param context
     * @param account
     * @return
     */
    public static String getLoggedUserResource(Context context, Account account) {
        AccountManager mAccountManager = AccountManager.get(context);
        return mAccountManager.getUserData(account, MnopiAuthenticator.KEY_USER_RESOURCE);
    }

    /**
     * Returns logged user resoruce. A user must be logged
     * @param context
     * @return
     */
    public static String getLoggedUserResource(Context context) {
        Account account = getAccount(context);
        return getLoggedUserResource(context, account);
    }


}
