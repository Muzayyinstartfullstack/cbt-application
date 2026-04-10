package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cbt.api.RetrofitClient
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
        setContentView(R.layout.activity_login)

        // Initialize repository
        repository = ExamRepository(RetrofitClient.instance, this)

        // Check if already logged in
        if (repository.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        // Initialize views
        etNis = findViewById(R.id.etNis)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar) // Pastikan ada di XML

        btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val inputNis = etNis.text.toString().trim()
        val inputPassword = etPassword.text.toString().trim()

        // Validasi input
        if (inputNis.isEmpty() || inputPassword.isEmpty()) {
            Toast.makeText(this, "Harap isi semua kolom!", Toast.LENGTH_SHORT).show()
            return
        }

        if (inputNis.length < 5) {
            Toast.makeText(this, "NIS minimal 5 karakter!", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable button dan show loading
        btnLogin.isEnabled = false
        progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = repository.login(inputNis, inputPassword)

                result.onSuccess { loginResponse ->
                    Toast.makeText(
                        this@LoginActivity,
                        "Login berhasil! Selamat datang ${loginResponse.studentName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToDashboard()
                }

                result.onFailure { error ->
                    btnLogin.isEnabled = true
                    progressBar.visibility = android.view.View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "Login gagal: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                btnLogin.isEnabled = true
                progressBar.visibility = android.view.View.GONE
                Toast.makeText(
                    this@LoginActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}