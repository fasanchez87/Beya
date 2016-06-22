package com.techambits.beya.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techambits.beya.R;
import com.techambits.beya.beans.SolicitudServicio;

import java.util.List;

/**
 * Created by FABiO on 08/04/2016.
 */
public class ServiciosDisponiblesAdapter extends RecyclerView.Adapter <ServiciosDisponiblesAdapter.MyViewHolder>
{
    private List<SolicitudServicio> solicitudServicioList;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView codigoSolicitud, fechaSolicitud, direccionSolicitud, lugarSolicitud, horaSolicitud, esAtendida;

        public MyViewHolder(View view)
        {
            super(view);

            codigoSolicitud = (TextView) view.findViewById(R.id.textViewCodigoSolicitud);
            fechaSolicitud = (TextView) view.findViewById(R.id.textViewFechaSolicitudServicio);
            /*direccionSolicitud = (TextView) view.findViewById(R.id.textViewUbicacionSolicitudServicio);
            lugarSolicitud = (TextView) view.findViewById(R.id.textViewLugarSolicitudServicio);*/
            horaSolicitud = (TextView) view.findViewById(R.id.textViewHoraSolicitudServicio);
            esAtendida = (TextView) view.findViewById(R.id.esAtendidaSolicitudServicio);
        }
    }

    public ServiciosDisponiblesAdapter(List<SolicitudServicio> serviciosList)
    {
        this.solicitudServicioList = serviciosList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.solicitud_servicio_row, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {

        final SolicitudServicio solicitudServicio = solicitudServicioList.get(position);
        holder.codigoSolicitud.setText(solicitudServicio.getCodigoSolicitudServicio());
        holder.fechaSolicitud.setText(solicitudServicio.getFechaSolicitudServicio());
       /* holder.direccionSolicitud.setText(solicitudServicio.getUbicacionSolicitudServicio());
        holder.lugarSolicitud.setText(solicitudServicio.getLugarSolicitudServicio());*/
        holder.horaSolicitud.setText(solicitudServicio.getHoraSolicitudServicio());
        holder.esAtendida.setText(solicitudServicio.getEstadoSolicitud());


    }

    @Override
    public int getItemCount()
    {
        return solicitudServicioList.size();
    }

    public List<SolicitudServicio> getSolicitudServicioList() {
        return solicitudServicioList;
    }
}
