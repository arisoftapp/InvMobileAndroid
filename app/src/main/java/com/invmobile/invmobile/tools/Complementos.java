package com.invmobile.invmobile.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

public class Complementos {
    ProgressDialog progreso;
    public void mensajes(String mensaje, Context context)
    {
        Toast.makeText(context, ""+mensaje, Toast.LENGTH_SHORT).show();
    }
    public void inciar_barra_progreso_spinner(Context context,String titulo)
    {

        progreso = new ProgressDialog(context);
        progreso.setMessage(titulo);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setIndeterminate(true);
        progreso.setCancelable(false);
        progreso.show();
    }
    public void inciar_barra_progreso_horizontal(Context context,String titulo)
    {

        progreso = new ProgressDialog(context);
        progreso.setMessage(titulo);
        progreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progreso.setCancelable(false);
        progreso.setMax(0);
        progreso.setProgress(0);
        progreso.show();
    }
    public void setMax(int max)
    {
        progreso.setMax(max);
    }
    public void setProgreso(int i)
    {
        progreso.setProgress(i);
    }
    public void cerrar_barra_progreso()
    {
        progreso.dismiss();
    }
    public String getUrl()
    {
        return "http://wsar.homelinux.com:";
    }
    public int getTimeout()
    {
        return 10000;
    }
    public boolean eliminarTabla(String tabla,Context contexto)
    {
        boolean success;
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            //db.execSQL("DROP TABLE IF EXISTS "+tabla);
            db.execSQL("DELETE FROM " + tabla);
            db.close();
            success=true;
            mensajes("Se elimino tabla "+tabla,contexto);

        }catch (SQLiteException e)
        {
            success=false;
            mensajes("Error al eliminar tabla "+tabla+":"+e.getMessage(),contexto);
        }
        return success;
    }

}
