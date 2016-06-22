package com.techambits.beya.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
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
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.volley.ControllerSingleton;

import com.techambits.beya.vars.vars;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DatosUsuario extends Fragment
{

    private String _urlWebService;
    gestionSharedPreferences sharedPreferences;
    ProgressBar progressBarConfiguracion;
    Button botonGuardarCambios;
    EditText nombreUsuario, apellidoUsuario, documentoUsuario, direccionUsuario,
             ciudadUsuario, departamentoUsuario, telefonoUsuario;
    TextView textViewEstadoCambios;

    public vars vars;




    private String direccion, ciudad, departamento, telefono;

    public DatosUsuario()
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
        return inflater.inflate(R.layout.fragment_datos_usuario, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        nombreUsuario = (EditText) getActivity().findViewById(R.id.textViewNombresConfiguracion);
        apellidoUsuario = (EditText) getActivity().findViewById(R.id.textViewApellidosConfiguracion);
        documentoUsuario = (EditText) getActivity().findViewById(R.id.textViewDocumentoConfiguracion);
        direccionUsuario = (EditText) getActivity().findViewById(R.id.textViewdireccion_direccion_configuracion);
        ciudadUsuario = (EditText) getActivity().findViewById(R.id.textViewCiudadConfiguracion);
        departamentoUsuario = (EditText) getActivity().findViewById(R.id.textViewDepartamentoConfiguracion);
        telefonoUsuario = (EditText) getActivity().findViewById(R.id.textViewTelefonoConfiguracion);

        botonGuardarCambios = (Button) getActivity().findViewById(R.id.buttonGuardarCambiosConfiguracion);

        progressBarConfiguracion = (ProgressBar) getActivity().findViewById(R.id.progressBarConfiguracion);

        textViewEstadoCambios = (TextView) getActivity().findViewById(R.id.textViewEstadoCambios);

        botonGuardarCambios.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String nombre = nombreUsuario.getText().toString();
                String apellido = apellidoUsuario.getText().toString();
                String documento = documentoUsuario.getText().toString();
                direccion = direccionUsuario.getText().toString();
                ciudad = ciudadUsuario.getText().toString();
                departamento = departamentoUsuario.getText().toString();
                telefono = telefonoUsuario.getText().toString();
                String serialUsuario = sharedPreferences.getString("serialUsuario");

                _webServiceSaveInfoUsuario(serialUsuario, nombre, apellido, documento, direccion, ciudad, departamento, telefono);

            }
        });


        _webServiceGetInfoUsuario(sharedPreferences.getString("serialUsuario"));
    }


    private void _webServiceGetInfoUsuario(final String serialUsuario)
    {
        _urlWebService = vars.ipServer.concat("/ws/getInfoUsuario");



        progressBarConfiguracion.setVisibility(View.VISIBLE);

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
                            JSONObject object = response.getJSONObject("result");

                            if(status)
                            {
                                nombreUsuario.setText(object.getString("nombresUsuario").toString());
                                apellidoUsuario.setText(object.getString("apellidosUsuario").toString());
                                documentoUsuario.setText(object.getString("documentoUsuario").toString());
                                direccionUsuario.setText(object.getString("direccionUsuario").toString());
                                direccion = object.getString("direccionUsuario").toString();
                                ciudadUsuario.setText(object.getString("ciudadUsuario").toString());
                                ciudad = object.getString("ciudadUsuario").toString();
                                departamentoUsuario.setText(object.getString("dptoUsuario").toString());
                                departamento = object.getString("dptoUsuario").toString();
                                telefonoUsuario.setText(object.getString("telefonoUsuario").toString());
                                telefono = object.getString("telefonoUsuario").toString();
                                progressBarConfiguracion.setVisibility(View.GONE);
                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                        catch (JSONException e)
                        {
                            progressBarConfiguracion.setVisibility(View.GONE);
                            progressBarConfiguracion.setVisibility(View.GONE);

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

                        progressBarConfiguracion.setVisibility(View.GONE);
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
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }

        };
        // jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

    private void _webServiceSaveInfoUsuario(final String serialUsuario, final String nombreUsuario,
                                            final String apellidoUsuario, final String documentoUsuario,
                                            final String direccion, final String ciudad, final String departamento, final String telefono)
    {





        _urlWebService = vars.ipServer.concat("/ws/saveInfoGeneral");



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
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder
                                        .setMessage(message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                textViewEstadoCambios.setVisibility(View.VISIBLE);

                                            }
                                        }).show();
                            }

                            else
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

                        catch (JSONException e)
                        {
                            progressBarConfiguracion.setVisibility(View.GONE);
                            progressBarConfiguracion.setVisibility(View.GONE);

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

                        progressBarConfiguracion.setVisibility(View.GONE);
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
                headers.put("nombresUsuario", nombreUsuario);
                headers.put("apellidosUsuario", apellidoUsuario);
                headers.put("documentoUsuario", documentoUsuario);
                headers.put("direccionUsuario", direccion);
                headers.put("ciudadUsuario", ciudad);
                headers.put("dptoUsuario", departamento);
                headers.put("telefonoUsuario", telefono);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }

        };
        // jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }




}
