package com.app.rekog.database;

import android.content.Context;

import com.app.rekog.beans.emotions.EmotionInterface;
import com.app.rekog.beans.users.UserInterface;

/**
 * Created by akashdeep on 2/22/2018.
 */

public interface GenericDatabaseInterface extends UserInterface, EmotionInterface {

    void init(Context context);

}
