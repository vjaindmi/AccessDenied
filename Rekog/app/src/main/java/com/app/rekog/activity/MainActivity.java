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
import com.app.rekog.beans.EmotionResponse;
import com.app.rekog.beans.ResultBean;
import com.app.rekog.network.ApiClient;
import com.app.rekog.network.ApiInterface;
import com.google.gson.Gson;
import com.kairos.Kairos;
import com.kairos.KairosListener;
import com.rahul.media.main.MediaFactory;
import com.rahul.media.model.Define;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.json.JSONException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity implements KairosListener, View.OnClickListener {

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

        getAllGallery();
//        Define.ACTIONBAR_COLOR = getResources().getColor(R.color.blue_03A9F4);
    }

    private void initializeSdk() {
        String app_id = "c7d15241";
        String api_key = "fd3287889f836397be1857dd4d0adb11";
        kairos.setAuthentication(this, app_id, api_key);
    }

    private void enrollImage(String imageUrl) {
        String subjectId = "Bhavya";
        String galleryId = getString(R.string.gallery_name);
        try {
            kairos.enroll(getBitmap(imageUrl), subjectId, galleryId, null, null, null, this);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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

    private void getAllSubjects() {
        String galleryId = getString(R.string.gallery_name);
        try {
            kairos.listSubjectsForGallery(galleryId, this);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getAllGallery() {
        try {
            kairos.listGalleries(this);
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
                startActivity(new Intent(this, EnrollmentActivity.class));
                break;
            case R.id.recognise_button:
                isRecogniseImage = true;
                openCamera();
                break;
            case R.id.mark_attendance_button:
                break;
        }
    }

    private void detectEmotion(String enrolledImage) {
        try {
            kairos.detect(getBitmap(enrolledImage), null, null, this);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void checkEmotion(String enrolledImage) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        final File file;
        MultipartBody.Part part = null;
        try {
            file = new File(enrolledImage);
            RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            part = MultipartBody.Part.createFormData("source", file.getName(), fileBody);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<EmotionResponse> call3 = apiInterface.checkEmotion(part);

        call3.enqueue(new Callback<EmotionResponse>() {
            @Override
            public void onResponse(Call<EmotionResponse> call, Response<EmotionResponse> response) {
                EmotionResponse emotionResponse = response.body();

            }

            @Override
            public void onFailure(Call<EmotionResponse> call, Throwable t) {
                call.cancel();
            }
        });

    }

    private Bitmap getBitmap(String enrolledImage) {
        File image = new File(enrolledImage);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
    }
}