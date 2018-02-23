package com.app.rekog.events;

import android.graphics.Bitmap;
import java.util.ArrayList;

/**
 * Created by rahulgupta on 23/02/18.
 */

public class BitmapShareEvent {

    private ArrayList<Bitmap> mBitmapArrayList;

    public BitmapShareEvent(ArrayList<Bitmap> mBitmapArrayList) {

        this.mBitmapArrayList = mBitmapArrayList;
    }

    public ArrayList<Bitmap> getBitmapArrayList() {
        return mBitmapArrayList;
    }
}
