package com.androidclass.stryker.mplrss;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class DataAccess {
    public ContentResolver cr;
    public final static int VERSION = 11;
    public final static String TABLE_RSS = "rss";
    public final static String COLONNE_TITLE = "title";
    public final static String COLONNE_ITEM = "link";
    public final static String COLONNE_DESCRIPTION = "description";
    public final static String COLONNE_DATE_LAST_CHANGE = "date_last_change";
    public final static String COLONNE_DATE_CHOISI = "date_choisi";
    public final static String COLONNE_CHOISI = "choisi";
    public final static String COLONNE_IDFLUX = "id_flux";
    public final static String COLONNE_DESCFLUX = "flux_description";


    public final static String TABLE_FLUX = "flux";
    public final static String COLONNE_ID = "id";
    public final static String COLONNE_FLUX = "flux_link";
    public final static String COLONNE_TITLEFLUX = "flux_title";


    public DataAccess(Context c){
        this.cr = c.getContentResolver();
    }

    public void ajoutFlux(String link, String title,String description){
        ContentValues cv = new ContentValues();
        cv.put(COLONNE_FLUX,link);
        cv.put(COLONNE_TITLEFLUX,title);
        cv.put(COLONNE_DESCFLUX,description);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority("fr.cartman.respect.my.authority").appendPath(TABLE_FLUX);
        Uri uri = builder.build();
        uri = cr.insert(uri,cv);
    }

}
