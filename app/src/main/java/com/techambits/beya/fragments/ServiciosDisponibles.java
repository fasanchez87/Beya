package com.techambits.beya.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techambits.beya.vars.vars;

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
import com.techambits.beya.activities.SolitudServicioDetallada;
import com.techambits.beya.adapters.ServiciosDisponiblesAdapter;
import com.techambits.beya.app.Config;
import com.techambits.beya.beans.Servicio;
import com.techambits.beya.beans.SolicitudServicio;
import com.techambits.beya.decorators.DividerItemDecoration;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiciosDisponibles extends Fragment
{

    private String TAG = ServiciosDisponibles.class.getSimpleName();

    private String _urlWebService;
    private gestionSharedPreferences sharedPreferences;
    private ArrayList<Servicio> serviciosDisponibles;
    private ArrayList<SolicitudServicio> solicitudesServicios;
    private RecyclerView recyclerViewServiciosDisponibles;
    private ServiciosDisponiblesAdapter mAdapter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public vars vars;

    private TextView textViewAvisoSinSolicitudesServicio;

    HashMap <String, ArrayList<Servicio>> hashTableSolicitudDetallada;

    private SwipeRefreshLayout refreshLayout;

    boolean active= false;

    ProgressBar progressBar;

    vars var;


    public ServiciosDisponibles()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        vars = new vars();

        sharedPreferences = new gestionSharedPreferences(this.getActivity());
        serviciosDisponibles = new ArrayList<Servicio>();
        solicitudesServicios = new ArrayList<SolicitudServicio>();

        var = new vars();

        hashTableSolicitudDetallada =  new HashMap <String, ArrayList<Servicio>>();

        mRegistrationBroadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION))
                {
                    textViewAvisoSinSolicitudesServicio.setVisibility(View.GONE);
                    recyclerViewServiciosDisponibles.setVisibility(View.VISIBLE);
                    _webServiceGetSolicitudesServicios();
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_servicios_disponibles, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_update_servicio_onmap:

             if(sharedPreferences.getString("statusOnline").equals("1"))//SI ESTA ONLINE.
             {
                 _webServiceGetSolicitudesServicios();
                 mAdapter.notifyDataSetChanged();
                 Log.d("UPDATES", "LLENAS");
             }
             else
             {
                 Log.d("UPDATES", "UPDATE OFFLINE");
                 progressBar.setVisibility(View.GONE);
                 textViewAvisoSinSolicitudesServicio.setVisibility(View.VISIBLE);
                 recyclerViewServiciosDisponibles.setVisibility(View.GONE);

             }

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause()
    {
        //LocalBroadcastManager.getInstance(ServiciosDisponibles.this.getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(sharedPreferences.getString("statusOnline").equals("1"))
        {
            _webServiceGetSolicitudesServicios();
            mAdapter.notifyDataSetChanged();
        }

        else
        {
            progressBar.setVisibility(View.GONE);
            textViewAvisoSinSolicitudesServicio.setVisibility(View.VISIBLE);
            recyclerViewServiciosDisponibles.setVisibility(View.GONE);
        }

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(ServiciosDisponibles.this.getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_servicios_disponibles, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //ShortcutBadger.removeCount(ServiciosDisponibles.this.getActivity()); //for 1.1.4
        setHasOptionsMenu(true);

        sharedPreferences.remove("countPush");

        progressBar = (ProgressBar) this.getActivity().findViewById(R.id.toolbar_progress_bar);
        textViewAvisoSinSolicitudesServicio = (TextView) this.getActivity().findViewById(R.id.textViewAvisoSinSolicitudesServicio);
        recyclerViewServiciosDisponibles = (RecyclerView) this.getActivity().findViewById(R.id.recycler_view_servicios_disponibles);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewServiciosDisponibles.setLayoutManager(layoutManager);
        mAdapter = new ServiciosDisponiblesAdapter(solicitudesServicios);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerViewServiciosDisponibles.setLayoutManager(mLayoutManager);
        recyclerViewServiciosDisponibles.setItemAnimator(new DefaultItemAnimator());
        recyclerViewServiciosDisponibles.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerViewServiciosDisponibles.setAdapter(mAdapter);

        recyclerViewServiciosDisponibles.addOnItemTouchListener(new RecyclerTouchListener(this.getActivity(),
                recyclerViewServiciosDisponibles, new ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                SolicitudServicio solicitudServicio = solicitudesServicios.get(position);

                Intent intent = new Intent(ServiciosDisponibles.this.getActivity(), SolitudServicioDetallada.class);
                intent.putExtra("codigoSolicitud", solicitudServicio.getCodigoSolicitudServicio());
                intent.putExtra("codigoCliente", solicitudServicio.getCodigoClienteSolicitudServicio());
                intent.putExtra("ubicacionCliente", solicitudServicio.getUbicacionSolicitudServicio());
                intent.putExtra("nombreCliente", solicitudServicio.getNombreUsuario());
                intent.putExtra("fechaSolicitud", solicitudServicio.getFechaSolicitudServicio());
                intent.putExtra("telefonoUsuario", solicitudServicio.getTelefonoClienteSolicitudServicio());
                intent.putExtra("direccionDomicilio", solicitudServicio.getDireccion());
                intent.putExtra("costoSolicitud", solicitudServicio.getCostoSolicitud());
                //intent.putExtra("imgUsuario", var.ipServer.concat(solicitudServicio.getImagenClienteSolicitudServicio()));
                intent.putExtra("imgUsuario", solicitudServicio.getImagenClienteSolicitudServicio());


                sharedPreferences.putBoolean("mostrarMenuDetalleServicio", false);
                sharedPreferences.putBoolean("mostrarMenuRevisarServicios", true);
                sharedPreferences.putBoolean("mostrarMenuAceptarSolocitud", true);

                startActivity(intent);
                getActivity().finish();
                sharedPreferences.putHashMapObjectServicio(hashTableSolicitudDetallada);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //progressBar.setVisibility(View.VISIBLE);
      /*  _webServiceGetSolicitudesServicios();
        mAdapter.notifyDataSetChanged();*/


        // Obtener el refreshLayout
        //refreshLayout = (SwipeRefreshLayout) this.getActivity().findViewById(R.id.swipeRefreshServiciosDisponibles);

       /* refreshLayout.setColorSchemeResources(
                R.color.colorAccent

        );*/

// Iniciar la tarea asíncrona al revelar el indicador
/*        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener()
                {
                    @Override
                    public void onRefresh()
                    {

                       *//* if(solicitudesServicios.isEmpty())
                        {
                            progressBar.setVisibility(View.GONE);
                            textViewAvisoSinSolicitudesServicio.setVisibility(View.VISIBLE);
                            recyclerViewServiciosDisponibles.setVisibility(View.GONE);
                        }*//*

                        solicitudesServicios.clear();
                        _webServiceGetSolicitudesServicios();
                        mAdapter.notifyDataSetChanged();
                    }
                }
        );*/

    }

    public void _webServiceGetSolicitudesServicios()
    {

        _urlWebService = vars.ipServer.concat("/ws/ObtenerSolicitudesDisponibles");
        Log.w("web",""+_urlWebService);



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {
                    Geocoder geocoder;
                    List<Address> addresses;
                    ArrayList<String> fechaSolicitud;
                    ArrayList<String> location;
                    String ubicacionCliente;
                    Float lat;
                    Float lon;
                    Double latitude, longitude;

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {

                            Boolean solicitudesDisponibles = response.getBoolean("status");

                            if (solicitudesDisponibles)
                            {
                                Log.d("UPDATES", "solicitudesDisponibles");


                                solicitudesServicios.clear();

                                JSONArray solicitudes = response.getJSONArray("result");
                                JSONObject object;
                                JSONObject objectServicios;
                                JSONObject objectServiciosSolicitados;
                                JSONArray listadoServicios;

                                for (int i = 0; i <= solicitudes.length()-1; i++)
                                {
                                    object = solicitudes.getJSONObject(i);

                                    SolicitudServicio solicitudServicio = new SolicitudServicio();

                                    solicitudServicio.setCodigoSolicitudServicio(object.getString("codigoSolicitud"));
                                    solicitudServicio.setCodigoClienteSolicitudServicio(object.getString("codigoCliente"));
                                    String fecha = object.getString("fecSolicitudCliente");

                                    fechaSolicitud = new ArrayList<String>(Arrays.asList(fecha.split(" ")));

                                    solicitudServicio.setFechaSolicitudServicio(fechaSolicitud.get(0));
                                    solicitudServicio.setHoraSolicitudServicio(fechaSolicitud.get(1));
                                    solicitudServicio.setNombreUsuario(object.getString("nombreCompleto"));
                                    solicitudServicio.setFechaSolicitudServicio(object.getString("fecSolicitudCliente"));
                                    solicitudServicio.setTelefonoClienteSolicitudServicio(object.getString("telefonoUsuario"));
                                    solicitudServicio.setDireccion(object.getString("direccionDomicilio"));
                                    solicitudServicio.setImagenClienteSolicitudServicio(object.getString("imgUsuario"));

                                    solicitudServicio.setUbicacionSolicitudServicio(object.getString("ubicacionCliente"));
                                    solicitudServicio.setCostoSolicitud(object.getString("costoSolicitud"));
                                    // solicitudServicio.setEstadoSolicitud(object.getString("esAtendida"));

                                    solicitudesServicios.add(solicitudServicio);
                                    listadoServicios = object.getJSONArray("servicios");

                                    serviciosDisponibles = new ArrayList<Servicio>();

                                    for (int j = 0; j <= listadoServicios.length() - 1; j++)
                                    {
                                        objectServiciosSolicitados = listadoServicios.getJSONObject(j);
                                        Servicio servicio = new Servicio();
                                        servicio.setNombreServicio(objectServiciosSolicitados.getString("nombreServicio"));
                                        servicio.setDescripcionServicio(objectServiciosSolicitados.getString("descripcionServicio"));
                                        servicio.setValorServicio(objectServiciosSolicitados.getString("valorServicio"));
                                        serviciosDisponibles.add(servicio);
                                    }

                                    hashTableSolicitudDetallada.put(solicitudesServicios.get(i).getCodigoSolicitudServicio(),
                                            serviciosDisponibles);


                                }
                                progressBar.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();
                                //refreshLayout.setRefreshing(false);

                                textViewAvisoSinSolicitudesServicio.setVisibility(View.GONE);
                                recyclerViewServiciosDisponibles.setVisibility(View.VISIBLE);


                            }

                            else
                            {
                                Log.d("UPDATES", "NOT - solicitudesDisponibles");
                                solicitudesServicios.clear();

                                progressBar.setVisibility(View.GONE);
                                textViewAvisoSinSolicitudesServicio.setVisibility(View.VISIBLE);
                                recyclerViewServiciosDisponibles.setVisibility(View.GONE);

                            }

                        }
                        catch (JSONException e)
                        {


                            progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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

                      /*  catch (IOException e)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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
                            progressBar.setVisibility(View.GONE);


                            e.printStackTrace();
                        }*/


                    }

                },


                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        if (error instanceof TimeoutError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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
                            progressBar.setVisibility(View.GONE);



                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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
                            progressBar.setVisibility(View.GONE);


                        }

                        else

                        if (error instanceof AuthFailureError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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
                            progressBar.setVisibility(View.GONE);




                        }

                        else

                        if (error instanceof ServerError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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
                            progressBar.setVisibility(View.GONE);




                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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
                            progressBar.setVisibility(View.GONE);



                        }

                        else

                        if (error instanceof ParseError)
                        {

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServiciosDisponibles.this.getActivity());
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
                            progressBar.setVisibility(View.GONE);

                        }


                    }


                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("serialUsuario", sharedPreferences.getString("serialUsuario"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }
        };

//        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 6, DefaultRetryPolicy.DEFAULT_MAX_RETRIES));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }


    public interface ClickListener
    {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
    {

        private GestureDetector gestureDetector;
        private ServiciosDisponibles.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ServiciosDisponibles.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }



        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e)
        {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
        {

        }
    }



}
