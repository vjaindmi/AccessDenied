package com.app.rekog.beans.emotions;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sdixit on 2/23/2018.
 */

public class SurpriseEmotion extends RealmObject {
    @PrimaryKey
    long id;

    String surpriseGreeting;

    public SurpriseEmotion() {

    }

    public SurpriseEmotion(long id, String surpriseGreeting) {
        this.id = id;
        this.surpriseGreeting = surpriseGreeting;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSurpriseGreeting() {
        return surpriseGreeting;
    }

    public void setSurpriseGreeting(String surpriseGreeting) {
        this.surpriseGreeting = surpriseGreeting;
    }
}
