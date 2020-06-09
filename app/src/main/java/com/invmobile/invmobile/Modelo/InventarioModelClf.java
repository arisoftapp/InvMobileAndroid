package com.invmobile.invmobile.Modelo;

public class InventarioModelClf {
    private String CodigoArticulo;
    private String Descripcion;
    private Float Existencia;
    private Float Conteo;
    private Float Diferencia;
    private String Comentarios;
    private String Clasificacion;


    public InventarioModelClf(String codigo, String descripcion, String existencia, String conteo, String diferencia, String serie, String comentarios,String clf)
    {
        this.CodigoArticulo=codigo;
        this.Descripcion=descripcion;
        this.Existencia=Float.parseFloat(existencia);
        this.Conteo=Float.parseFloat(conteo);
        this.Diferencia=Float.parseFloat(diferencia);
        this.Serie=serie;
        this.Comentarios=comentarios;
        this.Clasificacion=clf;
    }

    public String getClasificacion() {
        return Clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        Clasificacion = clasificacion;
    }

    public String getCodigoArticulo() {
        return CodigoArticulo;
    }

    public void setCodigoArticulo(String codigoArticulo) {
        CodigoArticulo = codigoArticulo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public Float getExistencia() {
        return Existencia;
    }

    public void setExistencia(Float existencia) {
        Existencia = existencia;
    }

    public Float getConteo() {
        return Conteo;
    }

    public void setConteo(Float conteo) {
        Conteo = conteo;
    }

    public Float getDiferencia() {
        return Diferencia;
    }

    public void setDiferencia(Float diferencia) {
        Diferencia = diferencia;
    }

    public String getSerie() {
        return Serie;
    }

    public void setSerie(String serie) {
        Serie = serie;
    }

    private String Serie;
    public String getComentarios() {
        return Comentarios;
    }

    public void setComentarios(String comentarios) {
        Comentarios = comentarios;
    }


}
