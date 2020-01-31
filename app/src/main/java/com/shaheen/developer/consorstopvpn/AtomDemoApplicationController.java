/*
 * Copyright (c) 2018 Atom SDK Demo.
 * All rights reserved.
 */
package com.shaheen.developer.consorstopvpn;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.atom.core.models.AccessToken;
import com.atom.core.models.AtomConfiguration;
import com.atom.sdk.android.AtomManager;
import com.atom.sdk.android.exceptions.AtomValidationException;
import com.shaheen.developer.consorstopvpn.Locale.LocaleManager;


/**
 * AtomDemoApplicationController
 */

public class AtomDemoApplicationController extends MultiDexApplication {

    private static AtomDemoApplicationController mInstance;
    private AtomManager atomManager;
    public static final String TAG = "shani";
    AccessToken accessToken;


    public static synchronized AtomDemoApplicationController getInstance() {
        return mInstance;
    }

    public AtomManager getAtomManager() {
        return atomManager;
    }

    public void SetAtomManager(AtomManager atomManager){
        this.atomManager = atomManager;
    }

    public AccessToken GetAccessToken() {
        return accessToken;
    }

    public void SetAccessToken(AccessToken accessToken){
        this.accessToken = accessToken;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleManager.setLocale(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Log.d("shani","onCreate of AtomDemo");

        String ATOM_SECRET_KEY = getResources().getString(R.string.atom_secret_key);

        if (!TextUtils.isEmpty(ATOM_SECRET_KEY)) {
            Log.d(TAG,"secret key exist.......");

            AtomConfiguration.Builder atomConfigurationBuilder = new AtomConfiguration.Builder(ATOM_SECRET_KEY);
            AtomConfiguration atomConfiguration = atomConfigurationBuilder.build();


            try {

                AtomManager.initialize(this, atomConfiguration, new AtomManager.InitializeCallback() {
                    @Override
                    public void onInitialized(AtomManager aatomManager) {

                        atomManager = aatomManager;
                        Log.d(TAG,"initializedffffffffffffffffffffffffffffffffff");

                    }
                });

                Log.d(TAG,"after initialized");
            } catch (AtomValidationException e) {
                e.printStackTrace();
                Log.d(TAG,"exception...."+e.getMessage());
            }
        } else {
            Toast.makeText(this, "Secret Key is required.", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"empty secret key.......");
        }

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
