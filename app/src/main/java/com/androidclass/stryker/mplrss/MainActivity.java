package com.androidclass.stryker.mplrss;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ListeRSS.OnFragmentInteractionListener, ListeRSSFav.OnFragmentInteractionListener, ListeRSSSearch.OnFragmentInteractionListener {
    TextView tv;
    private FragmentManager f;
    private ListeRSS p;

    private String authority = "fr.cartman.respect.my.authority";
    public final static String TABLE_RSS = "rss";

    private ContentResolver contentResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentResolver = this.getContentResolver();

        f = getSupportFragmentManager();
        FragmentTransaction t = f.beginTransaction();
        p = ListeRSS.newInstance();
        p.setContentResolver(MainActivity.this);
        p.ajoutRSS("France", "Paris", "Europe");
        p.ajoutRSS("Togo", "Lomé", "Afrique");
        p.ajoutRSS("Bhoutan", "Thimphou", "Asie");
        p.ajoutRSS("France1", "Paris", "Europe");
        p.ajoutRSS("Togo1", "Lomé", "Afrique");
        p.ajoutRSS("Bhoutan1", "Thimphou", "Asie");
        p.ajoutRSS("France2", "Paris", "Europe");
        p.ajoutRSS("Togo2", "Lomé", "Afrique");
        p.ajoutRSS("Bhoutan2", "Thimphou", "Asie");
        p.ajoutRSS("France3", "Paris", "Europe");
        p.ajoutRSS("Togo3", "Lomé", "Afrique");
        p.ajoutRSS("Bhoutan3", "Thimphou", "Asie");
        p.ajoutRSS("France4", "Paris", "Europe");
        p.ajoutRSS("Togo4", "Lomé", "Afrique");
        p.ajoutRSS("Bhoutan4", "Thimphou", "Asie");
        p.ajoutRSS("France5", "Paris", "Europe");
        p.ajoutRSS("Togo5", "Lomé", "Afrique");
        p.ajoutRSS("Bhoutan5", "Thimphou", "Asie");
        p.ajoutRSS("France6", "Paris", "Europe");
        p.ajoutRSS("Togo6", "Lomé", "Afrique");
        p.ajoutRSS("Bhoutan6", "Thimphou", "Asie");
        p.ajoutRSS("France7", "Paris", "Europe");
        p.ajoutRSS("Togo7", "Lomé", "Afrique");
        p.ajoutRSS("Bhoutan7", "Thimphou", "Asie");
        p.ajoutRSS("France8", "Paris", "Europe");
        //t.replace(R.id.liste_fragment_frame, ListPaysFragment.newInstance());
        t.add(R.id.liste_fragment_frame, p);
        t.commit();
        Log.d("Tag", "test2");
        /*

        AssetManager am = getAssets();
        try {
            InputStreamReader idr = new InputStreamReader(am.open("BFMPOLITIQUE.xml"));
            XmlParser xp = new XmlParser();
            HashMap content = xp.content(xp.uriReader("file://android_asset/BFMPOLITIQUE.xml"));
            Object obj = content.get("title");
            ArrayList <String> title = null;
            if(obj instanceof ArrayList){
                 title = ((ArrayList <String>) obj);
            }
            String toset = "";
            for(String s : title){
                toset+= s;
            }
            System.out.println("TEST::::::" + toset);
            tv.setText(toset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

    }

    public boolean init(){

        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRSSSelection(String title) {
        UnRSS u = UnRSS.newInstance(title, false);
        u.setContentResolver(MainActivity.this);
        FragmentTransaction t = f.beginTransaction();
        //t.replace(R.id.liste_fragment_frame, ListPaysFragment.newInstance());
        t.replace(R.id.liste_fragment_frame, u);
        t.addToBackStack(null);
        //t.addToBackStack(null);FragmentTransaction t;
        t.commit();
    }

    @Override
    public void onRSSSelectionFav(String title) {
        UnRSS u = UnRSS.newInstance(title, true);
        u.setContentResolver(MainActivity.this);
        FragmentTransaction t = f.beginTransaction();
        //t.replace(R.id.liste_fragment_frame, ListPaysFragment.newInstance());
        t.replace(R.id.liste_fragment_frame, u);
        t.addToBackStack(null);
        //t.addToBackStack(null);FragmentTransaction t;
        t.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("content").authority(authority).appendPath(TABLE_RSS).appendPath("search");
                Uri uri = builder.build();
                Cursor c = contentResolver.query(uri, new String[] {"rowid as _id", "title"}, "title LIKE ?",new String [] {"%"+query+"%"}, null);

                f = getSupportFragmentManager();
                FragmentTransaction t = f.beginTransaction();
                ListeRSSSearch p = ListeRSSSearch.newInstance(query);
                p.setContentResolver(MainActivity.this);

                t.replace(R.id.liste_fragment_frame, p);
                t.addToBackStack(null);
                t.commit();


                Log.d( "SearchOnQueryTextSubmit: ", query);
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mes_rss:
                FragmentTransaction t = f.beginTransaction();
                ListeRSSFav p = ListeRSSFav.newInstance();
                p.setContentResolver(MainActivity.this);
                t.replace(R.id.liste_fragment_frame, p);
                t.addToBackStack(null);
                t.commit();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
