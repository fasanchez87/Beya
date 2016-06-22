package com.techambits.beya.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.techambits.beya.R;
import com.techambits.beya.adapters.ServiciosAdapter;
import com.techambits.beya.beans.Proveedor;
import com.techambits.beya.beans.Servicio;
import com.techambits.beya.decorators.DividerItemDecoration;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.volley.ControllerSingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.techambits.beya.vars.vars;



public class SolicitarServicio extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private String TAG = SolicitarServicio.class.getSimpleName();

    public vars vars;



    public static TextView valorTotalTextView;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String _urlWebService;

    final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ProgressBar progressBar;

    CheckBox checkBoxServicio;

    private gestionSharedPreferences sharedPreferences;

    private String serialUsuario;


    private ArrayList<Servicio> servicioList = new ArrayList<>();
    private ArrayList<Servicio> serviciosSeleccionadosList;
    private ArrayList<Proveedor> provedoresList;
    private LinearLayout linearLayoutPrecioTotal;

    private RecyclerView recyclerView;
    private ServiciosAdapter mAdapter;

   // private Button buttonSeleccionarServicios;

    private Cache cache;

    private boolean foundService;

    public SolicitarServicio()
    {
        // Required empty public constructor

    }





    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        sharedPreferences = new gestionSharedPreferences(this.getActivity());
        provedoresList = new ArrayList<Proveedor>();
        serviciosSeleccionadosList = new ArrayList<Servicio>();
        cache = ControllerSingleton.getInstance().getReqQueue().getCache();

        vars = new vars();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.fragment_solicitar_servicio, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        valorTotalTextView = (TextView) this.getActivity().findViewById(R.id.valorTotalServiciosSeleccionadosSolicitarServicios);


        linearLayoutPrecioTotal = (LinearLayout) getActivity().findViewById(R.id.linearLayoutPrecioTotalSolicitarServicioToMap);


        recyclerView = (RecyclerView) this.getActivity().findViewById(R.id.recycler_view);

        progressBar = (ProgressBar) this.getActivity().findViewById(R.id.toolbar_progress_bar);
        //buttonSeleccionarServicios = (Button) this.getActivity().findViewById(R.id.buttonSeleccionarServicioFragmentSolicitarServicio);

        checkBoxServicio = (CheckBox) this.getActivity().findViewById(R.id.checkBoxServicio);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ServiciosAdapter(servicioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

       /* buttonSeleccionarServicios.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            }
        });*/

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this.getActivity(), recyclerView, new ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Servicio servicio = servicioList.get(position);
                //Toast.makeText(SolicitarServicio.this.getActivity().getApplicationContext(), servicio.getNombreServicio().toString() + " is selected!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLongClick(View view, int position)
            {

            }
        }));

        _webServiceGetServices();
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.solicitar_servicio_menu, menu);
        /*SolicitarServicio.this.getActivity().getMenuInflater().inflate(R.menu.solicitar_servicio_menu, menu);*/
        super.onCreateOptionsMenu(menu,inflater);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_buscar_esteticista_on_map:

                String data = "";
                String serviciosEscogidosParaPush = "";

                sharedPreferences.putString("valorTotalServiciosTemporalSolicitarServicio",valorTotalTextView.getText().toString());

                List<Servicio> lista_servicios = ((ServiciosAdapter) mAdapter).getServiciosList();

                for (int i = 0; i < lista_servicios.size(); i++)
                {
                    Servicio servicio = lista_servicios.get(i);

                    if (servicio.isSelected() == true)
                    {
                        data = data+servicio.getId().toString()+",";

                        Servicio serviciosSeleccionados = new Servicio();

                        serviciosSeleccionados.setImagen( servicio.getImagen());
                        serviciosSeleccionados.setId( servicio.getId() );
                        serviciosSeleccionados.setNombreServicio(servicio.getNombreServicio());
                        serviciosSeleccionados.setDescripcionServicio(servicio.getDescripcionServicio());
                        serviciosSeleccionados.setValorServicio(servicio.getValorServicio().toString());
                        serviciosSeleccionadosList.add(serviciosSeleccionados);

                    }
                }

                if(data.isEmpty())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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
                    sharedPreferences.putString("serviciosEscogidos", serviciosEscogidos);
                    sharedPreferences.putString("serviciosEscogidosEnSolicitarServicio", serviciosEscogidos);

                  /*  Toast.makeText(SolicitarServicio.this.getActivity(),
                            "Selected Services: \n" + serviciosEscogidos+" "+sharedPreferences.getString("valorTotalServicios"), Toast.LENGTH_LONG)
                            .show();*/



                    sharedPreferences.putInt("valorTotalServiciosAdapter", ServiciosAdapter.valorTotal);

                    _webServiceGetProviderServicesOnMAP(serviciosEscogidos);

                }


            default:
                return super.onOptionsItemSelected(item);
        }
    }




    public boolean isFoundService() {
        return foundService;
    }

    public void setFoundService(boolean foundService) {
        this.foundService = foundService;
    }

    private void _webServiceGetProviderServicesOnMAP(final String id_services)
    {

        _urlWebService = vars.ipServer.concat("/ws/ObtenerProveedoresServicios");




        /*progressBar.setVisibility(View.VISIBLE);
        buttonSeleccionarServicios.setVisibility(View.GONE);*/

/*        Cache.Entry entry = cache.get(_urlWebService);
        if(entry != null)
        {
            try
            {
                String data = new String(entry.data, "UTF-8");
                Log.e("Cache",""+data);
                // handle data, like converting it to xml, json, bitmap etc.,
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
        else

        {
            Log.e("Cache","empty");


            // Cached response doesn't exists. Make network call here
        }*/

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(_urlWebService,
                new Response.Listener<JSONArray>()
                {

                    @Override
                    public void onResponse(JSONArray response)
                    {
                        try
                        {
                            JSONObject object;
                            String status="";


                            //VERIFICAR SI EXISTEN PROVEEDORES DEL SERVICIO REST.
                            if(response.length() == 0)
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
                                builder
                                        .setMessage("NO SE ENCONTRARON PROVEEDORES DE LOS SERVICIOS SELECCIONADOS.")
                                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                //Intent intent = new Intent(Pago.this.getApplicationContext(), Registro.class);
                                                //startActivity(intent);
                                                //finish();
                                            }
                                        }).show();

                                return;
                            }

                            else

                            {
                                for (int i = 0; i <= response.length() - 1; i++)
                                {
                                    object = response.getJSONObject(i);

                                    Proveedor proveedor = new Proveedor();
                                    proveedor.setNombreProveedor(object.getString("nombresUsuario"));
                                    proveedor.setApellidoProveedor(object.getString("apellidosUsuario"));
                                    proveedor.setEmailProveedor(object.getString("emailUsuario"));
                                    //proveedor.setPromedioServicios(object.getString("promedioServicios"));
                                    proveedor.setLatitudUsuario(object.getString("latitudUsuario"));
                                    proveedor.setLongitudUsuario(object.getString("longitudUsuario"));
                                    proveedor.setImgUsuario(object.getString("imgUsuario"));
                                    provedoresList.add(proveedor);




                                }

                                if (!provedoresList.isEmpty())
                                {
                                    Fragment fragment;
                                    fragment = new MapFragmentUbicarProveedores();

                                    //MOSTRAMOS EL FRAGMENT DEL MAPA REEMPLANZANDO EL CONTENIDO DEL FRAGMENT MANAGER AQUI.
                                    android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_container, fragment);
                                    fragmentTransaction.commit();

                                   /* sharedPreferences.putInt("totalServiciosEscogidosEnSolicitarServicio",
                                            Integer.parseInt(valorTotalTextView.getText().toString()));*/

                                    ServiciosAdapter.valorTotal=0;


                                }

                                //SHAREDPREFERENCES OBJETO PROVEEDOR.
                                sharedPreferences.putListObject("proveedores", provedoresList);

                                for (int i = 0; i <= provedoresList.size() - 1; i++)
                                {
                                    Log.w(TAG,"Proveedor"+"" + provedoresList.get(i).getNombreProveedor());
                                    Log.w(TAG,"Proveedor"+ provedoresList.get(i).getEmailProveedor().toString());
                                }

                                //progressBar.setVisibility(View.GONE);
                                //buttonSeleccionarServicios.setVisibility(View.VISIBLE);
                                //mAdapter.notifyDataSetChanged();
                            }

                        }
                        catch (JSONException e)
                        {

                            //progressBar.setVisibility(View.GONE);
                            //buttonSeleccionarServicios.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity().getApplicationContext());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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
                        //buttonSeleccionarServicios.setVisibility(View.GONE);
                    }


                })
        {

//                  GESTION DE PARAMETROS POR VIA GET.
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
                HashMap <String, String> headers = new HashMap <String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                headers.put("servicios", id_services);
                headers.put("serialUsuario", sharedPreferences.getString("serialUsuario"));
                headers.put("MyToken", sharedPreferences.getString("MyToken"));
                return headers;
            }

        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ControllerSingleton.getInstance().addToReqQueue(jsonObjReq, "");

    }


    private void _webServiceGetServices()
    {
        _urlWebService = vars.ipServer.concat("/ws/ObtenerServicios");


        progressBar.setVisibility(View.VISIBLE);
//        buttonSeleccionarServicios.setVisibility(View.GONE);


        Cache.Entry entry = cache.get(_urlWebService);
        if(entry != null)
        {
            try
            {
                String data = new String(entry.data, "UTF-8");
                Log.e("Cache",""+data);
                // handle data, like converting it to xml, json, bitmap etc.,
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
        }
        else

        {
            Log.e("Cache","empty");


            // Cached response doesn't exists. Make network call here
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, _urlWebService, null,
                new Response.Listener<JSONObject>()
                {

                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            String message = response.getString("message");
                            Log.w("Mensaje : ", message);
                            JSONArray servicios = response.getJSONArray("result");
                            JSONObject object;


                            for (int i = 0; i <= servicios.length()-1; i++)
                            {
                                object = servicios.getJSONObject(i);

                                Servicio servicio = new Servicio();
                                servicio.setImagen(object.getString("imagenServicio"));
                                servicio.setId(object.getString("codigoServicio"));
                                servicio.setNombreServicio(object.getString("nombreServicio"));
                                servicio.setDescripcionServicio(object.getString("descripcionServicio"));
                                servicio.setValorServicio(object.getString("valorServicio"));

                                sharedPreferences.putString("valorServicio", object.getString("valorServicio") );

                                servicioList.add(servicio);

                            }

                            progressBar.setVisibility(View.GONE);
                            linearLayoutPrecioTotal.setVisibility(View.VISIBLE);
//                            buttonSeleccionarServicios.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();

                        }
                        catch (JSONException e)
                        {

                            progressBar.setVisibility(View.GONE);
                           // buttonSeleccionarServicios.setVisibility(View.GONE);

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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

                            AlertDialog.Builder builder = new AlertDialog.Builder(SolicitarServicio.this.getActivity());
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
                       // buttonSeleccionarServicios.setVisibility(View.GONE);
                    }


                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError
                    {
                        HashMap <String, String> headers = new HashMap <String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("WWW-Authenticate", "xBasic realm=".concat(""));
                        headers.put("MyToken", sharedPreferences.getString("MyToken"));
                        return headers;
                    }
                };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(10000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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
        private SolicitarServicio.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SolicitarServicio.ClickListener clickListener) {
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
