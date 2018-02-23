package com.app.rekog.base;

import android.app.Application;
import com.app.rekog.R;
import com.app.rekog.database.RealmDatabaseController;
import com.app.rekog.sqlitedb.DatabaseHelper;
import com.facebook.stetho.Stetho;
import com.kairos.Kairos;
import com.rahul.media.model.Define;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

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
        Stetho.initializeWithDefaults(this);
        DatabaseHelper.getDatabaseHelperInstance(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Muller-Regular.otf").setFontAttrId(R.attr.fontPath)
                .build());

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
