package com.app.rekog.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.app.rekog.R;
import com.app.rekog.customui.MaterialProgressDialog;
import java.io.ByteArrayInputStream;

/**
 * Created by rahulgupta on 23/02/18.
 */

public class Utility {

    /**
     * Static method to get an instance of material styled progress dialog
     *
     * @param mContext Context of the calling class
     * @return An instance of MaterialProgressDialog
     */
    public static MaterialProgressDialog getProgressDialogInstance(Context mContext) {
        MaterialProgressDialog mProgressDialog = new MaterialProgressDialog(mContext,
                mContext.getString(R.string.loading));
        mProgressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mProgressDialog.setCancelable(false);
        return mProgressDialog;
    }

    public static Bitmap convertToBitmap(byte[] byteArray) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }

}
