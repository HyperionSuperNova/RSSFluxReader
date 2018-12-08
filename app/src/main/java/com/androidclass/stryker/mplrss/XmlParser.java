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
    public String datepub;
    public String dateChoisi;

    public XmlParser(String title, String link, String description, String datepub, String dateChoisi) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.datepub = datepub;
        this.dateChoisi = dateChoisi;
    }

}