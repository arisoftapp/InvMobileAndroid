package com.invmobile.invmobile.tools;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.invmobile.invmobile.R;

import java.util.Vector;

public class ConsultaClasificaciones {
    ArrayAdapter<String> adapter;
    Vector<String> datos ,codigos;
    public String getIdClf(Context contexto)
    {
        String resultado="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT id_clf FROM clasificaciones",null);
            if(fila.moveToFirst())
            {
                Log.i("consultaalmacenes"," | "+fila.getString(0));
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
    public String getClasificacion(Context contexto,String id)
    {
        String resultado="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT clf_desc FROM clasificaciones where id_clf='"+id+"'",null);
            if(fila.moveToFirst())
            {
                Log.i("consultaalmacenes"," | "+fila.getString(0));
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
    public ArrayAdapter<String> getClasificaciones (Context contexto){
        try {
            datos = new Vector<String>();
            //datos.clear();
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM clasificaciones", null);
            if (fila.moveToFirst()) {
                do {
                    datos.add(fila.getString(1) + " - " + fila.getString(2));
                } while (fila.moveToNext());
                adapter = new ArrayAdapter<>(contexto, R.layout.support_simple_spinner_dropdown_item, datos);
            } else {
            }
            db.close();
        }catch (SQLException e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        return adapter;
    }
    public Vector<String>getCodigos(Context contexto)
    {
        try {
            codigos = new Vector<String>();
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM clasificaciones", null);
            if (fila.moveToFirst()) {
                do {
                    codigos.add(fila.getString(1));
                } while (fila.moveToNext());
            } else {
            }
            db.close();
        }catch (SQLException e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        return codigos;
    }
    public Boolean getTablaVacia(Context contexto) {
        Boolean vacio = true;
        Database admin = new Database(contexto, null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM clasificaciones ",null);
            if(fila.moveToFirst()) {
                vacio=false;
            }

        }catch (SQLiteException sql){
            vacio = true;
        }
        db.close();
        return vacio;
    }

}
