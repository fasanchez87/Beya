package com.techambits.beya.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


public class HeartBeatServiceGCM extends Service
{
    private Timer mTimer = null;
    public static final long NOTIFY_INTERVAL = 2 * 1000; // 2 seconds
    private Handler mHandler = new Handler();

    public HeartBeatServiceGCM()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        mHandler.postDelayed(ToastRunnable, 2000);


        return START_STICKY;

    }

    final Runnable ToastRunnable = new Runnable()
    {
        public void run()
        {
            if(haveNetworkConnection())
            {
                getApplicationContext().sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
                getApplicationContext().sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
                Log.w("GCM", "Enviado latido ahora!");
            }
           /* else
            {
                Toast.makeText(getApplicationContext(), "SIN CONEXIÓN A INTERNET",
                        Toast.LENGTH_SHORT).show();
            }*/

            mHandler.postDelayed(ToastRunnable, 5000);


        }
    };

    private boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi =  false ;
        boolean haveConnectedMobile =  false ;

        ConnectivityManager cm =  (ConnectivityManager) getSystemService ( Context. CONNECTIVITY_SERVICE );
        NetworkInfo[] netInfo = cm . getAllNetworkInfo ();
        for  ( NetworkInfo ni : netInfo )
        {
            if  ( ni . getTypeName (). equalsIgnoreCase ( "WIFI" ))
                if  ( ni . isConnected ())
                    haveConnectedWifi =  true ;
            if  ( ni . getTypeName (). equalsIgnoreCase ( "MOBILE" ))
                if  ( ni . isConnected ())
                    haveConnectedMobile =  true ;
        }
        return haveConnectedWifi || haveConnectedMobile ;
    }
}
