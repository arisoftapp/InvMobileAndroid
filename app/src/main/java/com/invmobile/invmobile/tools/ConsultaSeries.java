package com.invmobile.invmobile.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class ConsultaSeries {
    public Boolean getTablaVacia(Context contexto) {
        Boolean vacio = true;
        Database admin = new Database(contexto, null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM series ",null);
            if(fila.moveToFirst()) {
                vacio=false;
            }

        }catch (SQLiteException sql){
            vacio = true;
        }
        db.close();
        return vacio;
    }
    public void insertarSerie(String serie,String codigo,String almacen, String estatus,Context contexto)
    {
        //boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("serie",serie);
            r.put("codigo",codigo);
            r.put("almacen",almacen);
            r.put("estatus",estatus);

            db.insert("series",null,r);
            db.close();
            //validar=true;
                Log.i("consultaSeries"," |  exito al insertar");
        }catch (SQLiteException e)
        {
            Log.i("consultaSeries"," |  error al insertar :"+e.getMessage());
            //mensaje="error al insertar articulo:"+e.getMessage();
            //validar=false;
        }
        //return validar;
    }
    public void eliminarSerie(String serie,String idalmacen, Context contexto)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("series","serie='"+serie+"' and almacen='"+idalmacen+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }
        //return validar;
    }
    public void eliminarSeriesPorArt(String codigo,String idalmacen, Context contexto)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("series","codigo='"+codigo+"' and almacen='"+idalmacen+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }
        //return validar;
    }
    public Boolean buscarSerie(String codigo,String idalmacen,String serie, Context contexto) {
        Boolean dato = false;
        Database admin = new Database(contexto, null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM series where codigo='"+codigo+"' and almacen='"+idalmacen+"' and serie='"+serie+"'",null);
            if(fila.moveToFirst()) {
                dato=true;
            }

        }catch (SQLiteException sql){
            dato = false;
        }
        db.close();
        return dato;
    }
    public String getEstatus(String codigo,String idalmacen,String serie, Context contexto) {
        String dato = "";
        Database admin = new Database(contexto, null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT estatus FROM series where codigo='"+codigo+"' and almacen='"+idalmacen+"' and serie='"+serie+"' ",null);
            if(fila.moveToFirst()) {
                dato=fila.getString(0);
            }

        }catch (SQLiteException sql){
            dato = "";
        }
        db.close();
        return dato;
    }
    public void cambiarEstatus(String codigo,String idalmacen,String estatus,String serie, Context contexto)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("estatus",estatus);
            db.update("series",r,"codigo='"+codigo+"' and almacen='"+idalmacen+"' and serie='"+serie+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }
        //return validar;
    }
    public void eliminarSeries(String codigo,String idalmacen, Context contexto)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("series","codigo='"+codigo+"' and almacen='"+idalmacen+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }
        //return validar;
    }
   public float getSeriesContadas(String codigo,String idalmacen,Context contexto)
   {
       Log.i("seriescontadas", "series contadas" );
       float total=0;
       try{
           Database admin = new Database(contexto,null,1);
           SQLiteDatabase db = admin.getWritableDatabase();
           Cursor fila = db.rawQuery("SELECT serie,estatus FROM series where almacen='"+idalmacen+"' and codigo='"+codigo+"' and estatus!='N'  ",null);
           if(fila.moveToFirst())
           {
              total=fila.getCount();
               do {

                   Log.i("seriescontadas", " | " + fila.getString(0)+" | "+fila.getString(1)+" | "+total);

               }while (fila.moveToNext());
           }

           db.close();
       }catch (Exception e)
       {
           Log.e("Error:",""+e.getMessage());
       }
       return total;
   }
    public float getSeriesExistencia(String codigo,String idalmacen,Context contexto)
    {
        Log.i("seriescontadas", "series contadas" );
        float total=0;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT serie,estatus FROM series where almacen='"+idalmacen+"' and codigo='"+codigo+"' and estatus!='ND'  ",null);
            if(fila.moveToFirst())
            {
                total=fila.getCount();
                do {

                    Log.i("seriescontadas", " | " + fila.getString(0)+" | "+fila.getString(1)+" | "+total);

                }while (fila.moveToNext());
            }

            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        return total;
    }
    public void eliminarTodasSeries(String idalmacen, Context contexto)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("series","almacen='"+idalmacen+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }
        //return validar;
    }
}
