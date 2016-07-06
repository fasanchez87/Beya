package com.techambits.beya.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.techambits.beya.CircularImageView.CircularNetworkImageView;
import com.techambits.beya.R;

import com.techambits.beya.adapters.ServiciosSeleccionadosPush;
import com.techambits.beya.app.Config;
import com.techambits.beya.beans.Servicio;

import com.techambits.beya.beans.ValorServicio;
import com.techambits.beya.decorators.DividerItemDecoration;
import com.techambits.beya.services.ServiceActualizarUbicacionProveedor;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.vars.vars;
import com.techambits.beya.volley.ControllerSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SolitudServicioDetallada extends AppCompatActivity implements LocationListener,
        GoogleMap.OnMarkerClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    private String _urlWebServiceAceptarSolicitudServicio;
    private String _urlWebServiceCancelarSolicitudServicio;

    private String statusDisponible;

    private String ubicacionEsteticista;
    Double latitud, longitud;

    Button botonFinalizarServicio;
    public static Button buttonLlegadaEsteticista;

    public TextView nombreClienteSolicitudServicioDetallada, telefonoClienteSolicitudServicioDetallada,
            fechaClienteSolicitudServicioDetallada,DireccionClienteSolicitudServicioDetallada;

    public static TextView precioClienteSolicitudServicioDetallada;

    private String tiempoLlegada;

    private boolean isCheckedSwitch;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private RecyclerView recyclerView;

    GoogleMap mGoogleMap;
    Spinner mSprPlaceType;
    MapView mapView;
    private Marker marker;
    private MarkerOptions markerOptions;

    private MenuItem menuDetalleServicio;
    private MenuItem menuRevisarServicios;
    private MenuItem menuAceptarSolocitud;
    private MenuItem action_cancelar_aceptacion_servicio_esteticista;
    private boolean mostrarItemDetalleServicio = false;

    double mLatitude = 0;
    double mLongitude = 0;

    GoogleApiClient mGoogleApiClient;
    vars var;

    Location mCurrentLocation;

    Gestion gestion;

    CheckBox checkBoxServicio;

    private ArrayList<Servicio> servicioList;

    public SwitchCompat switchActivarLocation;
    String datosCliente;

    private gestionSharedPreferences sharedPreferences;

    private ServiciosSeleccionadosPush mAdapter;
    ArrayList<String> locationCliente;


    private String keyCodigoSolicitudSeleccionado, costoSolicitud, direccionDomicilio,keyCodigoClienteSolicitudSeleccionada,
            ubicacionCliente, nombreCliente, fechaSolicitud, telefonoCliente,imgUsuario;

    private static final String TAG = "SOLICITUD SERVICIO DETALLADA";
    LocationRequest mLocationRequest;
    private static final long INTERVAL = 1000 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    private String statusOnline;


    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solitud_servicio_detallada);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        statusDisponible = "0"; //POR DEFECTO NO ESTA DISPONIBLE

        sharedPreferences = new gestionSharedPreferences(this);

        var = new vars();

        sharedPreferences.putBoolean("ifBack",false);

        CircularNetworkImageView imagenCliente = ((CircularNetworkImageView) findViewById(R.id.imagenClienteSolicitudServicioDetallada));
        imagenCliente.setErrorImageResId(R.drawable.user);
        imagenCliente.setDefaultImageResId(R.drawable.user);

        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);

        botonFinalizarServicio = (Button) findViewById(R.id.buttonFinalizarServicio);
        buttonLlegadaEsteticista = (Button) findViewById(R.id.buttonLlegadaEsteticista);

        Log.d(TAG, "onCreate ...............................");
        if (!isGooglePlayServicesAvailable())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("not google play services availables now!")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                            //startActivity(intent);
                            finish();
                        }
                    }).show();

        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_CANCELAR_SERVICIO_ESTETICISTA))
                {
                    String codigoSolicitud = intent.getExtras().getString("codigoSolicitud");
                    String message = intent.getExtras().getString("message");

                    //displayAlertDialogFinalizarServicioCliente(codigoSolicitud);

                    AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                    builder
                            .setMessage(message)
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    statusDisponible = "0";
                                    _webServiceActualizarDisponibilidadEsteticista(statusDisponible);

                                    sharedPreferences.putBoolean("saveInstanceStateEsteticista", false);
                                    sharedPreferences.putBoolean("mostrarMenuCancelarSolicitud", false);
                                    isCheckedSwitch = true;
                                    sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                                    statusOnline = "1";
                                    sharedPreferences.putString("statusOnline", statusOnline);
                                    Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).setCancelable(false).show();
                }

                else

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_LLEGADA_ESTETICISTA))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                    builder
                            .setTitle("INFORMACIÓN SERVICIO")
                            . setMessage("El Cliente ha notificado su llegada, por favor dirigirse al Boton HE LLEGADO en la parte posterior y de click" +
                                    "para proceder a realizar el servicio.")
                            .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    sharedPreferences.putBoolean("habilitarbotonLlegadaEsteticista", true);
                                    buttonLlegadaEsteticista.setEnabled(sharedPreferences.getBoolean("habilitarbotonLlegadaEsteticista"));
                                }
                            }).show().setCancelable(false);
                }

                else

                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_CANCELACION_SERVICIO_TARJETA))
                {

                    String message = intent.getExtras().getString("message");

                    AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                    builder
                            .setTitle("SERVICIO CANCELADO").
                            setMessage("Motivo: " + message+"\n"+"Puedes sugerirle al Cliente que cambie los datos de la tarjeta y te solicite el servicio nuevamente.")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    statusDisponible = "0";
                                    _webServiceActualizarDisponibilidadEsteticista(statusDisponible);

                                    sharedPreferences.remove("valorTotalServiciosTemporalSolicitarServicio");
                                    sharedPreferences.remove("serviciosEscogidos");
                                    sharedPreferences.remove("serviciosEscogidosEnSolicitarServicio");
                                    sharedPreferences.remove("proveedores");
                                    sharedPreferences.remove("latitudCliente");
                                    sharedPreferences.remove("longitudCliente");
                                    sharedPreferences.remove("direccionDomicilio");
                                    sharedPreferences.remove("serviciosEscojidosListaServiciosCliente");
                                    sharedPreferences.remove("serviciosEscogidosEnListaServiciosCliente");

                                    sharedPreferences.putBoolean("saveInstanceStateEsteticista", false);
                                    sharedPreferences.putBoolean("habilitarbotonLlegadaEsteticista", false);
                                    sharedPreferences.putBoolean("enableBotonFinalizarServicio", false);
                                    sharedPreferences.putBoolean("mostrarMenuCancelarSolicitud", false);

                                    sharedPreferences.putBoolean("ifBack", false);

                                    sharedPreferences.putInt("mostrarBotonLlegadaEsteticista", 8);
                                    sharedPreferences.putInt("mostrarBotonFinalizarServicio", 8);

                                    Servicio servicio = new Servicio();
                                    servicio = null;

                                    //stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                                    //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                                    isCheckedSwitch = false;
                                    sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                                    statusOnline = "0";
                                    sharedPreferences.putString("statusOnline", statusOnline);


                                    sharedPreferences.putBoolean("saveInstanceStateEsteticista", false);
                                    sharedPreferences.putBoolean("mostrarMenuCancelarSolicitud", false);
                                    isCheckedSwitch = true;
                                    sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                                    statusOnline = "1";
                                    sharedPreferences.putString("statusOnline", statusOnline);

                                    Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
                                    startActivity(intent);
                                    dialog.dismiss();
                                    finish();
                                }
                            }).setCancelable(false).show();

                    /*sharedPreferences.putBoolean("habilitarbotonLlegadaEsteticista", true);
                    buttonLlegadaEsteticista.setEnabled(sharedPreferences.getBoolean("habilitarbotonLlegadaEsteticista"));*/

                }

            }
        };


        Intent intent = getIntent();
        keyCodigoSolicitudSeleccionado = intent.getStringExtra("codigoSolicitud");
        ubicacionCliente = intent.getStringExtra("ubicacionCliente");
        keyCodigoClienteSolicitudSeleccionada = intent.getStringExtra("codigoCliente");
        nombreCliente = intent.getStringExtra("nombreCliente");
        fechaSolicitud = intent.getStringExtra("fechaSolicitud");
        telefonoCliente = intent.getStringExtra("telefonoUsuario");
        direccionDomicilio = intent.getStringExtra("direccionDomicilio");
        costoSolicitud = intent.getStringExtra("costoSolicitud");

        imgUsuario = intent.getStringExtra("imgUsuario");

        if (sharedPreferences.getBoolean("saveInstanceStateEsteticista"))
        {

            Log.w("INICIASERVICIO","STARTSERVICE");


            startService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));

            keyCodigoSolicitudSeleccionado = sharedPreferences.getString("keyCodigoSolicitudSeleccionado");
            ubicacionCliente = sharedPreferences.getString("ubicacionCliente");
            keyCodigoClienteSolicitudSeleccionada = sharedPreferences.getString("keyCodigoClienteSolicitudSeleccionada");
            telefonoCliente = sharedPreferences.getString("telefonoCliente");
            nombreCliente = sharedPreferences.getString("nombreCliente");
            fechaSolicitud = sharedPreferences.getString("fechaSolicitud");
            direccionDomicilio = sharedPreferences.getString("direccionDomicilio");
            costoSolicitud = sharedPreferences.getString("costoSolicitud");
            imgUsuario = sharedPreferences.getString("imgUsuario");


            botonFinalizarServicio.setVisibility(sharedPreferences.getInt("mostrarBotonFinalizarServicio"));
            buttonLlegadaEsteticista.setVisibility(sharedPreferences.getInt("mostrarBotonLlegadaEsteticista"));

            buttonLlegadaEsteticista.setEnabled(sharedPreferences.getBoolean("habilitarbotonLlegadaEsteticista"));
            botonFinalizarServicio.setEnabled(sharedPreferences.getBoolean("enableBotonFinalizarServicio"));

            sharedPreferences.putBoolean("ifBack",true);




        }

        gestion = new Gestion();

        tiempoLlegada = "";

        TextView nombreClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.nombreClienteSolicitudServicioDetallada);
        TextView telefonoClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.telefonoClienteSolicitudServicioDetallada);
        TextView fechaClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.fechaClienteSolicitudServicioDetallada);
        precioClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.precioClienteSolicitudServicioDetallada);
        TextView DireccionClienteSolicitudServicioDetallada = (TextView) findViewById(R.id.DireccionClienteSolicitudServicioDetallada);


        imagenCliente.setImageUrl(imgUsuario, imageLoader);


        nombreClienteSolicitudServicioDetallada.setText(nombreCliente);
        fechaClienteSolicitudServicioDetallada.setText(fechaSolicitud);
        precioClienteSolicitudServicioDetallada.setText("$"+nf.format(Integer.parseInt(costoSolicitud)));
        ValorServicio.setValorServicio(Integer.parseInt(costoSolicitud));
        telefonoClienteSolicitudServicioDetallada.setText(telefonoCliente);
        DireccionClienteSolicitudServicioDetallada.setText(direccionDomicilio);


        botonFinalizarServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                builder
                        .setMessage("¿Esta seguro de finalizar el servicio?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                displayAlertDialogFinalizarServicio();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                        //startActivity(intent);
                        //finish();
                    }
                }).show();


            }
        });


        buttonLlegadaEsteticista.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {


                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                builder
                        .setMessage("¿Esta seguro de haber llegado al domicilio?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {

                                //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                                isCheckedSwitch = false;
                                sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                                statusOnline = "0";
                                sharedPreferences.putString("statusOnline", statusOnline);
                                stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));


                                sharedPreferences.putInt("mostrarBotonLlegadaEsteticista", 8);
                                buttonLlegadaEsteticista.setVisibility(sharedPreferences.getInt("mostrarBotonLlegadaEsteticista"));

                                sharedPreferences.putBoolean("enableBotonFinalizarServicio", true);
                                botonFinalizarServicio.setEnabled(sharedPreferences.getBoolean("enableBotonFinalizarServicio"));

                                sharedPreferences.putBoolean("mostrarMenuCancelarSolicitud", false);
                                action_cancelar_aceptacion_servicio_esteticista.
                                        setVisible(sharedPreferences.getBoolean("mostrarMenuCancelarSolicitud"));


                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                        //startActivity(intent);
                        //finish();
                    }
                }).show();



            }
        });



        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapClienteSolicitudServicioViaPush)).getMap();

        servicioList = new ArrayList<Servicio>();


        locationCliente = new ArrayList<String>(Arrays.asList(ubicacionCliente.split(",")));

        latitud = Double.parseDouble(locationCliente.get(0));
        longitud = Double.parseDouble(locationCliente.get(1));

        servicioList = sharedPreferences.getHashMapObjectServicio().get(keyCodigoSolicitudSeleccionado);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_servicioSolictadoViaPush);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;

            }

            ubicacionEsteticista = mLatitude+","+mLongitude;
            setUbicacionEsteticista(ubicacionEsteticista);

            markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(latitud,longitud);
            markerOptions.position(latLng);
            markerOptions.title("Tu cliente aqui!");

            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.beya_logo_on_map));

            mGoogleMap.addMarker(markerOptions);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        mAdapter = new ServiciosSeleccionadosPush(servicioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        sharedPreferences.putString("keyCodigoSolicitudSeleccionado", keyCodigoSolicitudSeleccionado);
        sharedPreferences.putString("ubicacionCliente", ubicacionCliente);
        sharedPreferences.putString("keyCodigoClienteSolicitudSeleccionada", keyCodigoClienteSolicitudSeleccionada);
        sharedPreferences.putString("nombreCliente", nombreCliente);
        sharedPreferences.putString("fechaSolicitud", fechaSolicitud);
        sharedPreferences.putString("direccionDomicilio", direccionDomicilio);
        sharedPreferences.putString("costoSolicitud", costoSolicitud);
        sharedPreferences.putString("imgUsuario", imgUsuario);
        sharedPreferences.putString("telefonoCliente", telefonoCliente);

        super.onSaveInstanceState(savedInstanceState);

    }



    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        //stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));

    }

    @Override
    public void onPause()
    {
        //SI LA APP PIERDE EL FOCO, IGUALMENTE DEBERIA HABILITARSE EL BOTON DE "HE LLEGADO". POR ESO COMENTO ESTA LINEA.

        //LocalBroadcastManager.getInstance(this.getApplicationContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        //buttonLlegadaEsteticista.setEnabled(true);
        super.onPause();
    }



    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }





    @Override
    public void onBackPressed()
    {
        if (sharedPreferences.getBoolean("ifBack"))
        {
            //DISABLED BUTTON BACK
            //Toast.makeText(this,"TRUE.", Toast.LENGTH_LONG).show();
        }

        else
        {
            //Toast.makeText(this,"false.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
            startActivity(intent);
            finish();
            super.onBackPressed(); // Process Back key  default behavior.
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_CANCELAR_SERVICIO_ESTETICISTA));

        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_LLEGADA_ESTETICISTA));

        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_CANCELACION_SERVICIO_TARJETA));

       NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();

    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menuDetalleServicio.setVisible(sharedPreferences.getBoolean("mostrarMenuDetalleServicio"));
        menuRevisarServicios.setVisible(sharedPreferences.getBoolean("mostrarMenuRevisarServicios"));
        menuAceptarSolocitud.setVisible(sharedPreferences.getBoolean("mostrarMenuAceptarSolocitud"));
        action_cancelar_aceptacion_servicio_esteticista.setVisible(sharedPreferences.getBoolean("mostrarMenuCancelarSolicitud"));


        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_esteticista_solicitud_detallada, menu);

        menuDetalleServicio = (MenuItem) menu.findItem(R.id.action_observar_servicios);
        menuRevisarServicios = (MenuItem) menu.findItem(R.id.action_revisar_servicios_solicitud);
        menuAceptarSolocitud = (MenuItem) menu.findItem(R.id.action_aceptar_servicio_aceptacion_servicio_detallado);
        action_cancelar_aceptacion_servicio_esteticista = (MenuItem) menu.findItem(R.id.action_cancelar_aceptacion_servicio_esteticista);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_observar_servicios:
                Intent intent = new Intent(SolitudServicioDetallada.this, ListaServiciosEsteticista.class);
                intent.putExtra("codigoSolicitud", keyCodigoSolicitudSeleccionado);
                startActivity(intent);
                return true;

            case R.id.action_cancelar_aceptacion_servicio_esteticista:
                //cancelar servicio por parte del esteticista
                AlertDialog.Builder builder2 = new AlertDialog.Builder(SolitudServicioDetallada.this);
                builder2
                        .setMessage("¿Esta seguro de cancelar su asistencia a la solicitud de servicio?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                        {
                            @Override
                             public void onClick(DialogInterface dialog, int id)
                            {
                                displayAlertDialogCancelarServicioEsteticista();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                        //startActivity(intent);
                        //finish();
                    }
                }).setCancelable(false).show();

                return true;

            case R.id.action_aceptar_servicio_aceptacion_servicio_detallado:
            displayAlertDialog();
            return true;

            case R.id.action_revisar_servicios_solicitud:
                Intent intentRevisionServicios = new Intent(SolitudServicioDetallada.this, ListaServiciosRevisarEsteticista.class);
                intentRevisionServicios.putExtra("codigoSolicitud", keyCodigoSolicitudSeleccionado);
                intentRevisionServicios.putExtra("costoSolicitud", costoSolicitud);
                startActivity(intentRevisionServicios);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRadioButtonClicked(View view)
    {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId())
        {
            case R.id.radioButtonDiezMinutos:
                if (checked)
                    tiempoLlegada = "10";
                    break;
            case R.id.radioButtonTreintaMinutos:
                if (checked)
                    tiempoLlegada = "30";
                    break;
            case R.id.radioButtonCincuentaMinutos:
                if (checked)
                    tiempoLlegada = "50";
                    break;
            default:
                    tiempoLlegada = "10";
        }
    }

    public void displayAlertDialog()
    {

        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_aceptacion_servicio_esteticista, null);
        RadioButton radioTiempoButton;

        final RadioGroup RadioGroup = (RadioGroup) alertLayout.findViewById(R.id.radioGroupTiemposLlegadaEsteticista);
        final Button buttonAceptarServicioDialogAceptacionServicio = (Button) alertLayout.findViewById(R.id.buttonAceptarServicioDialogAceptacionServicio);
        final Button buttonCancelarServicioDialogAceptacionServicio = (Button) alertLayout.findViewById(R.id.buttonCancelarServicioDialogAceptacionServicio);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("TIEMPO DE LLEGADA");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        buttonAceptarServicioDialogAceptacionServicio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //sharedPreferences.putString("statusOnline","0");
                _webServiceAceptarSolicitudServicios();
                dialog.dismiss();
            }
        });

        buttonCancelarServicioDialogAceptacionServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        dialog.show();


    }

    public void displayAlertDialogFinalizarServicio()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.dialog_finalizar_servicio_esteticista, null);
        RadioButton radioTiempoButton;

        final Button buttonEnviarComentarioFinalizarServicio = (Button) alertLayout.findViewById(R.id.buttonEnviarComentarioFinalizarServicio);
        final EditText editTextComentariosServicioPrestadoEsteticista = (EditText)
                         alertLayout.findViewById(R.id.editTextComentariosServicioPrestadoEsteticista);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Finalizar Servicio");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        buttonEnviarComentarioFinalizarServicio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                statusDisponible = "0";
                _webServiceActualizarDisponibilidadEsteticista(statusDisponible);

                //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                isCheckedSwitch = true;
                sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                statusOnline = "1";
                sharedPreferences.putString("statusOnline", statusOnline);

                buttonLlegadaEsteticista.setVisibility(sharedPreferences.getInt("mostrarBotonLlegadaEsteticista"));

                sharedPreferences.putInt("buttonLlegadaEsteticista", 8);

                sharedPreferences.putBoolean("mostrarMenuCancelarSolicitud", false);
                sharedPreferences.putBoolean("ifBack",false);



                buttonLlegadaEsteticista.setVisibility(sharedPreferences.getInt("mostrarBotonLlegadaEsteticista"));

                sharedPreferences.putBoolean("habilitarbotonLlegadaEsteticista", false);
                sharedPreferences.putBoolean("enableBotonFinalizarServicio", false);

                if (sharedPreferences.getBoolean("saveInstanceStateEsteticista"))
                {
                    stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                    sharedPreferences.putBoolean("saveInstanceStateEsteticista", false);
                    Log.d("PARO SERVICIO", " EN SAVED");
                }


               /* stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                isCheckedSwitch = false;
                sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                statusOnline = "0";
                sharedPreferences.putString("statusOnline", statusOnline);*/




                Log.d("START SERVICIO", " EN SAVED");

                String comentarioEsteticista = editTextComentariosServicioPrestadoEsteticista.getText().toString();
                _webServiceFinalizarServicio(comentarioEsteticista);
                dialog.dismiss();
                Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
                startActivity(intent);
                finish();

                startService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));

            }
        });

        dialog.show();

    }

    public String getUbicacionEsteticista() {
        return ubicacionEsteticista;
    }

    public void setUbicacionEsteticista(String ubicacionEsteticista) {
        this.ubicacionEsteticista = ubicacionEsteticista;
    }

    public void displayAlertDialogCancelarServicioEsteticista()
    {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.custom_dialog_finalizar_servicio_esteticista, null);

        final Button buttonEnviarEsteticistaCancelarServicio = (Button) alertLayout.findViewById(R.id.buttonEnviarEsteticistaCancelarServicio);
        final EditText editTextObservacionEsteticistaCancelarServicio = (EditText) alertLayout.findViewById(R.id.editTextObservacionEsteticistaCancelarServicio);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Cancelación Servicio");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        buttonEnviarEsteticistaCancelarServicio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                /*stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                stopService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));*/


                statusDisponible = "0";
                _webServiceActualizarDisponibilidadEsteticista(statusDisponible);

                sharedPreferences.remove("valorTotalServiciosTemporalSolicitarServicio");
                sharedPreferences.remove("serviciosEscogidos");
                sharedPreferences.remove("serviciosEscogidosEnSolicitarServicio");
                sharedPreferences.remove("proveedores");
                sharedPreferences.remove("latitudCliente");
                sharedPreferences.remove("longitudCliente");
                sharedPreferences.remove("direccionDomicilio");
                sharedPreferences.remove("serviciosEscojidosListaServiciosCliente");
                sharedPreferences.remove("serviciosEscogidosEnListaServiciosCliente");

                sharedPreferences.putBoolean("saveInstanceStateEsteticista", false);
                sharedPreferences.putBoolean("habilitarbotonLlegadaEsteticista", false);
                sharedPreferences.putBoolean("enableBotonFinalizarServicio", false);
                sharedPreferences.putBoolean("mostrarMenuCancelarSolicitud", false);

                sharedPreferences.putBoolean("ifBack",false);



                sharedPreferences.putInt("mostrarBotonLlegadaEsteticista", 8);
                sharedPreferences.putInt("mostrarBotonFinalizarServicio", 8);


                Servicio servicio = new Servicio();
                servicio = null;

                String observacionEsteticista = editTextObservacionEsteticistaCancelarServicio.getText().toString();
                //stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                isCheckedSwitch = true;
                sharedPreferences.putBoolean("isCheckedSwitch", isCheckedSwitch);
                statusOnline = "1";
                sharedPreferences.putString("statusOnline", statusOnline);
                _webServiceCancelarSolicitudServicioEsteticista(keyCodigoSolicitudSeleccionado, observacionEsteticista);
                dialog.dismiss();

            }
        });

        dialog.show();


    }


    public void _webServiceCancelarSolicitudServicioEsteticista(final String codigoSolicitud, final String observacionEsteticista)
    {
        _urlWebServiceAceptarSolicitudServicio = var.ipServer.concat("/ws/CancelarSolicitudEsteticista");

        Log.w("SolicitudServicio", "" + _urlWebServiceAceptarSolicitudServicio);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceAceptarSolicitudServicio, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if(status)
                            {

                                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage(message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).setCancelable(false).show();
                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage(message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                //startActivity(intent);
                                                //finish();
                                            }
                                        }).setCancelable(false).show();
                            }
                        }
                        catch (JSONException e)
                        {


                            //progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();



                            e.printStackTrace();
                        }
                    }

                },


                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        if (error instanceof TimeoutError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Por favor, conectese a la red.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof AuthFailureError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de autentificación en la red, favor contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof ServerError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error server, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de red, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                           // progressBar.setVisibility(View.GONE);
                        }

                        else

                        if (error instanceof ParseError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de conversión Parser, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                    }

                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("codigoSolicitud", codigoSolicitud);
                headers.put("observacionEsteticista", observacionEsteticista);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;


            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }


    public void _webServiceAceptarSolicitudServicios()
    {

        _urlWebServiceAceptarSolicitudServicio = var.ipServer.concat("/ws/AceptarSolicitudServicio");

        if(tiempoLlegada.toString().isEmpty())//POR DEFECTO 10 MIN.
        {
            tiempoLlegada = "10";
        }

        Log.w("ESTETICISTA","serialUsuarioEsteticista"+sharedPreferences.getString("serialUsuario"));
        Log.w("ESTETICISTA", "serialUsuarioCliente" + keyCodigoClienteSolicitudSeleccionada);
        Log.w("ESTETICISTA", "codigoSolicitud" + keyCodigoSolicitudSeleccionado);
        Log.w("ESTETICISTA", "ubicacionEsteticista" + getUbicacionEsteticista());
        Log.w("ESTETICISTA", "tiempoLlegadaEsteticista" + tiempoLlegada);
        Log.w("ESTETICISTA", "tokenGCM" + sharedPreferences.getString("TOKEN"));
        Log.w("ESTETICISTA", "MyToken" + sharedPreferences.getString("MyToken"));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceAceptarSolicitudServicio, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if(status)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage("Solicitud de servicio en Marcha, guiese en el mapa para llegar donde su Cliente.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {




                                                statusDisponible = "1";//1: ESTA OCUPADO.

                                                sharedPreferences.putBoolean("ifBack",true);
                                                sharedPreferences.putBoolean("saveInstanceStateEsteticista", true);
                                                //_webServiceAceptarSolicitudServicios();
                                                mostrarItemDetalleServicio = true;
                                                sharedPreferences.putBoolean("mostrarMenuDetalleServicio", true);
                                                menuDetalleServicio.setVisible(sharedPreferences.getBoolean("mostrarMenuDetalleServicio"));
                                                sharedPreferences.putBoolean("mostrarMenuRevisarServicios", false);
                                                menuRevisarServicios.setVisible(sharedPreferences.getBoolean("mostrarMenuRevisarServicios"));
                                                sharedPreferences.putBoolean("mostrarMenuAceptarSolocitud", false);
                                                menuAceptarSolocitud.setVisible(sharedPreferences.getBoolean("mostrarMenuAceptarSolocitud"));
                                                sharedPreferences.putBoolean("mostrarMenuCancelarSolicitud", true);
                                                action_cancelar_aceptacion_servicio_esteticista.setVisible(sharedPreferences.getBoolean("mostrarMenuCancelarSolicitud"));

                                                sharedPreferences.putInt("mostrarBotonFinalizarServicio", 0);
                                                botonFinalizarServicio.setVisibility(sharedPreferences.getInt("mostrarBotonFinalizarServicio"));
                                                sharedPreferences.putBoolean("mostrarbotonLlegadaEsteticista", true);
                                                sharedPreferences.putBoolean("enableBotonFinalizarServicio",false);
                                                sharedPreferences.putInt("mostrarBotonLlegadaEsteticista", 0);
                                                buttonLlegadaEsteticista.setVisibility(sharedPreferences.getInt("mostrarBotonLlegadaEsteticista"));
                                                botonFinalizarServicio.setEnabled(sharedPreferences.getBoolean("enableBotonFinalizarServicio"));
                                                Log.e("TOKENPRUEBAACEPTASOLICITUD", sharedPreferences.getString("TOKEN"));



                                            }
                                        }).show().setCancelable(false);
                            }

                            else
                            {
                               AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setTitle("INFORMACIÓN SOLICITUD").setMessage("Aviso: "+message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {

                                                sharedPreferences.putBoolean("saveInstanceStateEsteticista", false);





                                                Intent intent = new Intent(SolitudServicioDetallada.this, Gestion.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).show().setCancelable(false);
                            }
                        }
                        catch (JSONException e)
                        {


                            //progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();

                            e.printStackTrace();
                        }
                    }

                },


                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        if (error instanceof TimeoutError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Por favor, conectese a la red.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof AuthFailureError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de autentificación en la red, favor contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof ServerError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error server, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de red, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);
                        }

                        else

                        if (error instanceof ParseError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de conversión Parser, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                    }

                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("serialUsuarioEsteticista", sharedPreferences.getString("serialUsuario"));
                headers.put("serialUsuarioCliente", keyCodigoClienteSolicitudSeleccionada);
                headers.put("codigoSolicitud", keyCodigoSolicitudSeleccionado);
                headers.put("ubicacionEsteticista", getUbicacionEsteticista());
                headers.put("tiempoLlegadaEsteticista", tiempoLlegada);
                headers.put("statusDisponible", "1");
                headers.put("tokenGCM", sharedPreferences.getString("TOKEN"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void _webServiceFinalizarServicio(final String comentarioEsteticista)
    {

        _urlWebServiceAceptarSolicitudServicio = var.ipServer.concat("/ws/FinalizarServicio");


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebServiceAceptarSolicitudServicio, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            // String message = response.getString("status");

                            if(status)
                            {


                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                                builder
                                        .setMessage(message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                //startActivity(intent);
                                                //finish();
                                            }
                                        }).show();
                            }
                        }
                        catch (JSONException e)
                        {


                            //progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();



                            e.printStackTrace();
                        }
                    }

                },


                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        if (error instanceof TimeoutError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Por favor, conectese a la red.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof AuthFailureError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de autentificación en la red, favor contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof ServerError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error server, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de red, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);
                        }

                        else

                        if (error instanceof ParseError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolitudServicioDetallada.this);
                            builder
                                    .setMessage("Error de conversión Parser, contacte a su proveedor de servicios.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();
                            //progressBar.setVisibility(View.GONE);

                        }

                    }

                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("codigoSolicitud", keyCodigoSolicitudSeleccionado);
                headers.put("observaServicio", comentarioEsteticista );
                //headers.put("observaServicio", statusDisponible );
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;


            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void _webServiceActualizarDisponibilidadEsteticista(final String statusDisponible)
    {
        _urlWebServiceAceptarSolicitudServicio = var.ipServer.concat("/ws/ActualizarDisponibilidadEsteticista");

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebServiceAceptarSolicitudServicio, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message = response.getString("message");

                            if(status)
                            {
                                Log.w("RESPUESTA", ""+message);
                            }
                            else
                            {
                                Log.w("RESPUESTA", ""+message);

                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                    }
                })

        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("codigoEsteticista", sharedPreferences.getString("serialUsuario"));
                headers.put("statusDisponibilidad", statusDisponible);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }



    private void updateUI()
    {
        Log.d(TAG, "UI update initiated............."+TAG);
        if (null != mCurrentLocation) {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());
            Log.d(TAG, "At Time: " + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider());

        } else {
            Log.d(TAG, "location is null ...............");
        }
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }



    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLatitude = mCurrentLocation.getLatitude();
        mLongitude = mCurrentLocation.getLongitude();
        LatLng latLng = new LatLng(mLatitude, mLongitude);
       // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
       // mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(100));
        updateUI();
    }
}
