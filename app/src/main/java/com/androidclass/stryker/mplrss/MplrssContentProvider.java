package com.androidclass.stryker.mplrss;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class MplrssContentProvider extends ContentProvider {
    private Base base;

    private static final String LOG = "BookContentProvider";
    private String authority = "fr.cartman.respect.my.authority";

    public final static int TABLE_RSS = 1;
    public final static int COLONNE_TITLE = 2;
    public final static int COLONNE_LINK = 3;
    public final static int COLONNE_DESCRIPTION = 4;
    public final static int COLONNE_SEARCH = 5;
    public final static int COLONNE_CHOISI = 6;
    public final static int CODE_FLUX = 10;
    public final static int CODE_FLUXDEL = 11;

    private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    {
        uriMatcher.addURI(authority, "rss", TABLE_RSS);
        uriMatcher.addURI(authority, "rss/title", COLONNE_TITLE);
        uriMatcher.addURI(authority, "rss/link", COLONNE_LINK);
        uriMatcher.addURI(authority, "rss/description", COLONNE_DESCRIPTION);
        uriMatcher.addURI(authority, "rss/choisi", COLONNE_CHOISI);
        uriMatcher.addURI(authority, "rss/search", COLONNE_SEARCH);
        uriMatcher.addURI(authority, "flux", CODE_FLUX);
        uriMatcher.addURI(authority, "flux/#", CODE_FLUXDEL);
    }

    public MplrssContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase sdb = base.getWritableDatabase();
        int code = uriMatcher.match(uri);
        int id;
        switch(code){
            case CODE_FLUXDEL:
                id = sdb.delete("rss","id_flux=? and choisi=?",new String[]{uri.getLastPathSegment(),"0"});
                id = sdb.delete("flux","id=?", new String[]{uri.getLastPathSegment()});
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return id;
    }

    @Override
    public String getType(Uri uri) {
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        base = Base.getInstance(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase sdb = base.getWritableDatabase();
        int code = uriMatcher.match(uri);
        long id;
        String path;
        switch (code) {
            case CODE_FLUX:
                Log.d("Uri Provider : ", uri.toString());
                id = sdb.insertOrThrow("flux", null, values);
                path = "rss";
                break;
            case TABLE_RSS:
                Log.d("Uri Provider : ", uri.toString());
                id = sdb.insertOrThrow("rss", null, values);
                path = "flux";
                break;
            default:
                Log.d("Uri Provider : ", uri.toString());
                throw new UnsupportedOperationException("Not yet implemented");
        }
        Uri.Builder builder = (new Uri.Builder()).authority(authority).appendPath(path);
        return ContentUris.appendId(builder, id).build();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d("provider", "start query");
        SQLiteDatabase db = base.getReadableDatabase();
        int code = uriMatcher.match(uri);
        Cursor cursor;
        switch (code) {
            case CODE_FLUX:
                cursor = db.query("flux", projection, selection, selectionArgs, null, null, sortOrder);
                break;
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
            case COLONNE_SEARCH:
                cursor = db.query("rss", projection, selection, selectionArgs, null, null, null);
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
        db.update("rss", values, selection, selectionArgs);
        return 1;
    }


}
