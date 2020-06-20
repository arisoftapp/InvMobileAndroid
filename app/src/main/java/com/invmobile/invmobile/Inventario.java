package com.invmobile.invmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.invmobile.invmobile.Modelo.InventarioModel;
import com.invmobile.invmobile.tools.Complementos;
import com.invmobile.invmobile.tools.ConsultaAlmacenes;
import com.invmobile.invmobile.tools.ConsultasConteo;
import com.invmobile.invmobile.tools.ConsultasSinConteo;
import com.invmobile.invmobile.tools.ConsultasUsuario;
import com.invmobile.invmobile.tools.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Inventario extends AppCompatActivity {
    TextView tv_alm,tv_cod_edit,tv_desc_edit,tv_exi_edit;
    Complementos complementos;
    ConsultasUsuario consultasUsuario;
    ConsultaAlmacenes consultaAlmacenes;
    ConsultasConteo consultasConteo;
    ConsultasSinConteo consultasSinConteo;
    Context contexto;
    String idalmacen;
    EditText et_conteo,et_cant,et_conteo_edit;
    String ruta,mensaje;
    int timeout,opc;
    ListView lista_conteo;
    AdaptadorConteo adaptadorConteo;
    CheckBox cb_cant,cb_busqueda;
    ArrayList<InventarioModel>lista;
    AlertDialog dialog;
    View vista;
    LayoutInflater inflater;
    ImageView btn_menos,btn_mas;
    Button btn_act;
    RadioButton rb_completo,rb_conteo,rb_diferencias;
    LinearLayout ll_busqueda;
    MediaPlayer mp,mpError;

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
        cb_busqueda = (CheckBox) findViewById(R.id.cb_buscar);
        consultaAlmacenes=new ConsultaAlmacenes();
        complementos=new Complementos();
        consultasUsuario=new ConsultasUsuario();
        consultasConteo=new ConsultasConteo();
        consultasSinConteo=new ConsultasSinConteo();
        idalmacen=consultasUsuario.getAlmacenSeleccionado(contexto);
        ll_busqueda=findViewById(R.id.ll_busqueda);
        tv_alm.setText(idalmacen+"-"+consultaAlmacenes.getAlmacen(contexto,idalmacen));
        ruta=complementos.getUrl()+consultasUsuario.getDominio(contexto)+"/";
        timeout=complementos.getTimeout();
        mp=MediaPlayer.create(this,R.raw.beepmicro);
        mpError=MediaPlayer.create(this,R.raw.error);
        actualizarLista();
        //lista_conteo.setClickable(true);
        lista_conteo.setLongClickable(true);

        lista_conteo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(consultasConteo.buscarArticulo(lista.get(i).getCodigoArticulo(),idalmacen,contexto))
                {
                    if(lista.get(i).getSerie().equalsIgnoreCase("N"))
                    {
                        inflater = Inventario.this.getLayoutInflater();
                        vista = inflater.inflate(R.layout.dialog_editar_conteo, null);
                        tv_cod_edit=vista.findViewById(R.id.tv_cod);
                        tv_desc_edit=vista.findViewById(R.id.tv_desc_edit);
                        et_conteo_edit=vista.findViewById(R.id.et_conteo_edit);
                        btn_menos=vista.findViewById(R.id.btn_menos);
                        btn_mas=vista.findViewById(R.id.btn_mas);
                        tv_exi_edit=vista.findViewById(R.id.tv_exi_edit);
                        btn_act=vista.findViewById(R.id.btn_act);

                        btn_act.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //new actualizarSoloExistencia().execute(tv_cod_edit.getText().toString());
                                String resultadoAsynctask = null;
                                try {
                                    resultadoAsynctask = new actualizarSoloExistencia().execute(tv_cod_edit.getText().toString()).get();
                                    tv_exi_edit.setText(resultadoAsynctask);

                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                //complementos.mensajes("despues de consulta "+resultadoAsynctask,contexto);
                            }
                        });
                        btn_mas.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v) {
                                Float cont=Float.parseFloat(et_conteo_edit.getText().toString())+1;
                                et_conteo_edit.setText(""+cont);

                            }
                        });
                        btn_menos.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View v) {

                                Float cont=Float.parseFloat(et_conteo_edit.getText().toString());
                                if(cont>0)
                                {
                                    cont=cont-1;
                                    et_conteo_edit.setText(""+cont);
                                }


                            }
                        });
                        tv_exi_edit.setText(lista.get(i).getExistencia().toString());
                        et_conteo_edit.setText(lista.get(i).getConteo().toString());
                        tv_cod_edit.setText(lista.get(i).getCodigoArticulo());
                        tv_desc_edit.setText(lista.get(i).getDescripcion().toString());
                        final String codigo=lista.get(i).getCodigoArticulo();
                        final Float existencia=Float.parseFloat(lista.get(i).getExistencia().toString());
                        dialog=new AlertDialog.Builder(contexto)
                                .setTitle("Editar")
                                .setView(vista)
                                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Float cont=Float.parseFloat(et_conteo_edit.getText().toString());
                                        Float diferencia=existencia-cont;
                                        consultasConteo.actualizarConteo(codigo,cont,existencia,idalmacen,diferencia,contexto);
                                        actualizarLista();
                                        //complementos.mensajes("guardar "+codigo+" "+cont+" "+existencia+" "+idalmacen+" "+diferencia,contexto);
                                    }
                                })
                                .setNegativeButton("Cancelar",null)
                                .setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        consultasConteo.eliminarCodigoConteo(codigo,idalmacen,contexto);
                                        actualizarLista();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(),SeriesActivity.class);
                        intent.putExtra("codigo",lista.get(i).getCodigoArticulo());
                        startActivity(intent);
                    }
                }





            }
        });
        lista_conteo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                complementos.mensajes("long click"+lista.get(i).getCodigoArticulo(),contexto);
                inflater = Inventario.this.getLayoutInflater();
                vista = inflater.inflate(R.layout.dialog_comentarios, null);
                final TextView tvcodigo = vista.findViewById(R.id.tv_cod);
                final TextView tvdesc = vista.findViewById(R.id.tv_desc);
                final EditText coment=vista.findViewById(R.id.comentarios);
                final ImageButton ibLimpiar=vista.findViewById(R.id.ib_limpiar);
                tvcodigo.setText(lista.get(i).getCodigoArticulo());
                tvdesc.setText(lista.get(i).getDescripcion());
                if(consultasConteo.buscarArticulo(tvcodigo.getText().toString(),idalmacen,contexto))
                {
                    coment.setText(consultasConteo.getComentarios(lista.get(i).getCodigoArticulo(),idalmacen,contexto));
                }
                else if(consultasSinConteo.buscarArticulo(tvcodigo.getText().toString(),idalmacen,contexto))
                {
                    coment.setText(consultasSinConteo.getComentarios(lista.get(i).getCodigoArticulo(),idalmacen,contexto));
                }

                ibLimpiar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        coment.setText("");
                    }
                });


                AlertDialog dialog = new AlertDialog.Builder(Inventario.this)
                        .setTitle("Comentarios")
                        .setView(vista)
                        .setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("comentarios", "SALIR DE POSITIVE BUTTON");
                                if(consultasConteo.buscarArticulo(tvcodigo.getText().toString(),idalmacen,contexto))
                                {
                                    consultasConteo.setComentarios(tvcodigo.getText().toString(),idalmacen,coment.getText().toString(),contexto);
                                }
                                else if(consultasSinConteo.buscarArticulo(tvcodigo.getText().toString(),idalmacen,contexto))
                                {
                                    complementos.mensajes("guardar en sinconteo",contexto);
                                    consultasSinConteo.setComentarios(tvcodigo.getText().toString(),idalmacen,coment.getText().toString(),contexto);
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create();
                dialog.show();

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

                    if(consultasConteo.getTablaVaciaAlm(idalmacen,contexto)==true)
                    {
                        complementos.mensajes("Tabla vacia",contexto);
                        //insertar
                        new consultaArticulo().execute(codigo.getText().toString().trim(),cantConteo);

                    }
                    else
                    {
                        if(consultasConteo.buscarArticulo(codigo.getText().toString().trim(),idalmacen,contexto)==true)
                        {

                            //actualizar
                            //buscar codigo1-2
                            complementos.mensajes("actualizar "+codigo.getText().toString().trim()+cantConteo,contexto);
                            if(consultasConteo.codigo2(codigo.getText().toString().trim(),idalmacen,contexto))
                            {
                                //si es codigo2
                                complementos.mensajes("codigo 2 ",contexto);
                                new actualizarArticulo().execute(consultasConteo.getCodigo1(codigo.getText().toString().trim(),idalmacen,contexto),cantConteo);
                            }
                            else
                            {

                                complementos.mensajes("codigo 1",contexto);
                                new actualizarArticulo().execute(codigo.getText().toString().trim(),cantConteo);
                            }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filtro) {
            //complementos.mensajes("filtrar",contexto);
            inflater = Inventario.this.getLayoutInflater();
            vista = inflater.inflate(R.layout.dialog_opc_dif, null);
            rb_completo=vista.findViewById(R.id.rb_completo);
            rb_conteo=vista.findViewById(R.id.rb_conteo);
            rb_diferencias=vista.findViewById(R.id.rb_diferencias);
            rb_completo.setChecked(true);



            dialog=new AlertDialog.Builder(contexto)
                    .setView(vista)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(rb_completo.isChecked())
                            {
                                et_conteo.setVisibility(View.GONE);
                                ll_busqueda.setVisibility(View.GONE);
                                lista=consultasConteo.getConteoCompleto(idalmacen,contexto);
                                cambiarOpciones();
                            }
                            if(rb_conteo.isChecked())
                            {

                            }
                            if(rb_diferencias.isChecked())
                            {
                                et_conteo.setVisibility(View.GONE);
                                ll_busqueda.setVisibility(View.GONE);
                                lista=consultasConteo.getConteoCompletoSD(idalmacen,contexto);
                                cambiarOpciones();
                                lista_conteo.setClickable(false);
                                complementos.mensajes("sin conteo",contexto);
                            }

                        }
                    })
                    .setNegativeButton("Continuar Conteo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            et_conteo.setVisibility(View.VISIBLE);
                            ll_busqueda.setVisibility(View.VISIBLE);
                            lista=consultasConteo.getSoloConteo(idalmacen,contexto);
                            cambiarOpciones();
                            lista_conteo.setClickable(true);
                            complementos.mensajes(" conteo",contexto);
                        }
                    })
                    .create();
            dialog.show();
            //new sinConteo().execute();
            return true;
        }

        if (id == R.id.actualizar) {
            complementos.mensajes("consultar sin conteo",contexto);
            consultasSinConteo.eliminarSinConteo(idalmacen,contexto);
            new sinConteo().execute();
            //consultasSinConteo.getSinConteo(idalmacen,contexto);
            return true;
        }
        if(id==R.id.action_guardar)
        {
            if(consultasSinConteo.getTablaVacia(contexto))
            {
                complementos.mensajes("Falta consultar articulos sin conteo",contexto);
            }
            else
            {
                //complementos.mensajes("guardar",contexto);
                //crearJson();
                //crearJson2();
                new guardarConteo().execute();
            }

        }
        return super.onOptionsItemSelected(item);
    }
    public void cambiarOpciones()
    {
        adaptadorConteo=new AdaptadorConteo(this,lista);
        lista_conteo.setAdapter(adaptadorConteo);
    }
    public void actualizarLista()
    {
        lista=new ArrayList<InventarioModel>();
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,descripcion,existencia,conteo,diferencia,serie,comentarios FROM conteo where idalmacen='"+idalmacen+"' ",null);
            if(fila.moveToFirst())
            {
                do {
                    String codigo = fila.getString(0);
                    String descripcion = fila.getString(1);
                    Float existencia = fila.getFloat(2);
                    lista.add(0, new InventarioModel(fila.getString(0), fila.getString(1), fila.getString(2), fila.getString(3), fila.getString(4), fila.getString(5),fila.getString(6)));
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

    @Override
    protected void onStart() {
        Log.i("ciclo","onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.i("ciclo","onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("ciclo","onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.i("ciclo","onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.i("ciclo","onResume");
        actualizarLista();
        super.onResume();
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
                            codigo=objeto.getString("codigo");
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
                            if(consultasSinConteo.getTablaVacia(contexto)==false)
                            {
                                consultasSinConteo.eliminarCodigoSinConteo(codigo,idalmacen,contexto);
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
                mp.start();

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
                mpError.start();
                complementos.mensajes("Consultar Articulo de nuevo",contexto);
            }
            if(cb_cant.isChecked())
            {
                et_cant.setText("1");
                cb_cant.setChecked(false);
                et_cant.setEnabled(false);
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
                Log.i("pruebaactualizar"," | "+conteo+" | "+codigo);
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
                            //codigo=objeto.getString("codigo");
                            Log.i("pruebaactualizar"," | "+codigo+" | "+serie+" | "+objeto.getString("existenciaActual"));
                            existencia=Float.parseFloat(objeto.getString("existenciaActual"));

                            Log.i("pruebaactualizar"," | "+codigo+" | "+serie);
                            if(serie.equalsIgnoreCase("S"))
                            {
                                //consultasConteo.insertarConteo(codigo,codigo2,descripcion,conteo,existencia,idalmacen,serie,diferencia,contexto);
                            }
                            else
                            {
                                conteo=conteo+Float.valueOf(consultasConteo.getConteo(codigo,idalmacen,contexto)) ;
                                diferencia=existencia-conteo;
                                //Log.i("pruebaactualizar"," | "+codigo);
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
                mp.start();
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
                mpError.start();
                complementos.mensajes("Consultar Articulo de nuevo",contexto);
            }
            if(cb_cant.isChecked())
            {
                et_cant.setText("1");
                cb_cant.setChecked(false);
                et_cant.setEnabled(false);
            }

            super.onPostExecute(s);
        }
    }

    class actualizarSoloExistencia extends AsyncTask<String,Integer,String>
    {
        String validar;
        String serie;
        String codigo;
        Float existencia;
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
                            existencia=Float.parseFloat(objeto.getString("existenciaActual"));
                            consultasConteo.setExistencia(codigo,idalmacen,contexto,existencia);

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
            return ""+existencia;

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
            super.onPostExecute(s);
        }
    }

    class sinConteo extends AsyncTask<String,Integer,String>
    {
        String validar;
        String serie;
        String codigo;
        Float existencia;
        @Override
        protected void onPreExecute()
        {
            complementos.inciar_barra_progreso_spinner(contexto,"Consultando Articulo Sin conteo");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            try {

                URL url = new URL(ruta+"existenciatotal/"+idalmacen); //in the real code, there is an ip and a port
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

                            codigo=objeto.getString("codigo");

                            if( consultasConteo.buscarArticulo(codigo,idalmacen,contexto)==false)
                            {
                                String descripcion=objeto.getString("descripcion");
                                String codigo2=objeto.getString("codigo2");
                                serie=objeto.getString("serie");
                                Float existenciaActual=Float.parseFloat(objeto.getString("existenciaActual")) ;
                                Float conteo= Float.valueOf(0);
                                Float diferencia=existenciaActual;
                                //Log.i("peticion"," | "+objeto.getString("codigo"));
                                consultasSinConteo.insertarSinConteo(codigo,codigo2,descripcion,conteo,existenciaActual,idalmacen,serie,diferencia,contexto);

                            }

                            //existencia=Float.parseFloat(objeto.getString("existenciaActual"));
                            //consultasConteo.setExistencia(codigo,idalmacen,contexto,existencia);

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
            return ""+existencia;

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
            //actualizarLista();
            super.onPostExecute(s);
        }
    }
    public JSONObject crearJson()
    {
        JSONArray conteo = new JSONArray();
        JSONArray series = new JSONArray();
        for (int i=0; i <   consultasConteo.getConteoCompleto(idalmacen,contexto).size(); i++) {

            JSONObject producto = new JSONObject();
            try {
                producto.put("cod_prod", consultasConteo.getConteoCompleto(idalmacen,contexto).get(i).getCodigoArticulo());
                producto.put("descripcion", consultasConteo.getConteoCompleto(idalmacen,contexto).get(i).getDescripcion());
                producto.put("existencia", consultasConteo.getConteoCompleto(idalmacen,contexto).get(i).getExistencia());
                producto.put("conteo", consultasConteo.getConteoCompleto(idalmacen,contexto).get(i).getConteo());
                producto.put("diferencia", consultasConteo.getConteoCompleto(idalmacen,contexto).get(i).getDiferencia());
                producto.put("es_series", consultasConteo.getConteoCompleto(idalmacen,contexto).get(i).getSerie());
                producto.put("comentario",consultasConteo.getConteoCompleto(idalmacen,contexto).get(i).getComentarios());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            conteo.put(producto);

        }
        //Log.i("PARAMETROS", "" + conteo);
        Database admin = new Database(this.getApplicationContext(),null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor fila = db.rawQuery("SELECT * FROM series WHERE almacen = '" + idalmacen+"'" ,null);
        if(fila.moveToFirst()) {
            do{
                JSONObject serie = new JSONObject();
                try {
                    serie.put("cod_prod", fila.getString(2));
                    serie.put("serie", fila.getString(1));
                    serie.put("estatus", fila.getString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                series.put(serie);

            }while (fila.moveToNext());
        }
        else {
            //Toast.makeText(this, "Almacen sin conteo", Toast.LENGTH_SHORT).show();
        }
        db.close();
        final JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("alm", idalmacen);
            jsonParams.put("nombre_alm", consultaAlmacenes.getAlmacen(contexto,idalmacen));
            jsonParams.put("params", conteo);
            jsonParams.put("series", series);
            jsonParams.put("idEmpresa",consultasUsuario.getIdEmpresa(contexto));
            jsonParams.put("idUsuario",consultasUsuario.getUsuario(contexto));


        }catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("PARAMETROS", "" + jsonParams);
        return jsonParams;
    }
    public JSONObject crearJson2()
    {
        ArrayList<InventarioModel>listajson=consultasConteo.getConteoCompleto(idalmacen,contexto);
        JSONArray conteo = new JSONArray();
        JSONArray series = new JSONArray();

        for (int i=0; i <   consultasConteo.getConteoCompleto(idalmacen,contexto).size(); i++) {

            JSONObject producto = new JSONObject();
            try {
                producto.put("cod_prod", listajson.get(i).getCodigoArticulo());
                producto.put("descripcion", listajson.get(i).getDescripcion());
                producto.put("existencia", listajson.get(i).getExistencia());
                producto.put("conteo", listajson.get(i).getConteo());
                producto.put("diferencia",listajson.get(i).getDiferencia());
                producto.put("es_series", listajson.get(i).getSerie());
                producto.put("comentario",listajson.get(i).getComentarios());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            conteo.put(producto);

        }
        //Log.i("PARAMETROS", "" + conteo);
        Database admin = new Database(this.getApplicationContext(),null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor fila = db.rawQuery("SELECT * FROM series WHERE almacen = '" + idalmacen+"'" ,null);
        if(fila.moveToFirst()) {
            do{
                JSONObject serie = new JSONObject();
                try {
                    serie.put("cod_prod", fila.getString(2));
                    serie.put("serie", fila.getString(1));
                    serie.put("estatus", fila.getString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                series.put(serie);

            }while (fila.moveToNext());
        }
        else {
            //Toast.makeText(this, "Almacen sin conteo", Toast.LENGTH_SHORT).show();
        }
        db.close();
        final JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("alm", idalmacen);
            jsonParams.put("nombre_alm", consultaAlmacenes.getAlmacen(contexto,idalmacen));
            jsonParams.put("params", conteo);
            jsonParams.put("series", series);
            jsonParams.put("idEmpresa",consultasUsuario.getIdEmpresa(contexto));
            jsonParams.put("idUsuario",consultasUsuario.getUsuario(contexto));


        }catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("PARAMETROS", "" + jsonParams);
        return jsonParams;
    }
    class guardarConteo extends AsyncTask<String,Integer,String>
    {
        String validar;
        String idConteo="";
        @Override
        protected void onPreExecute()
        {
            complementos.inciar_barra_progreso_spinner(contexto,"Guardando Datos");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            //Log.i("CONTEOGUARDADO"," " +crearJson());
            try{
                URL url = new URL(ruta+"conteo"); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                bw.write(crearJson2().toString());
                bw.flush();
                bw.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                //String status=String.valueOf(conn.getResponseCode());
                String finalJSON = sb.toString();
                int status = conn.getResponseCode();
                JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                mensaje=jObject.getString("message");
                //Log.i("CONTEOGUARDADO"," " +mensaje+"   todo "+jObject.toString());
                if(status<400)
                {
                    validar="TRUE";
                    //String finalJSON = sb.toString();
                    //JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                    //mensaje=jObject.getString("message");
                    if(jObject.getBoolean("success")==true)
                    {
                        idConteo=jObject.getString("conteo_id");
                        Log.i("CONTEOGUARDADO"," " + jObject.getString("conteo_id")+" "+jObject.getString("almacen_id"));
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
                }
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
        }
        protected void onPostExecute(String s)
        {

            complementos.cerrar_barra_progreso();
            complementos.mensajes(""+mensaje,contexto);
            if(s.equalsIgnoreCase("TRUE"))
            {
                complementos.mensajes("ID CONTEO:"+idConteo+" ",contexto);
                //Intent i=new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(i);
                //finish();
            }
            else
            {

            }
            super.onPostExecute(s);
        }
    }
}
