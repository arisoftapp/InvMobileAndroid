package com.invmobile.invmobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.invmobile.invmobile.Modelo.InventarioModel;
import com.invmobile.invmobile.tools.Complementos;
import com.invmobile.invmobile.tools.ConsultaAlmacenes;
import com.invmobile.invmobile.tools.ConsultasConteo;
import com.invmobile.invmobile.tools.ConsultasUsuario;
import com.invmobile.invmobile.tools.Database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Inventario extends AppCompatActivity {
    TextView tv_alm;
    Complementos complementos;
    ConsultasUsuario consultasUsuario;
    ConsultaAlmacenes consultaAlmacenes;
    ConsultasConteo consultasConteo;
    Context contexto;
    String idalmacen;
    EditText et_conteo;
    String ruta,mensaje;
    int timeout;
    ListView lista_conteo;
    AdaptadorConteo adaptadorConteo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        //inicializando
        contexto=this;
        tv_alm=(TextView)findViewById(R.id.tv_alm);
        et_conteo=(EditText)findViewById(R.id.et_conteo);
        lista_conteo=(ListView)findViewById(R.id.lista_conteo);
        consultaAlmacenes=new ConsultaAlmacenes();
        complementos=new Complementos();
        consultasUsuario=new ConsultasUsuario();
        consultasConteo=new ConsultasConteo();
        idalmacen=consultasUsuario.getAlmacenSeleccionado(contexto);
        tv_alm.setText(idalmacen+"-"+consultaAlmacenes.getAlmacen(contexto,idalmacen));
        ruta=complementos.getUrl()+consultasUsuario.getDominio(contexto)+"/";
        timeout=complementos.getTimeout();
        actualizarLista();


        et_conteo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView codigo, int i, KeyEvent keyEvent) {
                //complementos.mensajes("ingrese codigo"+textView.getText(),contexto);
                if(codigo.getText().toString().trim().isEmpty())
                {
                    complementos.mensajes("Ingrese Codigo",contexto);
                }
                else
                {

                    if(consultasConteo.getTablaVacia(contexto)==true)
                    {
                        //complementos.mensajes("Tabla vacia",contexto);
                        //insertar
                        new consultaArticulo().execute(codigo.getText().toString().trim(),"INSERTAR","1");
                    }
                    else
                    {
                        if(consultasConteo.buscarArticulo(codigo.getText().toString().trim(),idalmacen,contexto)==true)
                        {
                            //actualizar
                        }
                        else
                        {
                            //insertar
                            new consultaArticulo().execute(codigo.getText().toString().trim(),"INSERTAR","1");
                        }
                    }

                }
                return false;
            }
        });

    }
    public void actualizarLista()
    {
        ArrayList<InventarioModel>lista=new ArrayList<>();
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie FROM conteo",null);
            if(fila.moveToFirst())
            {
                do {
                    String codigo = fila.getString(0);
                    String descripcion = fila.getString(1);
                    Float existencia = fila.getFloat(2);

                    lista.add(0, new InventarioModel(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5)));
                    Log.i("consultausuario", " | " + fila.getString(0));
                }while (fila.moveToNext());
            }

            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        //lista.add(0,new InventarioModel("01","desakjs","5","2","3","S") );
        adaptadorConteo=new AdaptadorConteo(this,lista);
        lista_conteo.setAdapter(adaptadorConteo);
    }

    public void pruebas(View view) {
        //actualizarLista();
    }

    class consultaArticulo extends AsyncTask<String,Integer,String>
    {
        String validar;
        String serie;
        @Override
        protected void onPreExecute()
        {
            complementos.inciar_barra_progreso_spinner(contexto,"Consultando Articulo");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            try {
                String codigo=params[0];
                Float conteo=Float.parseFloat(params[2]),existencia,diferencia;

                boolean insertar;
                if(params[1].toString().equalsIgnoreCase("INSERTAR"))
                {
                    insertar=true;
                }
                else
                {
                    insertar=false;
                }
                URL url = new URL(ruta+"conteo/"+idalmacen+"/"+codigo); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                int status=conn.getResponseCode();
                if(status<400)
                {
                    validar="TRUE";
                    String finalJSON = sb.toString();
                    JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                    mensaje=jObject.getString("mensaje");
                    if(jObject.getBoolean("success")==true)
                    {
                        JSONArray jArray = jObject.getJSONArray("datos");
                        //complementos.setMax(jArray.length());
                        for (int i=0;i<jArray.length();i++)
                        {
                            //publishProgress(i+1);
                            JSONObject objeto = jArray.getJSONObject(i);

                            if(insertar==true)
                            {
                                existencia=Float.parseFloat(objeto.getString("existenciaActual"));
                                diferencia=existencia-conteo;
                                serie=objeto.getString("serie");

                                try{
                                    Database admin=new Database(contexto,null,1);
                                    SQLiteDatabase db = admin.getWritableDatabase();
                                    ContentValues r = new ContentValues();
                                    r.put("codigo",objeto.getString("codigo"));
                                    r.put("codigo2",objeto.getString("codigo2"));
                                    r.put("descripcion",objeto.getString("descripcion"));
                                    r.put("conteo",conteo);
                                    r.put("existencia",existencia);
                                    r.put("idalmacen",idalmacen);
                                    r.put("serie",objeto.getString("serie"));
                                    r.put("diferencia",diferencia);
                                    r.put("estatus","");
                                    r.put("comentarios","");
                                    db.insert("conteo",null,r);
                                    db.close();

                                }catch (SQLiteException e)
                                {
                                    mensaje="error al insertar articulo:"+e.getMessage();
                                    validar="FALSE";
                                }
                            }
                            else
                            {

                            }
                            /*
                            try{
                                Database admin=new Database(contexto,null,1);
                                SQLiteDatabase db = admin.getWritableDatabase();
                                ContentValues r = new ContentValues();
                                r.put("id_almacen",objeto.getString("idalmacen"));
                                r.put("almacen",objeto.getString("almacen"));
                                db.insert("almacenes",null,r);
                                db.close();

                            }catch (SQLiteException e)
                            {
                                mensaje="error al insertar almacenes:"+e.getMessage();
                                validar="FALSE";
                            }

                             */

                        }
                        Log.i("peticion"," | "+jObject.toString());
                        Log.i("peticion"," | "+jArray.toString());
                    }
                    else
                    {
                        validar="FALSE";
                    }
                    br.close();

                }
                else
                {
                    validar="FALSE";
                    mensaje=conn.getResponseMessage();
                }

                Log.i("peticion"," | "+status);
                conn.disconnect();
            }
            catch (Exception e)
            {
                validar="FALSE";
                mensaje=e.getMessage();
            }
            return validar;

        }
        protected void onProgressUpdate(Integer... i)
        {
            //progreso.setProgress(i[0]);
            complementos.setProgreso(i[0]);
        }
        protected void onPostExecute(String s)
        {

            complementos.cerrar_barra_progreso();
            complementos.mensajes(mensaje,contexto);
            actualizarLista();
            if(s.equalsIgnoreCase("TRUE"))
            {

                if(serie.equalsIgnoreCase("S"))
                {
                    et_conteo.setText("");
                }
                else
                {

                }
            }
            else
            {
                complementos.mensajes("Consultar Articulo de nuevo",contexto);
            }

            super.onPostExecute(s);
        }
    }


}
