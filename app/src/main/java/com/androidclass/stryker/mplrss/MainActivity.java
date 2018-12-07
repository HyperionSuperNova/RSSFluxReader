package com.androidclass.stryker.mplrss;

import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    private FragmentManager f;
    ProgressBar pb;
    DownloadManager dm;
    Uri uri;
    long id;
    public DataAccess ad;
    RecyclerView mRecyclerView;
    private List<XmlParser> mFeedModelList;
    private List<Flux> fluxList;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;
    private String mFeedDateLastChange;
    private RecyclerView.LayoutManager mLayoutManager;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ad = new DataAccess(this);
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        List<Flux> flux = ad.storedFlux();
        if(!flux.isEmpty()){
            fluxList = flux;
            mRecyclerView.setAdapter(new FluxAdapter(fluxList));
        }else{
            fluxList = new ArrayList<>();
        }
    }

    private void load(String s) {
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                act();
            }
        };
        uri = Uri.parse(s);
        DownloadManager.Request req = new DownloadManager.Request(uri);
        id = dm.enqueue(req);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        //ad.ajoutFlux(s,mFeedTitle);
    }

    private void act() {
        DownloadManager.Query question = new DownloadManager.Query();
        question.setFilterById(id).setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cur = dm.query(question);
        System.out.println(cur != null);
        if (cur != null) {
            ParcelFileDescriptor pDesc = null;
            ParcelFileDescriptor pDesc2 = null;

            try {
                pDesc = dm.openDownloadedFile(id);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (pDesc != null) {
                FileDescriptor desc = pDesc.getFileDescriptor();
                try {
                    ArrayList<List> al =parseName(new FileInputStream(desc));
                    List <Flux> tmp = al.get(0);
                    int id = -1;
                    for(Flux f : tmp){
                        fluxList.add(f);
                        ad.ajoutFlux(f.link,f.title,f.description);
                        id = ad.getIdFlux(f.title);
                    }
                    List<XmlParser> xp = al.get(1);
                    for(XmlParser x : xp){
                        ad.ajoutItems(x.title,id,x.link,x.description,x.datepub);
                    }

                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
                mRecyclerView.setAdapter(new FluxAdapter(fluxList));
                //mRecyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));

            }
        } else {
            Toast t = Toast.makeText(this, "Not Done Yet Please Wait", Toast.LENGTH_SHORT);
            t.show();
        }
    }

    public static String dateFormater(String dateFromJSON, String expectedFormat, String oldFormat) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat, Locale.ENGLISH);
        Date date = null;
        String convertedDate = null;
        try {
            date = dateFormat.parse(dateFromJSON);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(expectedFormat, Locale.ENGLISH);
            convertedDate = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertedDate;
    }

    public ArrayList<List> parseName(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        String dateLastChange = null;
        boolean isItem = false;
        String pubDate = null;
        List<XmlParser> items = new ArrayList<>();
        List<Flux> items2 = new ArrayList<>();
        ArrayList<List> al = new ArrayList<>();
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;

                }else if (name.equalsIgnoreCase("pubDate")) {
                    dateLastChange = result;

                }

                if (title != null && link != null && description != null) {
                    if (isItem) {
                        String formattedDate = dateFormater(dateLastChange, "yyyy-MM-dd","EEE, dd MMM yyyy HH:mm:ss Z");
                        XmlParser item = new XmlParser(title, link, description,formattedDate);
                        items.add(item);

                    } else {
                        mFeedTitle = title;
                        mFeedLink = link;
                        mFeedDescription = description;
                        mFeedDateLastChange = dateLastChange;
                        items2.add(new Flux(mFeedLink,mFeedTitle,description, dateLastChange));


                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

        }finally {
            inputStream.close();
        }
        al.add(items2);
        al.add(items);
        return al;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
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

            case R.id.load:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                final EditText et = new EditText(MainActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String adresse = et.getText().toString();
                        Toast.makeText(MainActivity.this, "téléchargement de: " + adresse, Toast.LENGTH_SHORT).show();
                        if(!adresse.isEmpty()){
                            load(adresse);
                        }

                        Intent i = new Intent(MainActivity.this, Notification_Download.class);
                        i.putExtra("adresse", adresse);
                        i.setAction("notif");
                        startService(i);
                    }
                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();

                return true;

            case R.id.search:
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

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
                        item2.collapseActionView();
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(String s) {
                        // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                        return false;
                    }
                });

                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public List<XmlParser> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException, ParseException {

        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<XmlParser> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }else if(name.equalsIgnoreCase("pubDate")){
                    //date = formatter.parse(result);
                }

                if (title != null && link != null && description != null) {
                    if (isItem) {
                        XmlParser item = new XmlParser(title, link, description,"");
                        items.add(item);
                    } else {
                        mFeedTitle = title;
                        mFeedLink = link;
                        mFeedDescription = description;

                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

        } finally {
            inputStream.close();
        }
        return items;

    }

}
