package com.example.testhackathon

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

 class RetrofitInstance{
    val retrofit = Retrofit.Builder()
        .baseUrl(HttpClient.baseurl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
