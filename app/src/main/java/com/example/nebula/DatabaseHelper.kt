package com.example.myapp.database.com.example.nebula

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "myapp.db"
        const val DATABASE_VERSION = 2

        // Tablas y columnas
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_IMAGE_URL = "image_uri"
        const val COLUMN_USERNAME_POST = "username"

        const val TABLE_POSTS = "posts"
        const val COLUMN_POST_ID = "id"
        const val COLUMN_USER_ID_FK = "user_id"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_TIMESTAMP = "timestamp"

        const val TABLE_MESSAGES = "messages"
        const val COLUMN_MESSAGE_ID = "id"
        const val COLUMN_SENDER_ID = "sender_id"
        const val COLUMN_RECEIVER_ID = "receiver_id"
        const val COLUMN_MESSAGE_TEXT = "message"
        const val COLUMN_MESSAGE_TIMESTAMP = "timestamp"
    }

    // Creación de las tablas en la base de datos
    override fun onCreate(db: SQLiteDatabase) {

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """
        db.execSQL(createUsersTable)

        val createPostsTable = """
            CREATE TABLE $TABLE_POSTS (
                $COLUMN_POST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID_FK INTEGER,
                $COLUMN_CONTENT TEXT NOT NULL,
                $COLUMN_IMAGE_URL TEXT,
                $COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """

        db.execSQL(createPostsTable)

        val createMessagesTable = """
            CREATE TABLE $TABLE_MESSAGES (
                $COLUMN_MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SENDER_ID INTEGER,
                $COLUMN_RECEIVER_ID INTEGER,
                $COLUMN_MESSAGE_TEXT TEXT NOT NULL,
                $COLUMN_MESSAGE_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY($COLUMN_SENDER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID),
                FOREIGN KEY($COLUMN_RECEIVER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)
            )
        """
        db.execSQL(createMessagesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POSTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        onCreate(db)
    }

    // Insertar un nuevo usuario
    fun insertUser(username: String, password: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, contentValues)
    }

    // Validacion de inicio de sesión
    fun getUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password)
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // Insertar un nuevo post
    fun insertPost(userId: Int, content: String, imageUrl: String?): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_CONTENT, content)
            put(COLUMN_IMAGE_URL, imageUrl)
            put(COLUMN_TIMESTAMP, System.currentTimeMillis())
        }
        return db.insert(TABLE_POSTS, null, values)
    }


    fun getAllPosts(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_POSTS", null)
    }



    // Insertar un mensaje en el chat
    fun insertMessage(senderId: Int, receiverId: Int, message: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_SENDER_ID, senderId)
            put(COLUMN_RECEIVER_ID, receiverId)
            put(COLUMN_MESSAGE_TEXT, message)
        }
        return db.insert(TABLE_MESSAGES, null, contentValues)
    }

    // Obtener los mensajes entre dos usuarios
    fun getChatMessages(senderId: Int, receiverId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery(
            """
            SELECT * FROM $TABLE_MESSAGES 
            WHERE ($COLUMN_SENDER_ID = ? AND $COLUMN_RECEIVER_ID = ?) 
               OR ($COLUMN_SENDER_ID = ? AND $COLUMN_RECEIVER_ID = ?)
            ORDER BY $COLUMN_MESSAGE_TIMESTAMP ASC
            """,
            arrayOf(senderId.toString(), receiverId.toString(), receiverId.toString(), senderId.toString())
        )
    }

    // Obtener perfil de usuario por ID
    fun getUserById(userId: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }

    // Actualizar datos del perfil de usuario
    fun updateUserProfile(userId: Int, newUsername: String, newPassword: String): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_USERNAME, newUsername)
            put(COLUMN_PASSWORD, newPassword)
        }
        return db.update(TABLE_USERS, contentValues, "$COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }

    // Eliminar un post por ID
    fun deletePost(postId: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_POSTS, "$COLUMN_POST_ID = ?", arrayOf(postId.toString()))
    }
}
