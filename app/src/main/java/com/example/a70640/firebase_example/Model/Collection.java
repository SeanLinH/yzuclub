package com.example.a70640.firebase_example.Model;

/**
 * Created by min on 2018/2/21.
 */

public class Collection {
    String databaseURL;

    public Collection() {
    }

    public Collection(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public String getDatabaseURL() {
        return databaseURL;
    }
}
