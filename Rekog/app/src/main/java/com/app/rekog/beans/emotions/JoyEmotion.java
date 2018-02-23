package com.app.rekog.beans.emotions;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sdixit on 2/23/2018.
 */

public class JoyEmotion extends RealmObject {
    @PrimaryKey
    long id;

    String joyGreeting;

    public JoyEmotion(){

    }
    public JoyEmotion(long id, String joyGreeting) {
        this.id = id;
        this.joyGreeting = joyGreeting;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJoyGreeting() {
        return joyGreeting;
    }

    public void setJoyGreeting(String joyGreeting) {
        this.joyGreeting = joyGreeting;
    }
}
