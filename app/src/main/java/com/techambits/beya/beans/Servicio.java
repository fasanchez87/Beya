package com.techambits.beya.beans;

/**
 * Created by FABiO on 05/02/2016.
 */
public class Servicio
{

    private String nombreServicio;
    private String descripcionServicio;
    private String valorServicio;
    private String id;
    private String imagen;



    private String indicaSolicitado;



    private boolean isSelected;

    public Servicio()
    {
    }

    public Servicio(String id, String nombreServicio, String descripcionServicio, String valorServicio, boolean isSelected)
    {
        this.nombreServicio = nombreServicio;
        this.descripcionServicio = descripcionServicio;
        this.valorServicio = valorServicio;
        this.id = id;
        this.isSelected = isSelected;

    }

    public Servicio(String id, String nombreServicio, String descripcionServicio, String valorServicio, boolean isSelected, String imagen)
    {
        this.nombreServicio = nombreServicio;
        this.descripcionServicio = descripcionServicio;
        this.valorServicio = valorServicio;
        this.id = id;
        this.imagen = imagen;
        this.isSelected = isSelected;

    }

    public String getIndicaSolicitado() {
        return indicaSolicitado;
    }

    public void setIndicaSolicitado(String indicaSolicitado) {
        this.indicaSolicitado = indicaSolicitado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNombreServicio()
    {
        return nombreServicio;
    }

    public void setNombreServicio(String nombreServicio)
    {
        this.nombreServicio = nombreServicio;
    }

    public String getDescripcionServicio()
    {
        return descripcionServicio;
    }

    public void setDescripcionServicio(String descripcionServicio)
    {
        this.descripcionServicio = descripcionServicio;
    }
    public String getValorServicio()
    {
        return valorServicio;
    }

    public void setValorServicio(String valorServicio)
    {
        this.valorServicio = valorServicio;
    }
}
