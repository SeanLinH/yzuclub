package com.example.a70640.firebase_example;

/**
 * Created by 70640 on 2018/2/10.
 */

public class Recipe {
    int id;
    String name;
    String description;
    double price;
    String thumbnail;
    String chef;
    String timestamp;

    public Recipe() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getChef() {
        return chef;
    }

    public String getTimestamp() {
        return timestamp;
    }
}