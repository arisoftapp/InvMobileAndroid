package com.invmobile.invmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
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
    EditText et_conteo,et_cant;
    String ruta,mensaje;
    int timeout;
    ListView lista_conteo;
    AdaptadorConteo adaptadorConteo;
    CheckBox cb_cant;
    ArrayList<InventarioModel>lista;
    AlertDialog dialog;
    View vista;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);
        //inicializando
        contexto=this;
        tv_alm=(TextView)findViewById(R.id.tv_alm);
        et_conteo=(EditText)findViewById(R.id.et_conteo);
        lista_conteo=(ListView)findViewById(R.id.lista_conteo);
        et_cant=(EditText)findViewById(R.id.et_cant);
        cb_cant=(CheckBox)findViewById(R.id.cb_cant);
        consultaAlmacenes=new ConsultaAlmacenes();
        complementos=new Complementos();
        consultasUsuario=new ConsultasUsuario();
        consultasConteo=new ConsultasConteo();
        idalmacen=consultasUsuario.getAlmacenSeleccionado(contexto);
        tv_alm.setText(idalmacen+"-"+consultaAlmacenes.getAlmacen(contexto,idalmacen));
        ruta=complementos.getUrl()+consultasUsuario.getDominio(contexto)+"/";
        timeout=complementos.getTimeout();
        actualizarLista();
        //lista_conteo.setClickable(true);
        lista_conteo.setLongClickable(true);
        lista_conteo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                complementos.mensajes(""+lista.get(i).getCodigoArticulo(),contexto);
                inflater = Inventario.this.getLayoutInflater();
                vista = inflater.inflate(R.layout.dialog_editar_conteo, null);
                dialog=new AlertDialog.Builder(contexto)
                        .setTitle("Editar")
                        .setView(vista)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("Cancelar",null)
                        .create();
                dialog.show();
            }
        });
        lista_conteo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                complementos.mensajes("long click"+lista.get(i).getCodigoArticulo(),contexto);
                return true;
            }
        });


        et_conteo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView codigo, int i, KeyEvent keyEvent) {
                //complementos.mensajes("ingrese codigo"+textView.getText(),contexto);
                String cantConteo="1";
                if(cb_cant.isChecked())
                {
                    if(et_cant.getText().toString().isEmpty())
                    {
                        cantConteo="1";
                    }
                    else
                    {
                        cantConteo=et_cant.getText().toString();
                    }

                }
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
                        new consultaArticulo().execute(codigo.getText().toString().trim(),cantConteo);
                    }
                    else
                    {
                        if(consultasConteo.buscarArticulo(codigo.getText().toString().trim(),idalmacen,contexto)==true)
                        {
                            //actualizar
                            new actualizarArticulo().execute(codigo.getText().toString().trim(),cantConteo);
                        }
                        else
                        {
                            //insertar
                            new consultaArticulo().execute(codigo.getText().toString().trim(),cantConteo);
                        }
                    }

                }
                return false;
            }
        });

    }
    public void actualizarLista()
    {
        lista=new ArrayList<>();
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie FROM conteo where idalmacen='"+idalmacen+"' ",null);
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

    public void onCheckboxClicked(View view) {
        if(cb_cant.isChecked())
        {
            et_cant.setEnabled(true);
        }
        else
        {
            et_cant.setText("1");
            et_cant.setEnabled(false);
        }
    }

    class consultaArticulo extends AsyncTask<String,Integer,String>
    {
        String validar;
        String serie;
        String codigo;
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
                codigo=params[0];
                String codigo2,descripcion;
                Float conteo=Float.parseFloat(params[1]),existencia,diferencia;

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
                            serie=objeto.getString("serie");
                            existencia=Float.parseFloat(objeto.getString("existenciaActual"));
                            diferencia=existencia-conteo;
                            codigo2=objeto.getString("codigo2");
                            descripcion=objeto.getString("descripcion");
                            if(serie.equalsIgnoreCase("S"))
                            {
                                conteo=Float.valueOf(0);
                                existencia=Float.valueOf(0);
                                diferencia=Float.valueOf(0);
                                consultasConteo.insertarConteo(codigo,codigo2,descripcion,conteo,existencia,idalmacen,serie,diferencia,contexto);
                            }
                            else
                            {
                                consultasConteo.insertarConteo(codigo,codigo2,descripcion,conteo,existencia,idalmacen,serie,diferencia,contexto);
                            }

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
            et_conteo.setText("");
            actualizarLista();
            if(s.equalsIgnoreCase("TRUE"))
            {

                if(serie.equalsIgnoreCase("S"))
                {
                    complementos.mensajes("Articulo con series",contexto);
                    Intent i = new Intent(getApplicationContext(),SeriesActivity.class);
                    i.putExtra("codigo",codigo);
                    startActivity(i);
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

    class actualizarArticulo extends AsyncTask<String,Integer,String>
    {
        String validar;
        String serie;
        String codigo;
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
                codigo=params[0];
                Float conteo=Float.parseFloat(params[1]),existencia,diferencia;

                URL url = new URL(ruta+"soloexistencia/"+idalmacen+"/"+codigo); //in the real code, there is an ip and a port
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
                            serie=objeto.getString("serie");
                            existencia=Float.parseFloat(objeto.getString("existenciaActual"));

                            if(serie.equalsIgnoreCase("S"))
                            {
                                //consultasConteo.insertarConteo(codigo,codigo2,descripcion,conteo,existencia,idalmacen,serie,diferencia,contexto);
                            }
                            else
                            {
                                conteo=conteo+Float.valueOf(consultasConteo.getConteo(codigo,idalmacen,contexto)) ;
                                diferencia=existencia-conteo;
                                consultasConteo.actualizarConteo(codigo,conteo,existencia,idalmacen,diferencia,contexto);
                                //consultasConteo.insertarConteo(codigo,codigo2,descripcion,conteo,existencia,idalmacen,serie,diferencia,contexto);
                            }

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
            et_conteo.setText("");
            actualizarLista();
            if(s.equalsIgnoreCase("TRUE"))
            {

                if(serie.equalsIgnoreCase("S"))
                {
                    complementos.mensajes("Articulo con series",contexto);
                    Intent i = new Intent(getApplicationContext(),SeriesActivity.class);
                    i.putExtra("codigo",codigo);
                    startActivity(i);
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
