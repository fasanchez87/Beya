package com.techambits.beya.adapters;

/**
 * Created by FABiO on 05/02/2016.
 */

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.techambits.beya.R;
import com.techambits.beya.activities.AceptacionServicio;
import com.techambits.beya.activities.ListaServiciosCliente;
import com.techambits.beya.beans.Servicio;
import com.techambits.beya.beans.ValorServicio;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.internal.zzir.runOnUiThread;

public class ServiciosAceptacionAdapter extends RecyclerView.Adapter <ServiciosAceptacionAdapter.MyViewHolder>
{

    private List<Servicio> serviciosList;
    public static int valorTotal = 0;
    public static int valorAcarreado = 0;

    private static CheckBox lastChecked = null;
    private static int lastCheckedPos = 0;

    SharedPreferences sharedPreferences;

    final NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView nombreServicio, valorServicio, idServicio;
        public CheckBox checkServicio;



        public MyViewHolder(View view)
        {
            super(view);

            nombreServicio = (TextView) view.findViewById(R.id.textViewNombreServicioAceptacionServicios);
            valorServicio = (TextView) view.findViewById(R.id.textViewValorServicioAceptacionServicios);
            checkServicio = (CheckBox) view.findViewById(R.id.checkBoxServicioAceptacionServicios);
            idServicio = (TextView) view.findViewById(R.id.textViewIDServicioAceptacionServicios);
        }
    }


    public ServiciosAceptacionAdapter(List<Servicio> serviciosList)
    {
        this.serviciosList = serviciosList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.aceptacion_servicios_row, parent, false);

        return new MyViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        final Servicio servicio = serviciosList.get(position);

        holder.idServicio.setText(servicio.getId());
        holder.nombreServicio.setText(servicio.getNombreServicio());
        holder.valorServicio.setText("$"+nf.format(Integer.parseInt(servicio.getValorServicio())));
        holder.checkServicio.setChecked(servicio.isSelected());
        holder.checkServicio.setTag(servicio);

        valorAcarreado = ValorServicio.getValorServicio();

        //for default check in first item
        if(position == 0 && serviciosList.get(0).isSelected() && holder.checkServicio.isChecked())
        {
            lastChecked = holder.checkServicio;
            lastCheckedPos = 0;
        }


        holder.checkServicio.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                CheckBox cb = (CheckBox) v;
                Servicio s = (Servicio) cb.getTag();
                s.setSelected(cb.isChecked());
                serviciosList.get(position).setSelected(cb.isChecked());
                Toast.makeText(v.getContext(), "Clicked on Checkbox: " + cb.getText() + " is "+ cb.isChecked(), Toast.LENGTH_LONG).show();


               /* if(cb.isChecked())
                {
                    if(lastChecked != null)
                    {
                        //lastChecked.setChecked(false);
                       // serviciosList.get(lastCheckedPos).setSelected(false);
                    }

                    lastChecked = cb;
                    lastCheckedPos = position;
                }
                else
                    lastChecked = null;*/



                //valorTotal = Integer.parseInt(AceptacionServicio.precioTemporalAceptacionServicios.getText().toString());



                if(cb.isChecked())
                {
                    //valorAcarreado = Integer.parseInt(AceptacionServicio.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.getText().toString());
                    //valorAcarreado = ValorServicio.getValorServicio();

                    valorAcarreado += Integer.parseInt(s.getValorServicio());

                    Toast.makeText(v.getContext(), "valorAcarreado "+valorAcarreado, Toast.LENGTH_LONG).show();

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            AceptacionServicio.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.
                                    setText("" + nf.format(valorAcarreado));
                            ListaServiciosCliente.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.
                                    setText("" + nf.format(valorAcarreado));
                            AceptacionServicio.precioTemporalAceptacionServicios.setText("" + nf.format(valorAcarreado));

                        }
                    });

                }

                else
                {
                    //valorAcarreado = Integer.parseInt(AceptacionServicio.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.getText().toString());
                    //valorAcarreado = ValorServicio.getValorServicio();

                    valorAcarreado -= Integer.parseInt(s.getValorServicio());
                    Toast.makeText(v.getContext(), "valorAcarreado "+valorAcarreado, Toast.LENGTH_LONG).show();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            AceptacionServicio.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.
                                    setText("" + nf.format(valorAcarreado));
                            ListaServiciosCliente.valorTotalServiciosSeleccionadosEsteticistaAceptacionServicios.
                                    setText("" + nf.format(valorAcarreado));


                        }
                    });
                }

                ValorServicio.setValorServicio(valorAcarreado);


            }
        });

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