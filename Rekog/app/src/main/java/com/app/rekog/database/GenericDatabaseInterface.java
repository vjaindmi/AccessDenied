package com.app.rekog.database;

import android.content.Context;
import com.app.rekog.beans.users.UserInterface;

/**
 * Created by akashdeep on 2/22/2018.
 */

public interface GenericDatabaseInterface extends UserInterface {

    void init(Context context);

}
