package com.example.cbt

import android.content.Intent // Tambahkan ini agar Intent tidak merah
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Inisialisasi View
        val etNis = findViewById<EditText>(R.id.etNis)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // 2. Set klik listener
        btnLogin.setOnClickListener {
            val nis = etNis.text.toString()
            val password = etPassword.text.toString()

            // 3. Logika Validasi (Urutan harus benar)
            if (nis.isEmpty() || password.isEmpty()) {
                // Jika kosong, tampilkan peringatan
                Toast.makeText(this, "Harap isi NIS dan Password!", Toast.LENGTH_SHORT).show()
            } else {
                // Jika terisi, baru pindah ke Dashboard
                Toast.makeText(this, "Login Berhasil! Selamat mengerjakan.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}