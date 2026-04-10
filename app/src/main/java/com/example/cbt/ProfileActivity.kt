package com.example.cbt

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.app.AlertDialog
import android.widget.Toast

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        loadUserData()
        setupClickListeners()
    }

    private fun initViews() {
        // Existing views initialization
    }

    private fun loadUserData() {
        // Load user name and ID dari SharedPreferences atau database
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserId = findViewById<TextView>(R.id.tvUserId)

        // Example: tvUserName.text = "User Menuser"
        // Example: tvUserId.text = "ID: 202422123"
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Menu items
        //val informasiPribadi = findViewById<LinearLayout>(R.id.menu_informasi_pribadi)
        //informasiPribadi?.setOnClickListener {
        //    startActivity(Intent(this, InformasiPribadiActivity::class.java))
        //}

        //val pengaturanAkun = findViewById<LinearLayout>(R.id.menu_pengaturan_akun)
        //pengaturanAkun?.setOnClickListener {
        //    startActivity(Intent(this, PengaturanAkunActivity::class.java))
        //}

        // Bottom navigation
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
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun performLogout() {
        // Clear SharedPreferences session
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        Toast.makeText(this, "Anda telah keluar", Toast.LENGTH_SHORT).show()

        // Navigate to LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}