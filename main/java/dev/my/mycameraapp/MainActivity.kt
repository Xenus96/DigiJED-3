package dev.my.mycameraapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private var photoUri: Uri? = null
    private lateinit var imageView: ImageView

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageView.setImageURI(photoUri)
        } else {
            Toast.makeText(this, "Failed to take picture", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val takeAPhoto = findViewById<Button>(R.id.TakeAPhoto)
        val shareOptions = findViewById<Button>(R.id.ShareOptions)
        imageView = findViewById(R.id.imageView)

        // Set click listeners for buttons
        takeAPhoto.setOnClickListener { openCamera() }
        shareOptions.setOnClickListener { shareImage() }

    }

    private fun openCamera() {
        val photoFile = createImageFile()
        photoFile?.let {
            val localPhotoUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                it
            )
            photoUri = localPhotoUri  // Assign to class-level photoUri for later use
            takePictureLauncher.launch(localPhotoUri)
        }
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                Date()
            )
            val imageFileName = "JPEG_${timeStamp}_"
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(imageFileName, ".jpg", storageDir)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    @SuppressLint("IntentReset")
    private fun shareImage() {
        val emailAddress = "hodovychenko@op.edu.ua"
        val linkToGit = "https://github.com/Xenus96/DigiJED-3/tree/"

        if (photoUri != null) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_EMAIL, emailAddress)
                putExtra(Intent.EXTRA_STREAM, photoUri)
                putExtra(Intent.EXTRA_SUBJECT, "DigiJED Rieznichenko Dymytrii")
                putExtra(Intent.EXTRA_TEXT, "My Git Repository for this project: $linkToGit")
            }
            startActivity(Intent.createChooser(shareIntent, "Share Image via"))
        } else {
            Toast.makeText(this, "Please take a picture first", Toast.LENGTH_SHORT).show()
        }
    }
}

