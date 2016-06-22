package com.techambits.beya.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

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
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import com.techambits.beya.vars.vars;



public class ValidarCodigoSeguridad extends AppCompatActivity
{


    private String _urlWebService;

    public vars vars;


    TextInputLayout textInputLayoutValidarCodigoSeguridad;

    EditText EditTextValidarCodigoSeguridad;

    Button botonSiguienteValidarCodigoSeguridad;

    private String codigoSeguridad;

    private gestionSharedPreferences sharedPreferences;

    ProgressBar progressBar;






    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validar_codigo_seguridad);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vars = new vars();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences = new gestionSharedPreferences(getApplicationContext());

        textInputLayoutValidarCodigoSeguridad = (TextInputLayout) findViewById(R.id.input_layout_codigo_seguridad);

        EditTextValidarCodigoSeguridad = (EditText) findViewById(R.id.edit_text_codigo_seguridad);

        progressBar = (ProgressBar) findViewById(R.id.progressBarValidarCodigoSeguridad);

        botonSiguienteValidarCodigoSeguridad = (Button) findViewById(R.id.btn_siguiente_codigo_seguridad);
        botonSiguienteValidarCodigoSeguridad.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                intentToActivityCambioClave();
            }
        });

        EditTextValidarCodigoSeguridad.addTextChangedListener(new RevisorText(EditTextValidarCodigoSeguridad));


    }

    private boolean ValidarFormulario()
    {

        if (!validateCodigo())
        {
            return false;
        }

        return true;

    }

    public void intentToActivityCambioClave()
    {
        if(ValidarFormulario())
        {
            String codigo = EditTextValidarCodigoSeguridad.getText().toString();
            _webServiceValidateSecurityCode(codigo);


            //Intent intent = new Intent(ValidarEmail.this, ValidarCodigoSeguridad.class);
            //startActivity(intent);
            //overridePendingTransition(R.anim.right_in, R.anim.right_in);
            //finish();

        }
    }

    private void _webServiceValidateSecurityCode(final String codigo)
    {

        _urlWebService = vars.ipServer.concat("/ws/ValidarCodigoRecuperacion");


        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            String status = response.getString("status");
                            String message = response.getString("message");

                            if(status.equals("recovery_code_accept"))
                            {

                                progressBar.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(ValidarCodigoSeguridad.this);
                                builder
                                        .setMessage(message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                Intent intent = new Intent(ValidarCodigoSeguridad.this.getApplicationContext(), CambiarClave.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).show();

                            }

                            else
                            {
                                if(status.equals("recovery_code_denied"))                                {

                                    progressBar.setVisibility(View.GONE);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(ValidarCodigoSeguridad.this);
                                    builder
                                            .setMessage(message)
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id)
                                                {
                                                    EditTextValidarCodigoSeguridad.setText(null);
                                                }
                                            }).show();
                                }

                            }
                        }

                        catch (JSONException e)
                        {

                            progressBar.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ValidarCodigoSeguridad.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ValidarCodigoSeguridad.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ValidarCodigoSeguridad.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ValidarCodigoSeguridad.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(ValidarCodigoSeguridad.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ValidarCodigoSeguridad.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ValidarCodigoSeguridad.this);
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
                headers.put("codigoRecuperacion", codigo);
                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateCodigo()
    {
        String codigo = EditTextValidarCodigoSeguridad.getText().toString().trim();

        if (codigo.trim().isEmpty())
        {
            textInputLayoutValidarCodigoSeguridad.setError("Digite el codigo de seguridad correctamente");//cambiar a edittext en register!!
            requestFocus(EditTextValidarCodigoSeguridad);
            return false;
        }

        else
        {
            textInputLayoutValidarCodigoSeguridad.setErrorEnabled(false);
        }

        return true;
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

            char space = ' ';

            switch (view.getId())
            {
                case R.id.edit_text_codigo_seguridad:
                    validateCodigo();
                    break;

            }

        }
    }

}
