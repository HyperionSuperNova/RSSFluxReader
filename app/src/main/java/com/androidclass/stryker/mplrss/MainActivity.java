package com.androidclass.stryker.mplrss;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            tv.setText(toset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public boolean init(){
        return true;
    }
}
