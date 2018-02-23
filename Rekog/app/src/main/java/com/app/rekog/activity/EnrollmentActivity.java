package com.app.rekog.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
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
import com.app.rekog.beans.BitmapBean;
import com.app.rekog.beans.RequestBean;
import com.app.rekog.beans.ResultBean;
import com.app.rekog.customui.MaterialProgressDialog;
import com.app.rekog.events.BitmapShareEvent;
import com.app.rekog.facetracker.FaceTrackerActivity;
import com.app.rekog.network.ApiClient;
import com.app.rekog.network.ApiInterface;
import com.google.gson.Gson;
import com.kairos.Kairos;
import com.kairos.KairosListener;
import com.rahul.media.model.Define;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by bkhera on 2/22/2018.
 */

public class EnrollmentActivity extends Activity implements View.OnClickListener, KairosListener {

    private EditText mNameEd;

    private com.kairos.Kairos kairos = new Kairos();

    private ArrayList<BitmapBean> imageList = new ArrayList<>();

    private ProgressBar mProgressBar;

    private ImageView mImageView1, mImageView2, mImageView3, mImageView4, mImageView5, mImageView6;

    private int currentIndex = 0;

    private EditText mEmailEd;

    private ArrayList<String> faceIds = new ArrayList<>();

    private View mImageContainer;

    private TextView mEnrollTv, mUploadImagesTv;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);
        EventBus.getDefault().register(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Enroll");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                onBackPressed();
            }
        });
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
        }
    }

    private void checkIfUserExists() {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        RequestBean requestBean = new RequestBean();
        requestBean.Email = mEmailEd.getText().toString();
        requestBean.isUserRegister = false;
        requestBean.faceIds = new ArrayList<>();
        Call<BaseResponse> call3 = apiInterface.checkEmail(requestBean);

        call3.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                showProgressDialog(false);
                BaseResponse baseResponse = response.body();
                //If true then not exist -> move to image
                //else If false then exist
                //if face id null -> move to image
                if (baseResponse.data == null) {
                    moveToImageUploadScreen();
                } else if (baseResponse.data.FaceIDs == null) {
                    moveToImageUploadScreen();

                } else {
                    Toast.makeText(EnrollmentActivity.this, R.string.already_enrolled, Toast.LENGTH_SHORT).show();
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
        startActivity(new Intent(this, FaceTrackerActivity.class));
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
        } else if (!Utility.validateEmailFields(mEmailEd.getText().toString().toLowerCase())) {
            Toast.makeText(this, R.string.enter_valid_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void enrollImage(BitmapBean bitmapBean) {
        showProgressDialog(true);
        String subjectId = mNameEd.getText().toString();
        String galleryId = getString(R.string.gallery_name);
        try {
            kairos.enroll(bitmapBean.bitmap, subjectId, galleryId, null, null, null, this);
        } catch (Exception e) {
            showProgressDialog(false);
            e.printStackTrace();
        }
    }



    @Override
    public void onSuccess(String response) {
        showProgressDialog(false);
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
        showProgressDialog(false);
        mProgressBar.setVisibility(View.GONE);
        Log.d("KAIROS DEMO", response);
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
    }

    private void loadImageToView(BitmapBean bitmapBean, ImageView imageView) {
        File f = new File(bitmapBean.filePath);
        Picasso.with(this).load(f).placeholder(R.drawable.placeholder_470x352).into(imageView);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBitmapEvent(BitmapShareEvent event) {
        if (event != null && event.getBitmapArrayList() != null && !event.getBitmapArrayList().isEmpty()) {
            Log.i("Bitmap list", event.toString());
            imageList.clear();
            imageList.addAll(event.getBitmapArrayList());

            for (int i = 0; i < imageList.size(); i++) {
                if (i == 0) {
                    loadImageToView(imageList.get(0), mImageView1);
                } else if (i == 1) {
                    loadImageToView(imageList.get(1), mImageView2);
                } else if (i == 2) {
                    loadImageToView(imageList.get(2), mImageView3);
                } else if (i == 3) {
                    loadImageToView(imageList.get(3), mImageView4);
                } else if (i == 4) {
                    loadImageToView(imageList.get(4), mImageView5);
                } else if (i == 5) {
                    loadImageToView(imageList.get(5), mImageView6);
                }
            }
            mImageContainer.setVisibility(View.VISIBLE);
            mEnrollTv.setVisibility(View.VISIBLE);
            mUploadImagesTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
