package com.techambits.beya.beans;

/**
 * Created by FABiO on 08/04/2016.
 */
public class SolicitudServicio
{

    private String codigoSolicitudServicio;
    private String codigoClienteSolicitudServicio;
    private String FechaSolicitudServicio;



    private String direccion;



    private String telefonoClienteSolicitudServicio;



    private String imagenClienteSolicitudServicio;


    private String HoraSolicitudServicio;
    private String UbicacionSolicitudServicio;
    private String LugarSolicitudServicio;
    private String estadoSolicitud;



    private String costoSolicitud;


    private String nombreUsuario;





    public SolicitudServicio()
    {

    }

    public SolicitudServicio(String codigoSolicitudServicio , String FechaSolicitudServicio,
                             String UbicacionSolicitudServicio, String codigoClienteSolicitudServicio , String nombreUsuario,
                             String telefonoClienteSolicitudServicio, String imagenClienteSolicitudServicio)
    {
        this.codigoSolicitudServicio = codigoSolicitudServicio;
        this.codigoClienteSolicitudServicio = codigoClienteSolicitudServicio;
        this.FechaSolicitudServicio = FechaSolicitudServicio;
        this.UbicacionSolicitudServicio = UbicacionSolicitudServicio;
        this.nombreUsuario = nombreUsuario;
        this.telefonoClienteSolicitudServicio = telefonoClienteSolicitudServicio;
        this.imagenClienteSolicitudServicio = imagenClienteSolicitudServicio;
    }

    public String getImagenClienteSolicitudServicio() {
        return imagenClienteSolicitudServicio;
    }

    public void setImagenClienteSolicitudServicio(String imagenClienteSolicitudServicio) {
        this.imagenClienteSolicitudServicio = imagenClienteSolicitudServicio;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCostoSolicitud() {
        return costoSolicitud;
    }

    public void setCostoSolicitud(String costoSolicitud) {
        this.costoSolicitud = costoSolicitud;
    }

    public String getCodigoSolicitudServicio() {
        return codigoSolicitudServicio;
    }

    public void setCodigoSolicitudServicio(String codigoSolicitudServicio) {
        this.codigoSolicitudServicio = codigoSolicitudServicio;
    }

    public String getTelefonoClienteSolicitudServicio() {
        return telefonoClienteSolicitudServicio;
    }

    public void setTelefonoClienteSolicitudServicio(String telefonoClienteSolicitudServicio) {
        this.telefonoClienteSolicitudServicio = telefonoClienteSolicitudServicio;
    }

    public String getFechaSolicitudServicio() {
        return FechaSolicitudServicio;
    }

    public void setFechaSolicitudServicio(String fechaSolicitudServicio) {
        FechaSolicitudServicio = fechaSolicitudServicio;
    }

    public String getUbicacionSolicitudServicio() {
        return UbicacionSolicitudServicio;
    }

    public void setUbicacionSolicitudServicio(String ubicacionSolicitudServicio) {
        UbicacionSolicitudServicio = ubicacionSolicitudServicio;
    }

    public String getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(String estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public String getCodigoClienteSolicitudServicio() {
        return codigoClienteSolicitudServicio;
    }

    public void setCodigoClienteSolicitudServicio(String codigoClienteSolicitudServicio) {
        this.codigoClienteSolicitudServicio = codigoClienteSolicitudServicio;
    }

    public String getLugarSolicitudServicio() {
        return LugarSolicitudServicio;
    }

    public void setLugarSolicitudServicio(String lugarSolicitudServicio) {
        LugarSolicitudServicio = lugarSolicitudServicio;
    }


    public String getHoraSolicitudServicio() {
        return HoraSolicitudServicio;
    }

    public void setHoraSolicitudServicio(String horaSolicitudServicio) {
        HoraSolicitudServicio = horaSolicitudServicio;
    }




}
