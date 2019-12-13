package com.invmobile.invmobile;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.invmobile.invmobile.Modelo.InventarioModel;

import java.util.ArrayList;

public class AdaptadorConteo extends BaseAdapter {
    private ArrayList<InventarioModel> datos;
    LayoutInflater inflater;
    Activity activity;

    public AdaptadorConteo(Activity activity, ArrayList<InventarioModel> datos) {
        this.datos = datos;
        inflater = activity.getLayoutInflater();
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int i) {
        return datos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.lista_conteo_item, viewGroup, false);
        }
        //view = inflater.inflate(R.layout.lista_conteo_item, viewGroup, false);
        TextView tv_codigo=(TextView)view.findViewById(R.id.tv_codigo);
        TextView tv_descripcion=(TextView)view.findViewById(R.id.tv_descripcion);
        TextView tv_serie=(TextView)view.findViewById(R.id.tv_serie);
        TextView tv_existencia=(TextView)view.findViewById(R.id.tv_existencia);
        TextView tv_conteo=(TextView)view.findViewById(R.id.tv_conteo);
        TextView tv_diferencia=(TextView)view.findViewById(R.id.tv_diferencia);
        InventarioModel item=(InventarioModel) getItem(i);
        tv_codigo.setText(item.getCodigoArticulo());
        tv_descripcion.setText(item.getDescripcion());
        tv_serie.setText(item.getSerie());
        tv_existencia.setText(Float.toString(item.getExistencia()));
        tv_conteo.setText(Float.toString(item.getConteo()));
        tv_diferencia.setText(Float.toString(item.getDiferencia()));

        return view;
    }
}
