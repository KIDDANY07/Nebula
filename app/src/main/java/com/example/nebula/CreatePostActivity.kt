package com.example.nebula

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp.database.com.example.nebula.DatabaseHelper

class CreatePostActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("USER_ID", -1)

        val editTextPostContent = findViewById<EditText>(R.id.editTextPostContent)
        val buttonCreatePost = findViewById<Button>(R.id.buttonCreatePost)
        val buttonSelectImage = findViewById<Button>(R.id.buttonSelectImage)
        val imageViewPreview = findViewById<ImageView>(R.id.imageViewPreview)

        buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }

        buttonCreatePost.setOnClickListener {
            val content = editTextPostContent.text.toString()
            if (content.isNotBlank()) {
                val postId = dbHelper.insertPost(userId, content, selectedImageUri.toString())
                Toast.makeText(this, "Publicación creada", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Por favor, escribe algo en la publicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            val imageViewPreview = findViewById<ImageView>(R.id.imageViewPreview)
            imageViewPreview.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val IMAGE_REQUEST_CODE = 1000
    }
}
