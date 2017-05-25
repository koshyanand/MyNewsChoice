package com.kojen.mynewschoice.util;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by koshy on 28/06/16.
 */
public class RSSGen {

    public static String generateSearchRss(String name) {
        String url = null;
        try {
            url = "https://news.google.com/news/feeds?output=rss&q="
                    + URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String generateAlterSearchRss(String name) {
        String url = "http://news.google.com/news?q="
                + URLEncoder.encode(name)
                + "&hl=en&prmd=imvns&um=1&ie=UTF-8&output=rss";
        return url;
    }

    public static List<SyndEntry> getSyndEntriesFromRss(byte[] byteArray) {
        InputStream is = new ByteArrayInputStream(byteArray);
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = null;
        try {
            feed = input.build(new XmlReader(is));
        } catch (IllegalArgumentException | UnsupportedEncodingException
                | FeedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<SyndEntry> entries = feed.getEntries();
        return entries;
    }

    public static byte[] getUrlData(String path) {
        URL website;
        try {
            website = new URL(path);
            URLConnection con = website.openConnection();
            con.setUseCaches(false);
            InputStream in = new BufferedInputStream(con.getInputStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            return response;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }




}
