package com.app.rekog.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bkhera on 2/21/2018.
 */

public class ApiClient {

    public static final String BASE_URL = "http://websocketchat.azurewebsites.net/api/Employee/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {

            HttpLoggingInterceptor interceptorlogging = new HttpLoggingInterceptor();
            interceptorlogging.setLevel(HttpLoggingInterceptor.Level.BODY);


            // Add the interceptor to OkHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(interceptorlogging);

            OkHttpClient client = builder.build();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
