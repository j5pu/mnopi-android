package com.mnopi.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;

import java.io.IOException;

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

    /**
     *
     * @param context
     * @return
     */
    public static boolean isLogged(Context context) {
        AccountManager mAccountManager = AccountManager.get(context);
        Account[] mnopiAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        return (mnopiAccounts.length > 0);
    }

    /**
     * Gets the authentication token for the account.
     * It uses blockingGetAuthToken, so it can't be called in the main thread
     * @param context
     * @return
     */
    public static String blockingGetAuthToken(Context context) {
        AccountManager mAccountManager = AccountManager.get(context);
        Account mnopiAccount = getAccount(context);
        String authToken = null;
        try {
            authToken = mAccountManager.blockingGetAuthToken(mnopiAccount,
                    MnopiAuthenticator.STANDARD_ACCOUNT_TYPE, true);
        } catch (Exception e) {
            e.printStackTrace();
            authToken = "";
        }
        return authToken;
    }


}
