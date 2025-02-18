package com.example.ukk

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class TaskActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var taskListView: ListView
    private lateinit var addTaskButton: Button
    private var userId: Int = 0
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)


        dbHelper = DatabaseHelper(this)
        taskListView = findViewById(R.id.taskListView)
        addTaskButton = findViewById(R.id.addTaskButton)


        userId = intent.getIntExtra("USER_ID", 0)


        loadTasks()


        addTaskButton.setOnClickListener {
            showTaskDialog(null)
        }


        taskListView.setOnItemClickListener { _, _, position, _ ->
            val selectedTask = taskAdapter.getItem(position)
            selectedTask?.let {
                showTaskDialog(it)
            }
        }
    }

    private fun showTaskDialog(it: Any) {
        TODO("Not yet implemented")
    }

    private fun loadTasks() {
        val cursor = dbHelper.getTasksByUser(userId)
        val tasks = mutableListOf<Task>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val category = cursor.getString(cursor.getColumnIndexOrThrow("category"))
                val status = cursor.getInt(cursor.getColumnIndexOrThrow("status"))

                tasks.add(Task(id, userId, title, description, category, status))
            } while (cursor.moveToNext())
        }
        cursor.close()

        taskAdapter = TaskAdapter(this, tasks)
        taskListView.adapter = taskAdapter
    }

    private fun showTaskDialog(task: Task?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.etTitle)
        val descriptionInput = dialogView.findViewById<EditText>(R.id.etDescription)
        val categoryInput = dialogView.findViewById<EditText>(R.id.etCategory)

        val isEdit = task != null

        if (isEdit) {
            titleInput.setText(task?.title)
            descriptionInput.setText(task?.description)
            categoryInput.setText(task?.category)
        }

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle(if (isEdit) "Ubah Tugas" else "Tambah Tugas")
            .setPositiveButton(if (isEdit) "Simpan" else "Tambah") { _, _ ->
                val title = titleInput.text.toString().trim()
                val description = descriptionInput.text.toString().trim()
                val category = categoryInput.text.toString().trim()

                if (title.isEmpty() || description.isEmpty() || category.isEmpty()) {
                    Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (isEdit) {
                    dbHelper.updateTask(task!!.id, title, description, category, task.status)
                    Toast.makeText(this, "Tugas Diperbarui", Toast.LENGTH_SHORT).show()
                } else {
                    dbHelper.addTask(userId, title, description, category)
                    Toast.makeText(this, "Tugas Ditambahkan", Toast.LENGTH_SHORT).show()
                }

                loadTasks()
            }
            .setNegativeButton("Batal", null)
            .setNeutralButton(if (isEdit) "Hapus" else "") { _, _ ->
                if (isEdit) {
                    showDeleteConfirmation(task!!)
                }
            }

        dialogBuilder.create().show()
    }

    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah kamu yakin ingin menghapus tugas ini?")
            .setPositiveButton("Hapus") { _, _ ->
                dbHelper.deleteTask(task.id)
                Toast.makeText(this, "Tugas Dihapus", Toast.LENGTH_SHORT).show()
                loadTasks()
            }
            .setNegativeButton("Batal", null)
            .create()
            .show()
    }
}
