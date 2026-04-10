package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etNis = findViewById<EditText>(R.id.etNis)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val inputNis = etNis.text.toString()
            val inputPassword = etPassword.text.toString()

            // 1. Validasi jika kosong
            if (inputNis.isEmpty() || inputPassword.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show()
            }
            // 2. Cek Data Dummy
            else if (inputNis == "12345" && inputPassword == "admin123") {
                // Jika Benar
                Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish() // Tutup login agar tidak bisa back
            }
            // 3. Jika Salah
            else {
                Toast.makeText(this, "NIS atau Password Salah!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}