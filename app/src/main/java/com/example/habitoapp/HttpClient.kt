package com.example.habitoapp


import okhttp3.OkHttpClient

object HttpClient {
    val baseurl = "https://73ba-103-61-255-177.ngrok-free.app"
    lateinit var username: String
    val client = OkHttpClient()
}