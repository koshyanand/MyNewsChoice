package com.kojen.mynewschoice.adapter;

import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kojen.mynewschoice.R;
import com.kojen.mynewschoice.pojo.Item;
import com.kojen.mynewschoice.util.Util;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by koshy on 27/07/16.
 */
public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {
    public static final String TAG = ItemListAdapter.class.getSimpleName();
    public int height;
    public int width;
    Activity activity;
    public static List<Item> itemList;

    public ItemListAdapter(Activity activity, List<Item> itemList) {
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_details_card_view, parent, false);
        ItemViewHolder nvh = new ItemViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        if (itemList.size() == 0) {
            if (Util.DEBUG) {
                Log.d(TAG, "Empty Adapter!!");
            }
            holder.image.setVisibility(View.GONE);
            holder.title.setText("No News");
            return;
        }
        final Item item = itemList.get(position);
//            Bitmap bitmap = NewsUtil.readLocalImage(item.getLink().hashCode() + ".png", "/news/Images", activity);

        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {
            if (Util.DEBUG) {
                Log.d(TAG, "Image Path not null : " + imageUrl);
            }
            Picasso.with(activity).load(imageUrl).memoryPolicy(MemoryPolicy.NO_CACHE).fit().centerInside().into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
            holder.description.setVisibility(View.GONE);

        } else {
            if (Util.DEBUG) {
                Log.d(TAG, "No Image : " + item.getDescription());
            }

            holder.image.setVisibility(View.GONE);
            holder.description.setText(item.getDescription());
            holder.description.setVisibility(View.VISIBLE);
        }

        String siteName = item.getAuthor();
        if (siteName == null) {
            siteName = Util.getSiteNamefromURL(item.getLink());
        }

        holder.title.setText(item.getTitle());
        holder.date.setText(item.getPublisherDate());
        holder.siteName.setText(siteName);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title;
        ImageView image;
        TextView description;
        TextView date;
        TextView siteName;
        View itemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            date = (TextView) itemView.findViewById(R.id.date);
            siteName = (TextView) itemView.findViewById(R.id.site_name);
            image = (ImageView) itemView.findViewById(R.id.itemImage);
            if (Build.VERSION.SDK_INT <= 16) {
                //image height and width here will be 0
                height = image.getLayoutParams().height;
                width = image.getLayoutParams().width;
            } else {
                height = image.getMaxHeight();
                width = image.getMaxWidth();
            }
        }

        @Override
        public void onClick(View v) {
            if (Util.DEBUG) {
                Log.d(TAG, "OnClick item : " + getLayoutPosition());
            }

            Util.openWebsite(activity, itemList.get(getLayoutPosition()).getLink());
        }
    }
}
