package com.androidclass.stryker.mplrss;

import android.content.Intent;
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
import android.widget.SearchView;

public class AfficheRSS extends AppCompatActivity implements ListeRSS.OnFragmentInteractionListener, ListeRSSFav.OnFragmentInteractionListener, ListeRSSSearch.OnFragmentInteractionListener {

    private FragmentManager f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiche_rss);
        Bundle extras = getIntent().getExtras();
        int id_flux = extras.getInt("id_flux");
        String fav = extras.getString("favori");
        f = getSupportFragmentManager();
        FragmentTransaction t = f.beginTransaction();
        if(fav != null && fav.equals("favori")){
            ListeRSSFav p = ListeRSSFav.newInstance();
            t.replace(R.id.liste_fragment_frame, p);
            t.addToBackStack(null);
            t.commit();
        }else {
            ListeRSS p = ListeRSS.newInstance(id_flux);
            p.setContentResolver(AfficheRSS.this);
            t.add(R.id.liste_fragment_frame, p);
            t.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRSSSelection(String title) {
        UnRSS u = UnRSS.newInstance(title, false);
        u.setContentResolver(AfficheRSS.this);
        FragmentTransaction t = f.beginTransaction();
        t.replace(R.id.liste_fragment_frame, u);
        t.addToBackStack(null);
        t.commit();
    }

    @Override
    public void onRSSSelectionFav(String title) {
        UnRSS u = UnRSS.newInstance(title, true);
        u.setContentResolver(AfficheRSS.this);
        FragmentTransaction t = f.beginTransaction();
        t.replace(R.id.liste_fragment_frame, u);
        t.addToBackStack(null);
        t.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_affiche, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final MenuItem item2 = item;
        final SearchView searchView = (SearchView) item.getActionView();
        switch (item.getItemId()) {
            case R.id.accueil:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;

            case R.id.mes_rss:
                FragmentTransaction t = f.beginTransaction();
                ListeRSSFav p = ListeRSSFav.newInstance();
                t.replace(R.id.liste_fragment_frame, p);
                t.addToBackStack(null);
                t.commit();
                return true;

            case R.id.search:
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        f = getSupportFragmentManager();
                        FragmentTransaction t = f.beginTransaction();
                        ListeRSSSearch p = ListeRSSSearch.newInstance(query);
                        p.setContentResolver(AfficheRSS.this);

                        t.replace(R.id.liste_fragment_frame, p);
                        t.addToBackStack(null);
                        t.commit();


                        Log.d( "SearchOnQueryTextSubmit: ", query);
                        if( ! searchView.isIconified()) {
                            searchView.setIconified(true);
                        }
                        item2.collapseActionView();
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    public void gotoBrowser(View view) {
        Intent iii = new Intent(Intent.ACTION_VIEW);
        iii.setData(Uri.parse("https://google.fr/search?q=lul"));
        startActivity(iii);
    }
}
