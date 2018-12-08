package com.androidclass.stryker.mplrss;

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