package com.kojen.mynewschoice.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.crazyhitty.chdev.ks.rssmanager.OnRssLoadListener;
import com.crazyhitty.chdev.ks.rssmanager.RssItem;
import com.crazyhitty.chdev.ks.rssmanager.RssReader;
import com.kojen.mynewschoice.R;
import com.kojen.mynewschoice.adapter.ItemListAdapter;
import com.kojen.mynewschoice.pojo.MetaInfo;
import com.kojen.mynewschoice.pojo.Item;
import com.kojen.mynewschoice.util.Constants;
import com.kojen.mynewschoice.util.ItemAsync;
import com.kojen.mynewschoice.util.ItemsLoadedCallback;
import com.kojen.mynewschoice.util.RSSGen;
import com.kojen.mynewschoice.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ItemListActivity extends AppCompatActivity implements ItemsLoadedCallback{
    private static final String TAG = ItemListActivity.class.getSimpleName();
    MetaInfo metaInfo;
    ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private ItemListAdapter mAdapter;// GridView items list
//    AdView adView;
//    public static Tracker mTracker;

    public static final String NO_NEW_DATA = "400";
    List<Item> itemList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Util.DEBUG) {
            Log.d(TAG, "From on create" );
        }
        setContentView(R.layout.item_list_activity);
        Intent intent = getIntent();
        metaInfo = (MetaInfo) intent.getSerializableExtra(Constants.META_INFO);
        progressBar = (ProgressBar) findViewById(R.id.items_loading);
        mRecyclerView = (RecyclerView) findViewById (R.id.recycler_view_items);
        progressBar.setVisibility(View.VISIBLE);
//        loadNewsImages = prefs.getBoolean(Constants.LOAD_NEWS_IMAGES, true);

        ItemAsync async = new ItemAsync(this, metaInfo, this);
        async.execute();

    }


    private void refreshAdapter() {
//        mRecyclerView.setLayoutManager(new GridLayoutManager(activity, getResources()
//                .getInteger(R.integer.grid_columns_news)));
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mAdapter = new ItemListAdapter(this, itemList);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void dataRecieved(List<Item> itemList) {
        if (itemList != null && itemList.size() > 0) {
            this.itemList = itemList;
            refreshAdapter();
        }
    }
}
