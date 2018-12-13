package com.androidclass.stryker.mplrss;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataAccess {
    public ContentResolver cr;
    public final static String authority = "fr.cartman.respect.my.authority";

    public final static int VERSION = 23;
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


    public DataAccess(Context c){
        this.cr = c.getContentResolver();
    }

    public boolean ajoutFlux(String link, String title,String description, String dateChoisi){
        ContentValues cv = new ContentValues();
        cv.put(COLONNE_FLUX,link);
        cv.put(COLONNE_TITLEFLUX,title);
        cv.put(COLONNE_DESCFLUX,description);
        cv.put(COLONNE_DATE_CHOISI, dateChoisi);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority("fr.cartman.respect.my.authority").appendPath(TABLE_FLUX);
        Uri uri = builder.build();
        Cursor c = cr.query(uri, new String[] {"*"}, "fluxTitle =?", new String[] {title}, null, null);
        System.out.println("TEST34:::::::::::::::::::::::::::::::::::::::::"+ (c.getCount() == 0));
        if(c.getCount() == 0){
            uri = cr.insert(uri,cv);
            return true;
        }
        return false;
    }

    public int getIdFlux(String title){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority("fr.cartman.respect.my.authority").appendPath(TABLE_FLUX);
        Uri uri = builder.build();
        Cursor c = cr.query(uri, new String[] {"rowid as id"}, "fluxTitle = ?", new String[] {title}, null, null);
        assert c != null;
        c.moveToFirst();
        return c.getInt(c.getColumnIndexOrThrow("id"));
    }

    public ArrayList<Integer> getOldFlux(){
        ArrayList<Integer> id_flux = new ArrayList<Integer>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        Date currentDate = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority("fr.cartman.respect.my.authority").appendPath(TABLE_FLUX);
        Uri uri = builder.build();
        Cursor c = cr.query(uri, new String[] {"id", "date_choisi"}, null, null, null, null);
        assert c != null;
        c.moveToFirst();
        while(c.moveToNext()){
            String dateChoisi = c.getString(c.getColumnIndexOrThrow("date_choisi"));
            try {
                Date d = format.parse(dateChoisi);
                int monthCurrent = currentDate.getMonth();
                int monthChoisi = d.getMonth();
                if(monthCurrent - monthChoisi > 3)  id_flux.add(c.getInt(c.getColumnIndexOrThrow("id")));
            }catch (ParseException e){}
        }
        return id_flux;
    }

    public void ajoutItems(String title, int idFlux, String link, String description, String pubDate){
        ContentValues cv = new ContentValues();
        cv.put(COLONNE_TITLE,title);
        cv.put(COLONNE_IDFLUX,idFlux);
        cv.put(COLONNE_ITEM,link);
        cv.put(COLONNE_DESCRIPTION,description);
        cv.put(COLONNE_DATE_LAST_CHANGE,pubDate);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority("fr.cartman.respect.my.authority").appendPath(TABLE_RSS);
        Uri uri = builder.build();
        uri = cr.insert(uri,cv);
    }

    public List<Flux> storedFlux(){
        List<Flux> f = new ArrayList<>();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(TABLE_FLUX);
        Uri uri = builder.build();
        Cursor c = cr.query(uri,null,null,null,null);
        if(c != null){
            c.moveToFirst();
            while(c.moveToNext()){
                String link = c.getString(c.getColumnIndexOrThrow(COLONNE_FLUX));
                String title = c.getString(c.getColumnIndexOrThrow(COLONNE_TITLEFLUX));
                String description = c.getString(c.getColumnIndexOrThrow(COLONNE_DESCFLUX));
                String dateChoisi = c.getString(c.getColumnIndexOrThrow(COLONNE_DATE_CHOISI));
                f.add(new Flux(link,title,description, dateChoisi));
            }
        }
        c.close();
        return f;
    }

    public int deleteItem(int id){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath(TABLE_FLUX).appendPath(id+"");
        Uri uri = builder.build();
        int ret = cr.delete(uri,null,null);
        return ret;
    }
}
