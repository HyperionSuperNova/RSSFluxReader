package com.androidclass.stryker.mplrss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;

public class Base extends SQLiteOpenHelper {

    public final static int VERSION = 9;
    public final static String DB_NAME = "base_rss";
    public final static String TABLE_RSS = "rss";
    public final static String COLONNE_TITLE = "title";
    public final static String COLONNE_LINK = "link";
    public final static String COLONNE_DESCRIPTION = "description";
    public final static String COLONNE_DATE_LAST_CHANGE = "date_last_change";
    public final static String COLONNE_DATE_CHOISI = "date_choisi";



    public final static String CREATE_GEO = "create table " + TABLE_RSS + "(" +
            _ID + "integer primary key autoincrement, " +
            COLONNE_TITLE + " string, " +
            COLONNE_LINK + " string, " +
            COLONNE_DESCRIPTION + " string, " +
            COLONNE_DATE_LAST_CHANGE + " date, " +
            COLONNE_DATE_CHOISI + " date " + ");";


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
        db.execSQL(CREATE_GEO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TABLE_RSS);
            onCreate(db);
        }
    }
}

