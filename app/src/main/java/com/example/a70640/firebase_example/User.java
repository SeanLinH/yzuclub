package com.example.a70640.firebase_example;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by min on 2018/2/15.
 */

@IgnoreExtraProperties
public class User {

    public String email;

    public User() {
    }

    public User(String email) {
        this.email = email;
    }
}