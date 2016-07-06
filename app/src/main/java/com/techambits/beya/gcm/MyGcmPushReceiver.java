package com.techambits.beya.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.techambits.beya.activities.AceptacionServicio;
import com.techambits.beya.activities.Gestion;
import com.techambits.beya.activities.SolitudServicioDetallada;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.google.android.gms.gcm.GcmListenerService;

import com.techambits.beya.app.Config;

//import me.leolin.shortcutbadger.ShortcutBadger;

public class MyGcmPushReceiver extends GcmListenerService
{
    private static final String TAG = MyGcmPushReceiver.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private gestionSharedPreferences sharedPreferences;
    private int countPush=0;
    private String tipoUsuario;

    /**
     * Called when message is received.
     *
     * @param from   SenderID of the sender.
     * @param bundle Data bundle containing message data as key/value pairs.
     *               For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle bundle)
    {
        String title = bundle.getString("title");
        String message = bundle.getString("message");
        Boolean isBackground = Boolean.valueOf(bundle.getString("is_background"));
        String timestamp = bundle.getString("created_at");
        String pantallaMostrarPushAndroid = bundle.getString("pantallaMostrarPushAndroid");
        String datosEsteticista = bundle.getString("datosEsteticista");//aqui esta el error
        String datosCliente = bundle.getString("datosCliente");//aqui esta el error
        String codigoCliente = bundle.getString("codigoCliente");//aqui esta el error
        String codigoSolicitud = bundle.getString("codigoSolicitud");//aqui esta el error
        String codigoEsteticista = bundle.getString("codigoEsteticista");//aqui esta el error
        String indicaPush = bundle.getString("indicaPush");
        String pushMessage = bundle.getString("pushMessage");
     /*   Log.e(TAG, "From: " + from);
        Log.e(TAG, "Title: " + title);
        Log.e(TAG, "message: " + message);
        Log.e(TAG, "timestamp: " + timestamp);
        Log.e(TAG, "isBackground: " + pantallaMostrarPushAndroid);*/
        sharedPreferences = new gestionSharedPreferences(getApplicationContext());
        /*tipoUsuario = sharedPreferences.getString("tipoUsuario");

        if (tipoUsuario.equals("E"))
        {
            Log.w(TAG, "ES ESTETICISTA");
            countPush=0;
            //sharedPreferences.putInt("countPush", countPush = sharedPreferences.getInt("countPush") + 1);
            //Log.w(TAG, "" + sharedPreferences.getInt("countPush"));
           // ShortcutBadger.applyCount(this, sharedPreferences.getInt("countPush")); //for 1.1.4
        }*/

        Log.e(TAG, "pantallaMostrarPushAndroid: => " + pantallaMostrarPushAndroid);


        if (!isBackground)
        {
            // verifying whether the app is in background or foreground
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext()))
            {
                if(pantallaMostrarPushAndroid.equals("pushNotificationNormal"))
                {
                    Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                    pushNotification.putExtra("message", message);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    // play notification sound
                    NotificationUtils notificationUtils = new NotificationUtils();
                    notificationUtils.playNotificationSound();
                }
            }

            else
            {
                // app is in background. show the message in notification try
                Intent resultIntent = new Intent(getApplicationContext(), Gestion.class);
                // check for push notification image attachment
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
            }
        }

        else
        {
            if(pantallaMostrarPushAndroid.equals("pushNotificationAceptacionServicio"))
            {
                //Creamos un intent receiver de modo que apenas llegue me lleve a la activity onMap para poder que se cierre el dialog de espera
                //del cliente.
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION_PANTALLA);
                pushNotification.putExtra("datosEsteticista", datosEsteticista);
                Log.e("datosEsteticista",datosEsteticista);
                pushNotification.putExtra("datosCliente", datosCliente);
                pushNotification.putExtra("codigoCliente", codigoCliente);
                pushNotification.putExtra("codigoSolicitud", codigoSolicitud);
                pushNotification.putExtra("codigoEsteticista", codigoEsteticista);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();
                sharedPreferences.putString("datosCliente", datosCliente); //datos del cliente que hizo la solicutud.
            }

            else

            if(pantallaMostrarPushAndroid.equals("pushNotificationFinalizarServicio"))
            {   //Creamos un intent receiver de modo que apenas llegue me lleve a la activity onMap para poder que se cierre el dialog de espera
                //del cliente.
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION_FINALIZAR_SERVICIO_ESTETICISTA);
                pushNotification.putExtra("codigoSolicitud", codigoSolicitud);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();
            }

            else

            if(pantallaMostrarPushAndroid.equals("pushNotificationCancelarServicio"))
            {   //Creamos un intent receiver de modo que apenas llegue me lleve a la activity onMap para poder que se cierre el dialog de espera
                //del cliente.
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION_CANCELAR_SERVICIO_ESTETICISTA);
                pushNotification.putExtra("codigoSolicitud", codigoSolicitud);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();

                if(indicaPush.equals("SolicitudCancelada") && NotificationUtils.isAppIsInBackground(getApplicationContext()))
                {
                    Intent resultIntent = new Intent(getApplicationContext(), SolitudServicioDetallada.class);
                    showNotificationMessage(getApplicationContext(), title, pushMessage, timestamp ,resultIntent);
                }
            }

            else

            if(pantallaMostrarPushAndroid.equals("pushNotificationLlegadaEsteticista"))
            {   //Creamos un intent receiver de modo que apenas llegue me lleve a la activity onMap para poder que se cierre el dialog de espera
                //del cliente.

                Intent pushNotifica = new Intent(Config.PUSH_NOTIFICATION_LLEGADA_ESTETICISTA);
                pushNotifica.putExtra("codigoSolicitud", codigoSolicitud);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotifica);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();

                if(indicaPush.equals("NotificarLlegada") && NotificationUtils.isAppIsInBackground(getApplicationContext()))
                {
                    Intent resultIntent = new Intent(getApplicationContext(), SolitudServicioDetallada.class);
                    showNotificationMessage(getApplicationContext(), title, pushMessage, timestamp ,resultIntent);
                }
            }

            else

            if(pantallaMostrarPushAndroid.equals("pushNotificationCancelarServicioEsteticista"))
            {   //Creamos un intent receiver de modo que apenas llegue me lleve a la activity onMap para poder que se cierre el dialog de espera
                //del cliente.
                Intent pushNotifica = new Intent(Config.PUSH_NOTIFICATION_CANCELAR_SERVICIO_ESTETICISTA);
                pushNotifica.putExtra("codigoSolicitud", codigoSolicitud);
                pushNotifica.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotifica);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();

                //Intent resultIntent = new Intent(getApplicationContext(), Gestion.class);
                // check for push notification image attachment
                if(indicaPush.equals("CancelarSolicitudEsteticista") && NotificationUtils.isAppIsInBackground(getApplicationContext()))
                {
                    Intent resultIntent = new Intent(getApplicationContext(), AceptacionServicio.class);
                    showNotificationMessage(getApplicationContext(), title, message, timestamp ,resultIntent);
                }

            }

            else

            if(pantallaMostrarPushAndroid.equals("pushNotificationCancelarServicioTarjeta"))
            {   //Creamos un intent receiver de modo que apenas llegue me lleve a la activity onMap para poder que se cierre el dialog de espera
                //del cliente.
                Intent pushNotifica = new Intent(Config.PUSH_NOTIFICATION_CANCELACION_SERVICIO_TARJETA);
                pushNotifica.putExtra("message", message);
                pushNotifica.putExtra("codigoSolicitud", codigoSolicitud);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotifica);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils();
                notificationUtils.playNotificationSound();

                if(indicaPush.equals("CancelarServicioTarjeta") && NotificationUtils.isAppIsInBackground(getApplicationContext()))
                {
                    Intent resultIntent = new Intent(getApplicationContext(), SolitudServicioDetallada.class);
                    showNotificationMessage(getApplicationContext(), title, pushMessage, timestamp ,resultIntent);
                }
            }

            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
            // podemos almacenar los mensajes si en el backend se desea almacenar los mensajes enviados
            // sin necesidad de mostrar el push al Cliente.
        }

    }


    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent)
    {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }



}