package com.invmobile.invmobile.tools;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.invmobile.invmobile.AdaptadorConteo;
import com.invmobile.invmobile.Modelo.InventarioModel;
import com.invmobile.invmobile.R;

import java.util.ArrayList;
import java.util.Vector;

public class ConsultasConteo {
    ArrayAdapter<String> adapter;
    ArrayList<InventarioModel>datos ;

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
    public Boolean getTablaVaciaAlm(String id_alm,Context contexto) {
        Boolean vacio = true;
        Database admin = new Database(contexto, null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM conteo where idalmacen='"+id_alm+"' ",null);
            if(fila.moveToFirst()) {
                vacio=false;
            }

        }catch (SQLiteException sql){
            vacio = true;
        }
        db.close();
        Log.i("tablavaciaalmacen",""+vacio);
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
    public Boolean codigo2(String codigo,String idalmacen, Context contexto) {
        Boolean dato = false;
        Database admin = new Database(contexto, null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT codigo2 FROM conteo where codigo='"+codigo+"' and idalmacen='"+idalmacen+"' or codigo2='"+codigo+"' and idalmacen='"+idalmacen+"' ",null);
            if(fila.moveToFirst()) {
                if(fila.getString(0).equalsIgnoreCase(codigo))
                {
                    dato=true;
                }
                else
                {
                    dato=false;
                }

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
            Log.i("insertarconteo:","se inserto conteo");

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            Log.e("Error:",""+e.getMessage());
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
    public void eliminarCodigoConteo(String codigo,String idalmacen, Context contexto)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("conteo","codigo='"+codigo+"' and idalmacen='"+idalmacen+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }
        //return validar;
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
    public String getCodigo1(String codigo,String idalmacen, Context contexto)
    {
        String resultado="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo FROM conteo where codigo2='"+codigo+"' and idalmacen='"+idalmacen+"' ",null);
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
    public String getComentarios(String codigo,String idalmacen, Context contexto)
    {
        String resultado="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT comentarios FROM conteo where codigo='"+codigo+"' and idalmacen='"+idalmacen+"' ",null);
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

    public void setComentarios(String codigo,String idalmacen,String comentarios, Context contexto)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("comentarios",comentarios);
            db.update("conteo",r,"codigo='"+codigo+"' and idalmacen='"+idalmacen+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }
        //return validar;
    }
    public void setConteo(String codigo,String idalmacen,Context contexto, String conteo)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("conteo",conteo);
            db.update("conteo",r,"codigo='"+codigo+"' and idalmacen='"+idalmacen+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }

    }
    public void setExistencia(String codigo,String idalmacen,Context contexto, Float existencia)
    {
        boolean validar;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("existencia",existencia);
            db.update("conteo",r,"codigo='"+codigo+"' and idalmacen='"+idalmacen+"'  ",null);
            db.close();
            validar=true;

        }catch (SQLiteException e)
        {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar=false;
        }

    }
    public ArrayList<InventarioModel> getConteoCompleto (String idalmacen, Context contexto){
        try {
            datos = new ArrayList<>();
            //datos.clear();
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM conteo where idalmacen='"+idalmacen+"' ", null);
            if (fila.moveToFirst()) {
                do {
                    String codigo = fila.getString(0);
                    String descripcion = fila.getString(1);
                    Float existencia = fila.getFloat(2);
                    datos.add(0, new InventarioModel(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5),fila.getString(6)));

                } while (fila.moveToNext());

            }
            fila=db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM sinconteo where idalmacen='"+idalmacen+"' ", null);
            if (fila.moveToFirst()) {
                do {
                    datos.add(0, new InventarioModel(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5),fila.getString(6)));
                } while (fila.moveToNext());

            }


            db.close();

        }catch (SQLException e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        //adaptadorConteo=new AdaptadorConteo((Activity) contexto,datos);
        return datos;
    }
    public ArrayList<InventarioModel> getConteoCompletoSD (String idalmacen, Context contexto){
        try {
            datos = new ArrayList<>();
            //datos.clear();
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM conteo where idalmacen='"+idalmacen+"' and diferencia!=0 ", null);
            if (fila.moveToFirst()) {
                do {
                    String codigo = fila.getString(0);
                    String descripcion = fila.getString(1);
                    Float existencia = fila.getFloat(2);
                    datos.add(0, new InventarioModel(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5),fila.getString(6)));

                } while (fila.moveToNext());

            }
            fila.close();
            Cursor fila2=db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM sinconteo where idalmacen='"+idalmacen+"' and diferencia!=0  ", null);
            if (fila2.moveToFirst()) {
                do {
                    datos.add(0, new InventarioModel(fila2.getString(0), fila2.getString(1), fila2.getString(2), fila2.getString(3), fila2.getString(4), fila2.getString(5),fila2.getString(6)));
                } while (fila2.moveToNext());

            }
            fila2.close();
            db.close();

        }catch (SQLException e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        return datos;
    }
    public ArrayList<InventarioModel> getSoloConteo (String idalmacen, Context contexto){
        try {
            datos = new ArrayList<>();
            //datos.clear();
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM conteo where idalmacen='"+idalmacen+"' ", null);
            if (fila.moveToFirst()) {
                do {
                    String codigo = fila.getString(0);
                    String descripcion = fila.getString(1);
                    Float existencia = fila.getFloat(2);
                    datos.add(0, new InventarioModel(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5),fila.getString(6)));

                } while (fila.moveToNext());

            }

            db.close();

        }catch (SQLException e)
        {
            Log.e("Error:",""+e.getMessage());
        }

        return datos;
    }
    public ArrayList<InventarioModel> getSoloConteoSD (String idalmacen, Context contexto){
        try {
            datos = new ArrayList<>();
            //datos.clear();
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM conteo where idalmacen='"+idalmacen+"' and diferencia!=0 ", null);
            if (fila.moveToFirst()) {
                do {
                    String codigo = fila.getString(0);
                    String descripcion = fila.getString(1);
                    Float existencia = fila.getFloat(2);
                    datos.add(0, new InventarioModel(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5),fila.getString(6)));

                } while (fila.moveToNext());

            }

            db.close();

        }catch (SQLException e)
        {
            Log.e("Error:",""+e.getMessage());
        }

        return datos;
    }
    public ArrayList<InventarioModel> getSinConteo (String idalmacen, Context contexto){
        try {
            datos = new ArrayList<>();
            //datos.clear();
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM sinconteo where idalmacen='"+idalmacen+"' ", null);
            if (fila.moveToFirst()) {
                do {
                    String codigo = fila.getString(0);
                    String descripcion = fila.getString(1);
                    Float existencia = fila.getFloat(2);
                    datos.add(0, new InventarioModel(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5),fila.getString(6)));

                } while (fila.moveToNext());

            }

            db.close();

        }catch (SQLException e)
        {
            Log.e("Error:",""+e.getMessage());
        }

        return datos;
    }
}
