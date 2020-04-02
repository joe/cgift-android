package com.breadwallet.api

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Created by afresina on 2020-01-08.
 * Copyright 2020 CGift.io. All rights reserved.
 */
object CgiftAPIClient {

    const val V1_BASE_API = "https://api.cgift.io/api/v1/"

    @JvmStatic
    fun getApi(context: Context): CgiftAPI {
        return buildRetrofit(context).create(CgiftAPI::class.java)
    }

    private fun buildRetrofit(context: Context): Retrofit {
        val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createAsync())
                .baseUrl(V1_BASE_API)
        val client = OkHttpClient.Builder()
        retrofit.client(client.build())
        return retrofit.build()
    }

}