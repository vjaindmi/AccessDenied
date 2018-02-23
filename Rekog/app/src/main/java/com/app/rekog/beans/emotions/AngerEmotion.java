package com.app.rekog.beans.emotions;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sdixit on 2/23/2018.
 */

public class AngerEmotion extends RealmObject {

    @PrimaryKey
    long id;

    String angerGreeting;

    public AngerEmotion() {

    }

    public AngerEmotion(long id, String angerGreeting) {
        this.id = id;
        this.angerGreeting = angerGreeting;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAngerGreeting() {
        return angerGreeting;
    }

    public void setAngerGreeting(String angerGreeting) {
        this.angerGreeting = angerGreeting;
    }
}
