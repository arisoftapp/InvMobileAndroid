package com.invmobile.invmobile.tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

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
}
