package com.kojen.mynewschoice.pojo;

import java.io.Serializable;

/**
 * Created by koshy on 27/07/16.
 */
public class MetaInfo implements Serializable{
    int id;
    String topicName;
    int refreshRate;

    public MetaInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(int refreshRate) {
        this.refreshRate = refreshRate;
    }
}
