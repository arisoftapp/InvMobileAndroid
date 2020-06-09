package com.invmobile.invmobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.invmobile.invmobile.tools.Complementos;
import com.invmobile.invmobile.tools.ConsultaAlmacenes;
import com.invmobile.invmobile.tools.ConsultaClasificaciones;
import com.invmobile.invmobile.tools.ConsultaSeries;
import com.invmobile.invmobile.tools.ConsultaSeriesCLF;
import com.invmobile.invmobile.tools.ConsultasConteo;
import com.invmobile.invmobile.tools.ConsultasConteoClf;
import com.invmobile.invmobile.tools.ConsultasSinConteo;
import com.invmobile.invmobile.tools.ConsultasUsuario;
import com.invmobile.invmobile.tools.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Complementos complementos;
    ConsultasUsuario consultasUsuario;
    ConsultaAlmacenes consultaAlmacenes;
    ConsultasConteo consultasConteo;
    ConsultasSinConteo consultasSinConteo;
    ConsultaSeries consultaSeries;
    ConsultaClasificaciones consultaClasificaciones;
    ConsultasConteoClf consultasConteoClf;
    ConsultaSeriesCLF consultaSeriesClf;
    int timeout;
    String mensaje;
    String ruta;
    Context contexto;
    CardView card_inventario,card_almacenes;
    AlertDialog dialog;
    View vista;
    LayoutInflater inflater;
    Spinner spn_almacenes,spn_clf;
    Vector<String> codigos,cod_clf;
    Button btn_alm,btn_clf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //inicializando
        card_almacenes=(CardView)findViewById(R.id.card_almacenes);
        card_inventario=(CardView)findViewById(R.id.card_inventario);
        consultaAlmacenes=new ConsultaAlmacenes();
        complementos=new Complementos();
        consultasUsuario=new ConsultasUsuario();
        consultasConteo=new ConsultasConteo();
        consultasSinConteo=new ConsultasSinConteo();
        consultaSeries=new ConsultaSeries();
        consultaClasificaciones=new ConsultaClasificaciones();
        consultasConteoClf=new ConsultasConteoClf();
        consultaSeriesClf=new ConsultaSeriesCLF();
        ruta=complementos.getUrl()+consultasUsuario.getDominio(getApplicationContext())+"/";
        timeout=complementos.getTimeout();
        contexto=this;
        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        ((TextView) header.findViewById(R.id.txt_name)).setText(consultasUsuario.getUsuario(contexto));
        ((TextView) header.findViewById(R.id.txt_empresa)).setText(consultasUsuario.getIdEmpresa(contexto)+" - "+consultasUsuario.getEmpresa(contexto));


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
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
            complementos.mensajes("filtrar",contexto);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_inicializar) {
            dialogInicializar();
            // Handle the camera action
        } else if (id == R.id.nav_inventario) {
            dialogInventario(1);
        } else if (id == R.id.nav_cerrar_sesion) {
            if(complementos.eliminarTabla("almacenes",contexto)==true && complementos.eliminarTabla("login",contexto)==true
                    && complementos.eliminarTabla("conteo",contexto)==true
                    && complementos.eliminarTabla("sinconteo",contexto)==true
                    && complementos.eliminarTabla("series",contexto)==true
            )
            {
                Intent i=new Intent(contexto,Login.class);
                startActivity(i);
                finish();
            }
            else
            {
                complementos.mensajes("Error al cerrar sesion",contexto);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void dialogInicializar()
    {
        dialog=new AlertDialog.Builder(contexto)
                .setTitle("Inicializar Empresa")
                .setMessage("Al inicializar la empresa se borraran todos los registros que se tengan guardados.\r\n\r\n¿Esta seguro que desea continuar?...")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        complementos.eliminarTabla("conteo",contexto);
                        complementos.eliminarTabla("sinconteo",contexto);
                        complementos.eliminarTabla("series",contexto);
                        complementos.eliminarTabla("conteoclf",contexto);
                        complementos.eliminarTabla("seriesclf",contexto);
                    }
                })
                .setNegativeButton("Cancelar",null)
                .create();
        dialog.show();
    }
    public void dialogInventario(final int select)
    {
        inflater = MainActivity.this.getLayoutInflater();
        vista = inflater.inflate(R.layout.dialog_almacenes, null);

        spn_almacenes=vista.findViewById(R.id.spn_almacenes);
        spn_almacenes.setAdapter(consultaAlmacenes.getAlmacenes(contexto));
        btn_alm=vista.findViewById(R.id.btn_alm);
        codigos=consultaAlmacenes.getCodigos(contexto);
        btn_alm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(consultaAlmacenes.getTablaVacia(contexto))
                {

                    new consultaAlmacenes().execute();
                }
                else
                {
                    complementos.eliminarTabla("almacenes",contexto);
                    new consultaAlmacenes().execute();

                }
            }
        });
        dialog=new AlertDialog.Builder(contexto)
                .setTitle("Almacenes")
                .setView(vista)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(consultaAlmacenes.getTablaVacia(contexto))
                        {
                            complementos.mensajes("Consultar almacenes",contexto);

                        }
                        else
                        {
                            int size = spn_almacenes.getAdapter().getCount();
                            if (size>0){
                                int pos = spn_almacenes.getSelectedItemPosition();
                                try {
                                    Database admin = new Database(getApplicationContext(), null, 1);
                                    SQLiteDatabase db = admin.getWritableDatabase();
                                    ContentValues r = new ContentValues();
                                    r.put("almacen",codigos.get(pos).toString());
                                    db.update("login",r, "usuario='"+consultasUsuario.getUsuario(contexto)+"'",null);
                                    db.close();
                                } catch (SQLiteException e) {
                                    complementos.mensajes("error al actualizar en base de datos:" + e.getMessage(),contexto);
                                }
                                if(select==1)
                                {
                                    verificarConteo();
                                }
                                else
                                {
                                    verificarConteoClf();
                                }

                            /*
                            Intent intent=new Intent(contexto,Inventario.class);
                            startActivity(intent);
                            */
                                //complementos.mensajes(""+consultasUsuario.getAlmacenSeleccionado(contexto),contexto);
                            }
                        }


                    }
                })
                .setNegativeButton("Cancelar",null)
                .create();
        dialog.show();
    }
    public void dialogClasificaciones()
    {
        inflater = MainActivity.this.getLayoutInflater();
        vista = inflater.inflate(R.layout.dialog_clasificaciones, null);

        spn_clf=vista.findViewById(R.id.spn_clf);
        spn_clf.setAdapter(consultaClasificaciones.getClasificaciones(contexto));
        btn_clf=vista.findViewById(R.id.btn_clf);
        cod_clf=consultaClasificaciones.getCodigos(contexto);
        btn_clf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(consultaClasificaciones.getTablaVacia(contexto))
                {

                    new consultaClasificaciones().execute();
                }
                else
                {
                    complementos.eliminarTabla("clasificaciones",contexto);
                    new consultaClasificaciones().execute();

                }
            }
        });
        dialog=new AlertDialog.Builder(contexto)
                .setTitle("Clasificaciones")
                .setView(vista)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(consultaClasificaciones.getTablaVacia(contexto))
                        {
                            complementos.mensajes("Consultar Clasificaciones",contexto);

                        }
                        else
                        {
                            int size = spn_clf.getAdapter().getCount();
                            if (size>0){
                                int pos = spn_clf.getSelectedItemPosition();
                                try {
                                    Database admin = new Database(getApplicationContext(), null, 1);
                                    SQLiteDatabase db = admin.getWritableDatabase();
                                    ContentValues r = new ContentValues();
                                    r.put("id_clf",cod_clf.get(pos).toString());
                                    db.update("login",r, "usuario='"+consultasUsuario.getUsuario(contexto)+"'",null);
                                    db.close();
                                } catch (SQLiteException e) {
                                    complementos.mensajes("error al actualizar en base de datos:" + e.getMessage(),contexto);
                                }
                                dialogInventario(2);

                            /*
                            Intent intent=new Intent(contexto,Inventario.class);
                            startActivity(intent);
                            */
                                //complementos.mensajes(""+consultasUsuario.getAlmacenSeleccionado(contexto),contexto);
                            }
                        }


                    }
                })
                .setNegativeButton("Cancelar",null)
                .create();
        dialog.show();
    }

    public void btnInventario(View view) {
        dialogInventario(1);

    }
    public void btnPorFamilia(View view){
        dialogClasificaciones();
    }
    public void verificarConteo()
    {
        final String idalmacen=consultasUsuario.getAlmacenSeleccionado(contexto);
        if(consultasConteo.validarConteo(idalmacen,contexto)==true)
        {
            complementos.mensajes("tiene conteo",contexto);
            dialog=new AlertDialog.Builder(contexto)
                    .setTitle("Conteo encontrado")
                    .setMessage("\nSe ha detectado que ya cuenta con un conteo iniciado en el almacen seleccionado.\n \n¿Desea continuar con el conteo?\n")
                    .setCancelable(false)
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(contexto,Inventario.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Nuevo Conteo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            complementos.eliminarTabla("conteo",contexto);
                            consultasSinConteo.eliminarSinConteo(idalmacen,contexto);
                            consultaSeries.eliminarTodasSeries(idalmacen,contexto);
                            Intent intent=new Intent(contexto,Inventario.class);
                            startActivity(intent);
                        }
                    })
                    .create();
            dialog.show();
        }
        else
        {
            Intent intent=new Intent(contexto,Inventario.class);
            startActivity(intent);
        }
    }
    public void verificarConteoClf()
    {
        final String idalmacen=consultasUsuario.getAlmacenSeleccionado(contexto);
        final String id_clf=consultasUsuario.getClfSeleccionado(contexto);
        if(consultasConteoClf.validarConteo(idalmacen,id_clf,contexto)==true)
        {
            complementos.mensajes("tiene conteo",contexto);
            dialog=new AlertDialog.Builder(contexto)
                    .setTitle("Conteo encontrado")
                    .setMessage("\nSe ha detectado que ya cuenta con un conteo iniciado en el almacen seleccionado.\n \n¿Desea continuar con el conteo?\n")
                    .setCancelable(false)
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(contexto,InventarioClf.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Nuevo Conteo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //complementos.eliminarTabla("conteoclf",contexto);
                            consultasConteoClf.eliminarCoteoAlmClf(idalmacen,id_clf,contexto);
                            consultaSeriesClf.eliminarTodasSeries(idalmacen,id_clf,contexto);
                            //complementos.eliminarTabla("seriesclf",contexto);
                            Intent intent=new Intent(contexto,InventarioClf.class);
                            startActivity(intent);
                        }
                    })
                    .create();
            dialog.show();
        }
        else
        {
            Intent intent=new Intent(contexto,InventarioClf.class);
            startActivity(intent);
        }
    }
    public void btnalmacenes(View view) {
        if(consultaAlmacenes.getIdAlmacen(contexto).isEmpty())
        {
            new consultaAlmacenes().execute();
        }
        else
        {
            complementos.eliminarTabla("almacenes",contexto);
            new consultaAlmacenes().execute();

        }

    }

    class consultaAlmacenes extends AsyncTask<String,Integer,String>
    {
        String validar;
        @Override
        protected void onPreExecute()
        {
            complementos.inciar_barra_progreso_horizontal(contexto,"Consultando Almacenes");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            try {

                URL url = new URL(ruta+"almacenes"); //in the real code, there is an ip and a port
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
                        JSONArray jArray = jObject.getJSONArray("almacenes");
                        complementos.setMax(jArray.length());
                        for (int i=0;i<jArray.length();i++)
                        {
                            publishProgress(i+1);
                            JSONObject objeto = jArray.getJSONObject(i);
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
            if(s.equalsIgnoreCase("TRUE"))
            {
                //card_inventario.setVisibility(View.VISIBLE);
                spn_almacenes.setAdapter(consultaAlmacenes.getAlmacenes(contexto));
                codigos=consultaAlmacenes.getCodigos(contexto);
            }
            else
            {
                complementos.mensajes("Consultar Almacenes de nuevo",contexto);
            }

            super.onPostExecute(s);
        }
    }
    class consultaClasificaciones extends AsyncTask<String,Integer,String>
    {
        String validar;
        @Override
        protected void onPreExecute()
        {
            complementos.inciar_barra_progreso_horizontal(contexto,"Consultando Clasificaciones");
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            try {

                URL url = new URL(ruta+"clasificaciones"); //in the real code, there is an ip and a port
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
                        JSONArray jArray = jObject.getJSONArray("clasificaciones");
                        complementos.setMax(jArray.length());
                        for (int i=0;i<jArray.length();i++)
                        {
                            publishProgress(i+1);
                            JSONObject objeto = jArray.getJSONObject(i);
                            try{
                                Database admin=new Database(contexto,null,1);
                                SQLiteDatabase db = admin.getWritableDatabase();
                                ContentValues r = new ContentValues();
                                r.put("id_clf",objeto.getString("id_clf"));
                                r.put("clf_desc",objeto.getString("descripcion"));
                                r.put("clf_nivel",objeto.getString("nivel"));
                                db.insert("clasificaciones",null,r);
                                db.close();

                            }catch (SQLiteException e)
                            {
                                mensaje="error al insertar clasificaciones:"+e.getMessage();
                                validar="FALSE";
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
            if(s.equalsIgnoreCase("TRUE"))
            {
                //card_inventario.setVisibility(View.VISIBLE);
                spn_clf.setAdapter(consultaClasificaciones.getClasificaciones(contexto));
                cod_clf=consultaClasificaciones.getCodigos(contexto);
            }
            else
            {
                complementos.mensajes("Consultar Almacenes de nuevo",contexto);
            }

            super.onPostExecute(s);
        }
    }


}
