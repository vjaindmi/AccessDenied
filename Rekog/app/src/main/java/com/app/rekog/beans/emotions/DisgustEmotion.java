package com.app.rekog.beans.emotions;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sdixit on 2/23/2018.
 */

public class DisgustEmotion extends RealmObject {
    @PrimaryKey
    long id;

    String disgustGreeting;


    public DisgustEmotion(){

    }
    public DisgustEmotion(long id, String disgustGreeting) {
        this.id = id;
        this.disgustGreeting = disgustGreeting;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDisgustGreeting() {
        return disgustGreeting;
    }

    public void setDisgustGreeting(String disgustGreeting) {
        this.disgustGreeting = disgustGreeting;
    }
}
