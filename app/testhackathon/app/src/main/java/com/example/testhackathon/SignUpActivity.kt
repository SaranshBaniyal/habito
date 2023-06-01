package com.example.testhackathon


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.testhackathon.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.IOException

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignIn2.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        }
        binding.btnSignUp2.setOnClickListener {
            //sending redundant values because abhi backend change karne ki himmat nahi hai
            val full_name = binding.etUsername2.text.toString()
//            val email = binding.etUsername2.text.toString()
            //yaha tak. Maybe later
            val username = binding.etUsername2.text.toString()
            val password = binding.etPassword2.text.toString()
            val cpassword = binding.etConfirmPassword2.text.toString()
            if(password.equals(cpassword)){
                val requestBody = FormBody.Builder()
                    .add("username", username)
                    .add("full_name", full_name)
//                    .add("email", email)
                    .add("password", password)
                    .build()

                val headers = Headers.Builder()
//            .add("Authorization", "Bearer <your_token>")
                    .add("Content-Type", "application/json")
                    .add("ngrok-skip-browser-warning", "abc")
                    .build()

                val request = Request.Builder()
                    .url("${HttpClient.baseurl}/api/accounts/signup/")
                    .post(requestBody)
                    .headers(headers)
                    .build()

                HttpClient.client.newCall(request).enqueue(object : Callback {
                    override fun onResponse(call: Call, response: Response) {
                        val ans = response.body!!.string()
                        Log.d("TAG", "Signup Response:$ans")
                        HttpClient.username = username
                        val intent = Intent(this@SignUpActivity, MyHabits::class.java)
                        startActivity(intent)
                        finish()
                    }

                    override fun onFailure(call: Call, e: IOException) {
                        Log.d("TAG","Signup Failure:"+e)
                        val snackbar = Snackbar.make(binding.root, "Network Error", Snackbar.LENGTH_SHORT)
                        snackbar.setAction("Retry") {
                            binding.etUsername2.text.clear()
                            binding.etPassword2.text.clear()
                            binding.etConfirmPassword2.text.clear()
                        }
                        snackbar.show()
                    }
                })
            }
            else{
                val snackbar = Snackbar.make(binding.root, "Password doesn't match", Snackbar.LENGTH_SHORT)
                snackbar.setAction("Retry") {
                    binding.etUsername2.text.clear()
                    binding.etPassword2.text.clear()
                    binding.etConfirmPassword2.text.clear()
                }
                snackbar.show()
            }
        }
    }
}
