package com.invmobile.invmobile;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.invmobile.invmobile.tools.Complementos;
import com.invmobile.invmobile.tools.ConsultasUsuario;
import com.invmobile.invmobile.tools.Database;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity {
    Complementos complementos;
    ConsultasUsuario consultasUsuario;
    EditText et_usuario,et_contra;
    TextView tv_mensaje;
    LinearLayout ll_mensaje;
    String mensaje;
    int timeout=10000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        complementos=new Complementos();
        consultasUsuario=new ConsultasUsuario();
        et_usuario=(EditText)findViewById(R.id.et_usuario);
        et_contra=(EditText)findViewById(R.id.et_contra);
        ll_mensaje=(LinearLayout)findViewById(R.id.ll_mensaje);
        tv_mensaje=(TextView)findViewById(R.id.tv_mensaje);
        //String dominio=complementos.dominio();
        //complementos.mensajes("prueba:"+consultasUsuario.getUsuario(this),this);
        //String usuario=consultasUsuario.getUsuario(this);
        //complementos.mensajes(usuario,this);

        if(consultasUsuario.getUsuario(this).isEmpty())
        {
            complementos.mensajes("sin datos en bd",this);
        }
        else
        {
            complementos.mensajes("con datos en bd",this);
            complementos.mensajes("¡Bienvenido!",getApplicationContext());
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        }


    }


    public void btnAccesar(View view) {

        String usu=et_usuario.getText().toString().trim();
        String contra=et_contra.getText().toString().trim();
        if(usu.isEmpty() || contra.isEmpty())
        {
            //complementos.mensajes("campos vacios",this);
            ll_mensaje.setVisibility(View.VISIBLE);
            if(usu.isEmpty())
            {
                tv_mensaje.setText("¡Ingrese Usuario!");
            }
            else
            {
                if(contra.isEmpty())
                {
                    tv_mensaje.setText("¡Ingrese Contraseña!");
                }
            }

        }
        else
        {
            new consultalogin().execute(et_usuario.getText().toString().trim(),et_contra.getText().toString().trim());
            /*
            Intent i=new Intent(this,MainActivity.class);
            startActivity(i);
            finish();
            */
        }

    }

    class consultalogin extends AsyncTask<String,Integer,String>
    {
        String validar;
        @Override
        protected void onPreExecute()
        {
            //complementos.inciar_barra_progreso(getApplicationContext());
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            String usuario=params[0],
                    contra=params[1];
            try {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("usuario",usuario);
                    obj.put("contra", contra);
                }
                catch (JSONException e)
                {
                    Log.e("error al crear JSON:",e.getMessage());
                }
                String ruta="http://wsar.homelinux.com:3000/";
                URL url = new URL(ruta+"loginInv"); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(obj.toString());
                os.flush();
                os.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                //String status=String.valueOf(conn.getResponseCode());
                int status = conn.getResponseCode();

                if(status<400)
                {
                    validar="TRUE";
                    String finalJSON = sb.toString();
                    JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                    mensaje=jObject.getString("mensaje");
                    String token=jObject.getString("token");

                    if(jObject.getBoolean("success")==true)
                    {
                        JSONArray jArray = jObject.getJSONArray("usuario");
                        for(int i=0;i<jArray.length();i++) {
                            try {
                                Log.i("peticion",jArray.toString());
                                //publishProgress(i+1);
                                JSONObject objeto=jArray.getJSONObject(i);
                                validar = "TRUE";
                                Database admin = new Database(getApplicationContext(), null, 1);
                                SQLiteDatabase db = admin.getWritableDatabase();
                                ContentValues r = new ContentValues();
                                r.put("usuario", objeto.getString("usuario"));
                                r.put("id_empresa", objeto.getString("id_empresa"));
                                r.put("empresa", objeto.getString("empresa"));
                                r.put("dominio",objeto.getString("dominio"));
                                r.put("almacen","");
                                r.put("token",token);
                                //r.put("dominio", "3011");
                                db.insert("login", null, r);
                                db.close();
                            } catch (SQLiteException e) {
                                mensaje = "error al insertar en base de datos:" + e.getMessage();
                                validar = "FALSE";
                            }
                        }
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
            ll_mensaje.setVisibility(View.VISIBLE);
            tv_mensaje.setText(mensaje);
            //complementos.cerrar_barra_progreso();
            if(s.equalsIgnoreCase("TRUE"))
            {
                tv_mensaje.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                complementos.mensajes("¡Bienvenido!",getApplicationContext());
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                finish();
            }
            else
            {
                et_contra.setText("");
                tv_mensaje.setTextColor(getResources().getColor(R.color.error));
            }
            super.onPostExecute(s);
        }
    }


}
