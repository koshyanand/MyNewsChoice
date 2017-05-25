package com.kojen.mynewschoice.util;

import com.kojen.mynewschoice.pojo.Item;

import java.util.List;

/**
 * Created by koshy on 27/07/16.
 */
public interface ItemsLoadedCallback {
    public void dataRecieved(List<Item> itemList);
}
