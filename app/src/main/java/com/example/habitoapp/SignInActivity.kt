package com.example.habitoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import okhttp3.*
import com.example.habitoapp.databinding.ActivitySignInBinding
import com.google.android.material.snackbar.Snackbar
import okio.IOException
import org.json.JSONObject


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignUp1.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        }

        binding.btnSignIn1.setOnClickListener {

            val username = binding.etUsername1.text.toString()
            val password = binding.etPassword1.text.toString()
            val requestBody =
                FormBody.Builder().add("username", username).add("password", password).build()

            val headers = Headers.Builder()
//            .add("Authorization", "Bearer <your_token>")
                .add("Content-Type", "application/json").add("ngrok-skip-browser-warning", "abc")
                .build()

            val request =
                Request.Builder().url("${HttpClient.baseurl}/api/login/").post(requestBody)
                    .headers(headers).build()

            HttpClient.client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    val ans = response.body!!.string()
                    val json = JSONObject(ans)
                    val success = json.getBoolean("success")
                    if (success) {
                        Log.d("TAG", "Login Success:$ans")
                        HttpClient.username = username
                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("TAG", "Login Failed:$ans")
                        val snackbar = Snackbar.make(
                            binding.root, "Invalid Credentials", Snackbar.LENGTH_SHORT
                        )
                        snackbar.setAction("Retry") {
                            binding.etUsername1.text.clear()
                            binding.etPassword1.text.clear()
                        }
                        snackbar.show()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    Log.d("TAG", "Network Error:$e")
                    val snackbar =
                        Snackbar.make(binding.root, "Network Error", Snackbar.LENGTH_SHORT)
                    snackbar.setAction("Retry") {
                        binding.etUsername1.text.clear()
                        binding.etPassword1.text.clear()
                    }
                    snackbar.show()
                }
            })
        }
    }
}