package com.app.rekog.base;

import android.app.Application;
import com.app.rekog.R;
import com.kairos.Kairos;
import com.kairos.android.example.database.RealmDatabaseController;
import com.rahul.media.model.Define;

/**
 * Created by bkhera on 2/22/2018.
 */

public class AppController extends Application {

    private Kairos kairos = new Kairos();

    @Override
    public void onCreate() {
        super.onCreate();
        initializeSdk();
        initDatabase();
    }

    /*initialized sdk*/
    private void initializeSdk() {
        Define.MEDIA_PROVIDER = getString(R.string.image_provider);
        String app_id = "c7d15241";
        String api_key = "fd3287889f836397be1857dd4d0adb11";
        kairos.setAuthentication(this, app_id, api_key);
    }

    private void initDatabase(){
        RealmDatabaseController.getInstance().init(this);
    }
}
