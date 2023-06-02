package com.example.habitoapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.habitoapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var binding: ActivityMainBinding
    lateinit var imageUrl: String

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        private const val PERMISSION_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = findViewById(R.id.imageView)
        val captureButton: Button = findViewById(R.id.captureButton)

        storage = Firebase.storage
        storageRef = storage.reference

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("https://example.com")
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle network failure
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle response
            }
        })

        binding.submitbutton.setOnClickListener {
            Log.d("TAG", "pressed")
            if (binding.etHabitName.text.isNotEmpty()) {
                Log.d("TAG", "not empty")
                val habitname = binding.etHabitName.text.toString()
                val requestBody =
                    FormBody.Builder().add("username", HttpClient.username)
                        .add("url", imageUrl)
                        .add("habitname", habitname).build()

                val headers = Headers.Builder()
                    .add("Content-Type", "application/json")
                    .add("ngrok-skip-browser-warning", "abc")
                    .build()

                val request1 =
                    Request.Builder().url("${HttpClient.baseurl}/api/create/")
                        .post(requestBody)
                        .headers(headers).build()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = httpClient.newCall(request1).execute()
                        val ans = response.body!!.string()
                        val json = JSONObject(ans)
                        val success = json.getInt("success")
                        withContext(Dispatchers.Main) {
                            if (success == 1) {
                                Log.d("TAG", "Standard Photo Added Success:$ans")
                                val intent =
                                    Intent(this@MainActivity, MyHabitActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                Log.d("TAG", "Standard Photo Failed:$ans")
                                val snackbar = Snackbar.make(
                                    binding.root,
                                    "Standard Photo Failed",
                                    Snackbar.LENGTH_SHORT
                                )
                                snackbar.setAction("Retry") {
                                    binding.etHabitName.text.clear()
                                }
                                snackbar.show()
                            }
                        }
                    } catch (e: IOException) {
                        Log.d("TAG", "Network Error:$e")
                        withContext(Dispatchers.Main) {
                            val snackbar = Snackbar.make(
                                binding.root,
                                "Network Error",
                                Snackbar.LENGTH_SHORT
                            )
                            snackbar.setAction("Retry") {
                                binding.etHabitName.text.clear()
                            }
                            snackbar.show()
                        }
                    }
                }
            } else {
                binding.etHabitName.error = "Required"
            }
        }

        captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE
                )
            } else {
                dispatchTakePictureIntent()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(
                    this, "Camera permission denied", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                imageView.setImageBitmap(imageBitmap)
                uploadImage(imageBitmap)
            }
        }
    }

    private fun uploadImage(imageBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val filename = "image_${UUID.randomUUID()}.jpg"
        val imageRef = storageRef.child(filename)
        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                imageUrl = uri.toString()
                Log.d("url", imageUrl)
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }
}