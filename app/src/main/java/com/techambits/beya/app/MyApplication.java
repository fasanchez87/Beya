package com.techambits.beya.app;

import android.app.Application;

import com.techambits.beya.helper.MyPreferenceManager;

/**
 * Created by FABiO on 23/02/2016.
 * This is a singleton class needs to be added to AndroidManifest.xml file.
 */
public class MyApplication extends Application
{

    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;

    private MyPreferenceManager pref;

    @Override
    public void onCreate()
    {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance()
    {
        return mInstance;
    }


    public MyPreferenceManager getPrefManager()
    {
        if (pref == null)
        {
            pref = new MyPreferenceManager(this);
        }

        return pref;
    }
}