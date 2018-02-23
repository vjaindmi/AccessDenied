package com.app.rekog.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.app.rekog.R;
import com.app.rekog.beans.emotions.AngerEmotion;
import com.app.rekog.beans.emotions.DisgustEmotion;
import com.app.rekog.beans.emotions.FearEmotion;
import com.app.rekog.beans.emotions.JoyEmotion;
import com.app.rekog.beans.emotions.SadEmotion;
import com.app.rekog.beans.emotions.SurpriseEmotion;
import com.app.rekog.database.RealmDatabaseController;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by rahulgupta on 23/02/18.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        RealmDatabaseController.getInstance().init(this);

        if (!getSharedPreferences()) {
            saveIntoSharedPreference();
            RealmDatabaseController.getInstance().insertAngerEmotions(new AngerEmotion(1,
                    "Holding on to anger is like grasping a hot coal so throw that coal right away and stay happy."));
            RealmDatabaseController.getInstance().insertAngerEmotions(
                    new AngerEmotion(2, "The greatest remedy for anger is delay."));
            RealmDatabaseController.getInstance().insertAngerEmotions(new AngerEmotion(3,
                    "You can’t selectively numb your anger, any more than you can turn off all lights in a room, and still expect to see the light."));
            RealmDatabaseController.getInstance().insertAngerEmotions(new AngerEmotion(4,
                    "Do not let your anger lead to hatred, as you will hurt yourself more than you would the other."));
            RealmDatabaseController.getInstance().insertAngerEmotions(new AngerEmotion(5,
                    "Sometimes it took seconds to control your anger, only to avoid the state of eternal feud."));

            RealmDatabaseController.getInstance().insertDisgustEmotions(new DisgustEmotion(1,
                    "you are hard to disgust, but a pretentious poet can do it."));
            RealmDatabaseController.getInstance().insertDisgustEmotions(
                    new DisgustEmotion(2, "I had forgotten. Disgust shadows desire."));
            RealmDatabaseController.getInstance().insertDisgustEmotions(new DisgustEmotion(3,
                    "you are hard to disgust, but a pretentious poet can do it."));
            RealmDatabaseController.getInstance().insertDisgustEmotions(
                    new DisgustEmotion(4, "I had forgotten. Disgust shadows desire."));
            RealmDatabaseController.getInstance().insertDisgustEmotions(new DisgustEmotion(5,
                    "you are hard to disgust, but a pretentious poet can do it."));

            RealmDatabaseController.getInstance().insertFearEmotions(
                    new FearEmotion(1, "Do one thing every day that scares you."));
            RealmDatabaseController.getInstance().insertFearEmotions(
                    new FearEmotion(2, "Fear doesn't shut you down; it wakes you up"));
            RealmDatabaseController.getInstance().insertFearEmotions(new FearEmotion(3,
                    "Don't be afraid of your fears. They're not there to scare you. They're there to let you know that something is worth it."));
            RealmDatabaseController.getInstance().insertFearEmotions(new FearEmotion(4,
                    "Scared is what you're feeling. Brave is what you're doing."));
            RealmDatabaseController.getInstance().insertFearEmotions(new FearEmotion(5,
                    "Don't give in to your fears. If you do, you won't be able to talk to your heart."));

            RealmDatabaseController.getInstance().insertJoyEmotions(new JoyEmotion(1,
                    "A smile can be the beginning of a good friendship. So why not make some friends today?"));
            RealmDatabaseController.getInstance().insertJoyEmotions(new JoyEmotion(2,
                    "Happiness can bring smile to other faces as well, Keep smiling."));
            RealmDatabaseController.getInstance().insertJoyEmotions(new JoyEmotion(3,
                    "Smiling is good for your health, so keep smiling and keep doctor's away."));
            RealmDatabaseController.getInstance().insertJoyEmotions(new JoyEmotion(4,
                    "One big reason to smile is because it is contagious. When somebody catches you smiling, they will want to smile too."));
            RealmDatabaseController.getInstance().insertJoyEmotions(new JoyEmotion(5,
                    "Always remember to smile. You never know who might be watching"));

            RealmDatabaseController.getInstance().insertSadEmotions(new SadEmotion(1,
                    "A smile is happiness that can be found right under your nose, so dont be sad."));
            RealmDatabaseController.getInstance().insertSadEmotions(
                    new SadEmotion(2, "Don’t forget to smile because smiling is infectious."));
            RealmDatabaseController.getInstance().insertSadEmotions(
                    new SadEmotion(3, "Count your life with smiles, not tears."));
            RealmDatabaseController.getInstance().insertSadEmotions(new SadEmotion(4,
                    "Sadness brings nothing but boredom and we are sure you are not a bore person so please smile."));
            RealmDatabaseController.getInstance().insertSadEmotions(new SadEmotion(5,
                    "Life is too short to be sad for any reason. Increase you life and health by smiling toady."));

            RealmDatabaseController.getInstance().insertSurpriseEmotions(new SurpriseEmotion(1,
                    "The secret to humor is surprise."));
            RealmDatabaseController.getInstance().insertSurpriseEmotions(
                    new SurpriseEmotion(2,
                            "Humor is a spontaneous, wonderful bit of an outburst that just comes. It's unbridled, its unplanned, it's full of suprises."));
            RealmDatabaseController.getInstance().insertSurpriseEmotions(
                    new SurpriseEmotion(3,
                            "I try not to be surprised. Surprise is the public face of a mind that has been closed."));
            RealmDatabaseController.getInstance().insertSurpriseEmotions(new SurpriseEmotion(4,
                    "A pessimist gets nothing but pleasant surprises, an optimist nothing but unpleasant."));
            RealmDatabaseController.getInstance().insertSurpriseEmotions(new SurpriseEmotion(5,
                    "Magic is the stunning art of surprising your audience, so that nothing else surprises them."));
        }

        final ImageView imageView = findViewById(R.id.splash_logo);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(SplashActivity.this,
                                imageView,
                                ViewCompat.getTransitionName(imageView));
                startActivity(intent, options.toBundle());
                finish();
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                finish();
            }
        }, 2000);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void saveIntoSharedPreference() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("first_time", true);
        editor.apply();
    }

    private boolean getSharedPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean defaultValue = sharedPref.getBoolean("first_time", false);
        return defaultValue;
    }
}
