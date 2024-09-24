package com.example.nebula

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.database.com.example.nebula.DatabaseHelper
import com.example.myapp.database.com.example.nebula.Post


class HomeActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var postAdapter: PostAdapter
    private lateinit var postList: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        dbHelper = DatabaseHelper(this)
        postList = mutableListOf()

        val recyclerView = findViewById<RecyclerView>(R.id.postsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postAdapter = PostAdapter(this, postList)
        recyclerView.adapter = postAdapter

        loadPosts()

        findViewById<Button>(R.id.btnChat).setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        findViewById<Button>(R.id.btnCreatePost).setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }
    }

    private fun loadPosts() {
        val cursor = dbHelper.getAllPosts()
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val content = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT))
                    val imageUrl = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_URL))
                    val timestamp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP))
                    postList.add(Post(content, imageUrl, timestamp))
                } while (cursor.moveToNext())
            }
            cursor.close()
        } else {
            Toast.makeText(this, "No se pudo cargar publicaciones", Toast.LENGTH_SHORT).show()
        }
        postAdapter.notifyDataSetChanged()
    }
}
