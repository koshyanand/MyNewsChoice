package com.kojen.mynewschoice.util;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.crazyhitty.chdev.ks.rssmanager.OnRssLoadListener;
import com.crazyhitty.chdev.ks.rssmanager.RssItem;
import com.crazyhitty.chdev.ks.rssmanager.RssReader;
import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;
import com.kojen.mynewschoice.pojo.MetaInfo;
import com.kojen.mynewschoice.ui.ItemListActivity;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.DataFormatException;

/**
 * Created by koshy on 27/07/16.
 */
public class ItemAsync extends AsyncTask<Void, Void, Void> {
    MetaInfo metaInfo;
    Activity activity;
    ItemsLoadedCallback callback;
    List<com.kojen.mynewschoice.pojo.Item> itemList;
    public static final String TAG = ItemAsync.class.getSimpleName();

    public ItemAsync(ItemListActivity itemListActivity, MetaInfo metaInfo, ItemsLoadedCallback callback) {
        this.activity = itemListActivity;
        this.metaInfo = metaInfo;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {

        String urlPath = RSSGen.generateSearchRss(metaInfo.getTopicName());
        Feed feed = null;
        try {
            InputStream inputStream = new URL(urlPath).openConnection().getInputStream();
            feed = EarlParser.parseOrThrow(inputStream, 0);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Processing feed: " + feed.getTitle());
        List<com.kojen.mynewschoice.pojo.Item> items = new ArrayList<>();
        for (Item item : feed.getItems()) {
            String link = Util.getURL(item.getLink());
            try {
                com.kojen.mynewschoice.pojo.Item newsItem = Util.getNewsItem(link);
                Date publicationDate = item.getPublicationDate();
                newsItem.setTitle(item.getTitle());
                newsItem.setLink(link);
                if (publicationDate != null) {
                    String dateDetails = Util.getDateDetails(publicationDate.toString());
                    newsItem.setPublisherDate(dateDetails);
                }
                items.add(newsItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        itemList = items;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.dataRecieved(itemList);
    }

    //load feeds


}
