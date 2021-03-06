/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.app.rekog.facetracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.app.rekog.R;
import com.app.rekog.activity.EnrollmentActivity;
import com.app.rekog.base.Utility;
import com.app.rekog.beans.BitmapBean;
import com.app.rekog.customui.MaterialProgressDialog;
import com.app.rekog.events.BitmapShareEvent;
import com.app.rekog.facetracker.ui.PhotoAdapter;
import com.app.rekog.facetracker.ui.camera.CameraSourcePreview;
import com.app.rekog.facetracker.ui.camera.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.io.IOException;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class FaceTrackerActivity extends AppCompatActivity {

    private static final String TAG = "FaceTracker";

    public static final String EXTRA_ENROLLMENT_DATA = "extra_enrollment_data";

    private static final int MIN_THRESHOLD = 1;

    private static final int MAX_THRESHOLD = 6;

    private CameraSource mCameraSource = null;

    private MaterialProgressDialog mMaterialProgressDialog;

    private CameraSourcePreview mPreview;

    private GraphicOverlay mGraphicOverlay;

    private static final int RC_HANDLE_GMS = 9001;

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private View captureButton;

    private RecyclerView mRecyclerView;

    private ArrayList<BitmapBean> mBitmapArrayList = new ArrayList<>();

    private PhotoAdapter mPhotoAdapter;

    private GraphicOverlay mOverlay;

    private FaceGraphic mFaceGraphic;

    private View mSaveButton;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_face_tracker);

        mSaveButton = findViewById(R.id.save_tv);
        mSaveButton.setVisibility(View.GONE);
        mSaveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View view) {
                setResult(Activity.RESULT_OK);
                EventBus.getDefault().post(new BitmapShareEvent(
                        mPhotoAdapter.getAllItems()));
                finish();
            }
        });
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);

        captureButton = findViewById(R.id.capture_button);
        mRecyclerView = findViewById(R.id.photo_progress_recycler_view);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mPhotoAdapter = new PhotoAdapter(mBitmapArrayList);
        mRecyclerView.setAdapter(mPhotoAdapter);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (mOverlay != null && mOverlay.isGraphicOverlayDetected()) {
                        mCameraSource.takePicture(new CameraSource.ShutterCallback() {
                            @Override
                            public void onShutter() {

                            }
                        }, new CameraSource.PictureCallback() {
                            @Override
                            public void onPictureTaken(final byte[] bytes) {
                                mSaveButton.setVisibility(View.VISIBLE);

                                if (mBitmapArrayList.size() == MIN_THRESHOLD) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(
                                            FaceTrackerActivity.this);
                                    builder.setTitle("Prompt!");
                                    builder.setMessage(
                                            "Do you wish to add more or you want to exit.")
                                            .setCancelable(false)
                                            .setNegativeButton("Yes",
                                                    new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog,
                                                                int which) {
                                                            CompressAsync compressAsync = new CompressAsync(bytes);
                                                            compressAsync.execute();
                                                        }
                                                    })
                                            .setPositiveButton("Exit",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog,
                                                                int id) {
                                                            //do things
                                                            setResult(Activity.RESULT_OK);
                                                            EventBus.getDefault().post(new BitmapShareEvent(
                                                                    mPhotoAdapter.getAllItems()));
                                                            finish();
                                                        }
                                                    });

                                    final AlertDialog alert = builder.create();
                                    alert.show();
                                } else if (mBitmapArrayList.size() == MAX_THRESHOLD) {
                                    Intent intent = new Intent(
                                            FaceTrackerActivity.this,
                                            EnrollmentActivity.class);
                                    startActivity(intent);
                                    EventBus.getDefault()
                                            .postSticky(new BitmapShareEvent(mPhotoAdapter.getAllItems()));
                                    finish();
                                } else {
                                    CompressAsync compressAsync = new CompressAsync(bytes);
                                    compressAsync.execute();
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mRecyclerView.scrollToPosition(mPhotoAdapter.getItemCount());
                                        }
                                    }, 300);
                                }

                            }
                        });
                    } else {
                        if(mOverlay != null)
                            mOverlay.clear();
                        Snackbar.make(captureButton, "Face is missing",
                                Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            }
        });
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


    private class CompressAsync extends AsyncTask<Void, Void, Void> {

        private byte[] mByteArray;

        public CompressAsync(byte[] byteArray) {
            mByteArray = byteArray;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(true);
        }

        @Override
        protected Void doInBackground(final Void... voids) {
            mBitmapArrayList.add(
                    Utility.convertToBitmap(FaceTrackerActivity.this,
                            mByteArray));
            return null;
        }

        @Override
        protected void onPostExecute(final Void aVoid) {
            super.onPostExecute(aVoid);
            showProgressDialog(false);

            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {

        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
}
