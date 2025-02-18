package com.example.ukk

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLoginHere: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        etUsername = findViewById(R.id.etUsernameReg)
        etPassword = findViewById(R.id.etPasswordReg)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginHere = findViewById(R.id.tvLoginHere)


        dbHelper = DatabaseHelper(this)


        tvLoginHere.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        btnRegister.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()


            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Password minimal 8 karakter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password dan Konfirmasi Password tidak sama", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val userExists = dbHelper.checkUser(username, password)
            if (userExists) {
                Toast.makeText(this, "Username sudah digunakan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val isRegistered = dbHelper.registerUser(username, password)
            if (isRegistered) {
                Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                // Pindah ke Login setelah Register
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Registrasi Gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
