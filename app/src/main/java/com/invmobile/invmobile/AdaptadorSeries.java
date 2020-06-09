package com.invmobile.invmobile;

import android.app.Activity;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.invmobile.invmobile.Modelo.SeriesModel;

import java.util.ArrayList;

public class AdaptadorSeries extends BaseAdapter {
    private ArrayList<SeriesModel> datos;
    LayoutInflater inflater;
    Activity activity;

    public AdaptadorSeries(Activity activity, ArrayList<SeriesModel> datos) {
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
            view = inflater.inflate(R.layout.lista_serie_item, viewGroup, false);
        }
        //view = inflater.inflate(R.layout.lista_conteo_item, viewGroup, false);
        TextView tv_serie=(TextView)view.findViewById(R.id.tv_serie);
        TextView tv_estatus=(TextView)view.findViewById(R.id.tv_estatus);
        LinearLayout ll_renglon=(LinearLayout)view.findViewById(R.id.ll_renglon);

        SeriesModel item=(SeriesModel) getItem(i);
        tv_estatus.setText(item.getEstatus());
        tv_serie.setText(item.getSerie());
        int rojo = Color.parseColor("#ffcdd2");
        int verde = Color.parseColor("#c8e6c9");
        if(item.getEstatus().equalsIgnoreCase("D"))
        {
            ll_renglon.setBackgroundColor(verde);
        }
        else if (item.getEstatus().equalsIgnoreCase("ND"))
        {
            ll_renglon.setBackgroundColor(rojo);
        }


        return view;
    }


}
