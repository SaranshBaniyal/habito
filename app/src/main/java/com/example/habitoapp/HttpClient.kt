package com.example.habitoapp


import okhttp3.OkHttpClient

object HttpClient {
    val baseurl = "https://b8d8-103-61-255-177.ngrok-free.app"
//    lateinit var username: String
    lateinit var habitname: String
    var username= "test6"
    val client = OkHttpClient()
}