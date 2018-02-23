package com.app.rekog.activity;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.rekog.R;
import com.app.rekog.beans.ResultBean;
import com.app.rekog.customui.MaterialProgressDialog;
import com.app.rekog.recognize.RecognizeActivity;
import com.google.gson.Gson;
import com.kairos.Kairos;
import com.kairos.KairosListener;
import com.rahul.media.main.MediaFactory;
import com.rahul.media.model.Define;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends Activity implements KairosListener, View.OnClickListener {

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private MaterialProgressDialog mMaterialProgressDialog;

    private MediaFactory.MediaBuilder mediaBuilder;

    private MediaFactory mediaFactory;

    private Kairos kairos = new Kairos();

    private String enrolledImage = "";

    private boolean isForEmotion = false;

    private boolean isRecogniseImage = false;

    private boolean isForEnroll = false;


    /**
     * Called when the activity is first created.
     */
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSdk();
        Define.MEDIA_PROVIDER = getString(R.string.image_provider);
        findViewById(R.id.image_button).setOnClickListener(this);
        findViewById(R.id.recognise_button).setOnClickListener(this);
        findViewById(R.id.mark_attendance_button).setOnClickListener(this);
        findViewById(R.id.view_attendance_button).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        }


    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestStoragePermission() {
        Log.w("MainActivity", "Storage permission is not granted. Requesting permission");

        final String[] permissions = new String[]{permission.WRITE_EXTERNAL_STORAGE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_storage_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    private void initializeSdk() {
        String app_id = getString(R.string.kairos_app_id);
        String api_key = getString(R.string.kairos_api_key);
        kairos.setAuthentication(this, app_id, api_key);
    }

    private void getImage(Bitmap imageUrl) {
        String galleryId = getString(R.string.gallery_name);
        try {
            kairos.recognize(imageUrl, galleryId, null, null, null, null, this);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSuccess(String response) {
        Log.d("KAIROS DEMO", response);
        Gson gson = new Gson();
        ResultBean resultBean = gson.fromJson(response, ResultBean.class);
        if (resultBean.images.size() != 0) {
            String userName = resultBean.images.get(0).transaction.subject_id;
            Toast.makeText(this, userName + " recognised successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFail(String response) {
        Log.d("KAIROS DEMO", response);
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
        ;
    }

    private void openCamera() {
        mediaBuilder = new MediaFactory.MediaBuilder(MainActivity.this)
                .isSquareCrop(false)
                .fromCamera();
        mediaFactory = MediaFactory.create().start(mediaBuilder);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> pathArrayList = mediaFactory.onActivityResult(requestCode, resultCode, data);
        if (pathArrayList.size() != 0) {
            enrolledImage = pathArrayList.get(0);
            if (isRecogniseImage) {
                getImage(getBitmap(enrolledImage));
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete:
                deleteGallery();
                break;
            case R.id.image_button:
                startActivity(new Intent(this, EnrollmentActivity.class));
                break;
            case R.id.recognise_button:
//                isRecogniseImage = true;
//                openCamera();
                startActivity(new Intent(this, RecognizeActivity.class));
                break;
            case R.id.mark_attendance_button:
                startActivity(new Intent(MainActivity.this, MarkAttendanceActivity.class));
                break;
            case R.id.view_attendance_button:
                startActivity(new Intent(this, AttendanceRecordActivity.class));
                break;
        }
    }


    private Bitmap getBitmap(String enrolledImage) {
        File image = new File(enrolledImage);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
    }

    private void deleteGallery() {
        String galleryId = getString(R.string.gallery_name);
        try {
            kairos.deleteGallery(galleryId, this);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}