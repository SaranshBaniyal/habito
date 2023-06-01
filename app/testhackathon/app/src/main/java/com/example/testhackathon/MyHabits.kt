package com.example.testhackathon

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testhackathon.databinding.ActivityMyHabitsBinding
import com.google.android.gms.common.api.Response
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.security.auth.callback.Callback

class MyHabits : AppCompatActivity() {
    private lateinit var binding: ActivityMyHabitsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHabitsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.createButton.setOnClickListener{
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

//        val retrofit = Retrofit.Builder().baseUrl(HttpClient.baseurl)
//            .addConverterFactory(GsonConverterFactory.create()).build()
//
//        val apiService = retrofit.create(RetrofitInterface::class.java)
//
//        val call = apiService.getMyHabits("test1")
//
//        call.enqueue(object : retrofit2.Callback<List<HabitsDataClass>> {
//            override fun onResponse(
//                call: retrofit2.Call<List<HabitsDataClass>>,
//                response: retrofit2.Response<List<HabitsDataClass>>
//            ) {
//                if (response.isSuccessful) {
//                    val res = response.body()
//                    Log.d("tag", "onResponse: ${res}")
//
//                    binding.recyclerView.adapter = RecyclerAdapter(res ?: emptyList())
//                }
//            }
//
//            override fun onFailure(call: retrofit2.Call<List<HabitsDataClass>>, t: Throwable) {
//                Log.d("tag", "fail")
//            }
//        })
//        val requestBody = FormBody.Builder().add("username", HttpClient.username)
//            .build()
//
//        val headers = Headers.Builder()
////            .add("Authorization", "Bearer <your_token>")
//            .add("Content-Type", "application/json").add("ngrok-skip-browser-warning", "abc")
//            .build()
//
//        val request =
//            Request.Builder().url("${HttpClient.baseurl}/api/accounts/output/").post(requestBody)
//                .headers(headers).build()
//
//        HttpClient.client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                //response.body can only be consumed once from the buffer, so we have to save it (in OkHttp)
//                val body: ResponseBody? = response.body
//                if (body != null) {
//                    val jsonString = body!!.string()
//                    val journalArray = gson.fromJson(jsonString, Array<Journal>::class.java)
//                    dailyJournal = journalArray.toList()
//
//                    adapter.journalList = dailyJournal
//                    runOnUiThread {
//
//
//                    }
//
//                    Log.d("TAG", "Output Request Data:$dailyJournal")
//                } else {
//                    Log.d("TAG", "Response body is null")
//                }
//            }
//
//
//            override fun onFailure(call: Call, e: IOException) {
//                Log.d("TAG", "Output Network Error:$e")
//            }
//        })
    }
}