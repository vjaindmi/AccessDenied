package com.app.rekog.beans.emotions;

import java.util.ArrayList;

import com.app.rekog.beans.users.User;

/**
 * Created by Sdixit on 2/23/2018.
 */

public interface EmotionInterface {

    void insertAngerEmotions(AngerEmotion e);
    void insertDisgustEmotions(DisgustEmotion e);
    void insertFearEmotions(FearEmotion e);
    void insertJoyEmotions(JoyEmotion e);
    void insertSadEmotions(SadEmotion e);

    AngerEmotion getAngerEmotion(int id);
    DisgustEmotion getDisgustEmotion(int id);
    FearEmotion getFearEmotion(int id);
    JoyEmotion getJoyEmotion(int id);
    SadEmotion getSadEmotion(int id);

}
