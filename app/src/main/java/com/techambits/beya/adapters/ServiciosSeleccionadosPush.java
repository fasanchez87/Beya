package com.techambits.beya.adapters;

/**
 * Created by FABiO on 05/02/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.techambits.beya.R;
import com.techambits.beya.beans.Servicio;
import com.techambits.beya.volley.ControllerSingleton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ServiciosSeleccionadosPush extends RecyclerView.Adapter <ServiciosSeleccionadosPush.MyViewHolder>
{

    private List<Servicio> serviciosList;
    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView idServicio,nombreServicio, descripcionServicio, valorServicio;
        public NetworkImageView imagenServicio;


        public CheckBox checkServicio;

        public MyViewHolder(View view)
        {
            super(view);

            nombreServicio = (TextView) view.findViewById(R.id.textViewNombreServicioServiciosSeleccionadosPush);
            descripcionServicio = (TextView) view.findViewById(R.id.textViewDescServicioServiciosSeleccionadosPush);
            valorServicio = (TextView) view.findViewById(R.id.textViewValorServicioServiciosSeleccionadosPush);
        }
    }


    public ServiciosSeleccionadosPush(List<Servicio> serviciosList)
    {
        this.serviciosList = serviciosList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.servicios_seleccionados_push, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        final Servicio servicio = serviciosList.get(position);

        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);

        holder.nombreServicio.setText(servicio.getNombreServicio());
        holder.descripcionServicio.setText(servicio.getDescripcionServicio());
        holder.valorServicio.setText("$"+nf.format(Integer.parseInt(servicio.getValorServicio())));

    }

    @Override
    public int getItemCount()
    {
        return serviciosList.size();
    }

    public List<Servicio> getServiciosList() {
        return serviciosList;
    }
}