package com.app.rekog.base;

import static android.os.Environment.MEDIA_MOUNTED;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.app.rekog.R;
import com.app.rekog.beans.BitmapBean;
import com.app.rekog.customui.MaterialProgressDialog;
import com.rahul.media.activity.CameraPickActivity;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rahulgupta on 23/02/18.
 */

public class Utility {

    private static final String EMAIL_REGEX
            = "^([0-9a-zA-Z]([-\\.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$";

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

    public static Uri createImageFile(Context mContext) throws IOException {

        File image = null;

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File storageDir = new File(Environment.getExternalStorageDirectory(),
                    mContext.getString(com.rahul.media.R.string.imagepicker_parent));
            boolean parentCreationResult = storageDir.mkdirs();
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        } else {

            File storageDir = mContext.getFilesDir();
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        }

        // Save a file: path for use with ACTION_VIEW intents
        Log.d(CameraPickActivity.class.getSimpleName(), "file:" + image.getAbsolutePath());
        return Uri.fromFile(image);
    }


    private Bitmap getBitmap(String enrolledImage) {
        File image = new File(enrolledImage);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
    }


    public static BitmapBean convertToBitmap(Context context, byte[] byteArray) {
        BitmapBean bitmapBean = new BitmapBean();
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        bitmapBean.bitmap = bitmap;
        FileOutputStream out = null;
        Uri imageFile = null;
        try {
            imageFile = createImageFile(context);
            out = new FileOutputStream(imageFile.getPath());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bitmapBean.filePath = imageFile.getPath();
        return bitmapBean;
    }

    /**
     * Method to check email validation
     *
     * @param text String to match
     * @return status of the matching
     */
    public static boolean validateEmailFields(String text) {

        Pattern p = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

        Matcher m = p.matcher(text);
        if (m.matches() && !text.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}
