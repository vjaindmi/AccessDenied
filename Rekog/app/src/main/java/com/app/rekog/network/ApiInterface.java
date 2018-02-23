package com.app.rekog.network;

import com.app.rekog.beans.EmotionResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by bkhera on 2/21/2018.
 */

public interface ApiInterface {
    @Multipart
    @POST("media")
    Call<EmotionResponse> checkEmotion(@Part MultipartBody.Part file);

}
