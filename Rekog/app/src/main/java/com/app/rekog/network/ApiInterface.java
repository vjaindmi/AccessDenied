package com.app.rekog.network;

import com.app.rekog.beans.BaseResponse;
import com.app.rekog.beans.RequestBean;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by bkhera on 2/21/2018.
 */

public interface ApiInterface {

    @POST("checkEmail")
    Call<BaseResponse> checkEmail(@Body RequestBean requestBean);

}
