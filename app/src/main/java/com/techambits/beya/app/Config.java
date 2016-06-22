package com.techambits.beya.app;

/**
 * Created by FABiO on 23/02/2016.
 * This class contains app configuration information related to GCM.
 */
public class Config
{

    // flag to identify whether to show single line
    // or multi line text in push notification tray
    public static boolean appendNotificationMessages = true;

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String PUSH_NOTIFICATION_PANTALLA = "MapFragmentUbicarProveedores";
    public static final String PUSH_NOTIFICATION_FINALIZAR_SERVICIO_ESTETICISTA = "pushNotificationFinalizarServicio";
    public static final String PUSH_NOTIFICATION_CANCELAR_SERVICIO_ESTETICISTA = "pushNotificationCancelarServicioEsteticista";
    public static final String PUSH_NOTIFICATION_LLEGADA_ESTETICISTA = "pushNotificationLlegadaEsteticista";
    public static final String PUSH_NOTIFICATION_CANCELACION_SERVICIO_TARJETA = "pushNotificationCancelarServicioTarjeta";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;

    // id to handle the notification in the notification try
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
}