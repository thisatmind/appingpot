package com.thisatmind.appingpot.appingpot.rest;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by patrick on 2016-10-02.
 */

public class RestClient {

    private static final String URL = "https://appingpotnodeserver-patrick-shim.c9users.io/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create());

    public RestClient(){}

    public static <S> S createService(Class<S> serviceClass)
    {
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy(){
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                }).create();

        Retrofit retrofit = builder.client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();

        return retrofit.create(serviceClass);


//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(URL)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build();
//
//        return retrofit.create(EventService.class);
    }
}
