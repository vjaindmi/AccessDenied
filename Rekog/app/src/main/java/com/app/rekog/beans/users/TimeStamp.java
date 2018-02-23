package com.app.rekog.beans.users;

import io.realm.RealmObject;

/**
 * Created by akashdeep on 2/22/2018.
 */

public class TimeStamp extends RealmObject {

    String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
