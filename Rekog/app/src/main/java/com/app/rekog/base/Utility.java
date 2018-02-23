package com.app.rekog.base;

import static android.os.Environment.MEDIA_MOUNTED;
import static com.rahul.media.utils.MediaUtility.getUserImageDir;
import static com.rahul.media.utils.MediaUtility.initializeImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.app.rekog.R;
import com.app.rekog.beans.BitmapBean;
import com.app.rekog.customui.MaterialProgressDialog;
import com.rahul.media.activity.CameraPickActivity;
import com.rahul.media.utils.BitmapDecoder;
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

    public static String KAIROS_SEPARATOR = "_##_";

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
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, out); // bmp is your Bitmap instance
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
        bitmapBean.filePath = compressImage(imageFile.getPath(), bitmapBean, context);
        return bitmapBean;
    }

    private static String compressImage(String filePath, final BitmapBean bitmapBean,
            Context mContext) {

        try {
            initializeImageLoader(mContext);
            String imageName = filePath.substring(filePath.lastIndexOf("/") + 1,
                    filePath.length());
            File fileBackup = new File(getUserImageDir(mContext) + "/" + imageName);
            if (fileBackup.exists()) {
                return fileBackup.getAbsolutePath();
            }

            Bitmap scaledBitmap = null;

            BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

            float maxHeight = 480.0f;
            float maxWidth = 640.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;

                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original image

            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];

            try {
//          load the bitmap from its path
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();

            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2
                    , new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(fileBackup);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 80, fOut);
                bitmapBean.bitmap = scaledBitmap;
                filePath = fileBackup.getPath();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fOut != null) {
                        fOut.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.i(BitmapDecoder.class.getSimpleName(), e.getLocalizedMessage());
        }
        return filePath;

    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
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

    public static String getDate(long timeStamp){

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyy HH:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

}
