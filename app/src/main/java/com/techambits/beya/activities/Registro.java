package com.techambits.beya.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

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


/**
 * Created by FABiO on 23/01/2016.
 */
public class Registro extends AppCompatActivity
{

    TextInputLayout textInputLayoutNameUser;
    TextInputLayout textInputLayoutApellidoUser;
    TextInputLayout textInputLayoutEmailUser;
    TextInputLayout textInputLayoutConfirmarEmailUser;
    TextInputLayout textInputLayoutClaveUser;
    TextInputLayout textInputLayoutDocumentoUser;

    TextInputLayout textInputLayoutTelefonoUser;
    TextInputLayout textInputLayoutDireccionUser;
    TextInputLayout textInputLayoutCiudadUser;
    TextInputLayout textInputLayoutDepartamentoUser;

    public vars vars;

    private boolean emailDisponible;

    private String _urlWebService;

    public EditText EditTextNameUser;
    EditText EditTextApellidoUser;
    EditText EditTextEmailUser;
    EditText EditTextConfirmarEmailUser;
    EditText EditTextClaveUser;
    EditText EditTextDocumentoUser;

    EditText EditTextTelefonoUser;
    EditText EditTextDireccionUser;
    EditText EditTextCiudadUser;
    EditText EditTextDepartamentoUser;

    Button botonRegistroUsuario;

    private String nameFranquicia;

    private gestionSharedPreferences sharedPreferences;

    public String nombreUsuario;
    public String apellidoUsuario;
    public String emailUsuario;
    public String documentoUsuario;
    public String claveUsuario;

    public String telefonoUsuario;
    public String direccionUsuario;
    public String ciudadUsuario;
    public String departamentoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        vars = new vars();


        sharedPreferences = new gestionSharedPreferences(getApplicationContext());

        nameFranquicia = "";

        textInputLayoutNameUser = (TextInputLayout) findViewById(R.id.input_layout_nombre_registro);
        textInputLayoutApellidoUser = (TextInputLayout) findViewById(R.id.input_layout_apellido_registro);
        textInputLayoutEmailUser = (TextInputLayout) findViewById(R.id.input_layout_email_registro);

        textInputLayoutConfirmarEmailUser = (TextInputLayout) findViewById(R.id.input_layout_confirmar_email_registro);//

        textInputLayoutClaveUser = (TextInputLayout) findViewById(R.id.input_layout_clave_registro);
        textInputLayoutDocumentoUser = (TextInputLayout) findViewById(R.id.input_layout_documento_registro);

        EditTextNameUser = (EditText) findViewById(R.id.edit_text_nombre_registro);
        EditTextApellidoUser = (EditText) findViewById(R.id.edit_text_apellido_registro);
        EditTextEmailUser = (EditText) findViewById(R.id.edit_text_email_registro);

        EditTextConfirmarEmailUser = (EditText) findViewById(R.id.edit_text_confirmar_email_registro);//

        EditTextClaveUser = (EditText) findViewById(R.id.edit_text_clave_registro);
        EditTextDocumentoUser = (EditText) findViewById(R.id.edit_text_documento_registro);

        //INFORMACION ADICIONAL
        textInputLayoutTelefonoUser = (TextInputLayout) findViewById(R.id.input_layout_telefono_registro);
        textInputLayoutDireccionUser = (TextInputLayout) findViewById(R.id.input_layout_direccion_registro);
        textInputLayoutCiudadUser = (TextInputLayout) findViewById(R.id.input_layout_ciudad_registro);
        textInputLayoutDepartamentoUser = (TextInputLayout) findViewById(R.id.input_layout_departamento_registro);

        EditTextTelefonoUser = (EditText) findViewById(R.id.edit_text_telefono_registro);
        EditTextDireccionUser = (EditText) findViewById(R.id.edit_text_direccion_registro);
        EditTextCiudadUser = (EditText) findViewById(R.id.edit_text_ciudad_registro);
        EditTextDepartamentoUser = (EditText) findViewById(R.id.edit_text_departamento_registro);


        botonRegistroUsuario = (Button) findViewById(R.id.btn_siguiente);
        botonRegistroUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _webServiceValidarEmail(EditTextEmailUser.getText().toString());

            }
        });

        EditTextNameUser.setText(sharedPreferences.getString("nombresUsuario"));
        EditTextApellidoUser.setText(sharedPreferences.getString("apellidosUsuario"));
        EditTextEmailUser.setText(sharedPreferences.getString("emailUsuario"));
        EditTextDocumentoUser.setText(sharedPreferences.getString("documentoUsuario"));
        EditTextTelefonoUser.setText(sharedPreferences.getString("telefonoUsuario"));
        EditTextDireccionUser.setText(sharedPreferences.getString("direccionUsuario"));
        EditTextCiudadUser.setText(sharedPreferences.getString("ciudadUsuario"));
        EditTextDepartamentoUser.setText(sharedPreferences.getString("departamentoUsuario"));
        EditTextClaveUser.setText(sharedPreferences.getString("claveUsuario"));


        EditTextNameUser.addTextChangedListener(new RevisorText(EditTextNameUser));
        EditTextApellidoUser.addTextChangedListener(new RevisorText(EditTextApellidoUser));
        EditTextEmailUser.addTextChangedListener(new RevisorText(EditTextEmailUser));

        EditTextConfirmarEmailUser.addTextChangedListener(new RevisorText(EditTextConfirmarEmailUser));

        EditTextDocumentoUser.addTextChangedListener(new RevisorText(EditTextDocumentoUser));
        EditTextTelefonoUser.addTextChangedListener(new RevisorText(EditTextTelefonoUser));
        EditTextDireccionUser.addTextChangedListener(new RevisorText(EditTextDireccionUser));
        EditTextCiudadUser.addTextChangedListener(new RevisorText(EditTextCiudadUser));
        EditTextDepartamentoUser.addTextChangedListener(new RevisorText(EditTextDepartamentoUser));
        EditTextClaveUser.addTextChangedListener(new RevisorText(EditTextClaveUser));




        //EditTextNumeroTarjetaCreditoUser.setText("377813200654045");




    }

    public boolean isEmailDisponible() {
        return emailDisponible;
    }

    public void setEmailDisponible(boolean emailDisponible) {
        this.emailDisponible = emailDisponible;
    }

    private boolean validateConfirmarEmail()
    {
        String email = EditTextEmailUser.getText().toString().trim();
        String email_nuevo = EditTextConfirmarEmailUser.getText().toString();

        if (email_nuevo.trim().isEmpty())
        {
            EditTextConfirmarEmailUser.setError("Por favor, confirme su email");//cambiar a edittext en register!!
            requestFocus(EditTextConfirmarEmailUser);
            return false;
        }

        else

        if(!email_nuevo.equals(email))
        {
            EditTextConfirmarEmailUser.setError("Los email no coinciden");//cambiar a edittext en register!!
            requestFocus(EditTextConfirmarEmailUser);
            return false;
        }

        else
        {

            textInputLayoutConfirmarEmailUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean ValidarFormulario()
    {
        if (!validateName())
        {
            return false;
        }

        if (!validateApellido())
        {
            return false;
        }

        if (!validateEmail())
        {
            return false;
        }

        if (!validateConfirmarEmail())
        {
            return false;
        }

        if (!validateDocumento()) {
            return false;
        }

        if (!validateTelefono()) {
            return false;
        }


        if (!validateDireccion())
        {
            return false;
        }

        if (!validateCiudad()) {
            return false;
        }

        if (!validateDepartamento())
        {
            return false;
        }

        if (!validateClave())
        {
            return false;
        }



        return true;

    }

    private String getDireccionIP()
    {
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public void intentToActivityPago()
    {
        if(ValidarFormulario())
        {

            nombreUsuario = EditTextNameUser.getText().toString();
            apellidoUsuario = EditTextApellidoUser.getText().toString();
            emailUsuario = EditTextEmailUser.getText().toString();
            documentoUsuario = EditTextDocumentoUser.getText().toString();
            claveUsuario = EditTextClaveUser.getText().toString();

            telefonoUsuario = EditTextTelefonoUser.getText().toString();
            direccionUsuario = EditTextDireccionUser.getText().toString();
            ciudadUsuario = EditTextCiudadUser.getText().toString();
            departamentoUsuario = EditTextDepartamentoUser.getText().toString();

            sharedPreferences.clear();

            sharedPreferences.putString("nombresUsuario", nombreUsuario);
            sharedPreferences.putString("apellidosUsuario", apellidoUsuario);
            sharedPreferences.putString("emailUsuario", emailUsuario);
            sharedPreferences.putString("documentoUsuario", documentoUsuario);
            sharedPreferences.putString("claveUsuario",claveUsuario);
            sharedPreferences.putString("direccionIp", getDireccionIP());
            sharedPreferences.putString("sistemaOperativo", "Android");
            sharedPreferences.putString("tipoUsuario", "C");

            sharedPreferences.putString("telefonoUsuario", telefonoUsuario);
            sharedPreferences.putString("direccionUsuario", direccionUsuario);
            sharedPreferences.putString("ciudadUsuario", ciudadUsuario);
            sharedPreferences.putString("departamentoUsuario", departamentoUsuario);

            Intent intent = new Intent(Registro.this, Pago.class);
            startActivity(intent);
            //overridePendingTransition(R.anim.right_in, R.anim.right_in);
            finish();

        }
    }


    public String getNameFranquicia()
    {
        return nameFranquicia;
    }

    public void setNameFranquicia(String nameFranquicia)
    {
        this.nameFranquicia = nameFranquicia;
    }

    private boolean validateName()
    {



        if (EditTextNameUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutNameUser.setError(getString(R.string.err_msg_name));
            requestFocus(EditTextNameUser);
            return false;
        }

        else
        {
            textInputLayoutNameUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateApellido()
    {
        if (EditTextApellidoUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutApellidoUser.setError(getString(R.string.err_msg_apellido));
            requestFocus(EditTextApellidoUser);
            return false;
        }

        else
        {
            textInputLayoutApellidoUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail()
    {
        String email = EditTextEmailUser.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email))
        {
            textInputLayoutEmailUser.setError(getString(R.string.err_msg_email));//cambiar a edittext en register!!
            requestFocus(EditTextEmailUser);
            return false;
        }

        else
        {
            textInputLayoutEmailUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateClave()
    {
        if (EditTextClaveUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutClaveUser.setError(getString(R.string.err_msg_password));
            requestFocus(EditTextClaveUser);
            return false;
        }

        else

        if(EditTextClaveUser.getText().toString().trim().length() < 5)
        {
            textInputLayoutClaveUser.setError(getString(R.string.err_msg_password_length));
            requestFocus(EditTextClaveUser);
            return false;
        }

        else
        {
            textInputLayoutClaveUser.setErrorEnabled(false);
        }

        return true;
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
    }

    private boolean validateDocumento()
    {
        if (EditTextDocumentoUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutDocumentoUser.setError("Por favor, dígite numero de documento");
            requestFocus(EditTextDocumentoUser);
            return false;
        }

        else

        if(EditTextDocumentoUser.getText().toString().trim().length() < 5)
        {
            textInputLayoutDocumentoUser.setError("El documento debe tener al menos 5 digitos");
            requestFocus(EditTextDocumentoUser);
            return false;
        }

        else
        {
            textInputLayoutDocumentoUser.setErrorEnabled(false);
        }

        return true;
    }



    private boolean validateTelefono()
    {
        if (EditTextTelefonoUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutDocumentoUser.setError("Por favor, dígite numero de telefono");
            requestFocus(EditTextTelefonoUser);
            return false;
        }

        else

        if(EditTextTelefonoUser.getText().toString().trim().length() < 5)
        {
            textInputLayoutDocumentoUser.setError("El telefono debe tener al menos 5 digitos");
            requestFocus(EditTextTelefonoUser);
            return false;
        }

        else
        {
            textInputLayoutTelefonoUser.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateDireccion()
    {
        if (EditTextDireccionUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutDireccionUser.setError("Por favor, dígite su dirección");
            requestFocus(EditTextDireccionUser);
            return false;
        }

        else

        if(EditTextDireccionUser.getText().toString().trim().length() < 10)
        {
            textInputLayoutDireccionUser.setError("La dirección tener al menos 10 digitos");
            requestFocus(EditTextDireccionUser);
            return false;
        }

        else
        {
            textInputLayoutDireccionUser.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateCiudad()
    {
        if (EditTextCiudadUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutCiudadUser.setError("Por favor, dígite su ciudad");
            requestFocus(EditTextCiudadUser);
            return false;
        }

        else

        if(EditTextCiudadUser.getText().toString().trim().length() < 3)
        {
            textInputLayoutCiudadUser.setError("La ciudad tener al menos 3 letras");
            requestFocus(EditTextCiudadUser);
            return false;
        }

        else
        {
            textInputLayoutCiudadUser.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateDepartamento()
    {
        if (EditTextDepartamentoUser.getText().toString().trim().isEmpty())
        {
            textInputLayoutDepartamentoUser.setError("Por favor, dígite su ciudad");
            requestFocus(EditTextDepartamentoUser);
            return false;
        }

        else

        if(EditTextDepartamentoUser.getText().toString().trim().length() < 3)
        {
            textInputLayoutDepartamentoUser.setError("La ciudad tener al menos 3 letras");
            requestFocus(EditTextDepartamentoUser);
            return false;
        }

        else
        {
            textInputLayoutDepartamentoUser.setErrorEnabled(false);
        }

        return true;
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
                case R.id.edit_text_nombre_registro:
                    validateName();
                    break;

                case R.id.edit_text_apellido_registro:
                    validateApellido();
                    break;

                case R.id.edit_text_email_registro:
                    validateEmail();
                    break;

                case R.id.edit_text_clave_registro:
                    validateClave();
                    break;

                case R.id.edit_text_telefono_registro:
                    validateTelefono();
                    break;

                case R.id.edit_text_direccion_registro:
                    validateDireccion();
                    break;

                case R.id.edit_text_ciudad_registro:
                    validateCiudad();
                    break;

                case R.id.edit_text_departamento_registro:
                    validateDepartamento();
                    break;

                case R.id.edit_text_documento_registro:
                    validateDocumento();
                    break;

                case R.id.edit_text_confirmar_email_registro:
                    validateConfirmarEmail();
                    break;
            }

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                builder
                        .setMessage("¿Estás seguro de que deseas cancelar? No se guardara tu información")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int id)
                            {
                                sharedPreferences.clear();
                                Intent intent = new Intent(Registro.this, Login.class);
                                startActivity(intent);
                                finish();
                                //overridePendingTransition(R.anim.left_out, R.anim.left_in);
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {

                    }
                }).show();

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
            builder
                    .setMessage("¿Estás seguro de que deseas cancelar? No se guardara tu información")
                    .setPositiveButton("SI", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int id)
                        {
                            sharedPreferences.clear();
                            Intent intent = new Intent(Registro.this, Login.class);
                            startActivity(intent);
                            finish();
                            //overridePendingTransition(R.anim.left_out, R.anim.left_in);
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int id)
                {

                }
            }).show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


    private void _webServiceValidarEmail(final String emailUsuario)
    {


        _urlWebService = vars.ipServer.concat("/ws/ValidarCorreo");


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            Boolean status = response.getBoolean("status");
                            String message =response.getString("message");

                            if(status)
                            {
                                intentToActivityPago();
                                //setEmailDisponible(true);
                            }

                            else

                            if (!status)
                            {

                                AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                                builder
                                        .setMessage(message)
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                EditTextEmailUser.setFocusableInTouchMode(true);
                                                EditTextEmailUser.requestFocus();
                                                EditTextEmailUser.setPaintFlags(EditTextEmailUser.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


                                            }
                                        }).show().setCancelable(false);

                            }



                        }

                        catch (JSONException e)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Registro.this);
                        builder
                                .setMessage(error.toString())
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                        //startActivity(intent);
                                        //finish();
                                    }
                                }).show();





                        if (error instanceof TimeoutError)
                        {

                            builder
                                    .setMessage("Error de conexión, sin respuesta del servidor.")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                            //startActivity(intent);
                                            //finish();
                                        }
                                    }).show();


                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

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
                headers.put("emailUsuario", emailUsuario);
                return headers;
            }

        };

        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }

}
