package com.androidclass.stryker.mplrss;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements ListeRSS.OnFragmentInteractionListener {

    private FragmentManager f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        f = getSupportFragmentManager();
        FragmentTransaction t = f.beginTransaction();
        ListeRSS p = ListeRSS.newInstance();
        //t.replace(R.id.liste_fragment_frame, ListPaysFragment.newInstance());
        t.add(R.id.liste_fragment_frame, p);
        t.commit();
        Log.d("Tag", "test2");

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRSSSelection(String title) {
        UnRSS u = UnRSS.newInstance(title);
        FragmentTransaction t = f.beginTransaction();
        //t.replace(R.id.liste_fragment_frame, ListPaysFragment.newInstance());
        t.replace(R.id.liste_fragment_frame, u);
        t.addToBackStack(null);
        //t.addToBackStack(null);FragmentTransaction t;
        t.commit();
    }

    public boolean init(){
        return true;
    }
}
