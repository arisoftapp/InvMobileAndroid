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
    public static final String SQL_USUARIO="CREATE TABLE "+TABLA_USU+"(usuario text ," +
            "id_empresa text," +
            "empresa text," +
            "dominio text)";
    public static final String SQL_ALMACENES="CREATE TABLE "+TABLA_ALM+"(id_almacen text ," +
            "almacen text)";

    private static final String SQL_INICIOUSU = "DROP TABLE IF EXISTS "+TABLA_USU;
    private static final String SQL_INICIOALM = "DROP TABLE IF EXISTS "+TABLA_ALM;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_INICIOUSU);
        db.execSQL(SQL_INICIOALM);


        db.execSQL(SQL_USUARIO);
        db.execSQL(SQL_ALMACENES);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
