package com.techambits.beya.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by FABiO on 12/04/2016.
 */
public class SolicitudServicioSeleccionada implements Parcelable
{

    private HashMap <String, ArrayList<Servicio>> listaSolicitudesSeleccionadas;

    public SolicitudServicioSeleccionada()
    {

    }

    public SolicitudServicioSeleccionada(Parcel source)
    {
        // id = source.readLong();
        listaSolicitudesSeleccionadas = source.readHashMap(Servicio.class.getClassLoader());

    }

    public SolicitudServicioSeleccionada( HashMap <String, ArrayList<Servicio>> listaSolicitudesSeleccionadas )
    {
        super();
        this.listaSolicitudesSeleccionadas = listaSolicitudesSeleccionadas;
    }

    public HashMap<String, ArrayList<Servicio>> getListaSolicitudesSeleccionadas()
    {
        return listaSolicitudesSeleccionadas;
    }

    public void setListaSolicitudesSeleccionadas(HashMap<String, ArrayList<Servicio>> listaSolicitudesSeleccionadas)
    {
        this.listaSolicitudesSeleccionadas = listaSolicitudesSeleccionadas;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeMap(listaSolicitudesSeleccionadas);
    }

    public static final Parcelable.Creator<SolicitudServicioSeleccionada> CREATOR = new Parcelable.Creator<SolicitudServicioSeleccionada>()
    {
        public SolicitudServicioSeleccionada createFromParcel(Parcel in)
        {
            return new SolicitudServicioSeleccionada(in);
        }

        public SolicitudServicioSeleccionada[] newArray(int size)
        {
            return new SolicitudServicioSeleccionada[size];
        }
    };
}
