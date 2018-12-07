package com.androidclass.stryker.mplrss;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class Base extends SQLiteOpenHelper {

    public final static int VERSION = 28;
    public final static String DB_NAME = "base_rss";
    public final static String TABLE_RSS = "rss";
    public final static String COLONNE_TITLE = "title";
    public final static String COLONNE_ITEM = "link";
    public final static String COLONNE_DESCRIPTION = "description";
    public final static String COLONNE_DATE_LAST_CHANGE = "date_last_change";
    public final static String COLONNE_DATE_CHOISI = "date_choisi";
    public final static String COLONNE_CHOISI = "choisi";
    public final static String COLONNE_IDFLUX = "id_flux";

    public final static String TABLE_FLUX = "flux";
    public final static String COLONNE_ID = "id";
    public final static String COLONNE_FLUX = "fluxLink";
    public final static String COLONNE_TITLEFLUX = "fluxTitle";
    public final static String COLONNE_DESCFLUX = "fluxDescription";


    public final static String CREATE_RSS = "create table " + TABLE_RSS + "(" +
            COLONNE_TITLE + " string primary key, " +
            COLONNE_IDFLUX + " integer, " +
            COLONNE_ITEM + " string, " +
            COLONNE_DESCRIPTION + " string, " +
            COLONNE_DATE_LAST_CHANGE + " datetime, " +
            COLONNE_DATE_CHOISI + " string, " +
            COLONNE_CHOISI + " integer default 0, " +
            "FOREIGN KEY(id_flux) REFERENCES flux(id)" +
            ");";

    public final static String CREATE_FEED = "create table " + TABLE_FLUX + "(" +
            COLONNE_ID +" integer primary key autoincrement, "+
            COLONNE_FLUX + " string, " +
            COLONNE_TITLEFLUX + " string, " +
            COLONNE_DESCFLUX + " string" +
            ");";

    private static Base ourInstance;

    public static Base getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new Base(context);
        return ourInstance;
    }

    private Base(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FEED);
        db.execSQL(CREATE_RSS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TABLE_RSS);
            db.execSQL("drop table if exists " + TABLE_FLUX);
            onCreate(db);
        }
    }
}

