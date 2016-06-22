package com.techambits.beya.vars;

/**
 * Created by FABiO on 28/03/2016.
 */
public class vars
{

    //Variables Globales.
    public String indicaAndroid = "1"; //Variable que indica 1 si el device es Android.
    public String ipServer = "http://52.204.180.107";//Variable ip de servidor.




    //Variables SharedPreferences.
    public String TOKEN = "TOKEN";//Variable Token generado por GCM para las notificacion push.
    public String MyToken = "MyToken";//Variable que genera el ws para poder acceder a los ws en el Login.
    public String nombreUsuario = "nombreUsuario";//Variable con nombre del Usuario para la sesion iniciada.
    public String emailUser = "emailUser";//Variable con email del Usuario para la sesion iniciada.
    public String GuardarSesion = "GuardarSesion";//Variable que permite que la sesion mantenga iniciada y no loguearse en cada acceso.
    public String clave = "clave";//Variable que almacena la clave del usuario.
    public String tipoUsuario = "tipoUsuario";//Variable que almacena si el usuario el C=Cliente o E=Esteticista.
    public String serialUsuario = "serialUsuario";//Variable que almacena el serial o consecutivo generado en Base de datos del Usuario.
    public String isCheckedSwitch = "isCheckedSwitch";//Variable que almacena los estados true o false si activa o desactiva la geolocalizacion.
    public String statusOnline = "statusOnline";//Variable que almacena si el usuario activa el swicht de geolocalizacion.
    public String serviciosAgregados = "serviciosAgregados";//Variable que almacena los servicios agregados en la aceptacion de servicios.
    public String direccionDomicilio = "";//Almacena direccion domicilio.
    public String serviciosEscogidos = "";//Almacena los servicios escojidos al inicio de la solicitud antes de agregar mas.
    public String valorTotalServiciosTemporalSolicitarServicio = "";//Almacena el costo temporal de la solicitud de servicio; temporal
    //debido a que puede agregar mas servicios y este se actualiza si agrega mas.










}
