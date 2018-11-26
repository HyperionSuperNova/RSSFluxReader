package com.androidclass.stryker.mplrss;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MplrssContentProvider extends ContentProvider {
    private Base base;

    private static final String LOG = "BookContentProvider";
    private String authority = "fr.cartman.respect.my.authority";

    public final static int TABLE_RSS = 1;
    public final static int COLONNE_TITLE = 2;
    public final static int COLONNE_LINK = 3;
    public final static int COLONNE_DESCRIPTION = 4;
    public final static int COLONNE_DATE_LAST_CHANGE = 5;
    public final static int COLONNE_CHOISI = 6;

    private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    {
        uriMatcher.addURI(authority, "rss", TABLE_RSS);
        uriMatcher.addURI(authority, "rss/title", COLONNE_TITLE);
        uriMatcher.addURI(authority, "rss/link", COLONNE_LINK);
        uriMatcher.addURI(authority, "rss/description", COLONNE_DESCRIPTION);
        uriMatcher.addURI(authority, "rss/choisi", COLONNE_CHOISI);
    }
    public MplrssContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = base.getWritableDatabase();
        int code = uriMatcher.match(uri);
        Log.d(LOG, "Uri=" + uri.toString());
        long id = 0;
        String path;
        switch (code) {
            case TABLE_RSS:
                id = db.insert("rss",null, values);
                path = "rss";
                break;
            case COLONNE_TITLE:
                String pays = uri.getPathSegments().get(1);
                //System.out.println("TEST::::::" + pays + "    " + annee);
            default:
                throw new UnsupportedOperationException("this insert not yet implemented");
        }
        Uri.Builder builder = (new Uri.Builder())
                .authority(authority)
                .appendPath(path);

        return ContentUris.appendId(builder, id).build();
    }

    @Override
    public boolean onCreate() {
        base = Base.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d("provider","start query");
        SQLiteDatabase db = base.getReadableDatabase();
        int code = uriMatcher.match(uri);
        Cursor cursor;
        switch (code) {
            case TABLE_RSS:
                cursor = db.query("rss", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case COLONNE_TITLE:
                cursor = db.query("rss", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case COLONNE_LINK:
                cursor = null;
                break;
            case COLONNE_DESCRIPTION:
                cursor = null;
                break;
            case COLONNE_CHOISI:
                cursor = db.query("rss", projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.d("Uri provider def=", uri.toString());
                throw new UnsupportedOperationException("this query is not yet implemented  " +
                        uri.toString());
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = base.getReadableDatabase();
        int code = uriMatcher.match(uri);
        switch(code){
            case TABLE_RSS:
                db.update("rss", values, selection, selectionArgs);
                return 1;
            case COLONNE_CHOISI:
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                Date d = cal.getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                //String currentDate = format.format(d);

                selection = "choisi = 1";
                String date_choix = "";
                selectionArgs = new String []{date_choix};
                Cursor cursor = db.query("rss", new String []{"date_choisi"}, selection, selectionArgs, null, null, null);

                if(cursor.getCount() == 0){
                    return  -1;
                }
                cursor.moveToFirst();
                date_choix =  cursor.getString(cursor.getColumnIndex("date_choisi"));

                try {
                    Date databaseResult = format.parse(date_choix);
                    long diffDate = d.getTime() - databaseResult.getTime();
                    System.out.println("TEST::::" + diffDate);
                    if(diffDate > 3){
                        db.update("rss", values,selection, selectionArgs);
                    }
                }catch(ParseException p){}
                return 1;
            default:
                return 0;
        }
    }
}
