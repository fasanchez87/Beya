package com.techambits.beya.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.techambits.beya.gcm.GcmIntentService;
import com.techambits.beya.services.ServiceObtenerUbicacionEsteticista;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.vars.vars;
import com.techambits.beya.volley.ControllerSingleton;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
//import com.testfairy.TestFairy;
//import com.testfairy.TestFairy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by FABiO on 23/01/2016.
 */

public class Login extends AppCompatActivity implements LocationListener,OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{

    TextView textBienvenidoLogin;
    TextView textDescripcionLogin;
    TextView textOlvidoClave;
    TextView textRegistro;
    TextView textViewRegistroLogin;
    TextView textViewProgressBar;

    public vars vars;

    private gestionSharedPreferences sharedPreferences;

    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutClave;

    private Boolean saveLogin;

    EditText emailLogin;
    EditText claveLogin;

    Button botonLogin;

    ProgressBar progressBar;

    private String emailUser,claveUser,_urlWebService;

    private String tokenGCM;
    private String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     *
     *     GoogleApiClient mGoogleApiClient;

     */

    GoogleApiClient mGoogleApiClient;

    private GoogleApiClient client;

    String indicaAndroid = "";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //TestFairy.begin(this, "3ff5c9c8a83c68a375d949773fea322f2b47cef1"); // e.g "0000111122223333444455566667777788889999";


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        getSupportActionBar().setDisplayShowHomeEnabled(false);



        String fontPath = "fonts/Raleway-Medium.ttf";

        vars = new vars();

        indicaAndroid = vars.indicaAndroid;

        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE))
                {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    tokenGCM = intent.getStringExtra("token");
                    sharedPreferences.putString("TOKEN",tokenGCM);

                   // Toast.makeText(getApplicationContext(), "GCM registration token: " + tokenGCM, Toast.LENGTH_LONG).show();

                }

                else if (intent.getAction().equals(Config.SENT_TOKEN_TO_SERVER))
                {
                    // gcm registration id is stored in our server's MySQL

                    //Toast.makeText(getApplicationContext(), "GCM registration token is stored in server!", Toast.LENGTH_LONG).show();

                }

                else if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                {
                    // new push notification is received

                    Log.w("ALERTA", "Push notification is received!");

                    //Toast.makeText(getApplicationContext(), "Push notification is received!", Toast.LENGTH_LONG).show();
                }
            }
        };



        if (checkPlayServices())
        {
            registerGCM();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // text view label

        textOlvidoClave = (TextView) findViewById(R.id.textViewRecordarClaveLogin);
        textRegistro = (TextView) findViewById(R.id.textViewRegistroLogin);
        textViewProgressBar = (TextView) findViewById(R.id.textViewProgressBar);

        textOlvidoClave.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this, ValidarEmail.class);
                startActivity(intent);
                // overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();            }
        });

        textRegistro.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent intent = new Intent(Login.this, Registro.class);
                startActivity(intent);
               // overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();            }
        });

        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        textInputLayoutClave = (TextInputLayout) findViewById(R.id.input_layout_password);

        emailLogin = (EditText) findViewById(R.id.input_email);
        claveLogin = (EditText) findViewById(R.id.input_password);



        botonLogin = (Button) findViewById(R.id.btn_signup);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Login();
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);



        emailLogin.addTextChangedListener(new RevisorText(emailLogin));
        claveLogin.addTextChangedListener(new RevisorText(claveLogin));

        // Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        // Applying font
        //textBienvenidoLogin.setTypeface(tf);
        //textDescripcionLogin.setTypeface(tf);
        //textOlvidoClave.setTypeface(tf);
        //textRegistro.setTypeface(tf);

        //saveLogin = false;

        sharedPreferences = new gestionSharedPreferences(getApplicationContext());
        //sharedPreferences.putBoolean("GuardarSesion",false);
        saveLogin = sharedPreferences.getBoolean("GuardarSesion");

        if (saveLogin == true)
        {

            cargarActivitySharedPrefenrenceToGestion();
            emailLogin.setText(sharedPreferences.getString("email"));
            claveLogin.setText(sharedPreferences.getString("clave"));
            //recordarClave.setChecked(true);
        }


    }



    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported. Google Play Services not installed!");
                Toast.makeText(getApplicationContext(), "This device is not supported. Google Play Services not installed!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    // starting the service to register with GCM
    private void registerGCM()
    {
        Intent intent = new Intent(this, GcmIntentService.class);
        intent.putExtra("key", "register");
        startService(intent);
    }

    public void cargarActivitySharedPrefenrenceToGestion()
    {

        if(sharedPreferences.getBoolean("saveInstanceState"))
        {

            sharedPreferences.putBoolean("isSaveInstanceState",true);

            //startActivity(new Intent(Login.this, AceptacionServicio.class));
            //DEBO INICIAR SERVICIO DE OBTENER UBICACION ESTETICISTA
            startService(new Intent(getBaseContext(), ServiceObtenerUbicacionEsteticista.class));

            Login.this.finish();
        }

        else

        if(sharedPreferences.getBoolean("saveInstanceStateEsteticista"))
        {
            Intent intent = new Intent(Login.this, SolitudServicioDetallada.class);
            startActivity(intent);
            Login.this.finish();
        }

        else
        {
            startActivity(new Intent(Login.this, Gestion.class));
            Login.this.finish();
        }

    }

    private void Login()
    {
        if (!validateEmail())
        {
            return;
        }

        if (!validatePassword())
        {
            return;
        }

        emailUser = emailLogin.getText().toString();
        claveUser = claveLogin.getText().toString();


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

        else
        {
            _webServiceLogin(emailUser, claveUser);

        }


    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public void checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("ACTIVACION GPS")
                        .setMessage("Es necesario que se active el GPS para operar la aplicación con éxito.")
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
            {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

               /* AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("ACTIVACION GPS")
                        .setMessage("Es necesario que se active el GPS para operar la aplicación con éxito.")
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




            }


        }

        else

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)


        {



            //Toast.makeText(this, "permiso true", Toast.LENGTH_LONG).show();
            _webServiceLogin(emailUser, claveUser);



        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                    {

                        if (mGoogleApiClient == null)
                        {
                            buildGoogleApiClient();
                        }

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    private void _webServiceLogin(final String emailUser , final String claveUser)
    {
        _urlWebService = vars.ipServer.concat("/ws/login");

        progressBar.setVisibility(View.VISIBLE);
        textViewProgressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                               String status = response.getString("status");
                               String message = response.getString("message");



                            if(status.equals("ok"))
                                {




                                    String nombre = response.getString("nombresUsuario");
                                    String apellidos = response.getString("apellidosUsuario");
                                    String nombreUsuario = (nombre+" "+apellidos);
                                    String emailUser = response.getString("emailUsuario");
                                    String tipoUsuario = response.getString("tipoUsuario");
                                    String serialUsuario = response.getString("serialUsuario");
                                    String token = response.getString("MyToken");


                                    sharedPreferences.putString("nombreUsuario",nombreUsuario);
                                    sharedPreferences.putString("emailUser",emailUser);
                                    sharedPreferences.putString("MyToken",token);

                                    progressBar.setVisibility(View.GONE);
                                    textViewProgressBar.setVisibility(View.GONE);

                                    //AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                    //builder
                                      //      .setMessage(message)
                                        //    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                          //  {
                                            //    @Override
                                              //  public void onClick(DialogInterface dialog, int id)
                                                //{
                                                    Intent intent = new Intent(Login.this, Gestion.class);
                                                    startActivity(intent);
                                    sharedPreferences.putBoolean("GuardarSesion", true);
                                                    sharedPreferences.putString("email", emailUser);
                                                    sharedPreferences.putString("clave",claveUser);
                                    sharedPreferences.putString("tipoUsuario", tipoUsuario);
                                    sharedPreferences.putString("serialUsuario", serialUsuario);
                                                    finish();

                                    //}
                                            //}).show();


                                }

                                else
                                {
                                    if(status.equals("error"))
                                    {

                                        progressBar.setVisibility(View.GONE);
                                        textViewProgressBar.setVisibility(View.GONE);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                        builder
                                                .setMessage(message)
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

                            progressBar.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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

                        progressBar.setVisibility(View.GONE);
                        textViewProgressBar.setVisibility(View.GONE);

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
                HashMap <String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("user", emailUser);
                headers.put("pass", claveUser);
                headers.put("tokenGCM", tokenGCM);
                headers.put("indicaAndroid", indicaAndroid);
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }


    private boolean validateEmail()
    {
        String email = emailLogin.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email))
        {
            textInputLayoutEmail.setError(getString(R.string.err_msg_email));//cambiar a edittext en register!!
            requestFocus(emailLogin);
            return false;
        }

        else
        {
            textInputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword()
    {
        if (claveLogin.getText().toString().trim().isEmpty())
        {
            textInputLayoutClave.setError(getString(R.string.err_msg_password));
            requestFocus(claveLogin);
            return false;
        }

        else
        {
            textInputLayoutClave.setErrorEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private static boolean isValidEmail(String email)
    {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private class RevisorText implements TextWatcher
    {

        private View view;

        private RevisorText(View view)
        {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
        {
        }

        public void afterTextChanged(Editable editable)
        {
            switch (view.getId())
            {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;

            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {

            finish();
            //overridePendingTransition(R.anim.left_out, R.anim.left_in);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
