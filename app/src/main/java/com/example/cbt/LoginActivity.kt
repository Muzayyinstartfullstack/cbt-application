package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cbt.repository.ExamRepository
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository
    private lateinit var etNis: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Menambahkan dukungan layar penuh
        setContentView(R.layout.activity_login)

        // Setup Window Insets (Padding Status Bar/Navigation Bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // PERBAIKAN: Gunakan constructor kosong (Supabase tidak butuh RetrofitClient manual di sini)
        repository = ExamRepository()

        // Cek jika sudah login, langsung ke Dashboard
        if (repository.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        initializeViews()
    }

    private fun initializeViews() {
        etNis = findViewById(R.id.etNis)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        btnLogin.setOnClickListener { performLogin() }
    }

    private fun performLogin() {
        val inputNis = etNis.text.toString().trim()
        val inputPassword = etPassword.text.toString().trim()

        if (inputNis.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show()
            return
        }

        // UI State: Loading
        btnLogin.isEnabled = false
        btnLogin.text = "Mohon Tunggu..."
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = repository.login(inputNis, inputPassword)

                result.fold(
                    onSuccess = { profile ->
                        // PERBAIKAN: Gunakan fullName sesuai dengan file Profile.kt kamu
                        Toast.makeText(
                            this@LoginActivity,
                            "Login berhasil! Selamat datang ${profile.fullName}",
                            Toast.LENGTH_SHORT
                        ).show()

                        navigateToDashboard()
                    },
                    onFailure = { error ->
                        resetUIState()
                        Toast.makeText(this@LoginActivity, "Gagal: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                resetUIState()
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetUIState() {
        btnLogin.isEnabled = true
        btnLogin.text = "Login"
        progressBar.visibility = View.GONE
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        // Clear task agar user tidak bisa kembali ke layar login dengan tombol back
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}