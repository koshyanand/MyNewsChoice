package com.kojen.mynewschoice.util;

import com.rometools.rome.feed.synd.SyndEntry;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by koshy on 27/07/16.
 */
public class Test {
    public static final String TAG = Test.class.getSimpleName();

    public static void main(String[] args) throws IOException, SAXException {
        String path = RSSGen.generateSearchRss("Mancity Transfer News");
//        path = "https://news.google.com/news/feeds?output=rss&q=mancity";
        byte[] urlData = RSSGen.getUrlData(path);
        List<SyndEntry> syndEntriesFromRss = RSSGen.getSyndEntriesFromRss(urlData);
        System.out.println(syndEntriesFromRss.get(0).getDescription().getValue());
    }
}
