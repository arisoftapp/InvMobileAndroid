package com.invmobile.invmobile.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public Database(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "arisoft_app";
    public static final String TABLA_USU="login";
    public static final String TABLA_ALM="almacenes";
    public static final String TABLA_CTO="conteo";
    public static final String TABLA_SERIES="series";
    public static final String TABLA_SINCTO="sinconteo";
    public static final String TABLA_CTO_CLF="conteoclf";
    public static final String TABLA_SERIES_CLF="seriesclf";
    public static final String TABLA_CLF="clasificaciones";
    public static final String SQL_USUARIO="CREATE TABLE "+TABLA_USU+"(usuario text ," +
            "id_empresa text," +
            "empresa text," +
            "dominio text," +
            "almacen text," +
            "id_clf text)";
    public static final String SQL_ALMACENES="CREATE TABLE "+TABLA_ALM+"(id_almacen text ," +
            "almacen text)";
    public static final String SQL_CONTEO="CREATE TABLE "+TABLA_CTO+"(_id INTEGER PRIMARY KEY," +
            "codigo text," +
            "codigo2 text," +
            "descripcion text," +
            "conteo Float," +
            "existencia Float," +
            "idalmacen text," +
            "serie text," +
            "diferencia Float," +
            "estatus text," +
            "comentarios text"+
            ")";
    public static final String SQL_SERIES="CREATE TABLE "+TABLA_SERIES+"(_id INTEGER PRIMARY KEY, serie text," +
            "codigo text," +
            "almacen text," +
            "estatus text)";
    public static final String SQL_SINCONTEO="CREATE TABLE "+TABLA_SINCTO+" (_id INTEGER PRIMARY KEY," +
            "codigo text," +
            "codigo2 text," +
            "descripcion text," +
            "conteo float," +
            "existencia float," +
            "idalmacen text," +
            "serie text," +
            "diferencia float," +
            "estatus text," +
            "comentarios text"+
            ")";
    public static final String SQL_CONTEO_CLF="CREATE TABLE "+TABLA_CTO_CLF+"(_id INTEGER PRIMARY KEY," +
            "codigo text," +
            "codigo2 text," +
            "descripcion text," +
            "conteo Float," +
            "existencia Float," +
            "idalmacen text," +
            "serie text," +
            "diferencia Float," +
            "estatus text," +
            "comentarios text,"+
            "clasificacion text"+
            ")";
    public static final String SQL_SERIES_CLF="CREATE TABLE "+TABLA_SERIES_CLF+"(_id INTEGER PRIMARY KEY, serie text," +
            "codigo text," +
            "almacen text," +
            "estatus text," +
            "id_clf text)";
    public static final String SQL_CLF="CREATE TABLE "+TABLA_CLF+"(_id INTEGER PRIMARY KEY, id_clf text," +
            "clf_desc text," +
            "clf_nivel text)";

    private static final String SQL_INICIOUSU = "DROP TABLE IF EXISTS "+TABLA_USU;
    private static final String SQL_INICIOALM = "DROP TABLE IF EXISTS "+TABLA_ALM;
    private static final String SQL_INICIOCTO = "DROP TABLE IF EXISTS "+TABLA_CTO;
    private static final String SQL_INICIOSERIES = "DROP TABLE IF EXISTS "+TABLA_SERIES;
    private static final String SQL_INICIOSINCTO = "DROP TABLE IF EXISTS "+TABLA_SINCTO;
    private static final String SQL_INICIOCTO_CLF = "DROP TABLE IF EXISTS "+TABLA_CTO_CLF;
    private static final String SQL_INICIOSERIES_CLF = "DROP TABLE IF EXISTS "+TABLA_SERIES_CLF;
    private static final String SQL_INICIO_CLF = "DROP TABLE IF EXISTS "+TABLA_CLF;
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_INICIOUSU);
        db.execSQL(SQL_INICIOALM);
        db.execSQL(SQL_INICIOCTO);
        db.execSQL(SQL_INICIOSERIES);
        db.execSQL(SQL_INICIOSINCTO);
        db.execSQL(SQL_INICIOCTO_CLF);
        db.execSQL(SQL_INICIOSERIES_CLF);
        db.execSQL(SQL_INICIO_CLF);

        db.execSQL(SQL_USUARIO);
        db.execSQL(SQL_ALMACENES);
        db.execSQL(SQL_CONTEO);
        db.execSQL(SQL_SERIES);
        db.execSQL(SQL_SINCONTEO);
        db.execSQL(SQL_CONTEO_CLF);
        db.execSQL(SQL_SERIES_CLF);
        db.execSQL(SQL_CLF);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
