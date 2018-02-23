package com.app.rekog.database;

import android.content.Context;

import java.util.ArrayList;

import com.app.rekog.beans.emotions.AngerEmotion;
import com.app.rekog.beans.emotions.DisgustEmotion;
import com.app.rekog.beans.emotions.FearEmotion;
import com.app.rekog.beans.emotions.JoyEmotion;
import com.app.rekog.beans.emotions.SadEmotion;
import com.app.rekog.beans.emotions.SurpriseEmotion;
import com.app.rekog.beans.users.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

/**
 * Created by akashdeep on 2/22/2018.
 */

public class RealmDatabaseController implements GenericDatabaseInterface {

    private static RealmDatabaseController instance;
    private static Realm realm;

    public static RealmDatabaseController getInstance() {
        if(instance == null)
            instance = new RealmDatabaseController();
        return instance;
    }

    @Override
    public void init(Context context) {

        Realm.init(context);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name("tasky.realm")
                .schemaVersion(0)
                .build();
        Realm.setDefaultConfiguration(realmConfig);

        realm = Realm.getDefaultInstance();

    }

    @Override
    public void insertUser(User user) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }

    @Override
    public User getUser(String email) {
        return realm.where(User.class).equalTo("email", email).findFirst();
    }


    @Override
    public void insertAngerEmotions(AngerEmotion angerEmotion) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(angerEmotion);
        realm.commitTransaction();
    }

    @Override
    public void insertDisgustEmotions(DisgustEmotion disgustEmotion) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(disgustEmotion);
        realm.commitTransaction();
    }

    @Override
    public void insertFearEmotions(FearEmotion fearEmotion) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(fearEmotion);
        realm.commitTransaction();
    }

    @Override
    public void insertJoyEmotions(JoyEmotion joyEmotion) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(joyEmotion);
        realm.commitTransaction();
    }

    @Override
    public void insertSadEmotions(SadEmotion sadEmotion) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(sadEmotion);
        realm.commitTransaction();
    }

    @Override
    public void insertSurpriseEmotions(SurpriseEmotion surpriseEmotion) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(surpriseEmotion);
        realm.commitTransaction();
    }

    @Override
    public AngerEmotion getAngerEmotion(int id) {
         return realm.where(AngerEmotion.class).equalTo("id", id).findFirst();
    }

    @Override
    public DisgustEmotion getDisgustEmotion(int id) {
        return realm.where(DisgustEmotion.class).equalTo("id", id).findFirst();
    }

    @Override
    public FearEmotion getFearEmotion(int id) {
        return realm.where(FearEmotion.class).equalTo("id", id).findFirst();
    }

    @Override
    public JoyEmotion getJoyEmotion(int id) {
        return realm.where(JoyEmotion.class).equalTo("id", id).findFirst();
    }

    @Override
    public SadEmotion getSadEmotion(int id) {
        return realm.where(SadEmotion.class).equalTo("id", id).findFirst();
    }

    @Override
    public SurpriseEmotion getSurpriseEmotion(int id) {
        return realm.where(SurpriseEmotion.class).equalTo("id", id).findFirst();
    }
}
