package com.techambits.beya.gcm;

/**
 * Created by FABiO on 23/02/2016.
 *
 * This service extends IntentService which acts as a background service. This service basically used for three purposes:
 1. To connect with gcm server and fetch the registration token. Uses registerGCM() method.
 2. Subscribe to a topic. Uses subscribeToTopic(“topic”) method.
 3. Unsubscribe from a topic. Uses unsubscribeFromTopic(“topic”) method.
 */


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.techambits.beya.R;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import com.techambits.beya.app.Config;


public class GcmIntentService extends IntentService
{

    private static final String TAG = GcmIntentService.class.getSimpleName();

    gestionSharedPreferences sharedPreferencesGestion;

    public GcmIntentService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

        sharedPreferencesGestion = new gestionSharedPreferences(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = null;

        this.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        this.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));

        try
        {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sharedPreferencesGestion.putString("TOKEN", token);
            Log.e(TAG, "GCM Registration Token: " + token);
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, true).apply();
        }

        catch (Exception e)
        {
            Log.e(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(Config.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}