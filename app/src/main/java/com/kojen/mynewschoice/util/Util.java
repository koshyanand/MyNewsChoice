package com.kojen.mynewschoice.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;
import android.util.Log;

import com.crazyhitty.chdev.ks.rssmanager.RssItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kojen.mynewschoice.R;
import com.kojen.mynewschoice.pojo.Item;
import com.kojen.mynewschoice.pojo.MetaInfo;
import com.kojen.mynewschoice.ui.MainActivity;

import org.chromium.customtabsclient.shared.CustomTabActivityHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by koshy on 27/07/16.
 */
public class Util {
    public static final boolean DEBUG = true;
    private static final String URL_START = "://";
    private static final String WWW = "www.";
    private static final String URL_END = "/";
    private static final String SRC_DATE_FORMAT = "EEE MMM dd hh:mm:ss z yyyy";
    private static final String TARGET_DATE = "dd MMM";
    private static final String URL_TOKEN = "url=";
    private static String META = "meta[property=og:";
    private static String META_ALT = "meta[property=:";
    private static String[] META_DETAILS = { "description", "site_name",
            "image"};

    public static List<MetaInfo> getMetaInfoList(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        SharedPreferences.Editor edit = sharedPreferences.edit();
//        edit.remove(Constants.META_INFO_LIST);
//        edit.apply();
        String json = sharedPreferences.getString(Constants.META_INFO_LIST, null);
        if (json == null) {
            return null;
        }
        Gson gson = new Gson();
        List<MetaInfo> metaInfoList = gson.fromJson(json, new TypeToken<List<MetaInfo>>() {}.getType());
        return metaInfoList;
    }

    public static String getLocalFile(File configFilePath) throws IOException {
        FileInputStream is = new FileInputStream(configFilePath);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return new String(buffer, "UTF-8");
    }

    public static String getSiteNamefromURL(String url) {

        int index;
        int startIndex = url.indexOf(URL_START);
        int endIndex = url.indexOf(URL_END, startIndex + URL_START.length());

        if (endIndex < 1) {
            endIndex = url.length();
        }
        String siteName = url.substring(startIndex + URL_START.length(), endIndex);

        if ((index = siteName.indexOf(WWW)) > -1) {
            return  siteName.substring(index + WWW.length(), siteName.length());
        } else {
            return siteName;
        }
    }

    public String getNewsDate(String dateStr) {
        SimpleDateFormat formatted = new SimpleDateFormat(SRC_DATE_FORMAT);
        try {
            Date date = formatted.parse(dateStr);
            formatted = new SimpleDateFormat(TARGET_DATE);
            String format = formatted.format(date);
            return format;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return getNewsDate(Calendar.getInstance().getTime().toString());
    }

    public static Document parseLink(String link) throws IOException {
        Document doc = Jsoup.connect(link)
                .timeout(40000).ignoreHttpErrors(true).ignoreContentType(true).get();
        return doc;
    }

    public static String getURL(String url) {
        int startIndex = url.indexOf(URL_TOKEN);
        int endIndex = url.indexOf('&', startIndex);
        if (endIndex < 1) {
            endIndex = url.length();
        }
        return url.substring(startIndex + URL_TOKEN.length(), endIndex);
    }

    public static Item getNewsItem(String link)
            throws NullPointerException, IOException {
        Document document = parseLink(link);
        int metaLength = META_DETAILS.length;
        String[] meta = new String[metaLength];
        Item item = new Item();
        for (int i = 0; i < metaLength; i++) {
            Elements element = document.select(META + META_DETAILS[i] + "]");
            if (element == null) {
                element = document.select(META_ALT + META_DETAILS[i] + "]");
            }
            if (element != null) {
                meta[i] = element.attr("content");
            } else {
                meta[i] = "null";
            }
        }

//        String date = checkForDate(document);
//        if (date != null) {
//            item.setPublisherDate(date);
//        }
        if (meta[0] != null && !TextUtils.isEmpty(meta[0])) {
            item.setDescription(meta[0]);
        }

        if (meta[1] != null && !meta[1].isEmpty()) {
            item.setAuthor(meta[1]);
        } else {
            String siteNamefromURL = Util.getSiteNamefromURL(document.location());
            item.setAuthor(siteNamefromURL);
        }

        if (meta[2] != null && !meta[2].isEmpty()) {
            item.setImageUrl(meta[2]);
        }
        return item;
    }

    public static String checkForDate(Document doc) {
        String docInString = doc.toString();
        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date currentDate = cal.getTime();
        boolean dateIsPresent;
        cal.add(Calendar.MONTH, -1);
        Date monthOldDate = cal.getTime();

        String[] checkDate = new String[2];
        checkDate[0] = currentDate.toString();
        checkDate[1] = monthOldDate.toString();
        for (String date : checkDate) {
            String dateDetails = getDateDetails(date);
            String[] split = dateDetails.split("\\s+");
            int[] subIndexs = new int[2];
            int i = 0;
            dateIsPresent = true;
            for (String format : split) {
                int indexOf = docInString.indexOf(format);
                subIndexs[i] = indexOf;
                System.out.println("Index for : " + format + " :" + indexOf);
                if (indexOf > -1 && dateIsPresent) {
                    System.out.println("Found Date  : " + format);
                } else {
                    dateIsPresent = false;
                    System.out.println("Date not Found ");
                }
                i++;
            }
            if (dateIsPresent) {
                return currentDate.toString();
            }
        }
        return null;
    }

    public static String getDateDetails(String dateStr) {
        SimpleDateFormat formatted = new SimpleDateFormat(SRC_DATE_FORMAT);
        try {
            Date date = formatted.parse(dateStr);
            formatted = new SimpleDateFormat(TARGET_DATE);
            String format = formatted.format(date);
            return format;
        } catch (ParseException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return getDateDetails(Calendar.getInstance().getTime().toString());
    }

    public static String correctTitle(String title) {
        int endIndex = title.lastIndexOf(" - ");
        if (endIndex < 0) {
            endIndex = title.lastIndexOf(" | ");
        }
        if(endIndex > -1) {
            return title.substring(0, endIndex);
        }
        return title;
    }


    public static void openWebsite(final Activity activity, String link) {
//        mTracker.setScreenName(TAG);
//        mTracker.send(new HitBuilders.EventBuilder().setCategory("News")
//                .setAction(item.getPlayers().get(0)).setLabel(item.getTitle()).build());

        if (Build.VERSION.SDK_INT >= 16) {
            //chrome tab
            CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder().setToolbarColor(activity.getResources().getColor(R.color.black));
//            intentBuilder.setStartAnimations(activity, R.anim.right_slide_in, R.anim.left_side_out);
            intentBuilder.setExitAnimations(activity, android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
            CustomTabActivityHelper.openCustomTab(activity, intentBuilder.build(), Uri.parse(link),
                    new CustomTabActivityHelper.CustomTabFallback() {
                        @Override
                        public void openUri(Activity activity, Uri uri) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            activity.startActivity(intent);
                        }
                    });
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            activity.startActivity(intent);
        }
    }

}
