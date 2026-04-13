package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.view.View
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

        com.example.cbt.api.TokenManager.init(this)
        repository = ExamRepository(RetrofitClient.instance, this)

        if (repository.isLoggedIn()) {
            navigateToDashboard()
            return
        }

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

        btnLogin.isEnabled = false
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = repository.login(inputNis, inputPassword)

                result.onSuccess { loginResponse ->
                    // Simpan token ke TokenManager agar bisa dipakai di setiap request
                    com.example.cbt.api.TokenManager.saveToken(this@LoginActivity, loginResponse.token)
                    Toast.makeText(
                        this@LoginActivity,
                        "Login berhasil! Selamat datang ${loginResponse.nama}",
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToDashboard()
                }

                result.onFailure { error ->
                    btnLogin.isEnabled = true
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@LoginActivity,
                        "Login gagal: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                btnLogin.isEnabled = true
                progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}