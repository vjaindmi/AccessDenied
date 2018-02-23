package com.app.rekog.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.app.rekog.R;
import com.app.rekog.base.Utility;
import com.app.rekog.beans.ResultBean;
import com.app.rekog.customui.MaterialProgressDialog;
import com.app.rekog.recognize.RecognizeActivity;
import com.google.gson.Gson;
import com.kairos.Kairos;
import com.kairos.KairosListener;
import com.rahul.media.main.MediaFactory;
import com.rahul.media.model.Define;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.json.JSONException;

public class MainActivity extends Activity implements KairosListener, View.OnClickListener {

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
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeSdk();
        Define.MEDIA_PROVIDER = getString(R.string.image_provider);
        findViewById(R.id.image_button).setOnClickListener(this);
        findViewById(R.id.recognise_button).setOnClickListener(this);
        findViewById(R.id.mark_attendance_button).setOnClickListener(this);
    }

    private void initializeSdk() {
        String app_id = "c7d15241";
        String api_key = "fd3287889f836397be1857dd4d0adb11";
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
            case R.id.image_button:
//                startActivity(new Intent(this, EnrollmentActivity.class));
                break;
            case R.id.recognise_button:
//                isRecogniseImage = true;
//                openCamera();
                startActivity(new Intent(this, RecognizeActivity.class));
                break;
            case R.id.mark_attendance_button:
                break;
        }
    }


    private Bitmap getBitmap(String enrolledImage) {
        File image = new File(enrolledImage);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
    }

    /**
     * Method to show blocking progress dialog
     */
    private void showProgressDialog(boolean iShow) {
        if (mMaterialProgressDialog == null) {
            mMaterialProgressDialog = Utility.getProgressDialogInstance(this);
        }
        try {
            if (iShow) {
                mMaterialProgressDialog.show();
            } else {
                mMaterialProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}