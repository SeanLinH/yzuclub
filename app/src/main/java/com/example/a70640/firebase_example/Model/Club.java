package com.example.a70640.firebase_example.Model;

/**
 * Created by 70640 on 2018/2/8.
 */

public class Club {
    String databaseURL;
    String name;
    String thumbnail;
    String description;
    String video;
    boolean isCollection;
    int id;

    public Club() {
    }

    public Club(String name,String thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public int getId() {
        return id;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
