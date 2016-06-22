package com.techambits.beya.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.techambits.beya.adapters.ServiciosSeleccionadosPush;
import com.techambits.beya.beans.Servicio;
import com.techambits.beya.beans.ValorServicio;
import com.techambits.beya.decorators.DividerItemDecoration;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.techambits.beya.vars.vars;


public class ListaServiciosEsteticista extends AppCompatActivity
{

    private String _urlWebServiceAceptarSolicitudServicio;
    private SwipeRefreshLayout refreshLayout;

    public vars vars;

    private String _urlWebService;

    private String codigoSolicitud;


    ProgressBar progressBar;
    private RecyclerView recyclerView;

    private ArrayList<Servicio> allServicesCliente;

    private gestionSharedPreferences sharedPreferences;

    private ServiciosSeleccionadosPush mAdapter;

    private String keyCodigoSolicitudSeleccionado;

    private TextView valorTotalServiciosListarServiciosEsteticista;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_servicios_esteticista);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sharedPreferences = new gestionSharedPreferences(this);

        vars = new vars();


        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras == null)
            {
                codigoSolicitud = null;
            }
            else
            {
                codigoSolicitud = extras.getString("codigoSolicitud");
            }
        }

        else
        {
            codigoSolicitud = (String) savedInstanceState.getSerializable("codigoSolicitud");
        }



        Intent intent = getIntent();
        keyCodigoSolicitudSeleccionado = intent.getStringExtra("codigoSolicitud");

        Log.i("keyCodigoSolicitudSeleccionado", "" + keyCodigoSolicitudSeleccionado);

        valorTotalServiciosListarServiciosEsteticista = (TextView) findViewById(R.id.valorTotalServiciosListarServiciosEsteticista);

        allServicesCliente = new ArrayList<Servicio>();
        //servicioList = sharedPreferences.getHashMapObjectServicio().get(keyCodigoSolicitudSeleccionado);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_servicios_por_revisar_esteticista);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ServiciosSeleccionadosPush(allServicesCliente);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        //calcularTotales();



        _webServiceObtenerServicioCliente();
        mAdapter.notifyDataSetChanged();

        // Obtener el refreshLayout
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        refreshLayout.setColorSchemeResources(
                R.color.colorAccent

        );

// Iniciar la tarea asíncrona al revelar el indicador
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        allServicesCliente.clear();
                        _webServiceObtenerServicioCliente();
                        mAdapter.notifyDataSetChanged();
                    }
                }
        );





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_listar_servicios_esteticista, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_actualizar_servicios_listado_esteticista:
                allServicesCliente.clear();
                _webServiceObtenerServicioCliente();
                mAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void _webServiceObtenerServicioCliente()
    {

        _urlWebService = vars.ipServer.concat("/ws/ServiciosSolicitud");


        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);



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

                            int valorSolicitud = 0;

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

                                if (servicio.getIndicaSolicitado().equals("1"))
                                {
                                    valorSolicitud = valorSolicitud+Integer.parseInt(object.getString("valorServicio"));
                                    ValorServicio.setValorServicio(valorSolicitud);
                                    //valorTotalServiciosListarServiciosEsteticista.setText("$"+nf.format(valorSolicitud));
                                    valorTotalServiciosListarServiciosEsteticista.setText("$"+nf.format(valorSolicitud));
                                    SolitudServicioDetallada.precioClienteSolicitudServicioDetallada.setText("$"+nf.format(valorSolicitud));
                                    allServicesCliente.add(servicio);
                                }


                            }




                            mAdapter.notifyDataSetChanged();
                            // Parar la animación del indicador
                            refreshLayout.setRefreshing(false);



                        }
                        catch (JSONException e)
                        {

                            // progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosEsteticista.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosEsteticista.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosEsteticista.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosEsteticista.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosEsteticista.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosEsteticista.this.getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(ListaServiciosEsteticista.this.getApplicationContext());
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




}
