package com.androidclass.stryker.mplrss;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ListeRSSFav.OnFragmentInteractionListener {
    private FragmentManager f;
    ProgressBar pb;
    DownloadManager dm;
    Uri uri;
    long id;
    public DataAccess ad;
    RecyclerView mRecyclerView;
    private List<Flux> fluxList;
    private RecyclerView.LayoutManager mLayoutManager;
    FluxAdapter adapt;
    SwipeController sp;

    private int id_flux = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ad = new DataAccess(this);
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        sp = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                ad.deleteItem(ad.getIdFlux(adapt.l.get(position).title));
                adapt.l.remove(position);
                adapt.notifyItemRemoved(position);
                adapt.notifyItemRangeChanged(position, adapt.getItemCount());
            }
        });
        ItemTouchHelper ith = new ItemTouchHelper(sp);
        List<Flux> flux = ad.storedFlux();
        ith.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                sp.onDraw(c);
            }
        });
        if (!flux.isEmpty()) {
            fluxList = flux;
            adapt = new FluxAdapter(fluxList);
            mRecyclerView.setAdapter(adapt);
        } else {
            fluxList = new ArrayList<>();
        }

        ArrayList<Integer> oldFlux = ad.getOldFlux();
        for(Integer i: oldFlux){
            ad.deleteItem(i);
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
    }

    private void act() {
        DownloadManager.Query question = new DownloadManager.Query();
        question.setFilterById(id).setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor cur = dm.query(question);
        System.out.println(cur != null);
        if (cur != null) {
            ParcelFileDescriptor pDesc = null;

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
                    boolean ajout = false;
                    for(Flux f : tmp){
                        fluxList.add(f);
                        ajout = ad.ajoutFlux(f.link,f.title,f.description, f.dateChoisi);
                        if(ajout) id = ad.getIdFlux(f.title);
                    }
                    if(ajout) {
                        id_flux = id;
                        List<XmlParser> xp = al.get(1);
                        for (XmlParser x : xp) {
                            ad.ajoutItems(x.title, id, x.link, x.description, x.datepub);
                        }
                    }

                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
                fluxList = ad.storedFlux();
                adapt = new FluxAdapter(fluxList);
                mRecyclerView.setAdapter(adapt);

            }
        } else {
            Toast t = Toast.makeText(this, "Not Done Yet Please Wait", Toast.LENGTH_SHORT);
            t.show();
        }
    }


    public ArrayList<List> parseName(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        String dateLastChange = null;
        boolean isItem = false;
        List<XmlParser> items = new ArrayList<>();
        List<Flux> items2 = new ArrayList<>();
        ArrayList<List> al = new ArrayList<>();
        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(inputStream, null);

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
                if (title != null && link != null && description != null && dateLastChange != null) {
                    if (isItem) {
                        String formattedDate = dateFormater(dateLastChange, "yyyy-MM-dd","EEE, dd MMM yyyy HH:mm:ss Z");
                        XmlParser item = new XmlParser(title, link, description,formattedDate);
                        items.add(item);
                        Log.d("My XML PARSER", title + " " + link + " " + description + " " + " " + dateLastChange);
                    } else {
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, 1);
                        Date d = cal.getTime();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String dateChoisi = format.format(d);
                        items2.add(new Flux(link,title,description, dateChoisi));
                    }
                    title = null;
                    link = null;
                    description = null;
                    dateLastChange = null;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        f = getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.accueil:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                return true;

            case R.id.mes_rss:
                Intent iii = new Intent(MainActivity.this, AfficheRSS.class);
                iii.putExtra("favori", "favori");
                startActivity(iii);
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
                        if (!adresse.isEmpty()) {
                            load(adresse);
                        }

                        Intent i = new Intent(MainActivity.this, Notification_Download.class);
                        i.putExtra("adresse", adresse);
                        i.putExtra("id_flux", Integer.toString(id_flux));
                        i.setAction("notif");
                        startService(i);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onRSSSelectionFav(String title) {
        UnRSS u = UnRSS.newInstance(title, true);
        u.setContentResolver(MainActivity.this);
        FragmentTransaction t = f.beginTransaction();
        t.replace(R.id.liste_fragment_frame, u);
        t.addToBackStack(null);
        t.commit();
    }
}
