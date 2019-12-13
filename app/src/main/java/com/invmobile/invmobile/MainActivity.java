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
import android.widget.Spinner;

import com.invmobile.invmobile.tools.Complementos;
import com.invmobile.invmobile.tools.ConsultaAlmacenes;
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
    int timeout;
    String mensaje;
    String ruta;
    Context contexto;
    CardView card_inventario,card_almacenes;
    AlertDialog dialog;
    View vista;
    LayoutInflater inflater;
    Spinner spn_almacenes;
    Vector<String> codigos;

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
        ruta=complementos.getUrl()+consultasUsuario.getDominio(getApplicationContext())+"/";
        timeout=complementos.getTimeout();
        contexto=this;
        if(consultaAlmacenes.getIdAlmacen(this).isEmpty())
        {
            card_inventario.setVisibility(View.GONE);
        }
        else
        {
            card_inventario.setVisibility(View.VISIBLE);
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_inventario) {
            Intent i=new Intent(contexto,Inventario.class);
            startActivity(i);
        } else if (id == R.id.nav_cerrar_sesion) {
            if(complementos.eliminarTabla("almacenes",contexto)==true && complementos.eliminarTabla("login",contexto)==true)
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

    public void btnInventario(View view) {
        inflater = MainActivity.this.getLayoutInflater();
        vista = inflater.inflate(R.layout.dialog_almacenes, null);
        spn_almacenes=vista.findViewById(R.id.spn_almacenes);
        spn_almacenes.setAdapter(consultaAlmacenes.getAlmacenes(contexto));
        codigos=consultaAlmacenes.getCodigos(contexto);
        dialog=new AlertDialog.Builder(contexto)
        .setTitle("Almacenes")
                .setView(vista)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                            Intent intent=new Intent(contexto,Inventario.class);
                            startActivity(intent);
                            //complementos.mensajes(""+consultasUsuario.getAlmacenSeleccionado(contexto),contexto);
                        }

                    }
                })
                .setNegativeButton("Cancelar",null)
                .create();
        dialog.show();

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
                card_inventario.setVisibility(View.VISIBLE);
            }
            else
            {
                complementos.mensajes("Consultar Almacenes de nuevo",contexto);
            }

            super.onPostExecute(s);
        }
    }


}
