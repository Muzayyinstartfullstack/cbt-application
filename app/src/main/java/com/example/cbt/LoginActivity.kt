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

import com.example.cbt.data.repository.ExamRepository

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



        // Inisialisasi repository (biasanya butuh application context)

        repository = ExamRepository(applicationContext)



        // Jika sudah login, langsung ke dashboard

        if (repository.isLoggedIn()) {

            navigateToDashboard()

            return

        }



        initViews()

    }



    private fun initViews() {

        etNis = findViewById(R.id.etNis)

        etPassword = findViewById(R.id.etPassword)

        btnLogin = findViewById(R.id.btnLogin)

        progressBar = findViewById(R.id.progressBar)



        btnLogin.setOnClickListener { performLogin() }

    }



    private fun performLogin() {

        val nis = etNis.text.toString().trim()

        val password = etPassword.text.toString().trim()



        if (nis.isEmpty() || password.isEmpty()) {

            Toast.makeText(this, "NIS dan password harus diisi", Toast.LENGTH_SHORT).show()

            return

        }



        // Validasi format password (harus 8 digit angka, YYYYMMDD)

        if (!password.matches(Regex("\\d{8}"))) {

            Toast.makeText(this, "Password harus 8 digit angka (tanggal lahir YYYYMMDD)", Toast.LENGTH_SHORT).show()

            return

        }



        setLoading(true)



        lifecycleScope.launch {

            val result = repository.loginWithNisCustom(nis, password)

            setLoading(false)



            result.fold(

                onSuccess = { profile ->

                    Toast.makeText(

                        this@LoginActivity,

                        "Selamat datang, ${profile.fullName ?: profile.nis}",

                        Toast.LENGTH_SHORT

                    ).show()

                    navigateToDashboard()

                },

                onFailure = { error ->

                    Toast.makeText(

                        this@LoginActivity,

                        "Login gagal: ${error.message}",

                        Toast.LENGTH_LONG

                    ).show()

                }

            )

        }

    }



    private fun setLoading(isLoading: Boolean) {

        btnLogin.isEnabled = !isLoading

        btnLogin.text = if (isLoading) "Memproses..." else "Login"

        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

    }



    private fun navigateToDashboard() {

        val intent = Intent(this, DashboardActivity::class.java).apply {

            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        }

        startActivity(intent)

        finish()

    }

}