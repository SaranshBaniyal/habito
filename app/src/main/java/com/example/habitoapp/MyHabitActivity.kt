package com.example.habitoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habitoapp.databinding.ActivityMyHabitBinding
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.IOException

class MyHabitActivity : AppCompatActivity() {
    val gson = Gson()
    var habitList = listOf<HabitDataClass>()
    lateinit var adapter: RecyclerAdapter
    private lateinit var binding: ActivityMyHabitBinding

    open fun setEventAdapter() {
        adapter = RecyclerAdapter(this, habitList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        setEventAdapter()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setEventAdapter()


        binding.createButton.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val requestBody = FormBody.Builder().add("username", HttpClient.username)
            .build()

        val headers = Headers.Builder()
//            .add("Authorization", "Bearer <your_token>")
            .add("Content-Type", "application/json").add("ngrok-skip-browser-warning", "abc")
            .build()

        val request =
            Request.Builder().url("${HttpClient.baseurl}/api/listall/").post(requestBody)
                .headers(headers).build()

        HttpClient.client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                //response.body can only be consumed once from the buffer, so we have to save it (in OkHttp)
                val body: ResponseBody? = response.body
                if (body != null) {
                    val jsonString = body!!.string()
                    val habitArray = gson.fromJson(jsonString, Array<HabitDataClass>::class.java)
                    habitList = habitArray.toList()

                    adapter.data = habitList
                    runOnUiThread {


                    }

                    Log.d("TAG", "Output Request Data:$habitList")
                } else {
                    Log.d("TAG", "Response body is null")
                }
            }


            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", "Output Network Error:$e")
            }
        })
    }
}