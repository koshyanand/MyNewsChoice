package com.kojen.mynewschoice.adapter;

/**
 * Created by koshy on 27/07/16.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kojen.mynewschoice.R;
import com.kojen.mynewschoice.pojo.MetaInfo;
import com.kojen.mynewschoice.ui.ItemListActivity;
import com.kojen.mynewschoice.util.Constants;

import java.util.Arrays;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private List<MetaInfo> metaInfoList;
    private Activity activity;
    public static final String TAG = MainAdapter.class.getSimpleName();

    public MainAdapter(Activity activity, List<MetaInfo> metaInfoList) {
        this.metaInfoList = metaInfoList;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.items_header_card_view, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder customViewHolder, int i) {
        if (metaInfoList == null | (metaInfoList != null && metaInfoList.size() == i)) {
            customViewHolder.mNewsHeader.setVisibility(View.GONE);
            customViewHolder.addImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDataEntryDialog(activity);
                }
            });
            return;
        }
        final MetaInfo metaInfo = metaInfoList.get(i);
        customViewHolder.mNewsHeader.setText(metaInfo.getTopicName());
        customViewHolder.mNewsHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ItemListActivity.class);
                intent.putExtra(Constants.META_INFO, metaInfo);
                activity.startActivity(intent);
            }
        });
        customViewHolder.addImage.setVisibility(View.GONE);
    }

    private void showDataEntryDialog(final Activity activity) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.add_new_item_dialog, (ViewGroup) activity.findViewById(R.id.liner_layout));
        final EditText topicName = (EditText) layout.findViewById(R.id.topic_edit);
        final EditText frequencyCount = (EditText) layout.findViewById(R.id.frequency_edit);
        final Button addNewsButton = (Button) layout.findViewById(R.id.add_news_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.show();
        addNewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (topicName.getText().length() == 0) {
                    Toast.makeText(activity, "Please enter the topic name.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                MetaInfo metaInfo = new MetaInfo();
                metaInfo.setTopicName(topicName.getText().toString());
                int refreshRate = 20;
                if (frequencyCount.getText().length() > 0) {
                    refreshRate = Integer.valueOf(frequencyCount.getText().toString()).intValue();
                }
                metaInfo.setRefreshRate(refreshRate);
                setNewMetaInfo(activity, metaInfo);
//                Intent intent = activity.getIntent();
//                activity.finish();
//                activity.startActivity(intent);
                activity.recreate();
                dialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        if (metaInfoList == null) {
            return 1;
        }
        return metaInfoList.size() + 1;
    }

    private void setNewMetaInfo(Activity activity, MetaInfo metaInfo) {
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json;
        if (metaInfoList == null) {
            metaInfo.setId(0);
            List<MetaInfo> metaInfos = Arrays.asList(metaInfo);
            json = gson.toJson(metaInfos);
        } else {
            MetaInfo lastMetaInfo = metaInfoList.get(metaInfoList.size() - 1);
            metaInfo.setId(lastMetaInfo.getId() + 1);
            metaInfoList.add(metaInfo);
            json = gson.toJson(metaInfoList);
        }
        editor.putString(Constants.META_INFO_LIST, json);
        editor.apply();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mNewsHeader;
        public ImageView addImage;
        public ViewHolder(View v) {
            super(v);
            addImage = (ImageView) v
                    .findViewById(R.id.add_image);
            mNewsHeader = (TextView) v
                    .findViewById(R.id.news_header_title);

        }

    }
}
