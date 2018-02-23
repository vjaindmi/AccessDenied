package com.app.rekog.network;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bkhera on 2/21/2018.
 */

public class ApiClient {

    public static final String BASE_URL = "https://api.kairos.com/v2/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit == null) {
            Interceptor interceptor = new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("app_id", "c7d15241")
                            .addHeader("app_key", "fd3287889f836397be1857dd4d0adb11")
                            .build();
                    return chain.proceed(newRequest);
                }
            };

            HttpLoggingInterceptor interceptorlogging = new HttpLoggingInterceptor();
            interceptorlogging.setLevel(HttpLoggingInterceptor.Level.BODY);


            // Add the interceptor to OkHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.interceptors().add(interceptor);
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
