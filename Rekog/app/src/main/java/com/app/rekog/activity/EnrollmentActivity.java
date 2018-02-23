package com.app.rekog.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rekog.R;
import com.app.rekog.base.Utility;
import com.app.rekog.beans.BaseResponse;
import com.app.rekog.beans.RequestBean;
import com.app.rekog.beans.ResultBean;
import com.app.rekog.customui.MaterialProgressDialog;
import com.app.rekog.network.ApiClient;
import com.app.rekog.network.ApiInterface;
import com.google.gson.Gson;
import com.kairos.Kairos;
import com.kairos.KairosListener;
import com.rahul.media.main.MediaFactory;
import com.rahul.media.model.Define;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bkhera on 2/22/2018.
 */

public class EnrollmentActivity extends Activity implements View.OnClickListener, KairosListener {

    private EditText mNameEd;
    private com.kairos.Kairos kairos = new Kairos();
    private ArrayList<String> imageList = new ArrayList<>();
    private MediaFactory.MediaBuilder mediaBuilder;
    private MediaFactory mediaFactory;
    private ProgressBar mProgressBar;
    private ImageView mImageView1, mImageView2, mImageView3, mImageView4, mImageView5, mImageView6;
    private int clickedPosition = -1, currentIndex = 0;
    private EditText mEmailEd;
    private ArrayList<String> faceIds = new ArrayList<>();
    private View mImageContainer;
    private TextView mEnrollTv, mUploadImagesTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);
        initializeSdk();
        initView();
    }

    private MaterialProgressDialog mMaterialProgressDialog;

    /*initialized sdk*/
    private void initializeSdk() {
        Define.MEDIA_PROVIDER = getString(R.string.image_provider);
        String app_id = "c7d15241";
        String api_key = "fd3287889f836397be1857dd4d0adb11";
        kairos.setAuthentication(this, app_id, api_key);
    }

    private void initView() {
        mNameEd = findViewById(R.id.name_et);
        mEmailEd = findViewById(R.id.email_et);
        mImageContainer = findViewById(R.id.images_container);
        mEnrollTv = findViewById(R.id.enroll_tv);
        mProgressBar = findViewById(R.id.progressBar);
        mUploadImagesTv = findViewById(R.id.enroll_user_tv);
        mImageView1 = findViewById(R.id.image_view_1);
        mImageView2 = findViewById(R.id.image_view_2);
        mImageView3 = findViewById(R.id.image_view_3);
        mImageView4 = findViewById(R.id.image_view_4);
        mImageView5 = findViewById(R.id.image_view_5);
        mImageView6 = findViewById(R.id.image_view_6);

        mEnrollTv.setOnClickListener(this);
        mUploadImagesTv.setOnClickListener(this);
        mImageContainer.setVisibility(View.GONE);
        mEnrollTv.setVisibility(View.GONE);
        /*mImageView1.setOnClickListener(this);
        mImageView2.setOnClickListener(this);
        mImageView3.setOnClickListener(this);
        mImageView4.setOnClickListener(this);
        mImageView5.setOnClickListener(this);
        mImageView6.setOnClickListener(this);*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.enroll_tv:
                if (currentIndex != imageList.size()) {
                    enrollImage(imageList.get(currentIndex));
                }
                break;
            case R.id.enroll_user_tv:
                if (validArguments()) {
                    showProgressDialog(true);
                    checkIfUserExists();
                }
                break;
           /* case R.id.image_view_1:
                clickedPosition = 1;
                openCamera();
                break;
            case R.id.image_view_2:
                clickedPosition = 2;
                openCamera();
                break;
            case R.id.image_view_3:
                clickedPosition = 3;
                openCamera();
                break;
            case R.id.image_view_4:
                clickedPosition = 4;
                openCamera();
                break;
            case R.id.image_view_5:
                clickedPosition = 5;
                openCamera();
                break;
            case R.id.image_view_6:
                clickedPosition = 6;
                openCamera();
                break;*/
        }
    }

    private void checkIfUserExists() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBean requestBean = new RequestBean();
        requestBean.Email = mEmailEd.getText().toString();
        requestBean.isUserRegister = false;

        Call<BaseResponse> call3 = apiInterface.checkEmail(requestBean);

        call3.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                showProgressDialog(false);
                BaseResponse baseResponse = response.body();
                if (baseResponse.data.FaceIDs == null) {
                    moveToImageUploadScreen();

                } else {
                    Toast.makeText(EnrollmentActivity.this, baseResponse.responseMessage, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                showProgressDialog(false);
                call.cancel();
            }
        });
    }

    private void moveToImageUploadScreen() {
//        startActivity(new Intent(this, NextScreen.class));
    }

    private void postUserData() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBean requestBean = new RequestBean();
        requestBean.Email = mEmailEd.getText().toString();
        requestBean.faceIds.addAll(faceIds);
        requestBean.isUserRegister = true;

        Call<BaseResponse> call3 = apiInterface.checkEmail(requestBean);

        call3.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                mProgressBar.setVisibility(View.GONE);
                BaseResponse emotionResponse = response.body();

            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }


    private boolean validArguments() {
        if (TextUtils.isEmpty(mNameEd.getText().toString())) {
            Toast.makeText(this, R.string.enter_user_name, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(mEmailEd.getText().toString())) {
            Toast.makeText(this, R.string.enter_user_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void enrollImage(String imageUrl) {
        String subjectId = mNameEd.getText().toString();
        String galleryId = getString(R.string.gallery_name);
        try {
            kairos.enroll(getBitmap(imageUrl), subjectId, galleryId, null, null, null, this);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private Bitmap getBitmap(String enrolledImage) {
        File image = new File(enrolledImage);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        return BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
    }


    @Override
    public void onSuccess(String response) {
        currentIndex = currentIndex + 1;
        if (currentIndex != imageList.size()) {
            enrollImage(imageList.get(currentIndex));
        }
        Log.d("KAIROS DEMO", response);
        Gson gson = new Gson();
        ResultBean resultBean = gson.fromJson(response, ResultBean.class);
        if (resultBean.images.size() != 0) {
            String userName = resultBean.images.get(0).transaction.subject_id;
            faceIds.add(resultBean.images.get(0).transaction.face_id);
            if (currentIndex == imageList.size()) {
                postUserData();
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, userName + " enrolled successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onFail(String response) {
        mProgressBar.setVisibility(View.GONE);
        Log.d("KAIROS DEMO", response);
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
    }

    private void openCamera() {
        mediaBuilder = new MediaFactory.MediaBuilder(EnrollmentActivity.this)
                .isSquareCrop(false)
                .fromCamera();
        mediaFactory = MediaFactory.create().start(mediaBuilder);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            ArrayList<String> pathArrayList = mediaFactory.onActivityResult(requestCode, resultCode, data);

            if (pathArrayList.size() != 0) {
                imageList.addAll(pathArrayList);
                if (clickedPosition == 1) {
                    loadImageToView(pathArrayList.get(0), mImageView1);
                } else if (clickedPosition == 2) {
                    loadImageToView(pathArrayList.get(1), mImageView2);
                } else if (clickedPosition == 3) {
                    loadImageToView(pathArrayList.get(2), mImageView3);
                } else if (clickedPosition == 4) {
                    loadImageToView(pathArrayList.get(3), mImageView4);
                } else if (clickedPosition == 5) {
                    loadImageToView(pathArrayList.get(4), mImageView5);
                } else if (clickedPosition == 6) {
                    loadImageToView(pathArrayList.get(5), mImageView6);
                }
            }
        }


    }

    private void loadImageToView(String imageUrl, ImageView imageView) {
        File f = new File(imageUrl);
        Picasso.with(this).load(f).into(imageView);
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
