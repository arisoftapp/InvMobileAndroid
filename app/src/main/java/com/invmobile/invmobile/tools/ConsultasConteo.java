package com.invmobile.invmobile.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class ConsultasConteo {

    public Boolean getTablaVacia(Context contexto) {
        Boolean vacio = true;
        Database admin = new Database(contexto, null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM conteo ",null);
            if(fila.moveToFirst()) {
                vacio=false;
            }

        }catch (SQLiteException sql){
            vacio = true;
        }
        db.close();
        return vacio;
    }
    public Boolean buscarArticulo(String codigo,String idalmacen, Context contexto) {
        Boolean dato = false;
        Database admin = new Database(contexto, null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM conteo where codigo='"+codigo+"' and idalmacen='"+idalmacen+"' or codigo2='"+codigo+"' and idalmacen='"+idalmacen+"' ",null);
            if(fila.moveToFirst()) {
                dato=true;
            }

        }catch (SQLiteException sql){
            dato = false;
        }
        db.close();
        return dato;
    }
    public Boolean validarConteo(String idalmacen, Context contexto) {
        Boolean dato = false;
        Database admin = new Database(contexto, null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM conteo where idalmacen='"+idalmacen+"' ",null);
            if(fila.moveToFirst()) {
                dato=true;
            }
        }catch (SQLiteException sql){
            dato = false;
        }
        db.close();
        return dato;
    }
    public Boolean insertarConteo(String codigo,String codigo2,String descripcion, Float conteo,Float existencia,String idalmacen,String serie,Float diferencia, Context contexto)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("codigo",codigo);
            r.put("codigo2",codigo2);
            r.put("descripcion",descripcion);
            r.put("conteo",conteo);
            r.put("existencia",existencia);
            r.put("idalmacen",idalmacen);
            r.put("serie",serie);
            r.put("diferencia",diferencia);
            r.put("estatus","");
            r.put("comentarios","");
            db.insert("conteo",null,r);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }
        return validar;
    }
    public Boolean actualizarConteo(String codigo,Float conteo,Float existencia,String idalmacen,Float diferencia, Context contexto)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("conteo",conteo);
            r.put("existencia",existencia);
            r.put("diferencia",diferencia);
            db.update("conteo",r,"codigo='"+codigo+"' and idalmacen='"+idalmacen+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }
        return validar;
    }
    public String getDescripcion(String codigo,String idalmacen, Context contexto)
    {
        String resultado="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT descripcion FROM conteo where codigo='"+codigo+"' and idalmacen='"+idalmacen+"' ",null);
            if(fila.moveToFirst())
            {
                Log.i("consultaconteo"," | "+fila.getString(0));
                resultado=fila.getString(0);
            }
            else
            {
                resultado="";
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            resultado="";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }
    public String getConteo(String codigo,String idalmacen, Context contexto)
    {
        String resultado="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT conteo FROM conteo where codigo='"+codigo+"' and idalmacen='"+idalmacen+"' ",null);
            if(fila.moveToFirst())
            {
                Log.i("consultaconteo"," | "+fila.getString(0));
                resultado=fila.getString(0);
            }
            else
            {
                resultado="";
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            resultado="";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }
    public String getExistencia(String codigo,String idalmacen, Context contexto)
    {
        String resultado="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT existencia FROM conteo where codigo='"+codigo+"' and idalmacen='"+idalmacen+"' ",null);
            if(fila.moveToFirst())
            {
                Log.i("consultaconteo"," | "+fila.getString(0));
                resultado=fila.getString(0);
            }
            else
            {
                resultado="";
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            resultado="";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }
    public String getDiferencia(String codigo,String idalmacen, Context contexto)
    {
        String resultado="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT diferencia FROM conteo where codigo='"+codigo+"' and idalmacen='"+idalmacen+"' ",null);
            if(fila.moveToFirst())
            {
                Log.i("consultaconteo"," | "+fila.getString(0));
                resultado=fila.getString(0);
            }
            else
            {
                resultado="";
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            resultado="";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }
}
