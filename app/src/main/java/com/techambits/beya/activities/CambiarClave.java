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
import com.techambits.beya.vars.vars;
import com.techambits.beya.volley.ControllerSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CambiarClave extends AppCompatActivity
{

    private String _urlWebService;

    TextInputLayout textInputLayoutNuevaClave;
    TextInputLayout textInputLayoutConfirmarClave;

    EditText EditTextNuevaClave;
    EditText EditTextConfirmarClave;

    Button botonHechoRevisarClave;

    private String NuevaClave;
    private String ConfirmarClave;

    private gestionSharedPreferences sharedPreferences;

    ProgressBar progressBar;

    vars vars;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_clave);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences = new gestionSharedPreferences(getApplicationContext());

        vars = new vars();


        textInputLayoutNuevaClave = (TextInputLayout) findViewById(R.id.input_layout_nueva_clave);
        textInputLayoutConfirmarClave = (TextInputLayout) findViewById(R.id.input_layout_revisar_clave);

        EditTextNuevaClave = (EditText) findViewById(R.id.edit_text_nueva_clave);
        EditTextConfirmarClave = (EditText) findViewById(R.id.edit_text_revisar_clave);

        progressBar = (ProgressBar) findViewById(R.id.progressBarRevisarClave);

        botonHechoRevisarClave = (Button) findViewById(R.id.btn_hecho_revisar_clave);
        botonHechoRevisarClave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                intentToActivityLogin();
            }
        });

        EditTextNuevaClave.addTextChangedListener(new RevisorText(EditTextNuevaClave));
        EditTextConfirmarClave.addTextChangedListener(new RevisorText(EditTextConfirmarClave));

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

    private boolean validateConfirmarClave()
    {
        String confirmar_clave = EditTextConfirmarClave.getText().toString().trim();
        String clave_nueva = EditTextNuevaClave.getText().toString();

        if (confirmar_clave.trim().isEmpty())
        {
            textInputLayoutConfirmarClave.setError("Por favor, confirme su clave");//cambiar a edittext en register!!
            requestFocus(EditTextConfirmarClave);
            botonHechoRevisarClave.setEnabled(false);
            return false;
        }

        else

        if(!clave_nueva.equals(confirmar_clave))
        {
            textInputLayoutConfirmarClave.setError("No coincide con la nueva clave digitada");//cambiar a edittext en register!!
            botonHechoRevisarClave.setEnabled(false);
            requestFocus(EditTextConfirmarClave);
            return false;
        }

        else
        {

            botonHechoRevisarClave.setEnabled(true);
            textInputLayoutConfirmarClave.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateClaveNueva()
    {
        String clave_nueva = EditTextNuevaClave.getText().toString().trim();

        if (clave_nueva.trim().isEmpty())
        {
            textInputLayoutNuevaClave.setError("Por favor, digite su clave.");//cambiar a edittext en register!!
            requestFocus(EditTextNuevaClave);
            return false;
        }

        else

        if(EditTextNuevaClave.getText().toString().trim().length() < 5)
        {
            textInputLayoutNuevaClave.setError("La clave debe tener al menor 5 caracteres.");
            requestFocus(EditTextNuevaClave);
            return false;
        }


        else
        {
            textInputLayoutNuevaClave.setErrorEnabled(false);
        }

        return true;
    }

    private boolean ValidarFormulario()
    {

        if (!validateClaveNueva())
        {
            return false;
        }

        if (!validateConfirmarClave())
        {
            return false;
        }

        return true;

    }

    public void intentToActivityLogin()
    {
        if(ValidarFormulario())
        {
            String clave = EditTextConfirmarClave.getText().toString();
            String email = sharedPreferences.getString("emailUsuario");
            _webServiceUpdateClave(clave, email);


            //Intent intent = new Intent(ValidarEmail.this, ValidarCodigoSeguridad.class);
            //startActivity(intent);
            //overridePendingTransition(R.anim.right_in, R.anim.right_in);
            //finish();

        }
    }

    private void _webServiceUpdateClave(final String clave, final String email)
    {
        _urlWebService = vars.ipServer.concat("/ws/ModificarClaveUsuario");


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

                            if(status.equals("update_password_success"))
                            {

                                progressBar.setVisibility(View.GONE);

                                AlertDialog.Builder builder = new AlertDialog.Builder(CambiarClave.this);
                                builder
                                        .setMessage(message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                Intent intent = new Intent(CambiarClave.this.getApplicationContext(), Login.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).show();

                            }

                            else
                            {
                                if(status.equals("update_password_failed"))                                {

                                    progressBar.setVisibility(View.GONE);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(CambiarClave.this);
                                    builder
                                            .setMessage(message)
                                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialog, int id)
                                                {
                                                    EditTextNuevaClave.setText(null);
                                                    EditTextConfirmarClave.setText(null);

                                                }
                                            }).show();
                                }

                            }
                        }

                        catch (JSONException e)
                        {

                            progressBar.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(CambiarClave.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(CambiarClave.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(CambiarClave.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(CambiarClave.this);
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(CambiarClave.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(CambiarClave.this);
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(CambiarClave.this);
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
                headers.put("claveUsuario", clave);
                headers.put("emailUsuario", email);

                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq,"");

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
                case R.id.edit_text_nueva_clave:
                    validateClaveNueva();
                    break;

                case R.id.edit_text_revisar_clave:
                    validateConfirmarClave();
                    break;

            }

        }
    }

}
