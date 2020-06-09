package com.invmobile.invmobile;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.invmobile.invmobile.Modelo.SeriesModel;
import com.invmobile.invmobile.Modelo.SeriesModelClf;
import com.invmobile.invmobile.tools.Complementos;
import com.invmobile.invmobile.tools.ConsultaSeries;
import com.invmobile.invmobile.tools.ConsultaSeriesCLF;
import com.invmobile.invmobile.tools.ConsultasConteo;
import com.invmobile.invmobile.tools.ConsultasConteoClf;
import com.invmobile.invmobile.tools.ConsultasUsuario;
import com.invmobile.invmobile.tools.Database;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SeriesActivityClf extends AppCompatActivity {
    Complementos complementos;
    ConsultasUsuario consultasUsuario;
    ConsultasConteoClf consultasConteoClf;
    ConsultaSeriesCLF consultaSeriesClf;
    Context contexto;
    String codigo,idalmacen,ruta,mensaje,id_clf;
    TextView tv_codigo,tv_existencia,tv_conteo,tv_diferencia,tv_descripcion,tv_edit_serie,tv_edit_estatus;
    EditText et_serie;
    ListView lv_series;
    ArrayList<SeriesModel>lista;
    AdaptadorSeries adaptadorSeries;
    LayoutInflater inflater;
    View vista;
    AlertDialog dialog;
    Spinner sp_filtro_serie;
    Button btn_edit_estatus;
    int timeout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series_clf);
        complementos=new Complementos();
        consultasUsuario=new ConsultasUsuario();
        consultasConteoClf=new ConsultasConteoClf();
        consultaSeriesClf=new ConsultaSeriesCLF();
        contexto=this;
        Bundle bundle = this.getIntent().getExtras();
        codigo = bundle.getString("codigo");
        idalmacen=consultasUsuario.getAlmacenSeleccionado(contexto);
        id_clf=consultasUsuario.getClfSeleccionado(contexto);
        tv_codigo=(TextView)findViewById(R.id.tv_codigo);
        tv_existencia=(TextView)findViewById(R.id.tv_existencia);
        tv_conteo=(TextView)findViewById(R.id.tv_conteo);
        tv_diferencia=(TextView)findViewById(R.id.tv_diferencia);
        tv_descripcion=(TextView)findViewById(R.id.tv_descripcion);
        et_serie=(EditText)findViewById(R.id.et_serie);
        lv_series=(ListView)findViewById(R.id.lv_series);

        tv_codigo.setText(codigo);

        tv_descripcion.setText(consultasConteoClf.getDescripcion(codigo,idalmacen,id_clf,contexto));
        ruta=complementos.getUrl()+consultasUsuario.getDominio(contexto)+"/";
        timeout=complementos.getTimeout();

        if(consultaSeriesClf.getVerificarSeries(codigo,contexto)==true)
        {
            //tabla de series vacia
            new consultaSeries().execute();
        }
        else
        {

        }
        actualizarHeader();
        actualizarLista();
        et_serie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(et_serie.getText().toString().equalsIgnoreCase(""))
                {
                    complementos.mensajes("Ingrese serie",contexto);
                }
                else
                {
                    if(consultaSeriesClf.buscarSerie(codigo,idalmacen,et_serie.getText().toString().trim(),contexto)==true)
                    {
                        if(consultaSeriesClf.getEstatus(codigo,idalmacen,et_serie.getText().toString().trim(),contexto).equalsIgnoreCase("N"))
                        {
                            complementos.mensajes("si encontro",contexto);
                            consultaSeriesClf.cambiarEstatus(codigo,idalmacen,"D",et_serie.getText().toString().trim(),contexto);
                            float cont,exis,dif;
                            cont=Float.parseFloat(tv_conteo.getText().toString())+1;
                            exis=Float.parseFloat(tv_existencia.getText().toString());
                            dif=exis-cont;
                            consultasConteoClf.actualizarConteo(codigo,cont,idalmacen,dif,id_clf,contexto);
                        }
                        else
                        {
                            complementos.mensajes("ya contado",contexto);
                        }


                    }
                    else
                    {
                        complementos.mensajes("no encontro",contexto);
                        consultaSeriesClf.insertarSerie(et_serie.getText().toString().trim(),codigo,idalmacen,"ND",id_clf,contexto);
                        float cont,exis,dif;
                        cont=Float.parseFloat(tv_conteo.getText().toString())+1;
                        exis=Float.parseFloat(tv_existencia.getText().toString());
                        dif=exis-cont;
                        consultasConteoClf.actualizarConteo(codigo,cont,idalmacen,dif,id_clf,contexto);
                    }
                    et_serie.setText("");
                    actualizarHeader();
                    actualizarLista();
                }



                return false;
            }
        });
        lv_series.setLongClickable(true);
        lv_series.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, final long id) {
                if (lista.get(i).getEstatus().equalsIgnoreCase("N"))
                {

                }
                else
                {
                    complementos.mensajes("click "+  lista.get(i).getEstatus(),contexto);
                    inflater = SeriesActivityClf.this.getLayoutInflater();
                    vista = inflater.inflate(R.layout.dialog_editar_series, null);
                    tv_edit_serie=vista.findViewById(R.id.tv_serie);
                    tv_edit_estatus=vista.findViewById(R.id.tv_estatus);
                    btn_edit_estatus=vista.findViewById(R.id.btn_edit_estatus);
                    tv_edit_estatus.setText(lista.get(i).getEstatus());
                    tv_edit_serie.setText(lista.get(i).getSerie());
                    btn_edit_estatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(tv_edit_estatus.getText().toString().equalsIgnoreCase("ND"))
                            {
                                complementos.mensajes("click estatus eliminar",contexto);
                            }
                            else
                            {
                                if(tv_edit_estatus.getText().toString().equalsIgnoreCase("N"))
                                {

                                    tv_edit_estatus.setText("D");
                                }
                                else
                                {

                                    tv_edit_estatus.setText("N");
                                }
                                complementos.mensajes("click estatus cambiar",contexto);

                            }

                        }
                    });
                    String btn_text="";
                    if(tv_edit_estatus.getText().toString().equalsIgnoreCase("ND"))
                    {
                        btn_text="Eliminar";
                        btn_edit_estatus.setVisibility(View.GONE);
                    }
                    else
                    {
                        btn_text="Aceptar";
                        btn_edit_estatus.setVisibility(View.VISIBLE);
                    }
                    dialog=new AlertDialog.Builder(contexto)
                            .setTitle("Editar")
                            .setView(vista)
                            .setPositiveButton(btn_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if(tv_edit_estatus.getText().toString().equalsIgnoreCase("ND"))
                                    {
                                        consultaSeriesClf.eliminarSerie(tv_edit_serie.getText().toString(),idalmacen,contexto);
                                    }
                                    if(tv_edit_estatus.getText().toString().equalsIgnoreCase("N"))
                                    {
                                        consultaSeriesClf.cambiarEstatus(codigo,idalmacen,"N",tv_edit_serie.getText().toString(),contexto);
                                    }
                                    if(tv_edit_estatus.getText().toString().equalsIgnoreCase("D"))
                                    {
                                        consultaSeriesClf.cambiarEstatus(codigo,idalmacen,"D",tv_edit_serie.getText().toString(),contexto);
                                    }
                                    float contadas=consultaSeriesClf.getSeriesContadas(codigo,idalmacen,contexto);
                                    float exis=Float.parseFloat(consultasConteoClf.getExistencia(codigo,idalmacen,id_clf, contexto)) ;
                                    float dif=exis-contadas;
                                    consultasConteoClf.actualizarConteo(codigo,contadas,idalmacen,dif,id_clf,contexto);
                                    actualizarLista();
                                    actualizarHeader();
                                    //complementos.mensajes("guardar "+codigo+" "+cont+" "+existencia+" "+idalmacen+" "+diferencia,contexto);
                                }
                            })
                            .setNegativeButton("Cancelar",null)

                            .create();
                    dialog.show();
                }

            }
        });

        lv_series.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                complementos.mensajes("long click",contexto);
                return true;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_series_clf, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.actualizar:
                complementos.mensajes("actualizar",contexto);
                new reconsultarSeries().execute();
                return true;
            case R.id.filtro:
                final int[] idfiltro = {0};
                inflater = SeriesActivityClf.this.getLayoutInflater();
                vista = inflater.inflate(R.layout.dialog_filtro_series, null);
                sp_filtro_serie=vista.findViewById(R.id.sp_filtro_serie);
                sp_filtro_serie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //complementos.mensajes(""+position,contexto);
                        idfiltro[0] =position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                final String[] valores = {"Todos","Disponibles","No contados","No disponibles","Contadas"};
                sp_filtro_serie.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, valores));

                dialog=new AlertDialog.Builder(contexto)
                        .setTitle("FILTROS")
                        .setView(vista)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                complementos.mensajes(""+idfiltro[0],contexto);
                                if(idfiltro[0]==0)
                                {
                                    et_serie.setVisibility(View.VISIBLE);
                                    actualizarLista();
                                }
                                if(idfiltro[0]==1)
                                {
                                    actualizarListaPorEstatus("D");
                                    et_serie.setVisibility(View.GONE);
                                }
                                if(idfiltro[0]==2)
                                {
                                    actualizarListaPorEstatus("N");
                                    et_serie.setVisibility(View.GONE);
                                }
                                if(idfiltro[0]==3)
                                {
                                    actualizarListaPorEstatus("ND");
                                    et_serie.setVisibility(View.GONE);
                                }
                                if(idfiltro[0]==4)
                                {
                                    actualizarListaPorEstatus("C");
                                    et_serie.setVisibility(View.GONE);
                                    //lv_series.setClickable(false);
                                }
                                //complementos.mensajes("guardar "+codigo+" "+cont+" "+existencia+" "+idalmacen+" "+diferencia,contexto);
                            }
                        })
                        .setNegativeButton("Cancelar",null)

                        .create();
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void actualizarHeader()
    {
        tv_existencia.setText(consultasConteoClf.getExistencia(codigo,idalmacen,id_clf, contexto));
        tv_conteo.setText(consultasConteoClf.getConteo(codigo,idalmacen,id_clf,contexto));
        tv_diferencia.setText(consultasConteoClf.getDiferencia(codigo,idalmacen,id_clf,contexto));
    }

    public void actualizarListaPorEstatus(String estatus)
    {
        complementos.mensajes(estatus,contexto);
        lista=new ArrayList<>();

        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();

            if(estatus.equalsIgnoreCase("C"))
            {
                Cursor fila = db.rawQuery("SELECT serie,estatus FROM seriesclf where almacen='"+idalmacen+"' and codigo='"+codigo+"'  ",null);
                if(fila.moveToFirst())
                {
                    do {
                        if(fila.getString(1).equalsIgnoreCase("D") || fila.getString(1).equalsIgnoreCase("ND"))
                        {
                            lista.add(0, new SeriesModel(fila.getString(0), fila.getString(1)));
                        }

                        Log.i("consultaseries", " | " + fila.getString(0));
                    }while (fila.moveToNext());
                }
            }
            else
            {
                complementos.mensajes("xd",contexto);
                Cursor fila = db.rawQuery("SELECT serie,estatus FROM seriesclf where almacen='"+idalmacen+"' and codigo='"+codigo+"' and estatus='"+estatus+"'  ",null);
                if(fila.moveToFirst())
                {
                    do {
                        lista.add(0, new SeriesModel(fila.getString(0), fila.getString(1)));
                        Log.i("consultaseries", " | " + fila.getString(0));
                    }while (fila.moveToNext());
                }
            }


            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
        }


        //lista.add(0,new SeriesModel("01","S") );
        adaptadorSeries=new AdaptadorSeries(this,lista);
        lv_series.setAdapter( adaptadorSeries);
    }

    public void actualizarLista()
    {
        lista=new ArrayList<>();

        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT serie,estatus FROM seriesclf where almacen='"+idalmacen+"' and codigo='"+codigo+"'  ",null);
            if(fila.moveToFirst())
            {
                do {
                    if(fila.getString(1).equalsIgnoreCase("D"))
                    {
                        lista.add(0, new SeriesModel(fila.getString(0), fila.getString(1)));
                    }
                    else
                    {
                        lista.add( new SeriesModel(fila.getString(0), fila.getString(1)));
                    }

                    Log.i("consultaseries", " | " + fila.getString(0));
                }while (fila.moveToNext());
            }

            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
        }


        //lista.add(0,new SeriesModel("01","S") );
        adaptadorSeries=new AdaptadorSeries(this,lista);
        lv_series.setAdapter( adaptadorSeries);
    }
    class consultaSeries extends AsyncTask<String,Integer,String>
    {
        String validar;
        String serie;
        String estatus;
        String almacen;

        @Override
        protected void onPreExecute()
        {
            complementos.inciar_barra_progreso_spinner(contexto,"Consultando Series");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            try {
                URL url = new URL(ruta+"series/"+codigo+"/"+idalmacen); //in the real code, there is an ip and a port
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

                        JSONArray jArray = jObject.getJSONArray("respuesta");
                        float exis=jArray.length();
                        //complementos.setMax(jArray.length());
                        for (int i=0;i<jArray.length();i++)
                        {

                            //publishProgress(i+1);
                            JSONObject objeto = jArray.getJSONObject(i);
                            serie=objeto.getString("serie");
                            consultaSeriesClf.insertarSerie(serie,codigo,idalmacen,"N",id_clf,contexto);
                            Log.i("respuesta"," | "+serie);

                        }
                        float conteo=0;
                        float dif=exis-conteo;
                        consultasConteoClf.actualizarConteo(codigo,conteo,idalmacen,dif,id_clf,contexto);
                        //consultasConteo.setExistencia(codigo,idalmacen,contexto,exis);
                        //Log.i("peticion"," | "+jObject.toString());
                        //Log.i("peticion"," | "+jArray.toString());
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
            if(validar.equalsIgnoreCase("TRUE"))
            {
                //tv_existencia.setText(consultasConteoClf.getExistencia(codigo,idalmacen,id_clf,contexto));

            }
            else
            {
                //finish();
            }
            actualizarHeader();
            actualizarLista();
            super.onPostExecute(s);
        }
    }
    class reconsultarSeries extends AsyncTask<String,Integer,String>
    {
        String validar;
        String serie;
        String estatus;
        String almacen;

        @Override
        protected void onPreExecute()
        {
            complementos.inciar_barra_progreso_spinner(contexto,"Consultando Series");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            try {
                URL url = new URL(ruta+"series/"+codigo+"/"+idalmacen); //in the real code, there is an ip and a port
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

                        JSONArray jArray = jObject.getJSONArray("respuesta");
                        float exis=jArray.length();
                        //complementos.setMax(jArray.length());
                        for (int i=0;i<jArray.length();i++)
                        {

                            //publishProgress(i+1);
                            JSONObject objeto = jArray.getJSONObject(i);
                            serie=objeto.getString("serie");
                            if(consultaSeriesClf.buscarSerie(codigo,idalmacen,serie,contexto)==false)
                            {
                                consultaSeriesClf.insertarSerie(serie,codigo,idalmacen,"N",id_clf,contexto);
                            }

                            Log.i("respuesta"," | "+serie);

                        }
                        //float conteo=0;
                        //float dif=exis-conteo;
                        //consultasConteo.actualizarConteo(codigo,conteo,exis,idalmacen,dif,contexto);

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
            if(validar.equalsIgnoreCase("TRUE"))
            {
                complementos.mensajes("true",contexto);
                float contadas=consultaSeriesClf.getSeriesContadas(codigo,idalmacen,contexto);
                float exis=consultaSeriesClf.getSeriesExistencia(codigo,idalmacen,contexto) ;
                float dif=exis-contadas;
                consultasConteoClf.actualizarConteo(codigo,contadas,idalmacen,dif,id_clf,contexto);
                actualizarLista();
                actualizarHeader();
                //tv_existencia.setText(consultasConteo.getExistencia(codigo,idalmacen,contexto));
            }


            //actualizarLista();
            super.onPostExecute(s);
        }
    }

}
