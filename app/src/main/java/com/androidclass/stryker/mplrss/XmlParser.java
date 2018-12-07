package com.androidclass.stryker.mplrss;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.*;
public class XmlParser {

    public String title;
    public String link;
    public String description;

    public XmlParser(String title, String link, String description) {
        this.title = title;
        this.link = link;
        this.description = description;
    }

    public Document uriReader(String uri_path){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            try {
                return db.parse(uri_path);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String,ArrayList<String>> content(Document doc){
        HashMap <String,ArrayList<String>> tree = new HashMap<>();
        NodeList nl = doc.getElementsByTagName("channel");
        //NodeList nl2 = doc.getElementsByTagName("link");
        //NodeList nl3 = doc.getElementsByTagName("description");
        tree.put("title",nodesToArray(nl));
        //tree.put("description",nodesToArray(nl2));
        //tree.put("link",nodesToArray(nl3));
        return tree;
    }

    public ArrayList<String> nodesToArray(NodeList nl){
        ArrayList <String> al = new ArrayList<>();
        for(int i = 0; i < nl.getLength();i++){
            al.add(nl.item(i).getTextContent());
        }
        return al;
    }


}
