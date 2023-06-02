package com.example.habitoapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.habitoapp.databinding.ActivityMainBinding
import com.example.habitoapp.databinding.ActivityVerificationBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit

class VerificationActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var binding: ActivityVerificationBinding
    lateinit var imageUrl: String

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val PERMISSION_REQUEST_CODE = 2
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageView = findViewById(R.id.imageView)
        val captureButton: Button = findViewById(R.id.captureButton)

        storage = Firebase.storage
        storageRef = storage.reference

        binding.submitbutton.setOnClickListener {
            Log.d("TAG", "pressed")

            val requestBody =
                FormBody.Builder().add("username", HttpClient.username)
                    .add("currentdate", getCurrentDate())
                    .add("url", imageUrl).build()

            val headers = Headers.Builder()
                .add("Content-Type", "application/json")
                .add("ngrok-skip-browser-warning", "abc")
                .build()

            val request = Request.Builder()
                .url("${HttpClient.baseurl}/api/verify/")
                .patch(requestBody) // Use .patch() instead of .post()
                .headers(headers)
                .build()

            val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Set the timeout to 60 seconds
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = client.newCall(request).execute()
                    val ans = response.body!!.string()
                    val json = JSONObject(ans)
                    val success = json.getInt("success")
                    withContext(Dispatchers.Main) {
                        if (success == 1) {
                            Log.d("TAG", "Streak Updated")
                            Toast.makeText(this@VerificationActivity, "Streak Updated", Toast.LENGTH_SHORT).show()
                        } else if(success == 2) {
                            Log.d("TAG", "Streak Verification Failed")
                            Toast.makeText(this@VerificationActivity, "Streak Verification Failed", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Log.d("TAG", "Streak Reset")
                            Toast.makeText(this@VerificationActivity, "Streak Reset", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IOException) {
                    Log.d("TAG", "Network Error:$e")
                    Toast.makeText(this@VerificationActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
                val intent =
                    Intent(this@VerificationActivity, MyHabitActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA), VerificationActivity.PERMISSION_REQUEST_CODE
                )
            } else {
                dispatchTakePictureIntent()
            }
        }
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, VerificationActivity.REQUEST_IMAGE_CAPTURE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == VerificationActivity.PERMISSION_REQUEST_CODE) {
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
        if (requestCode == VerificationActivity.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentDate.format(formatter)
    }
}