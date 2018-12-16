package com.androidclass.stryker.mplrss;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UnRSS#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnRSS extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
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
    private WebView wv;

    public UnRSS() {
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
        title = (TextView) v.findViewById(R.id.title);
        link = (TextView) v.findViewById(R.id.link);
        description = (TextView) v.findViewById(R.id.description);
        wv = v.findViewById(R.id.Webview);
        wv.setWebViewClient(new WebViewClient());
        wv.setVerticalScrollBarEnabled(true);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("rss");
        Uri uri = builder.build();
        CursorLoader c = new CursorLoader(getActivity(),uri,new String[]{"rowid as _id","*"},"title=?",new String[]{titleRSS},null);
        Cursor cc = c.loadInBackground();
        cc.moveToFirst();

        title.setText(cc.getString(cc.getColumnIndexOrThrow("title")).trim());
        link.setText(cc.getString(cc.getColumnIndexOrThrow("link")).trim());
        description.setText(cc.getString(cc.getColumnIndexOrThrow("description")));


        Button maListe = (Button) v.findViewById(R.id.maListe);
        Button sup = (Button) v.findViewById(R.id.supprimer);
        Button open = (Button) v.findViewById(R.id.gotoBrowser);
        Button launchwebview = v.findViewById(R.id.openInWebView);
        if(favRSS){
            maListe.setVisibility(View.INVISIBLE);
            sup.setVisibility(View.VISIBLE);
        }else{
            sup.setVisibility(View.INVISIBLE);
            maListe.setVisibility(View.VISIBLE);
        }

        open.setVisibility(View.VISIBLE);

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iii = new Intent(Intent.ACTION_VIEW);
                iii.setData(Uri.parse(link.getText().toString().trim()));
                startActivity(iii);
            }
        });

        maListe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri.Builder builder = new Uri.Builder();
                Uri uri = uri = builder.scheme("content").authority(authority).appendPath("rss").build();

                ContentValues c = new ContentValues();
                c.put("choisi", 1);

                contentResolver.update(uri, c, "title= ?", new String [] {title.getText().toString()});
                getFragmentManager().popBackStack();

            }
        });

        sup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri.Builder builder = new Uri.Builder();
                Uri uri = builder.scheme("content").authority(authority).appendPath("rss").build();

                ContentValues c = new ContentValues();
                c.put("choisi", 0);
                contentResolver.update(uri, c, "title = ?", new String [] {title.getText().toString()});
                getFragmentManager().popBackStack();
            }
        });

        launchwebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv.loadUrl(link.getText().toString());
            }
        });



        return v;
    }

}

