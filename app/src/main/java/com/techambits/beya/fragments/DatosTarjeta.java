package com.techambits.beya.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.techambits.beya.vars.vars;


public class DatosTarjeta extends Fragment
{

    private String _urlWebService;
    gestionSharedPreferences sharedPreferences;
    ProgressBar progressBarConfiguracion;

    public vars vars;



    Button botonGuardarCambios;

    TextInputLayout textInputLayoutNumeroTarjetaCreditoUser;
    TextInputLayout textInputLayoutMesTarjetaCreditoUser;
    TextInputLayout textInputLayoutAñoTarjetaCreditoUser;
    TextInputLayout textInputLayoutCVVTarjetaCreditoUser;

    private String expitarionDate;

    private EditText numeroTarjeta, mesTarjeta, anioTarjeta, cvvTarjeta;

    TextView textViewEstadoCambios;

    private String nameFranquicia;

    public DatosTarjeta()
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
        return inflater.inflate(R.layout.fragment_datos_tarjeta, container, false);


    }

    private void requestFocus(View view)
    {
        if (view.requestFocus())
        {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        textInputLayoutNumeroTarjetaCreditoUser = (TextInputLayout) getActivity().findViewById(R.id.input_layout_numero_tarjeta_configuracion);
        textInputLayoutMesTarjetaCreditoUser = (TextInputLayout) getActivity().findViewById(R.id.input_layout_mes_tarjeta_credito_configuracion);
        textInputLayoutAñoTarjetaCreditoUser = (TextInputLayout) getActivity().findViewById(R.id.input_layout_año_tarjeta_credito_configuracion);
        textInputLayoutCVVTarjetaCreditoUser = (TextInputLayout) getActivity().findViewById(R.id.input_layout_cvv_tarjeta_credito_configuracion);

        numeroTarjeta = (EditText) getActivity().findViewById(R.id.edit_text_numero_tarjeta_configuracion);
        mesTarjeta = (EditText) getActivity().findViewById(R.id.edit_text_mes_tarjeta_credito_configuracion);
        anioTarjeta = (EditText) getActivity().findViewById(R.id.edit_text_año_tarjeta_credito_configuracion);
        cvvTarjeta = (EditText) getActivity().findViewById(R.id.edit_text_cvv_tarjeta_credito_configuracion);

        numeroTarjeta.addTextChangedListener(new RevisorText(numeroTarjeta));
        mesTarjeta.addTextChangedListener(new RevisorText(mesTarjeta));
        anioTarjeta.addTextChangedListener(new RevisorText(anioTarjeta));
        cvvTarjeta.addTextChangedListener(new RevisorText(cvvTarjeta));

        botonGuardarCambios = (Button) getActivity().findViewById(R.id.btn_registro_configuracion);

        progressBarConfiguracion = (ProgressBar) getActivity().findViewById(R.id.progressBarConfiguracion);

        textViewEstadoCambios = (TextView) getActivity().findViewById(R.id.textViewEstadoCambiosDatosTarjeta);

        cvvTarjeta.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    cvvTarjeta.setText(null);
                }
            }

        });

        botonGuardarCambios.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String numero = numeroTarjeta.getText().toString();
                String mes = mesTarjeta.getText().toString();
                String anio = anioTarjeta.getText().toString();
                expitarionDate = anio+"/"+mes;
                String cvv = cvvTarjeta.getText().toString();
                String franquicia = getNameFranquicia();

                String serialUsuario = sharedPreferences.getString("serialUsuario");



                if( validarDatosPago() )
                {
                    _webServiceSaveInfoTarjeta(serialUsuario, numero, expitarionDate, cvv, franquicia);
                }


            }
        });

        _webServiceGetInfoUsuario(sharedPreferences.getString("serialUsuario"));
    }

    public String getNameFranquicia()
    {
        return nameFranquicia;
    }

    public void setNameFranquicia(String nameFranquicia)
    {
        this.nameFranquicia = nameFranquicia;
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
                                numeroTarjeta.setText(object.getString("creditCard").toString());
                                anioTarjeta.setText(""+object.getString("anoTarjeta").toString());
                                mesTarjeta.setText(""+object.getString("mesTarjeta").toString());
                                cvvTarjeta.setText(object.getString("securityCode").toString());
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

    private void _webServiceSaveInfoTarjeta(final String serialUsuario, final String numeroTarjeta,
                                            final String expirationDate, final String cvv, final String nameFranquicia)
    {


        _urlWebService = vars.ipServer.concat("/ws/saveInfoTarjeta");


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
                headers.put("creditCard", numeroTarjeta);
                headers.put("expirationDate", expirationDate);
                headers.put("securityCode", cvv);
                headers.put("tipoTarjeta", nameFranquicia);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }

        };
        // jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

    private boolean validateNumberCreditCard()
    {

        String numberCard = numeroTarjeta.getText().toString();
        textInputLayoutNumeroTarjetaCreditoUser.setHintAnimationEnabled(true);

        if (numeroTarjeta.getText().toString().trim().isEmpty())
        {
            textInputLayoutNumeroTarjetaCreditoUser.setError("Número de tarjeta invalido");
            requestFocus(numeroTarjeta);
            return false;
        }


        else
        {
            textInputLayoutNumeroTarjetaCreditoUser.setErrorEnabled(false);
            textInputLayoutNumeroTarjetaCreditoUser.setError(null);
        }

        return true;
    }

    public boolean validateCardNumber(String cardNumber)
    {
        int sum = 0, digit, addend = 0;
        boolean doubled = false;

        for (int i = cardNumber.length () - 1; i >= 0; i--)
        {
            digit = Integer.parseInt (cardNumber.substring (i, i + 1));
            if (doubled) {
                addend = digit * 2;
                if (addend > 9)
                {
                    addend -= 9;
                }
            }

            else
            {
                addend = digit;
            }
            sum += addend;
            doubled = !doubled;
        }
        return (sum % 10) == 0;
    }

    private boolean validateCardAlgorithmLuhn()
    {

        String numberCard = numeroTarjeta.getText().toString().replaceAll("\\s+", "");
        Log.w("Error Tarjeta: ", numberCard);

        if (!validateCardNumber(numberCard) )
        {
            textInputLayoutNumeroTarjetaCreditoUser.setHintAnimationEnabled(true);
            textInputLayoutNumeroTarjetaCreditoUser.setError("Número de tarjeta invalido");
            requestFocus(numeroTarjeta);
            return false;
        }


        else
        {
            textInputLayoutNumeroTarjetaCreditoUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAñoCreditCard()
    {

        String numberCard = anioTarjeta.getText().toString();

        if (anioTarjeta.getText().toString().trim().isEmpty())
        {
            textInputLayoutAñoTarjetaCreditoUser.setError("Año no valido");
            requestFocus(anioTarjeta);
            return false;
        }

        else

        if (anioTarjeta.getText().toString().trim().length() < 4)
        {
            textInputLayoutAñoTarjetaCreditoUser.setError("Año no valido");
            requestFocus(anioTarjeta);
            return false;
        }

        else
        {
            textInputLayoutAñoTarjetaCreditoUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMesCreditCard()
    {

        String mesCard = mesTarjeta.getText().toString();


        if ((mesTarjeta.getText().toString().trim().isEmpty()) ||
                (Integer.parseInt(mesCard.toString()) >= 13 )  )
        {
            textInputLayoutMesTarjetaCreditoUser.setError("Mes no valido");
            requestFocus(mesTarjeta);
            return false;
        }

        else

        if (mesTarjeta.getText().toString().trim().length() < 2)
        {
            textInputLayoutMesTarjetaCreditoUser.setError("Mes no valido");
            requestFocus(mesTarjeta);
            return false;
        }

        else
        {
            textInputLayoutMesTarjetaCreditoUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validarDatosPago()
    {
        if (!validateNumberCreditCard())
        {
            return false;
        }

        if (!validateCardAlgorithmLuhn())
        {
            return false;
        }

        if (!validateMesCreditCard()) {
            return false;
        }

        if (!validateAñoCreditCard())
        {
            return false;
        }

        if (!validateCVVCreditCard())
        {
            return false;
        }

        if (!validateCVVFranquiciaCreditCard())
        {
            return false;
        }

        return true;
    }

    private boolean validateCVVFranquiciaCreditCard()
    {

        String cvvCard = cvvTarjeta.getText().toString();

        nameFranquicia = getNameFranquicia();

        if(nameFranquicia.equals("amex"))
        {
            nameFranquicia="";

            if (cvvTarjeta.getText().toString().trim().length() < 4)
            {
                textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
                requestFocus(cvvTarjeta);
                return false;
            }

            else
            {
                textInputLayoutNumeroTarjetaCreditoUser.setErrorEnabled(false);
            }
        }

        else

        if(nameFranquicia.equals("visa"))
        {

            if (cvvTarjeta.getText().toString().trim().length() < 3)
            {
                textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
                requestFocus(cvvTarjeta);
                return false;
            }
        }

        else

        if(nameFranquicia.equals("master"))
        {
            nameFranquicia="";

            if (cvvTarjeta.getText().toString().trim().length() < 3)
            {
                textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
                requestFocus(cvvTarjeta);
                return false;
            }

            else
            {
                textInputLayoutNumeroTarjetaCreditoUser.setErrorEnabled(false);
            }
        }

        else

        if(nameFranquicia.equals("generica"))
        {
            if (cvvTarjeta.getText().toString().trim().length() < 3)
            {
                textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
                requestFocus(cvvTarjeta);
                return false;
            }
        }

        else
        {
            textInputLayoutCVVTarjetaCreditoUser.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCVVCreditCard()
    {

        String cvvCard = cvvTarjeta.getText().toString();

        if (cvvTarjeta.getText().toString().trim().isEmpty())
        {
            textInputLayoutCVVTarjetaCreditoUser.setError("Código de seguridad invalido");
            requestFocus(cvvTarjeta);
            return false;
        }

        else
        {
            textInputLayoutCVVTarjetaCreditoUser.setErrorEnabled(false);
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
                case R.id.edit_text_numero_tarjeta_configuracion:
                    validateNumberCreditCard();
                    break;

                case R.id.edit_text_mes_tarjeta_credito_configuracion:
                    validateMesCreditCard();
                    break;

                case R.id.edit_text_año_tarjeta_credito_configuracion:
                    validateAñoCreditCard();
                    break;

                case R.id.edit_text_cvv_tarjeta_credito_configuracion:
                    validateCVVCreditCard();
                    break;
            }

            // Remove spacing char
            if (editable.length() > 0 && (editable.length() % 5) == 0)
            {
                final char c = editable.charAt(editable.length() - 1);
                if (space == c)
                {
                    editable.delete(editable.length() - 1, editable.length());
                }
            }
            // Insert char where needed.
            if (editable.length() > 0 && (editable.length() % 5) == 0)
            {
                char c = editable.charAt(editable.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(editable.toString(), String.valueOf(space)).length <= 3)
                {
                    editable.insert(editable.length() - 1, String.valueOf(space));
                }
            }

            //Validar tipos de tarjeta de credito.

            //American Express:

            if(numeroTarjeta.getText().toString().length() > 1 &&
                    (numeroTarjeta.getText().toString().substring(0, 2).equals("34") ||
                            numeroTarjeta.getText().toString().substring(0, 2).equals("37"))
                    )
            {

                numeroTarjeta.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.icon_amex, 0, 0, 0);
                numeroTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(18)});
                cvvTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                //setNameFranquicia("");
                setNameFranquicia("AMEX");
            }

            else

                //Visa

                if(numeroTarjeta.getText().toString().length() > 0 &&
                        (numeroTarjeta.getText().toString().substring(0, 1).equals("4")))
                {

                    numeroTarjeta.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.logo_visa, 0, 0, 0);
                    numeroTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                    cvvTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                    //setNameFranquicia("");
                    setNameFranquicia("VISA");

                }


                //MasterCard

                else

                if(numeroTarjeta.getText().toString().length() > 1 &&
                        (numeroTarjeta.getText().toString().substring(0, 2).equals("51") ||
                                numeroTarjeta.getText().toString().substring(0, 2).equals("52") ||
                                numeroTarjeta.getText().toString().substring(0, 2).equals("53") ||
                                numeroTarjeta.getText().toString().substring(0, 2).equals("54") ||
                                numeroTarjeta.getText().toString().substring(0, 2).equals("55")
                        ))
                {

                    numeroTarjeta.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.logo_master, 0, 0, 0);
                    numeroTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                    cvvTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                    // setNameFranquicia("");
                    setNameFranquicia("MASTERCARD");

                }

            else

            if(numeroTarjeta.getText().toString().length() > 1 &&
                    (numeroTarjeta.getText().toString().substring(0, 2).equals("300") ||
                     numeroTarjeta.getText().toString().substring(0, 2).equals("305") ||
                     numeroTarjeta.getText().toString().substring(0, 2).equals("309") ||
                     numeroTarjeta.getText().toString().substring(0, 2).equals("36") ||
                     numeroTarjeta.getText().toString().substring(0, 2).equals("38") ||
                     numeroTarjeta.getText().toString().substring(0, 2).equals("39")
                    ))
            {
                numeroTarjeta.setCompoundDrawablesWithIntrinsicBounds( R.mipmap.ic_diners_club, 0, 0, 0);
                numeroTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(17)});
                cvvTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                // setNameFranquicia("");
                setNameFranquicia("DINERS");

            }

            else
            {

                numeroTarjeta.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.logo_tarjeta_x, 0, 0, 0);
               // textInputLayoutNumeroTarjetaCreditoUser.setError("Franquicia no admitida");
                requestFocus(numeroTarjeta);

             /* *//**//*


                numeroTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
                cvvTarjeta.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                //setNameFranquicia("");
                setNameFranquicia("GENERICA");*//**//**/

            }

        }
    }



}
