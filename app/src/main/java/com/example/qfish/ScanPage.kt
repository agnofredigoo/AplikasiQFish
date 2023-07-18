package com.example.qfish

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.qfish.databinding.ActivityHomeBinding
import com.example.qfish.databinding.ActivityMainBinding
import com.example.qfish.databinding.ActivityScanPageBinding
import android.Manifest
import android.content.Context
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ScanPage : AppCompatActivity() {
    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
    private lateinit var currentPhotoPath: String
    private lateinit var selectedImageUri: Uri
    private lateinit var bind: ActivityScanPageBinding
    private lateinit var image : Bitmap
    private val pixel = 224

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun updateButtonBackground() {
        val enabledColor = Color.parseColor("#A2D2FF")  // Replace with your desired color
        val disabledColor = Color.parseColor("#D3D3D3")  // Replace with your desired color

        val colorStateList = ColorStateList(
            arrayOf(intArrayOf(androidx.appcompat.R.attr.background), intArrayOf()),
            intArrayOf(enabledColor, disabledColor)
        )

        bind.unggahbutton.backgroundTintList = colorStateList
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityScanPageBinding.inflate(layoutInflater)
        setContentView(bind.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val mulaicamera = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                val myFile = File(currentPhotoPath)

//            Silakan gunakan kode ini jika mengalami perubahan rotasi
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                true
            )

                bind.imageView2.setImageBitmap(result)
            }
        }

        bind.camerabotton.setOnClickListener { val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)

            createTempFile(application).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@ScanPage,
                    "com.example.qfish",
                    it
                )
                currentPhotoPath = it.absolutePath

//                val bitmap = BitmapFactory.decodeFile(selectedImageUri.path)
//                image = Bitmap.createScaledBitmap(bitmap, 224,224, true)
                this.selectedImageUri = photoURI
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)


        mulaicamera.launch(intent)
            } }

        val mulaiGallery = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg: Uri = result.data?.data as Uri
//                val myFile = uriToFile(selectedImg, this@ScanPage)
                bind.imageView2.setImageURI(selectedImg)
//                val bitmap = BitmapFactory.decodeFile(selectedImg.path)
//                image = Bitmap.createScaledBitmap(bitmap, 224, 224, false)
//                val imageFish = image as Uri
                this.selectedImageUri = selectedImg
            }
        }



        bind.gallerybotton.setOnClickListener { val intent = Intent()
            intent.action = ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Choose a Picture")
            mulaiGallery.launch(chooser)
}

        bind.unggahbutton.setOnClickListener {
//            val goToIkanActivity = Intent(this, IkanActivity::class.java)
//            goToIkanActivity.putExtra("SELECTED_IMAGE", this.selectedImageUri)
//            startActivity(goToIkanActivity)
            bind.unggahbutton.isEnabled = false
            bind.unggahbutton.text = "DIPROSES..."
            updateButtonBackground()
            sendAPIRequest()
        }
    }

    private fun goToIkan(hasil: Double){
        val goToIkanActivity = Intent(this, IkanActivity::class.java)
        goToIkanActivity.putExtra("SELECTED_IMAGE", this.selectedImageUri)
        var nilai = ((1-hasil)*100).toInt()
        var score = nilai.toString()
        goToIkanActivity.putExtra("CONFIDENCE", score)
        if (hasil<0.5){
            goToIkanActivity.putExtra("LABEL", "DITERIMA")
        } else {
            goToIkanActivity.putExtra("LABEL", "DITOLAK")
        }

        startActivity(goToIkanActivity)
    }

    private fun sendAPIRequest() {
        val url = "http://34.101.154.74:5000/classify"

        val client = OkHttpClient()
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImageUri))
        image = Bitmap.createScaledBitmap(bitmap, 224,224, true)
        val uri = bitmapToFile(this,image)
        val sendPhoto = Uri.fromFile(uri)

        // Create the request body
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "image.jpg", createRequestBodyFromUri(sendPhoto))
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle request failure
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonString = responseBody.string()
                    val jsonObject = JSONObject(jsonString)
                    val hasil = jsonObject.getDouble("hasil")
//                    println(responseBody)
//                    println(jsonString)
//                    println(jsonObject)
//                    println(result)

                    goToIkan(hasil)

                }
            }
        })
    }

    private fun createRequestBodyFromUri(uri: Uri): RequestBody {
        val contentResolver = applicationContext.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        return inputStream?.use { inputStream ->
            val byteArray = inputStream.readBytes()
            return@use RequestBody.create(contentResolver.getType(uri)?.toMediaTypeOrNull(), byteArray)
        } ?: throw IOException("Failed to read input stream for Uri: $uri")
    }

    fun bitmapToFile(context: Context, bitmap: Bitmap): File? {
        // Get the directory for the app's private pictures directory
        val fileDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (fileDir != null) {
            try {
                // Create a file to save the bitmap
                val file = File(fileDir, "bitmap_image.jpg")

                // Compress the bitmap and save it to the file
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.flush()
                outputStream.close()

                return file
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return null
    }

}