
package com.techambits.beya.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.techambits.beya.R;
import com.techambits.beya.app.Config;
import com.techambits.beya.fragments.Configuracion;
import com.techambits.beya.fragments.DatosUsuario;
import com.techambits.beya.fragments.Historial;
import com.techambits.beya.fragments.ServiciosDisponibles;
import com.techambits.beya.fragments.ServiciosEsteticista;
import com.techambits.beya.fragments.SolicitarServicio;
import com.techambits.beya.fragments.Soporte;
import com.techambits.beya.fragments.Terminos;
import com.techambits.beya.services.HeartBeatServiceGCM;
import com.techambits.beya.services.ServiceActualizarUbicacionProveedor;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.vars.vars;
import com.techambits.beya.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;





public class Gestion extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{

    private gestionSharedPreferences sharedPreferences;
    private TextView textViewnameUser;
    private TextView textViewemailUser;
    String name, email, tipoUsuario;
    public SwitchCompat switchActivarLocation;
    public static String _urlWebService;
    public String event;

    public vars vars;


    ProgressBar progressBar;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    LocationManager locationManager;

    private NavigationView navigationView;

    //GUARDAR OPCION SELECTED SWITCH.
    private boolean isCheckedSwitch;


    private String statusOnline = "0";  // 0 is OffLine; 1 is Online.

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        startService(new Intent(getBaseContext(), HeartBeatServiceGCM.class));

        vars = new vars();


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            showGPSDisabledAlertToUser();
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {


                if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                {
                    // new push notification is received

                    Log.w("ALERTA", "Push notification is received!"+intent.getStringExtra("message"));

                    Toast.makeText(getApplicationContext(), "Nueva solicitud de servicio, revisa la opción SERVICIOS DISPONIBLES.", Toast.LENGTH_LONG).show();
                }
            }
        };



        sharedPreferences = new gestionSharedPreferences(getApplicationContext());
        name = sharedPreferences.getString("nombreUsuario");
        email = sharedPreferences.getString("emailUser");
        tipoUsuario = sharedPreferences.getString("tipoUsuario");

        Log.w("TOKEN :: ","."+sharedPreferences.getString("tokenGCM"));
        progressBar = (ProgressBar) this.findViewById(R.id.toolbar_progress_bar);


        Bundle extras = getIntent().getExtras();




        if (extras != null)
        {
            event = extras.getString("type");

            Log.w("Gestion",""+event);


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();

        MenuItem item = menu.findItem(R.id.nav_activar_geolocalizacion);

        //EVENTO DEL SWITCH BUTTON.
        switchActivarLocation = (SwitchCompat) MenuItemCompat.getActionView(item);

        switchActivarLocation.setChecked(sharedPreferences.getBoolean("isCheckedSwitch"));

        switchActivarLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                statusOnline = "";

                if (isChecked)
                {

                    String statusDisponible = "0";//QUEDA DISPONIBLE
                    _webServiceActualizarDisponibilidadEsteticista(statusDisponible);

                    //SERVICIO EN BACKGROUND PARA ACTUALIZAR LA UBICACION DEL PROVEEDOR.
                    isCheckedSwitch = true;
                    sharedPreferences.putBoolean("isCheckedSwitch",isCheckedSwitch );

                    Log.e("CHECKED", "onCheckedChanged" + isChecked);
                    statusOnline = "1";
                    sharedPreferences.putString("statusOnline",statusOnline );

                    if( haveNetworkConnection() )
                    {
                        startService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));


                    }

                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
                        builder
                                .setMessage("Se ha perdido la conexión a internet, revise e intente de nuevo.")
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                        //startActivity(intent);
                                        //finish();
                                        switchActivarLocation.setChecked(false);
                                    }
                                }).show();
                    }




                }

                else
                {

                    String statusDisponible = "1";//QUEDA NO DISPONIBLE
                    _webServiceActualizarDisponibilidadEsteticista(statusDisponible);

                    isCheckedSwitch = false;// PENDIENTE
                    sharedPreferences.putBoolean("isCheckedSwitch",isCheckedSwitch );
                    statusOnline = "0";
                    Log.e("CHECKED", "onCheckedChanged" + isChecked);
                    stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                    sharedPreferences.putString("statusOnline", statusOnline );
                }
            }
        });


        if (!tipoUsuario.equals("E"))
        {
            menu.getItem(1).setVisible(false);
            menu.getItem(0).getSubMenu().getItem(3).setVisible(false);
            menu.getItem(0).getSubMenu().getItem(0).setVisible(false);


        }

        else if (tipoUsuario.equals("E"))
        {
            menu.getItem(0).getSubMenu().getItem(3).setVisible(false);
            menu.getItem(0).getSubMenu().getItem(1).setVisible(false);
            Log.e("LEA :::: ", menu.getItem(0).getTitle().toString());
        }

        textViewnameUser = (TextView) navigationView.getHeaderView(0).findViewById(R.id.NombreUserHeaderNavGestion);
        textViewemailUser = (TextView) navigationView.getHeaderView(0).findViewById(R.id.EmailHeaderNavGestion);
        textViewnameUser.setText(""+name);
        textViewemailUser.setText("" + email);
        navigationView.setNavigationItemSelectedListener(this);

        textViewnameUser = (TextView) findViewById(R.id.NombreUserHeaderNavGestion);
        textViewemailUser = (TextView) findViewById(R.id.EmailHeaderNavGestion);



        if (savedInstanceState == null)
        {

            if (tipoUsuario.equals("E"))
            {

                //MOSTRAMOS POR DEFECTO EL NAV MIS SERVICIOS CUANDO ES ESTETICISTA
                progressBar.setVisibility(View.VISIBLE);

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new ServiciosDisponibles());
                fragmentTransaction.commit();


                //navigationView.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(true);
               //Titulo en Toolbar.
                setTitle(navigationView.getMenu().getItem(1).getSubMenu().getItem(0).getTitle());
                navigationView.setCheckedItem(R.id.nav_activar_geolocalizacion);


            }

            else
            {


                //MOSTRAMOS POR DEFECTO EL NAV SOLICITAR SERVICIOS CUANDO ES CLIENTE
                navigationView.setCheckedItem(R.id.nav_solicitar_servicio);

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, new SolicitarServicio());
                fragmentTransaction.commit();
            }

        }



    }




    private  boolean haveNetworkConnection()
    {
        boolean haveConnectedWifi =  false ;
        boolean haveConnectedMobile =  false ;

        ConnectivityManager cm =  (ConnectivityManager) getSystemService ( Context . CONNECTIVITY_SERVICE );
        NetworkInfo [] netInfo = cm . getAllNetworkInfo ();
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

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Su GPS esta apagado, para que Beya funcione debe encenderlo, ¿desea hacerlo?")
                .setCancelable(false)
                .setPositiveButton("Encender GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });/*);
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });*/
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }



    public String getLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;
        Log.w("UBICACION FALSA RANDOM", "Longitude: " + foundLongitude + "  Latitude: " + foundLatitude);
        return (foundLongitude + ":" + foundLatitude);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }

        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.gestion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        Class fragmentClass;

        switch (item.getItemId())
        {

            case R.id.nav_mis_servicios:
                fragmentClass = ServiciosEsteticista.class;
                break;

            case R.id.nav_solicitar_servicio:
                fragmentClass = SolicitarServicio.class;
                break;

            case R.id.nav_activar_geolocalizacion:
                progressBar.setVisibility(View.VISIBLE);
                fragmentClass = ServiciosDisponibles.class;
                break;

            case R.id.nav_historial_servicios:
                progressBar.setVisibility(View.VISIBLE);
                fragmentClass = Historial.class;
                break;

            case R.id.nav_soporte:
                fragmentClass = Soporte.class;
                break;

            case R.id.nav_terminos_condiciones:
                fragmentClass = Terminos.class;
                break;



            case R.id.nav_configuracion:

                if (tipoUsuario.equals("E"))
                {
                    fragmentClass = DatosUsuario.class;
                }

                else
                {
                    fragmentClass = Configuracion.class;
                }

                break;

            case R.id.nav_cerrar_sesion:

                AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
                builder
                        .setMessage("¿Deseas cerrar sesión? Se eliminaran los datos de ingreso.")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {

                                _webServiceCerrarSesionChangeStateOnLine(sharedPreferences.getString("serialUsuario"));
                                sharedPreferences.putBoolean("GuardarSesion",false);
                                sharedPreferences.clear();
                                stopService(new Intent(getBaseContext(), ServiceActualizarUbicacionProveedor.class));
                                Intent intent = new Intent(Gestion.this, Login.class);
                                startActivity(intent);
                                finish();
                                //overridePendingTransition(R.anim.left_out, R.anim.left_in);
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                        if(tipoUsuario.equals("E"))
                        {
                            setTitle("Servicios Disponibles");

                            navigationView.setCheckedItem(R.id.nav_activar_geolocalizacion);

                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_container, new ServiciosDisponibles());
                            fragmentTransaction.commit();                        }
                        else
                        {
                            setTitle("Solicitar Servicio");
                            navigationView.setCheckedItem(R.id.nav_solicitar_servicio);

                            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_container, new SolicitarServicio());
                            fragmentTransaction.commit();                        }



                    }
                }).setCancelable(false).show();

            default:

            if(tipoUsuario.equals("E"))
            {
                fragmentClass = ServiciosDisponibles.class;
            }
            else
            {
                fragmentClass = SolicitarServicio.class;
            }

        }

        try
        {
            fragment = (Fragment) fragmentClass.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();

        item.setChecked(true);
        setTitle(item.getTitle());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));*/

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives

       /* if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            showGPSDisabledAlertToUser();
        }*/


        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }




    private void _webServiceCerrarSesionChangeStateOnLine(final String serialUsuario)
    {


        _urlWebService = vars.ipServer.concat("/ws/CerrarSesion");


        // progressBar.setVisibility(View.VISIBLE);
       // textViewProgressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            String status = response.getString("status");

                            if(status.equals("cierre_session_success"))
                            {
                                Log.w("cierre_session_success ::", "cierre_session_success");
                            }

                            else
                            {
                                if(status.equals("cierre_session_failed"))
                                {


                                    AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
                                    builder
                                            .setMessage("cierre_session_failed")
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
                        }

                        catch (JSONException e)
                        {


                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion.this);
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

//				    @Override
//		            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError
//		            {
//				    	HashMap<String, String> params = new HashMap<String, String>();
//				    	//params.put("Content-Type", "application/json");
//				    	params.put("email_cliente", "MMM" );
//				    	params.put("pass_cliente", "MMM" );
//				    	params.put("name_cliente", "MMM");
//				    	params.put("ape_cliente", "MMM" );
//
//
//		                return params;
//		            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("serialUsuario", serialUsuario);
                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

    public void _webServiceActualizarDisponibilidadEsteticista(final String statusDisponible)
    {
        String _urlWebServiceAceptarSolicitudServicio = vars.ipServer.concat("/ws/ActualizarDisponibilidadEsteticista");

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
                                Log.w("CERRAR", ""+message);
                            }
                            else
                            {
                                Log.w("CERRAR", ""+message);

                            }
                        }
                        catch (JSONException e)
                        {
                            Log.w("CERRAR", ""+e.getMessage().toString());
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
    }





}
