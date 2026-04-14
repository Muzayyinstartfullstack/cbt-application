package com.example.cbt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cbt.repository.ExamRepository
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Gunakan ID yang sesuai dengan XML kamu, atau gunakan android.R.id.content agar aman
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // PERBAIKAN: Constructor kosong sesuai ExamRepository.kt
        repository = ExamRepository()

        // Load data user dari Supabase
        loadUserData()

        setupClickListeners()
    }

    private fun loadUserData() {
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserNis = findViewById<TextView>(R.id.tvUserId)

        lifecycleScope.launch {
            try {
                // 1. Ambil User ID yang sedang login
                val userId = repository.getCurrentUserId()

                if (userId != null) {
                    // 2. Ambil profile lengkap dari table 'profiles'
                    val result = repository.getProfile(userId)

                    result.fold(
                        onSuccess = { profile ->
                            // PERBAIKAN: Gunakan fullName dan nisnip sesuai model Profile.kt
                            tvUserName.text = profile.fullName
                            tvUserNis.text = "NIS/NIP: ${profile.nisnip}"
                        },
                        onFailure = { error ->
                            Toast.makeText(this@ProfileActivity, "Gagal memuat profil: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        // Tombol Back
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Tombol Logout
        findViewById<TextView>(R.id.btnLogout)?.setOnClickListener {
            showLogoutDialog()
        }

        // Bottom Navigation
        findViewById<ImageView>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.navHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.navProfile).setOnClickListener {
            Toast.makeText(this, "Anda sudah berada di Profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Keluar")
            .setMessage("Anda yakin ingin keluar?")
            .setCancelable(true)
            .setPositiveButton("Ya") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            // Memanggil fungsi logout di repository (Supabase auth.signOut)
            repository.logout()

            Toast.makeText(this@ProfileActivity, "Anda telah keluar", Toast.LENGTH_SHORT).show()

            // Kembali ke Login dan bersihkan history stack
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}