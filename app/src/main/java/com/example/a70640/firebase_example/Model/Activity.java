package com.example.a70640.firebase_example.Model;

/**
 * Created by min on 2018/2/19.
 */

public class Activity {
    String URL;
    String name;
    String description;
    String date;
    String time;
    String location;
    String thumbnail;
    boolean checked;
    Club club;

    public Activity() {
    }

    public String getDatabaseURL() {
        return URL;
    }

    public void setDatabaseURL(String URL) {
        this.URL = URL;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Club getClub() {
        return club;
    }
}
