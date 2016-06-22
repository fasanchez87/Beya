package com.techambits.beya.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.techambits.beya.R;
import com.techambits.beya.adapters.ServiciosSeleccionadosPush;
import com.techambits.beya.beans.Servicio;
import com.techambits.beya.decorators.DividerItemDecoration;
import com.techambits.beya.sharedPreferences.gestionSharedPreferences;
import com.techambits.beya.vars.vars;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import com.techambits.beya.vars.vars;


public class ListaServiciosRevisarEsteticista extends AppCompatActivity
{

    private String _urlWebServiceAceptarSolicitudServicio;
    private SwipeRefreshLayout refreshLayout;
    private String _urlWebService;
    private String codigoSolicitud;
    private String costoSolicitud;

    public vars vars;


    private ArrayList<Servicio> ser;


    ProgressBar progressBar;
    private RecyclerView recyclerView;

    private ArrayList<Servicio> servicioList;

    private gestionSharedPreferences sharedPreferences;

    private ServiciosSeleccionadosPush mAdapter;

    private String keyCodigoSolicitudSeleccionado;

    private TextView valorTotalServiciosPorRevisarEsteticista;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_servicios_revisar_esteticista);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        sharedPreferences = new gestionSharedPreferences(this);

        vars = new vars();

        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);


        servicioList = new ArrayList<Servicio>();

        valorTotalServiciosPorRevisarEsteticista = (TextView) findViewById(R.id.valorTotalServiciosPorRevisarEsteticista);



        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if(extras == null)
            {
                codigoSolicitud = null;
                costoSolicitud = null;
            }
            else
            {
                codigoSolicitud = extras.getString("codigoSolicitud");
                costoSolicitud = extras.getString("costoSolicitud");
            }
        }

        else
        {
            codigoSolicitud = (String) savedInstanceState.getSerializable("codigoSolicitud");
            costoSolicitud = (String) savedInstanceState.getSerializable("costoSolicitud");
        }


        Intent intent = getIntent();
        keyCodigoSolicitudSeleccionado = intent.getStringExtra("codigoSolicitud");

        Log.i("keyCodigoSolicitudSeleccionado", "" + keyCodigoSolicitudSeleccionado);
        servicioList = sharedPreferences.getHashMapObjectServicio().get(keyCodigoSolicitudSeleccionado);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_servicios_por_revisar_esteticista_y_aceptar_solicitud);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ServiciosSeleccionadosPush(servicioList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        valorTotalServiciosPorRevisarEsteticista.setText("$"+nf.format(Integer.parseInt(costoSolicitud)));

        mAdapter.notifyDataSetChanged();

    }
}
