package com.app.rekog.events;

import com.app.rekog.beans.BitmapBean;
import java.util.ArrayList;

/**
 * Created by rahulgupta on 23/02/18.
 */

public class BitmapShareEvent {

    private ArrayList<BitmapBean> mBitmapArrayList;

    public BitmapShareEvent(ArrayList<BitmapBean> mBitmapArrayList) {

        this.mBitmapArrayList = mBitmapArrayList;
    }

    public ArrayList<BitmapBean> getBitmapArrayList() {
        return mBitmapArrayList;
    }
}
