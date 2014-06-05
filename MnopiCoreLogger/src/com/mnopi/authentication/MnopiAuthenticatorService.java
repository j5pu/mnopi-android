package com.mnopi.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MnopiAuthenticatorService extends Service {

    private MnopiAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new MnopiAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }

}
