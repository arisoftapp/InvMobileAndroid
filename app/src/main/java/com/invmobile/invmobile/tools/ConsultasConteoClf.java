package com.invmobile.invmobile.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.invmobile.invmobile.Modelo.InventarioModel;
import com.invmobile.invmobile.Modelo.InventarioModelClf;

import java.util.ArrayList;

public class ConsultasConteoClf {
    ArrayAdapter<String> adapter;
    ArrayList<InventarioModelClf> datos;


    public Boolean getTablaVacia(Context contexto) {
        Boolean vacio = true;
        Database admin = new Database(contexto, null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM conteoclf ", null);
            if (fila.moveToFirst()) {
                vacio = false;
            }

        } catch (SQLiteException sql) {
            vacio = true;
        }
        db.close();
        return vacio;
    }

    public Boolean getVerificarConteo(String idalmacen, String id_clf, Context contexto) {
        Boolean vacio = true;
        Database admin = new Database(contexto, null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM conteoclf where idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "' ", null);
            if (fila.moveToFirst()) {
                vacio = false;
            }

        } catch (SQLiteException sql) {
            vacio = true;
        }
        db.close();
        return vacio;
    }

    public Boolean buscarArticulo(String codigo, String idalmacen, String id_clf, Context contexto) {
        Boolean dato = false;
        Database admin = new Database(contexto, null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM conteoclf where codigo='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "' or codigo2='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "' ", null);
            if (fila.moveToFirst()) {
                dato = true;
            }

        } catch (SQLiteException sql) {
            dato = false;
        }
        db.close();
        return dato;
    }

    public Boolean codigo2(String codigo, String idalmacen, Context contexto) {
        Boolean dato = false;
        Database admin = new Database(contexto, null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT codigo2 FROM conteoclf where codigo='" + codigo + "' and idalmacen='" + idalmacen + "' or codigo2='" + codigo + "' and idalmacen='" + idalmacen + "' ", null);
            if (fila.moveToFirst()) {
                if (fila.getString(0).equalsIgnoreCase(codigo)) {
                    dato = true;
                } else {
                    dato = false;
                }

            }

        } catch (SQLiteException sql) {
            dato = false;
        }
        db.close();
        return dato;
    }

    public Boolean validarConteo(String idalmacen, String clf, Context contexto) {
        Boolean dato = false;
        Database admin = new Database(contexto, null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT * FROM conteoclf where idalmacen='" + idalmacen + "' and clasificacion='" + clf + "' ", null);
            if (fila.moveToFirst()) {
                dato = true;
            }
        } catch (SQLiteException sql) {
            dato = false;
        }
        db.close();
        return dato;
    }

    public Boolean insertarConteo(String codigo, String codigo2, String descripcion, Float conteo, Float existencia, String idalmacen, String serie, Float diferencia, String clf, Context contexto) {
        boolean validar;
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("codigo", codigo);
            r.put("codigo2", codigo2);
            r.put("descripcion", descripcion);
            r.put("conteo", conteo);
            r.put("existencia", existencia);
            r.put("idalmacen", idalmacen);
            r.put("serie", serie);
            r.put("diferencia", diferencia);
            r.put("estatus", "false");
            r.put("comentarios", "");
            r.put("clasificacion", clf);
            db.insert("conteoclf", null, r);
            db.close();
            validar = true;
            Log.i("insertarconteoclf:", "se inserto conteo clf");

        } catch (SQLiteException e) {
            //mensaje="error al insertar articulo:"+e.getMessage();
            Log.e("Error:", "" + e.getMessage());
            validar = false;
        }
        return validar;
    }

    public void actualizarConteo(String codigo, Float conteo, String idalmacen, Float diferencia, String id_clf, Context contexto) {
        boolean validar;
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("conteo", conteo);
            r.put("diferencia", diferencia);
            db.update("conteoclf", r, "codigo='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "'  ", null);
            db.close();
            validar = true;

        } catch (SQLiteException e) {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar = false;
            Log.e("Error:", "" + e.getMessage());
        }
        //return validar;
    }

    public void eliminarCodigoConteo(String codigo, String idalmacen, String id_clf, Context contexto) {
        boolean validar;
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("conteoclf", "codigo='" + codigo + "' and idalmacen='" + idalmacen + "'  ", null);
            db.close();
            validar = true;

        } catch (SQLiteException e) {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar = false;
        }
        //return validar;
    }

    public void eliminarCoteoAlmClf(String idalmacen, String id_clf, Context contexto) {
        boolean validar;
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("conteoclf", "idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "'  ", null);
            db.close();
            validar = true;

        } catch (SQLiteException e) {
            Log.e("ELIMINAR", e.getMessage());
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar = false;
        }
        //return validar;
    }

    public String getDescripcion(String codigo, String idalmacen, String id_clf, Context contexto) {
        String resultado = "";
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT descripcion FROM conteoclf where codigo='" + codigo + "' and idalmacen='" + idalmacen + "' ", null);
            if (fila.moveToFirst()) {
                Log.i("consultaconteo", " | " + fila.getString(0));
                resultado = fila.getString(0);
            } else {
                resultado = "";
            }
            db.close();
        } catch (Exception e) {
            Log.e("Error:", "" + e.getMessage());
            resultado = "";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }

    public String getConteo(String codigo, String idalmacen, String id_clf, Context contexto) {
        String resultado = "";
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT conteo FROM conteoclf where codigo='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "'", null);
            if (fila.moveToFirst()) {
                Log.i("consultaconteo", " | " + fila.getString(0));
                resultado = fila.getString(0);
            } else {
                resultado = "";
            }
            db.close();
        } catch (Exception e) {
            Log.e("Error:", "" + e.getMessage());
            resultado = "";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }

    public String getSerie(String codigo, String idalmacen, String id_clf, Context contexto) {
        String resultado = "";
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT serie FROM conteoclf where codigo='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "'", null);
            if (fila.moveToFirst()) {
                Log.i("consultaconteo", " | " + fila.getString(0));
                resultado = fila.getString(0);
            } else {
                resultado = "";
            }
            db.close();
        } catch (Exception e) {
            Log.e("Error:", "" + e.getMessage());
            resultado = "";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }

    public String getExistencia(String codigo, String idalmacen, String id_clf, Context contexto) {
        String resultado = "";
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT existencia FROM conteoclf where codigo='" + codigo + "' and idalmacen='" + idalmacen + "' ", null);
            if (fila.moveToFirst()) {
                Log.i("consultaconteo", " | " + fila.getString(0));
                resultado = fila.getString(0);
            } else {
                resultado = "";
            }
            db.close();
        } catch (Exception e) {
            Log.e("Error:", "" + e.getMessage());
            resultado = "";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }

    public String getCodigo1(String codigo, String idalmacen, String id_clf, Context contexto) {
        String resultado = "";
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo FROM conteoclf where codigo2='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "' or codigo='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "' ", null);
            if (fila.moveToFirst()) {
                Log.i("consultaconteo", " | " + fila.getString(0));
                resultado = fila.getString(0);
            } else {
                resultado = "";
            }
            db.close();
        } catch (Exception e) {
            Log.e("Error:", "" + e.getMessage());
            resultado = "";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }

    public String getDiferencia(String codigo, String idalmacen, String id_clf, Context contexto) {
        String resultado = "";
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT diferencia FROM conteoclf where codigo='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "' ", null);
            if (fila.moveToFirst()) {
                Log.i("consultaconteo", " | " + fila.getString(0));
                resultado = fila.getString(0);
            } else {
                resultado = "";
            }
            db.close();
        } catch (Exception e) {
            Log.e("Error:", "" + e.getMessage());
            resultado = "";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }

    public String getComentarios(String codigo, String idalmacen, String id_clf, Context contexto) {
        String resultado = "";
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT comentarios FROM conteoclf where codigo='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "' ", null);
            if (fila.moveToFirst()) {
                Log.i("consultaconteo", " | " + fila.getString(0));
                resultado = fila.getString(0);
            } else {
                resultado = "";
            }
            db.close();
        } catch (Exception e) {
            Log.e("Error:", "" + e.getMessage());
            resultado = "";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return resultado;
    }

    public void setComentarios(String codigo, String idalmacen, String comentarios, String id_clf, Context contexto) {
        boolean validar;
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("comentarios", comentarios);
            db.update("conteoclf", r, "codigo='" + codigo + "' and idalmacen='" + idalmacen + "' and clasificacion='" + id_clf + "' ", null);
            db.close();
            validar = true;

        } catch (SQLiteException e) {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar = false;
        }
        //return validar;
    }

    public void setConteo(String codigo, String idalmacen, Context contexto, String conteo) {
        boolean validar;
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("conteo", conteo);
            db.update("conteoclf", r, "codigo='" + codigo + "' and idalmacen='" + idalmacen + "'  ", null);
            db.close();
            validar = true;

        } catch (SQLiteException e) {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar = false;
        }

    }

    public void setExistencia(String codigo, String idalmacen, Context contexto, Float existencia) {
        boolean validar;
        try {
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("existencia", existencia);
            db.update("conteoclf", r, "codigo='" + codigo + "' and idalmacen='" + idalmacen + "'  ", null);
            db.close();
            validar = true;

        } catch (SQLiteException e) {
            //mensaje="error al insertar articulo:"+e.getMessage();
            validar = false;
        }

    }

    public ArrayList<InventarioModelClf> getConteoCompleto(String idalmacen, String id_clf, Context contexto) {
        try {
            datos = new ArrayList<>();
            //datos.clear();
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM conteoclf where idalmacen='" + idalmacen + "' ", null);
            if (fila.moveToFirst()) {
                do {
                    String codigo = fila.getString(0);
                    String descripcion = fila.getString(1);
                    Float existencia = fila.getFloat(2);
                    datos.add(0, new InventarioModelClf(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5), fila.getString(6), id_clf));

                } while (fila.moveToNext());

            }
            db.close();

        } catch (SQLException e) {
            Log.e("Error:", "" + e.getMessage());
        }
        //adaptadorConteo=new AdaptadorConteo((Activity) contexto,datos);
        return datos;
    }

    public ArrayList<InventarioModelClf> getConteoDif(String idalmacen, String id_clf, Context contexto) {
        try {
            datos = new ArrayList<>();
            //datos.clear();
            Database admin = new Database(contexto, null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM conteoclf where idalmacen='" + idalmacen + "' and clasificacion='"+id_clf+"' and diferencia!=0   ", null);
            if (fila.moveToFirst()) {
                do {
                    String codigo = fila.getString(0);
                    String descripcion = fila.getString(1);
                    Float existencia = fila.getFloat(2);
                    datos.add(0, new InventarioModelClf(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5), fila.getString(6), id_clf));

                } while (fila.moveToNext());

            }

            db.close();

        } catch (SQLException e) {
            Log.e("Error:", "" + e.getMessage());
        }

        return datos;
    }

}