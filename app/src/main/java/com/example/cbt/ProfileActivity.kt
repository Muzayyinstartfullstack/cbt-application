package com.example.cbt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.example.cbt.data.repository.ExamRepository
import com.example.cbt.data.model.Profile
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository

    // Views untuk statistik (nullable karena mungkin tidak ada di layout)
    private var tvExamCompleted: TextView? = null
    private var tvAverageScore: TextView? = null
    private var tvRemedialCount: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Setup Window Insets
        val rootView = findViewById<View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi repository dengan context
        repository = ExamRepository(applicationContext)

        // Inisialisasi Views
        initViews()

        // Load Data
        loadUserData()
        loadUserStatistics()

        // Setup Listeners
        setupClickListeners()
    }

    private fun initViews() {
        // View statistik tidak ada di layout - fitur disabled
        // tvExamCompleted = findViewById(R.id.tvExamCompleted)
        // tvAverageScore = findViewById(R.id.tvAverageScore)
        // tvRemedialCount = findViewById(R.id.tvRemedialCount)
    }

    private fun loadUserData() {
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvUserNis = findViewById<TextView>(R.id.tvUserId)

        lifecycleScope.launch {
            val userId = repository.getCurrentUserId() ?: return@launch

            repository.getProfile(userId).fold(
                onSuccess = { profile ->
                    // PERBAIKAN 3: Gunakan fullName dan nisnip sesuai Profile.kt
                    tvUserName.text = profile.fullName
                    tvUserNis.text = "NIS: ${profile.nis}"
                },
                onFailure = { error ->
                    Toast.makeText(this@ProfileActivity, "Gagal: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun loadUserStatistics() {
        lifecycleScope.launch {
            val userId = repository.getCurrentUserId() ?: return@launch

            // PERBAIKAN 4: Panggil fungsi statistik di repository
            repository.getUserStatistics(userId).fold(
                onSuccess = { stats ->
                    tvExamCompleted?.text = stats.completedExams.toString()
                    tvAverageScore?.text = "${stats.averageScore.toInt()}%"
                    tvRemedialCount?.text = stats.remedialCount.toString()
                },
                onFailure = {
                    tvExamCompleted?.text = "0"
                    tvAverageScore?.text = "0%"
                    tvRemedialCount?.text = "0"
                }
            )
        }
    }

    private fun setupClickListeners() {
        // Back button
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener { finish() }

        // Logout
        findViewById<LinearLayout>(R.id.btnLogout)?.setOnClickListener { showLogoutDialog() }

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
            Toast.makeText(this, "Anda sudah di Profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Keluar")
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ -> performLogout() }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            repository.logout()
            Toast.makeText(this@ProfileActivity, "Berhasil keluar", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}