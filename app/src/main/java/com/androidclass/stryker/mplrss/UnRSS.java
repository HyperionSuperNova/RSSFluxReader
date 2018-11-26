package com.androidclass.stryker.mplrss;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UnRSS#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnRSS extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "titleRSS";
    private static final String ARG_PARAM2 = "fav";

    // TODO: Rename and change types of parameters
    private String titleRSS;
    private boolean favRSS;

    private ContentResolver contentResolver;

    private String authority = "fr.cartman.respect.my.authority";


    private TextView title;
    private TextView link;
    private TextView description;

    public UnRSS() {
        // Required empty public constructor
    }

    public void setContentResolver(Context context){
        contentResolver = context.getContentResolver();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param nomPays nom du pays.
     * @return A new instance of fragment UnPaysFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UnRSS newInstance(String nomPays, boolean fav) {
        UnRSS fragment = new UnRSS();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, nomPays);
        args.putBoolean(ARG_PARAM2, fav);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            titleRSS= getArguments().getString(ARG_PARAM1);
            favRSS = getArguments().getBoolean(ARG_PARAM2);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_un_rss, container, false);
        // Inflate the layout for this fragment
        title = (TextView) v.findViewById(R.id.title);
        link = (TextView) v.findViewById(R.id.link);
        description = (TextView) v.findViewById(R.id.description);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);

        Button maListe = (Button) v.findViewById(R.id.maListe);
        Button sup = (Button) v.findViewById(R.id.supprimer);

        if(favRSS){
            maListe.setVisibility(View.INVISIBLE);
            sup.setVisibility(View.VISIBLE);
        }else{
            sup.setVisibility(View.INVISIBLE);
            maListe.setVisibility(View.VISIBLE);
        }


        maListe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO mettre RSS dans liste utilisateur
                Uri.Builder builder = new Uri.Builder();
                Uri uri = builder.scheme("content").authority(authority).appendPath("rss").build();

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, 1);
                Date d = cal.getTime();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
                String currentDate = format.format(d);

                ContentValues c = new ContentValues();
                c.put("choisi", 1);
                c.put("date_choisi", currentDate);
                contentResolver.update(uri, c, "title = ?", new String [] {title.getText().toString()});

            }
        });

        return v;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content").authority(authority).appendPath("rss").build();
        return new CursorLoader(getActivity(), uri, new String[] {"rowid as _id","title", "link", "description"}, "title = ?", new String[] {titleRSS}, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        title.setText(titleRSS);
        cursor.moveToFirst();
        link.setText(cursor.getString(cursor.getColumnIndex("link")));
        description.setText(cursor.getString(cursor.getColumnIndex("description")));

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }
}

