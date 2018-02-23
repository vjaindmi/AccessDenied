package com.app.rekog.beans.emotions;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sdixit on 2/23/2018.
 */

public class FearEmotion extends RealmObject {
    @PrimaryKey
    long id;

    String fearGreeting;

    public FearEmotion(){

    }


    public FearEmotion(long id, String fearGreeting) {
        this.id = id;
        this.fearGreeting = fearGreeting;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFearGreeting() {
        return fearGreeting;
    }

    public void setFearGreeting(String fearGreeting) {
        this.fearGreeting = fearGreeting;
    }
}
