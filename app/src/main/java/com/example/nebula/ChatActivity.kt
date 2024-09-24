package com.example.nebula

import android.os.Bundle
import com.example.myapp.database.com.example.nebula.Message  // Asegúrate de que esta es la
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.database.com.example.nebula.DatabaseHelper

class ChatActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: MutableList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        dbHelper = DatabaseHelper(this)
        messageList = mutableListOf()

        val recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        chatAdapter = ChatAdapter(messageList)
        recyclerView.adapter = chatAdapter

        findViewById<Button>(R.id.sendMessageButton).setOnClickListener {
            val messageText = findViewById<EditText>(R.id.messageEditText).text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                findViewById<EditText>(R.id.messageEditText).text.clear()
            }
        }

        loadMessages()
    }

    private fun loadMessages() {
        val cursor = dbHelper.getChatMessages(senderId = 1, receiverId = 2)
        if (cursor.moveToFirst()) {
            do {
                val message = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_TEXT))
                val timestamp = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MESSAGE_TIMESTAMP))
                messageList.add(Message(message, timestamp))
            } while (cursor.moveToNext())
        }
        chatAdapter.notifyDataSetChanged()
    }

    private fun sendMessage(messageText: String) {
        val senderId = 1
        val receiverId = 2
        dbHelper.insertMessage(senderId, receiverId, messageText)
        loadMessages()
    }
}