package com.techambits.beya.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
import com.techambits.beya.adapters.ServiciosAceptacionAdapter;
import com.techambits.beya.beans.Servicio;
import com.techambits.beya.beans.ValorServicio;
import com.techambits.beya.decorators.DividerItemDecoration;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.vars.vars;
import com.techambits.beya.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ListaServiciosCliente extends AppCompatActivity
{

    private ArrayList<Servicio> allServicesEsteticista;
    private RecyclerView recyclerView;
    private ServiciosAceptacionAdapter mAdapter;

    private TextView textViewAvisoSinServiciosPantallaListarServiciosCliente;

    private String _urlWebService;

    private String codigoSolicitud;
    private String datosCliente;
    private String codigoCliente;

    public vars vars;


    AceptacionServicio aceptacionServicio;

    private gestionSharedPreferences sharedPreferences;

    public static TextView precioTemporalAceptacionServicios;

    public static TextView valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios;
    public static TextView valorTotalServiciosSeleccionados;

    private String serviciosEscojidosListaServiciosCliente;


    public static String serialUsuarioEsteticista;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_servicios_cliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);

        sharedPreferences = new gestionSharedPreferences(this);

        vars = new vars();

        serviciosEscojidosListaServiciosCliente = sharedPreferences.getString("serviciosEscogidosEnSolicitarServicio");//almaceno los datos que escojio
        sharedPreferences.putString("serviciosEscojidosListaServiciosCliente", serviciosEscojidosListaServiciosCliente);


        allServicesEsteticista = new ArrayList<Servicio>();

        aceptacionServicio = new AceptacionServicio();

        recyclerView = (RecyclerView) this.findViewById(R.id.recycler_view_servicios_por_agregar_cliente);

        valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios = (TextView) findViewById(R.id.valorTotalServiciosListarServiciosCliente);
       /* valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.
                setText("" + sharedPreferences.getInt("totalServiciosEscogidosEnSolicitarServicio"));*/

        valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.
                setText("" + AceptacionServicio.precioTemporalAceptacionServicios.getText());


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ServiciosAceptacionAdapter(allServicesEsteticista);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        textViewAvisoSinServiciosPantallaListarServiciosCliente = (TextView) findViewById(R.id.textViewAvisoSinServiciosPantallaListarServiciosCliente);


        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras == null)
            {
                codigoSolicitud = null;
                datosCliente = null;
            }
            else
            {
                codigoSolicitud = extras.getString("codigoSolicitud");
                datosCliente = extras.getString("datosCliente");
            }
        }

        else
        {
            codigoSolicitud = (String) savedInstanceState.getSerializable("codigoSolicitud");
            datosCliente = (String) savedInstanceState.getSerializable("datosCliente");
        }



        try
        {
            JSONObject jsonObject = new JSONObject(datosCliente);


                codigoCliente = jsonObject.getString("codigoCliente");

                Log.i("ListaClientesServicios :: ", "code" + codigoCliente);



        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        _webServiceObtenerServiciosEsteticista();

        mAdapter.notifyDataSetChanged();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_lista_servicios_cliente, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_agregar_servicios_esteticista:

                String data = "";
                List<Servicio> lista_servicios = ((ServiciosAceptacionAdapter) mAdapter).getServiciosList();
                mAdapter.getItemCount();

                for (int i = 0; i < lista_servicios.size(); i++)
                {
                    Servicio servicio = lista_servicios.get(i);

                    if (servicio.isSelected() == true)
                    {
                        data = data+servicio.getId().toString()+",";
                        lista_servicios.remove(servicio);
                        //lista_servicios.notify();
                        //mAdapter.notifyDataSetChanged();

                        if(lista_servicios.isEmpty())
                        {
                            textViewAvisoSinServiciosPantallaListarServiciosCliente.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                    }

                    //mAdapter.notifyDataSetChanged();

                }

                mAdapter.notifyDataSetChanged();


                if(data.isEmpty())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder
                            .setMessage("Debe seleccionar al menos (1) Servicio.")
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
                {
                    //BORRAR ULTIMA COMA Y SEPARARLOS POR DOS PUNTOS ':'
                    String serviciosEscogidos = data.substring(0, data.lastIndexOf(","));
                    sharedPreferences.putString("serviciosEscogidosEnListaServiciosCliente", serviciosEscogidos);

                  /*  Toast.makeText(SolicitarServicio.this.getActivity(),
                            "Selected Services: \n" + serviciosEscogidos+" "+sharedPreferences.getString("valorTotalServicios"), Toast.LENGTH_LONG)
                            .show();*/

                    AceptacionServicio.precioTemporalAceptacionServicios.setText(valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.getText());




                    serviciosEscojidosListaServiciosCliente = sharedPreferences.getString("serviciosEscojidosListaServiciosCliente").
                            concat("," + serviciosEscogidos);
                    sharedPreferences.putString("serviciosEscojidosListaServiciosCliente",serviciosEscojidosListaServiciosCliente);


                    Toast.makeText(this,
                            "serviciosEscojidosListaServiciosCliente: \n" + " " + sharedPreferences.getString("serviciosEscojidosListaServiciosCliente"), Toast.LENGTH_LONG)
                            .show();


                    _webServiceEnviarServiciosAgregadosCliente(serviciosEscogidos);

                }








                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
    }

    public static String getSerialUsuarioEsteticista()
    {
        return serialUsuarioEsteticista;
    }

    public static void setSerialUsuarioEsteticista(String serialUsuarioEsteticista)
    {
        ListaServiciosCliente.serialUsuarioEsteticista = serialUsuarioEsteticista;
    }

    private void _webServiceObtenerServiciosEsteticista()
    {

        _urlWebService = vars.ipServer.concat("/ws/ServiciosSolicitud");



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {

                            boolean status = response.getBoolean("status");
                            String message = response.getString("message");
                            JSONArray servicios = response.getJSONArray("result");
                            JSONObject object;

                            for (int i = 0; i <= servicios.length() - 1; i++)
                            {
                                object = servicios.getJSONObject(i);

                                Servicio servicio = new Servicio();
                                servicio.setImagen(object.getString("imagenServicio"));
                                servicio.setId(object.getString("codigoServicio"));
                                servicio.setNombreServicio(object.getString("nombreServicio"));
                                servicio.setDescripcionServicio(object.getString("descripcionServicio"));
                                servicio.setValorServicio(object.getString("valorServicio"));
                                servicio.setIndicaSolicitado(object.getString("indicaSolicitado"));

                                if (servicio.getIndicaSolicitado().equals("0"))
                                {
                                    allServicesEsteticista.add(servicio);
                                }


                            }

                            if (allServicesEsteticista.isEmpty())
                            {
                                textViewAvisoSinServiciosPantallaListarServiciosCliente.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                             //progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();

                        }
                        catch (JSONException e)
                        {

                            // progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                        //progressBar.setVisibility(View.GONE);
                        //buttonSeleccionarServicios.setVisibility(View.GONE);
                    }


                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("codigoSolicitud", codigoSolicitud);
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        // jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }


    private void _webServiceEnviarServiciosAgregadosCliente( final String serviciosEscogidos )
    {

        _urlWebService = vars.ipServer.concat("/ws/AgregarServiciosClienteOrdenServicio");


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            JSONArray servicios = response.getJSONArray("result");
                            boolean status = response.getBoolean("status");

                            if (status)
                            {
                                Log.i("status", ""+status);
                            }

                            else
                            {
                                Log.i("status", "" + status);

                            }

                          /*
                            if( servicios.length() == 0)
                            {

                                Log.i("NULOS SERVICES", "servicios nulos");
                                recyclerView.setVisibility(View.GONE);
                                recyclerView.clearFocus();

                                return;
                            }

*/

                            //progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();

                        }
                        catch (JSONException e)
                        {

                            // progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosCliente.this.getApplicationContext());
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

                        //progressBar.setVisibility(View.GONE);
                        //buttonSeleccionarServicios.setVisibility(View.GONE);
                    }


                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("codigoSolicitud", codigoSolicitud);
                headers.put("codigoServicios", serviciosEscogidos);
                headers.put("codigoCliente", codigoCliente );
                headers.put("totalValorServicio",  ""+ ValorServicio.getValorServicio());
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

        // jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }

}
