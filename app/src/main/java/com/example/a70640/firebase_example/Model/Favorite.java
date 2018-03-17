package com.example.a70640.firebase_example.Model;

/**
 * Created by min on 2018/2/17.
 */

public class Favorite {
    String databaseURL;

    public Favorite() {
    }

    public Favorite(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public String getDatabaseURL() {
        return databaseURL;
    }
}
