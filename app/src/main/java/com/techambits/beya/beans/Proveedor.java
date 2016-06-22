package com.techambits.beya.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by FABiO on 09/02/2016.......
 */
public class Proveedor implements Parcelable
{

    private long id;
    private String nombreProveedor;
    private String apellidoProveedor;
    private String emailProveedor;
    private String promedioServicios;
    private String latitudUsuario;
    private String longitudUsuario;



    private String imgUsuario;

    public Proveedor()
    {
    }

    public Proveedor(Parcel source)
    {
       // id = source.readLong();
        nombreProveedor = source.readString();
        apellidoProveedor = source.readString();
        emailProveedor = source.readString();
        promedioServicios = source.readString();
        latitudUsuario = source.readString();
        longitudUsuario = source.readString();
        imgUsuario = source.readString();
    }

    public Proveedor (String nombreProveedor, String apellidoProveedor, String emailProveedor,
                      String promedioServicios, String latitudUsuario, String longitudUsuario, String imgUsuario)
    {
        super();
        this.nombreProveedor = nombreProveedor;
        this.apellidoProveedor = apellidoProveedor;
        this.emailProveedor = emailProveedor;
        this.promedioServicios = promedioServicios;
        this.latitudUsuario = latitudUsuario;
        this.longitudUsuario = longitudUsuario;
        this.imgUsuario = imgUsuario;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getImgUsuario() {
        return imgUsuario;
    }

    public void setImgUsuario(String imgUsuario) {
        this.imgUsuario = imgUsuario;
    }

    public String getApellidoProveedor() {
        return apellidoProveedor;
    }

    public void setApellidoProveedor(String apellidoProveedor) {
        this.apellidoProveedor = apellidoProveedor;
    }

    public String getEmailProveedor() {
        return emailProveedor;
    }

    public void setEmailProveedor(String emailProveedor) {
        this.emailProveedor = emailProveedor;
    }

    public String getPromedioServicios() {
        return promedioServicios;
    }

    public void setPromedioServicios(String promedioServicios) {
        this.promedioServicios = promedioServicios;
    }

    public String getLatitudUsuario() {
        return latitudUsuario;
    }

    public void setLatitudUsuario(String latitudUsuario) {
        this.latitudUsuario = latitudUsuario;
    }

    public String getLongitudUsuario() {
        return longitudUsuario;
    }

    public void setLongitudUsuario(String longitudUsuario) {
        this.longitudUsuario = longitudUsuario;
    }


    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents()
    {
        return this.hashCode();
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeLong(id);
        dest.writeString(nombreProveedor);
        dest.writeString(apellidoProveedor);
        dest.writeString(emailProveedor);
        dest.writeString(promedioServicios);
        dest.writeString(latitudUsuario);
        dest.writeString(longitudUsuario);
        dest.writeString(imgUsuario);

    }

    public static final Parcelable.Creator<Proveedor> CREATOR = new Parcelable.Creator<Proveedor>()
    {
        public Proveedor createFromParcel(Parcel in)
        {
            return new Proveedor(in);
        }

        public Proveedor[] newArray(int size)
        {
            return new Proveedor[size];
        }
    };
}
