package com.techambits.beya.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.techambits.beya.CircularImageView.CircularNetworkImageView;
import com.techambits.beya.R;
import com.techambits.beya.activities.DetalleServicioHistorial;
import com.techambits.beya.adapters.HistorialSolicitudAdapter;
import com.techambits.beya.beans.HistorialSolicitud;
import com.techambits.beya.decorators.DividerItemDecoration;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.volley.ControllerSingleton;

import com.techambits.beya.vars.vars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Historial extends Fragment
{

    private String _urlWebService;
    gestionSharedPreferences sharedPreferences;
    private ArrayList<HistorialSolicitud> historialSolicitud;
    private RecyclerView recyclerView;
    private HistorialSolicitudAdapter mAdapter;
    //private SwipeRefreshLayout refreshLayout;
    private TextView textViewAvisoSinHistorial;


    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();



    public vars vars;



    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        sharedPreferences = new gestionSharedPreferences(this.getActivity());
        historialSolicitud = new ArrayList<HistorialSolicitud>();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        vars = new vars();




    }

    public Historial()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial, container, false);

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) this.getActivity().findViewById(R.id.recycler_view_historial_servicios);

        progressBar = (ProgressBar) this.getActivity().findViewById(R.id.toolbar_progress_bar);


        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new HistorialSolicitudAdapter(historialSolicitud);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        textViewAvisoSinHistorial = (TextView) getActivity().findViewById(R.id.textViewAvisoSinHistorial);





        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position)
            {
                HistorialSolicitud historial = historialSolicitud.get(position);
               /* Toast.makeText(Historial.this.getActivity().getApplicationContext(), historial.getNombreSolicitud().toString() +
                        " is selected!", Toast.LENGTH_SHORT).show();
*/
                Intent detalleServicioHistorial = new Intent(getActivity(),DetalleServicioHistorial.class);
                detalleServicioHistorial.putExtra("codigoSolicitud", historialSolicitud.get(position).getIdSolicitud());
                startActivity(detalleServicioHistorial);



            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        _webServiceGetHistorialSolicitudesServicios();
        mAdapter.notifyDataSetChanged();

        // Obtener el refreshLayout
       /* refreshLayout = (SwipeRefreshLayout) this.getActivity().findViewById(R.id.swipeRefreshHistorial);
        refreshLayout.setColorSchemeResources(R.color.colorAccent);
        refreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener()
            {
                @Override
                public void onRefresh()
                {
                     historialSolicitud.clear();
                    _webServiceGetHistorialSolicitudesServicios();
                     mAdapter.notifyDataSetChanged();
                }
            }
        );
*/
    }

    public void _webServiceGetHistorialSolicitudesServicios()
    {
        _urlWebService = vars.ipServer.concat("/ws/HistorialServicios");



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {

                            Boolean historialSolicitudDisponible = response.getBoolean("status");

                            if (historialSolicitudDisponible)
                            {
                                if (imageLoader == null)
                                    imageLoader = ControllerSingleton.getInstance().getImageLoader();

                                JSONArray historialSolicitudJSONArray = response.getJSONArray("result");
                                JSONObject object;
                                JSONObject objectServicios;
                                JSONObject objectServiciosSolicitados;
                                JSONArray listadoServicios;

                                for (int i = 0; i <= historialSolicitudJSONArray.length()-1; i++)
                                {
                                    object = historialSolicitudJSONArray.getJSONObject(i);

                                    HistorialSolicitud historialSolicitudServicio = new HistorialSolicitud();

                                    historialSolicitudServicio.setIdSolicitud(object.getString("codigoSolicitud"));
                                    historialSolicitudServicio.setNombreSolicitud("SOLICITUD N°: " +
                                            object.getString("codigoSolicitud"));
                                    historialSolicitudServicio.setFechaSolicitud(object.getString("fecSolicitudCliente"));
                                    historialSolicitudServicio.setEstadoSolicitud(object.getString("nombreEstado"));
                                    historialSolicitudServicio.setValorSolicitud(object.getString("valorSolicitud"));
                                    //historialSolicitudServicio.setValorSolicitud(object.getString("valorSolicitud"));


                                    if(object.getString("imgUsuario").toString() == "null")
                                    {
                                        historialSolicitudServicio.setImagenSolicitud(null);
                                    }

                                    else
                                    {
                                        //if(object.getString("indicaMulta").toString().equals("0"))
                                       // {
                                            historialSolicitudServicio.setImagenSolicitud(object.getString("imgUsuario"));
                                        //}

                                    }

                                    historialSolicitud.add(historialSolicitudServicio);
                                }

                                mAdapter.notifyDataSetChanged();
                                //refreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);

                            }

                            else
                            {

/*                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder
                                        .setMessage("No existen solicitudes de servicio en este momento.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id)
                                            {
                                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                //startActivity(intent);
                                                //finish();
                                                refreshLayout.setRefreshing(false);


                                            }
                                        }).show();
                                */
                                progressBar.setVisibility(View.GONE);
                                textViewAvisoSinHistorial.setVisibility(View.VISIBLE);



                            }

                        }

                        catch (JSONException e)
                        {

                            //refreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder
                                    .setMessage(e.getMessage().toString())
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
                                    {
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

                            //refreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);

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
                            //refreshLayout.setRefreshing(false);
                        }

                        else

                        if (error instanceof NoConnectionError)
                        {

                            //refreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);

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
                            //refreshLayout.setRefreshing(false);
                        }

                        else

                        if (error instanceof AuthFailureError)
                        {

                            //refreshLayout.setRefreshing(false);
                            //progressBar.setVisibility(View.GONE);

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

                            //refreshLayout.setRefreshing(false);
                        }

                        else

                        if (error instanceof ServerError)
                        {

                            //refreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);

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

                            //refreshLayout.setRefreshing(false);
                        }

                        else

                        if (error instanceof NetworkError)
                        {

                            //refreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);

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

                            //refreshLayout.setRefreshing(false);
                        }

                        else

                        if (error instanceof ParseError)
                        {

                            //refreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);

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

                            //refreshLayout.setRefreshing(false);

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
        private Historial.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView,
                                     final Historial.ClickListener clickListener) {
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
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}
