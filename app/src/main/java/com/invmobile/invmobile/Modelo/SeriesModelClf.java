package com.invmobile.invmobile.Modelo;

public class SeriesModelClf {
    private String serie;
    private String estatus;

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public SeriesModelClf(String serie, String estatus)
    {
        this.serie=serie;
        this.estatus=estatus;
    }
}
