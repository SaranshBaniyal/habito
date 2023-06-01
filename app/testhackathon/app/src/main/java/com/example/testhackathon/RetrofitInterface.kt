package com.example.testhackathon

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitInterface {
    @POST("api/listall/")
    fun getMyHabits(@Query("username") username: String): Call<List<HabitsDataClass>>
}
