package com.example.cbt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cbt.api.RetrofitClient
import com.example.cbt.repository.ExamRepository
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_profile)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize repository
        repository = ExamRepository(RetrofitClient.instance, this)

        // Load user data
        loadUserData()

        // Setup click listeners
        setupClickListeners()
    }

    private fun loadUserData() {
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserNis = findViewById<TextView>(R.id.tvUserId)

        // Get data dari repository
        val studentName = repository.getStudentName()
        val studentNis = repository.getNis()

        tvUserName.text = studentName.ifEmpty { "User" }
        tvUserNis.text = "NIS: $studentNis"

        // Load stats from API
        loadProfileStats()
    }

    private fun loadProfileStats() {
        lifecycleScope.launch {
            try {
                val result = repository.getExamHistory()
                result.onSuccess { examHistory ->
                    val results = examHistory.data
                    val completedCount = results.size
                    val avgScore = if (results.isNotEmpty()) {
                        results.map { it.scorePercentage }.average()
                    } else 0.0
                    val remedialCount = results.count {
                        !it.status.equals("PASSED", ignoreCase = true)
                    }

                    findViewById<TextView>(R.id.tvUjianSelesai)?.text = completedCount.toString()
                    findViewById<TextView>(R.id.tvRataNilaiProfile)?.text = avgScore.toInt().toString()
                    findViewById<TextView>(R.id.tvRemedial)?.text = remedialCount.toString()
                }
            } catch (_: Exception) {
                // Silently fail, stats will show default values
            }
        }
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        // Logout button (LinearLayout in XML, not TextView)
        val btnLogout = findViewById<LinearLayout>(R.id.btnLogout)
        btnLogout?.setOnClickListener {
            showLogoutDialog()
        }

        // Bottom navigation
        findViewById<ImageView>(R.id.navHome).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<ImageView>(R.id.navHistory).setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
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
        // Clear session dari repository
        repository.logout()

        Toast.makeText(this, "Anda telah keluar", Toast.LENGTH_SHORT).show()

        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}