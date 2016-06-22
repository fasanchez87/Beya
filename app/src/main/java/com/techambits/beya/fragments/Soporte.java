package com.techambits.beya.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.techambits.beya.R;
import com.techambits.beya.activities.Gestion;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.volley.ControllerSingleton;

import com.techambits.beya.vars.vars;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Soporte extends Fragment
{

    TextView asuntoSoporte, mensajeSoporte;
    Button botonEnviarSoporte;
    gestionSharedPreferences sharedPreferences;
    ProgressBar progressBarSoporte;

    public vars vars;



    private String _urlWebService;

    public Soporte()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sharedPreferences = new gestionSharedPreferences(getActivity());

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        vars = new vars();




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_soporte, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        asuntoSoporte = (TextView) getActivity().findViewById(R.id.input_email_soporte);
        mensajeSoporte = (TextView) getActivity().findViewById(R.id.input_mensaje_soporte);
        botonEnviarSoporte = (Button) getActivity().findViewById(R.id.boton_enviar_soporte);
        progressBarSoporte = (ProgressBar) getActivity().findViewById(R.id.progressBarSoporte);

        Log.i("email : ", sharedPreferences.getString("emailUser"));

        botonEnviarSoporte.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String asunto = asuntoSoporte.getText().toString();
                String mensaje = mensajeSoporte.getText().toString();
                sharedPreferences.getString("emailUser");
                _webServiceSoporte(asunto,mensaje,sharedPreferences.getString("emailUser"));
            }
        });
    }

    private void _webServiceSoporte(final String asunto, final String mensaje, final String email)
    {

        _urlWebService = vars.ipServer.concat("/ws/MensajeSoporte");



        progressBarSoporte.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
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
                                progressBarSoporte.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder
                                        .setMessage(message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                Intent intent = new Intent(getActivity(),Gestion.class);
                                                startActivity(intent);
                                            }
                                        }).show();
                            }
                        }

                        catch (JSONException e)
                        {
                            progressBarSoporte.setVisibility(View.GONE);
                            progressBarSoporte.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                        progressBarSoporte.setVisibility(View.GONE);
                    }
                })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("desAsunto", asunto);
                headers.put("desMensaje", mensaje);
                headers.put("emailUsuario", email);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

    }