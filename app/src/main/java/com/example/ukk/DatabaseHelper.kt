package com.example.ukk

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ToDoList.db"
        private const val DATABASE_VERSION = 1


        private const val TABLE_USERS = "Users"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"


        private const val TABLE_TASKS = "Tasks"
        private const val COLUMN_TASK_ID = "id"
        private const val COLUMN_USER_ID_FK = "user_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_STATUS = "status"
        private const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {

        val createUserTable = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USERNAME TEXT UNIQUE, " +
                "$COLUMN_PASSWORD TEXT)")


        val createTaskTable = ("CREATE TABLE $TABLE_TASKS (" +
                "$COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USER_ID_FK INTEGER, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_DESCRIPTION TEXT, " +
                "$COLUMN_CATEGORY TEXT, " +
                "$COLUMN_STATUS INTEGER DEFAULT 0, " +
                "$COLUMN_CREATED_AT TEXT, " +
                "FOREIGN KEY($COLUMN_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_USER_ID) ON DELETE CASCADE)")

        db?.execSQL(createUserTable)
        db?.execSQL(createTaskTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }


    fun registerUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }


    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = ("SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?")
        val cursor: Cursor = db.rawQuery(query, arrayOf(username, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }


    fun addTask(userId: Int, title: String, description: String, category: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_ID_FK, userId)
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_DESCRIPTION, description)
        values.put(COLUMN_CATEGORY, category)
        values.put(COLUMN_STATUS, 0)
        values.put(COLUMN_CREATED_AT, System.currentTimeMillis().toString())
        val result = db.insert(TABLE_TASKS, null, values)
        db.close()
        return result
    }


    fun getTasksByUser(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_USER_ID_FK=?",
            arrayOf(userId.toString()),
            null,
            null,
            "$COLUMN_CREATED_AT DESC"
        )
    }


    fun updateTask(taskId: Int, title: String, description: String, category: String, status: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_DESCRIPTION, description)
        values.put(COLUMN_CATEGORY, category)
        values.put(COLUMN_STATUS, status)
        val result = db.update(TABLE_TASKS, values, "$COLUMN_TASK_ID=?", arrayOf(taskId.toString()))
        db.close()
        return result > 0
    }


    fun deleteTask(taskId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_TASKS, "$COLUMN_TASK_ID=?", arrayOf(taskId.toString()))
        db.close()
        return result > 0
    }

    fun getUserId(username: String): Int {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USERNAME=?",
            arrayOf(username),
            null, null, null
        )

        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
        }
        cursor.close()
        db.close()
        return userId
    }

}
