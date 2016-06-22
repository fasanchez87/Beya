package com.techambits.beya.adapters;

/**
 * Created by FABiO on 05/02/2016.
 */

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.techambits.beya.CircularImageView.CircularNetworkImageView;
import com.techambits.beya.R;
import com.techambits.beya.beans.HistorialSolicitud;
import com.techambits.beya.volley.ControllerSingleton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class HistorialSolicitudAdapter extends RecyclerView.Adapter <HistorialSolicitudAdapter.MyViewHolder>
{

    SharedPreferences sharedPreferences;
    private List<HistorialSolicitud> historialList;

    ImageLoader imageLoader = ControllerSingleton.getInstance().getImageLoader();

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView idSolicitud, nombreSolicitud, fechaSolicitud, estadoSolicitud, valorSolicitud;
        public CircularNetworkImageView imagenServicioHistorial;

        public MyViewHolder(View view)
        {
            super(view);
            idSolicitud = (TextView) view.findViewById(R.id.textViewCodigoSolicitudHistorialServicio);
            nombreSolicitud = (TextView) view.findViewById(R.id.textViewNombreSolicitudHistorialServicio);
            fechaSolicitud = (TextView) view.findViewById(R.id.fechaSolicitudHistorialServicio);
            estadoSolicitud = (TextView) view.findViewById(R.id.textViewEstadoSolicitudHistorialServicio);
            valorSolicitud = (TextView) view.findViewById(R.id.textViewValorServicioHistorialServicio);
            imagenServicioHistorial = (CircularNetworkImageView) view.findViewById(R.id.imageItemServiceHistorial);

        }
    }

    public HistorialSolicitudAdapter(List<HistorialSolicitud> serviciosList)
    {
        this.historialList = serviciosList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.historial_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        final HistorialSolicitud historialItem = historialList.get(position);

        final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);

        if (imageLoader == null)
            imageLoader = ControllerSingleton.getInstance().getImageLoader();

        holder.idSolicitud.setText(historialItem.getIdSolicitud());
        holder.nombreSolicitud.setText(historialItem.getNombreSolicitud());
        holder.fechaSolicitud.setText(historialItem.getFechaSolicitud());
        holder.estadoSolicitud.setText(historialItem.getEstadoSolicitud());
        holder.valorSolicitud.setText("$"+nf.format(Integer.parseInt(historialItem.getValorSolicitud())));



        holder.imagenServicioHistorial.setImageUrl(historialItem.getImagenSolicitud(), imageLoader);
        holder.imagenServicioHistorial.setErrorImageResId(R.drawable.beya_historial);
        holder.imagenServicioHistorial.setDefaultImageResId(R.drawable.beya_historial);

    }

    @Override
    public int getItemCount()
    {
        return historialList.size();
    }

    public List<HistorialSolicitud> getHistorialList()
    {
        return historialList;
    }
}