package com.techambits.beya.beans;

/**
 * Created by FABiO on 06/05/2016.
 */
public class HistorialSolicitud
{
    private String idSolicitud;
    private String nombreSolicitud;
    private String fechaSolicitud;
    private String estadoSolicitud;
    private String valorSolicitud;
    private String imagenSolicitud;

    public HistorialSolicitud()
    {
    }

    public HistorialSolicitud( String idSolicitud, String nombreSolicitud, String fechaSolicitud ,
                               String estadoSolicitud, String valorSolicitud,String imagenSolicitud)
    {
        this.idSolicitud = idSolicitud;
        this.nombreSolicitud = nombreSolicitud;
        this.fechaSolicitud = fechaSolicitud;
        this.estadoSolicitud = estadoSolicitud;
        this.valorSolicitud = valorSolicitud;
        this.imagenSolicitud = imagenSolicitud;
    }

    public String getImagenSolicitud() {
        return imagenSolicitud;
    }

    public void setImagenSolicitud(String imagenSolicitud) {
        this.imagenSolicitud = imagenSolicitud;
    }

    public String getIdSolicitud()
    {
        return idSolicitud;
    }

    public void setIdSolicitud(String idSolicitud)
    {
        this.idSolicitud = idSolicitud;
    }

    public String getNombreSolicitud()
    {
        return nombreSolicitud;
    }

    public void setNombreSolicitud(String nombreSolicitud)
    {
        this.nombreSolicitud = nombreSolicitud;
    }

    public String getFechaSolicitud()
    {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud)
    {
        this.fechaSolicitud = fechaSolicitud;
    }

    public String getValorSolicitud()
    {
        return valorSolicitud;
    }

    public void setValorSolicitud(String valorSolicitud)
    {
        this.valorSolicitud = valorSolicitud;
    }

    public String getEstadoSolicitud()
    {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud)
    {
        this.estadoSolicitud = estadoSolicitud;
    }


}
