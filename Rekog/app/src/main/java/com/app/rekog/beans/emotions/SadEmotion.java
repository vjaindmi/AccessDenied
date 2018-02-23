package com.app.rekog.beans.emotions;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sdixit on 2/23/2018.
 */

public class SadEmotion extends RealmObject {

    @PrimaryKey
    long id;

    String sadGreeting;
    public SadEmotion()

    {

    }

    public SadEmotion(long id, String sadGreeting) {
        this.id = id;
        this.sadGreeting = sadGreeting;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSadGreeting() {
        return sadGreeting;
    }

    public void setSadGreeting(String sadGreeting) {
        this.sadGreeting = sadGreeting;
    }
}
