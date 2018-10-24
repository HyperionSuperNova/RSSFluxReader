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
    public final static int COLONNE_DATE_LAST_CHANGE = 5;
    public final static int COLONNE_DATE_CHOISI = 6;

    private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    {
        uriMatcher.addURI(authority, "rss", TABLE_RSS);
        uriMatcher.addURI(authority, "rss/title", COLONNE_TITLE);
        uriMatcher.addURI(authority, "rss/link", COLONNE_LINK);
        uriMatcher.addURI(authority, "rss/description", COLONNE_DESCRIPTION);
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
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
