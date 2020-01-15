package com.invmobile.invmobile;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.invmobile.invmobile.tools.Complementos;
import com.invmobile.invmobile.tools.ConsultasConteo;
import com.invmobile.invmobile.tools.ConsultasUsuario;

public class SeriesActivity extends AppCompatActivity {
    Complementos complementos;
    ConsultasUsuario consultasUsuario;
    ConsultasConteo consultasConteo;
    Context contexto;
    String codigo,idalmacen;
    TextView tv_codigo,tv_existencia,tv_conteo,tv_diferencia,tv_descripcion;
    EditText et_serie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);
        complementos=new Complementos();
        consultasUsuario=new ConsultasUsuario();
        consultasConteo=new ConsultasConteo();
        contexto=this;
        Bundle bundle = this.getIntent().getExtras();
        codigo = bundle.getString("codigo");
        idalmacen=consultasUsuario.getAlmacenSeleccionado(contexto);
        tv_codigo=(TextView)findViewById(R.id.tv_codigo);
        tv_existencia=(TextView)findViewById(R.id.tv_existencia);
        tv_conteo=(TextView)findViewById(R.id.tv_conteo);
        tv_diferencia=(TextView)findViewById(R.id.tv_diferencia);
        tv_descripcion=(TextView)findViewById(R.id.tv_descripcion);
        et_serie=(EditText)findViewById(R.id.et_serie);

        tv_codigo.setText(codigo);
        tv_existencia.setText(consultasConteo.getExistencia(codigo,idalmacen,contexto));
        tv_conteo.setText(consultasConteo.getConteo(codigo,idalmacen,contexto));
        tv_diferencia.setText(consultasConteo.getDiferencia(codigo,idalmacen,contexto));
        tv_descripcion.setText(consultasConteo.getDescripcion(codigo,idalmacen,contexto));

        et_serie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                return false;
            }
        });

    }

    public void fab_guardar(View view) {
        complementos.mensajes("guardar",contexto);

    }
}
