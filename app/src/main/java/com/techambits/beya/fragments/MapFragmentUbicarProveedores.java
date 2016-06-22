package com.techambits.beya.fragments;

import android.Manifest;
import android.app.AlertDialog;

import com.techambits.beya.app.Config;
import com.techambits.beya.beans.Servicio;
import com.techambits.beya.services.HeartBeatServiceGCM;
import com.techambits.beya.services.ServiceObtenerUbicacionEsteticista;
import com.techambits.beya.vars.vars;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.android.volley.toolbox.NetworkImageView;
import com.techambits.beya.R;
import com.techambits.beya.beans.Proveedor;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.volley.ControllerSingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
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

import com.google.android.gms.location.LocationListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class MapFragmentUbicarProveedores extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    GoogleMap mGoogleMap;
    Spinner mSprPlaceType;
    MapView mapView;
    private Marker marker;
    private MarkerOptions markerOptions;

    private static final long INTERVAL = 1000 * 5;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    Button btnFusedLocation;
    TextView tvLocation;

    Location mCurrentLocation;
    String mLastUpdateTime;


    private String TAG = MapFragmentUbicarProveedores.class.getSimpleName();

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static ProgressDialog progressDialog;
    public static AlertDialog alertDialogEsperaEsteticista;



    private Timer mTimer = null;
    public static final long TIEMPO_LIMITE = 120 * 1000; // 2 minute
    public static final long TIEMPO_INICIO = 1 * 1000; // 1 seconds
    private Handler mHandler = new Handler();

    private String codigoSolicitud;

    public static CountDownTimer countDownTimer;

    public static AlertDialog.Builder alertEsperaEsteticista;
    public static AlertDialog alertDialog;

    JSONArray jsonArray;

    private String indicaAndroid = "";

    GoogleApiClient mGoogleApiClient;
    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    public vars vars;

    private gestionSharedPreferences sharedPreferences;

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    String[] mPlaceType = null;
    String[] mPlaceTypeName = null;

    LocationRequest mLocationRequest;

    public static AlertDialog.Builder alertDialogBuilder;


    public static double getmLatitude() {
        return mLatitude;
    }

    public static void setmLatitude(double mLatitude) {
        MapFragmentUbicarProveedores.mLatitude = mLatitude;
    }

    public static double mLatitude = 0;
    public static double mLongitude = 0;

    private String _urlWebService;


    Button buttonFindCoach;
    LocationManager lm;

    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public MapFragmentUbicarProveedores() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //getActivity().startService(new Intent(getActivity(), HeartBeatServiceGCM.class));


        sharedPreferences = new gestionSharedPreferences(this.getActivity());
        vars = new vars();

        alertDialogBuilder = new AlertDialog.Builder(getActivity());


        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION_PANTALLA))
                {
                    // new push notification is received
                    //progressDialog.dismiss();
                    alertDialogEsperaEsteticista.dismiss();

                    String datosEsteticista = intent.getExtras().getString("datosEsteticista");
                    String datosCliente = intent.getExtras().getString("datosCliente");
                    String codigoCliente = intent.getExtras().getString("codigoCliente");
                    codigoSolicitud = intent.getExtras().getString("codigoSolicitud");
                    String codigoEsteticista = intent.getExtras().getString("codigoEsteticista");
/*
                    Intent serviceIntentOrdenServicio = new Intent("ServiceObtenerUbicacionEsteticista");
*/
                    Intent serviceIntentOrdenServicio = new Intent(context,ServiceObtenerUbicacionEsteticista.class);

                    serviceIntentOrdenServicio.putExtra("datosEsteticista", datosEsteticista);
                    serviceIntentOrdenServicio.putExtra("datosCliente", datosCliente);
                    serviceIntentOrdenServicio.putExtra("codigoCliente", codigoCliente);
                    serviceIntentOrdenServicio.putExtra("codigoSolicitud", codigoSolicitud);
                    serviceIntentOrdenServicio.putExtra("codigoEsteticista", codigoEsteticista);
                    context.startService(serviceIntentOrdenServicio);
                    //startService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));



                    //Log.w("ALERTA", "Push notification is received!" + intent.getStringExtra("datosEsteticista"));

                 /*  Toast.makeText(getActivity(), "SERVICIO ACEPTADO: " +
                            intent.getExtras().getString("datosEsteticista"), Toast.LENGTH_LONG).show();*/

                }
            }
        };

        mCustomMarkerView = ((LayoutInflater) MapFragmentUbicarProveedores.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_info_market_map, null);


    }






    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mGoogleMap = ((SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(MapFragmentUbicarProveedores.this.getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


            if (!isGooglePlayServicesAvailable())
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
                builder
                        .setMessage("SIN SOPORTE DE GOOGLE PLAY SERVICES.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                //startActivity(intent);
                                //finish();
                            }
                        }).show();
            }

            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);


            if (ActivityCompat.checkSelfPermission(MapFragmentUbicarProveedores.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapFragmentUbicarProveedores.this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }



        cargarProveedoresServicios();

    }






    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    public static double getmLongitude() {
        return mLongitude;
    }

    public static void setmLongitude(double mLongitude) {
        MapFragmentUbicarProveedores.mLongitude = mLongitude;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }


    private boolean isGooglePlayServicesAvailable()
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapFragmentUbicarProveedores.this.getActivity());
        if (ConnectionResult.SUCCESS == status)
        {
            return true;
        }
        else
        {
            GooglePlayServicesUtil.getErrorDialog(status, MapFragmentUbicarProveedores.this.getActivity(), 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map_fragment_ubicar_proveedores, container, false);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_ubicar_proveedores_on_map, menu);
        /*SolicitarServicio.this.getActivity().getMenuInflater().inflate(R.menu.solicitar_servicio_menu, menu);*/
        super.onCreateOptionsMenu(menu, inflater);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_solicitar_servicio_onmap:
                displayAlertDialog();

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void displayAlertDialog()
    {
        LayoutInflater inflater = MapFragmentUbicarProveedores.this.getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
        final TextInputLayout inputLayoutDireccionDomicilio = (TextInputLayout) alertLayout.findViewById(R.id.input_layout_direccion_domicilio);
        final EditText editTextDireccionDomicilio = (EditText) alertLayout.findViewById(R.id.edit_text_direccion_domicilio);

        editTextDireccionDomicilio.setText(sharedPreferences.getString("direccionDomicilio").toString());
        Log.d("DIRECCION",""+sharedPreferences.getString("direccionDomicilio").toString());

        final Button botonConfirmarDomicilio = (Button) alertLayout.findViewById(R.id.btn_confirmar_domiclio);
        final Button btn_cancelar_domiclio = (Button) alertLayout.findViewById(R.id.btn_cancelar_domiclio);

        AlertDialog.Builder alert = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
        alert.setTitle("Direccion Domicilio");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        final AlertDialog dialog = alert.create();

        botonConfirmarDomicilio.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String editTextDireccion = editTextDireccionDomicilio.getText().toString().trim();

                if (editTextDireccion.isEmpty())
                {
                    inputLayoutDireccionDomicilio.setError("Digite dirección.");//cambiar a edittext en register!!
                    view.requestFocus();
                }

                else
                {

                    _webServiceEnviarNotificacionPushATodos(sharedPreferences.getString("serialUsuario"));
                    sharedPreferences.putString("direccionDomicilio", editTextDireccionDomicilio.getText().toString());
                    dialog.dismiss();
                }

            }
        });

        btn_cancelar_domiclio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    public void cargarProveedoresServicios() {


        for (int i = 0; i <= sharedPreferences.getListObject("proveedores", Proveedor.class).size() - 1; i++) {

            mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.imagenProveedorCustomInfoMarket);


            mGoogleMap.setInfoWindowAdapter(new IconizedWindowAdapter(getActivity().getLayoutInflater()));

            markerOptions = new MarkerOptions();
            final LatLng latLng = new LatLng(Double.parseDouble(sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getLatitudUsuario()),
                    Double.parseDouble(sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getLongitudUsuario()));

            markerOptions.position(latLng);
            markerOptions.title(sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getNombreProveedor().toString() + " " + sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getApellidoProveedor());
            markerOptions.snippet(sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getEmailProveedor().toString());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.beya_logo_on_map));

/*
            Glide.with(MapFragmentUbicarProveedores.this.getActivity()).
                    load("http://52.72.85.214/ws/images/user1.jpg")
                    .asBitmap()
                    .fitCenter()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, bitmap))).anchor(0.5f, 0.5f));
                            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
                        }
                    });*/


            mGoogleMap.addMarker(markerOptions);

        }


    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        LocalBroadcastManager.getInstance(MapFragmentUbicarProveedores.this.getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);

    }

    @Override
    public void onPause() {
        super.onPause();
        //LocalBroadcastManager.getInstance(MapFragmentUbicarProveedores.this.getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");

    }

    @Override
    public void onResume()
    {


        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }

        super.onResume();

       /* // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));*/
        //ShortcutBadger.removeCount(MapFragmentUbicarProveedores.this.getActivity()); //for 1.1.4

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(MapFragmentUbicarProveedores.this.getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION_PANTALLA));


    }

    boolean ifBack = true;



    /**
     * Called when the location has changed.
     * <p>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location)
    {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        sharedPreferences.putDouble("latitudCliente", mCurrentLocation.getLatitude());
        sharedPreferences.putDouble("longitudCliente", mCurrentLocation.getLongitude());
        //updateUI();
    }

    private void updateUI()
    {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation)
        {
            String lat = String.valueOf(mCurrentLocation.getLatitude());
            String lng = String.valueOf(mCurrentLocation.getLongitude());

            Toast.makeText(MapFragmentUbicarProveedores.this.getActivity(), "At Time: " + mLastUpdateTime + "\n" +
                    "Latitude: " + lat + "\n" +
                    "Longitude: " + lng + "\n" +
                    "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                    "Provider: " + mCurrentLocation.getProvider(), Toast.LENGTH_LONG).show();
        }
        else
        {
            Log.d(TAG, "location is null ...............");
        }
    }



    @Override
    public void onConnectionSuspended(int i)
    {
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());

    }








    public class IconizedWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        LayoutInflater inflater=null;
        //private View view;
        View popup;

        public IconizedWindowAdapter(LayoutInflater inflater) {
            this.inflater=inflater;

        }

        public IconizedWindowAdapter() {
            popup = inflater.inflate(R.layout.custom_info_market_map, null);


        }

        @Override
        public View getInfoWindow(Marker marker)
        {





            // iv.setDefaultImageResId(R.drawable.ic_launcher);// poner imagen por default
            if (imageLoader == null)
                imageLoader = ControllerSingleton.getInstance().getImageLoader();


            return (null);


        }

        @Override
        public View getInfoContents(Marker marker)
        {


            if (imageLoader == null)
                imageLoader = ControllerSingleton.getInstance().getImageLoader();

            popup = inflater.inflate(R.layout.custom_info_market_map, null);


            NetworkImageView iv = (NetworkImageView) popup.findViewById(R.id.imagenProveedorCustomInfoMarket);

            iv.setImageUrl("http://52.72.85.214/ws/images/minion.jpg", imageLoader);



            TextView tvTitle = (TextView) popup.findViewById(R.id.title);
            TextView tvSnippet = (TextView) popup.findViewById(R.id.title);
            tvTitle.setText(marker.getTitle());
            tvSnippet = (TextView) popup.findViewById(R.id.snippet);
            tvSnippet.setText(marker.getSnippet());











/*
            for( int i = 0; i <= sharedPreferences.getListObject("proveedores", Proveedor.class).size()-1; i++ )
            {
                String img = sharedPreferences.getListObject("proveedores", Proveedor.class).get(i).getImgUsuario().toString();
                iv.setImageUrl(img, imageLoader);
                //iv.setErrorImageResId(R.drawable.ic_launcher);// en caso de error poner esta imagen.
            }*/



           /* RatingBar rb = (RatingBar) view.findViewById(R.id.infowindow_rating);
            rb.setRating(3.5);*/

            return(popup);
        }
    }

    public void displayAlertDialogEsperaEsteticistas()
    {
        LayoutInflater inflater = MapFragmentUbicarProveedores.this.getActivity().getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_espera_esteticista, null);
        GifImageView gifImageView =(GifImageView) alertLayout.findViewById(R.id.giv_demo);
        try
        {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.moto_espera);
            gifImageView.setImageDrawable(gifDrawable);
        }
        catch (Resources.NotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final TextView editTextDireccionDomicilio = (TextView) alertLayout.findViewById(R.id.textViewEsperaEsteticistas);


        AlertDialog.Builder alert = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
        //alert.setTitle("Direccion Domicilio");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alertDialogEsperaEsteticista = alert.create();

        alertDialogEsperaEsteticista.show();


    }

    private void _webServiceEnviarNotificacionPushATodos( final String serialUsuario )
    {

        _urlWebService = vars.ipServer.concat("/ws/SendPushNotificationForALL");



        Log.e(TAG, "Se escojieron: "+""+sharedPreferences.getString("serviciosEscogidos"));
        Log.e(TAG, "PRUEBA LATITUD " +""+sharedPreferences.getDouble("latitudCliente", 0));
        Log.e(TAG, "PRUEBA LONGITUD " +""+sharedPreferences.getDouble("longitudCliente", 0));


        indicaAndroid = vars.indicaAndroid;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            codigoSolicitud = response.getString("codigoSolicitud");

                            if(status)
                            {
                                displayAlertDialogEsperaEsteticistas();

                              /*  progressDialog = ProgressDialog.show(MapFragmentUbicarProveedores.this.getActivity(),
                                        "SOLICITUD DE SERVICIO.",
                                        "Por favor espere un momento, se está asigando un esteticista a su solicitud de servicio.");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setCancelable(false);

                                Log.e("TOKENPRUEBAMAP", sharedPreferences.getString("TOKEN"));*/


                                //MUESTRO EL AVISO DURANTE 10 SEGUNDOS Y LUEGO LO CIERRO.

                                countDownTimer = new CountDownTimer(TIEMPO_LIMITE, TIEMPO_INICIO)
                                {

                                    @Override
                                    public void onTick(long millisUntilFinished)
                                    {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onFinish()
                                    {
                                        // TODO Auto-generated method stub

                                        alertDialogEsperaEsteticista.dismiss();

                                       //progressDialog.dismiss();

                                        alertDialogBuilder = new AlertDialog.Builder(
                                                getActivity());
                                        // set title
                                        alertDialogBuilder.setTitle("Aviso");
                                        // set dialog message
                                        alertDialogBuilder
                                                .setMessage("En este momento no se encuentran esteticistas disponibles.")
                                                .setCancelable(false)
                                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                                {
                                                    public void onClick(DialogInterface dialog, int id)
                                                    {

                                                        getActivity().stopService(new Intent(getActivity(), ServiceObtenerUbicacionEsteticista.class));
                                                        String indicaMulta = "0";
                                                        _webServiceCancelarSolicitudServicioCliente(codigoSolicitud,"0");
                                                        Log.i("MAP FRAGMENT","codigoSolicitud : "+codigoSolicitud);

                                                        android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                                                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                        fragmentTransaction.replace(R.id.frame_container, new SolicitarServicio());
                                                        fragmentTransaction.commit();
                                                    }
                                                }).setCancelable(false);

                                        // create alert dialog
                                        alertDialog = alertDialogBuilder.create();
                                        // show it
                                        alertDialog.show();


                                        //cancelar servicio

                                    }
                                }.start();





                              /*  AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
                                builder
                                        .setMessage("Solicitud enviada con exito a todos los Esteticistas; en instantes se le asignara su servicio.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                //startActivity(intent);
                                                //finish();
                                            }
                                        }).show();*/
                             /*   Intent intent = new Intent(Login.this, Gestion.class);
                                startActivity(intent);
                                sharedPreferences.putBoolean("GuardarSesion", true);
                                sharedPreferences.putString("email", emailUser);
                                sharedPreferences.putString("clave",claveUser);
                                sharedPreferences.putString("tipoUsuario", tipoUsuario);
                                sharedPreferences.putString("serialUsuario", serialUsuario);
                                finish();*//*
                                //}
                                //}).show();*/


                            }

                            else
                            {
                                if(!status)
                                {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
                                    builder
                                            .setMessage("Error al enviar solicitud: Error: "+message)
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id) {
                                                    //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                    //startActivity(intent);
                                                    //finish();
                                                }
                                            }).show();
                                }
                            }
                        }

                        catch (JSONException e)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
                            builder
                                    .setMessage(e.getMessage().toString())
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id)
                                        {

                                        }
                                    }).show();
                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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
                        }

                        else

                        if (error instanceof AuthFailureError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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
                        }

                        else

                        if (error instanceof ServerError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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
                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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
                        }

                        else

                        if (error instanceof ParseError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MapFragmentUbicarProveedores.this.getActivity());
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
                        }
                    }
                })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("serialUsuario", serialUsuario);
                headers.put("latitudCliente", "" + sharedPreferences.getDouble("latitudCliente", 0));
                headers.put("longitudCliente", "" + sharedPreferences.getDouble("longitudCliente", 0));
                headers.put("servicios", sharedPreferences.getString("serviciosEscogidos"));
                headers.put("direccionDomicilio", sharedPreferences.getString("direccionDomicilio"));
                headers.put("valorTotalServiciosTemporalSolicitarServicio", sharedPreferences.getString("valorTotalServiciosTemporalSolicitarServicio"));
                headers.put("tokenGCM", sharedPreferences.getString("TOKEN"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;

            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");

    }

    public void _webServiceCancelarSolicitudServicioCliente(final String codigoSolicitud, final String indicaMulta)
    {

        _urlWebService = vars.ipServer.concat("/ws/CancelarSolicitudCliente");


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
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

                                sharedPreferences.remove("valorTotalServiciosTemporalSolicitarServicio");
                                sharedPreferences.remove("serviciosEscogidos");
                                sharedPreferences.remove("serviciosEscogidosEnSolicitarServicio");
                                sharedPreferences.remove("proveedores");
                                sharedPreferences.remove("latitudCliente");
                                sharedPreferences.remove("longitudCliente");
                                //sharedPreferences.remove("direccionDomicilio");
                                sharedPreferences.remove("serviciosEscojidosListaServiciosCliente");
                                sharedPreferences.remove("serviciosEscogidosEnListaServiciosCliente");

                                Servicio servicio = new Servicio();
                                servicio = null;


                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder
                                        .setMessage("Error cancelando la solicitud, intente de nuevo")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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


                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                        }

                        else

                        if (error instanceof AuthFailureError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                        }

                        else

                        if (error instanceof ServerError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                        }

                        else

                        if (error instanceof ParseError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                headers.put("indicaMulta", indicaMulta);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;


            }
        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
    }




















}