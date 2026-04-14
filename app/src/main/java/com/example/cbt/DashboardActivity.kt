package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cbt.adapter.ExamHistoryAdapter
import com.example.cbt.api.TokenManager
import com.example.cbt.database.SupabaseClient
import com.example.cbt.model.ExamSession
import com.example.cbt.model.Profile
import com.example.cbt.repository.ExamRepository
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository

    // Views
    private lateinit var tvWelcome: TextView
    private lateinit var btnMulaiUjian: Button
    private lateinit var recyclerExams: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var navHome: ImageView
    private lateinit var navHistory: ImageView
    private lateinit var navProfile: ImageView

    // Data user yang sedang login
    private var currentProfile: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_dashboard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ExamRepository tidak butuh parameter lagi
        repository = ExamRepository()

        // Cek apakah user sudah login via Supabase Auth
        if (!repository.isLoggedIn()) {
            navigateToLogin()
            return
        }

        initializeViews()
        setupClickListeners()

        // Load profil user lalu load ujian
        loadProfileAndExams()
    }

    // ─── INIT ─────────────────────────────────────────────────────────────────

    private fun initializeViews() {
        tvWelcome     = findViewById(R.id.tvWelcome)
        btnMulaiUjian = findViewById(R.id.btnMulaiUjian)
        recyclerExams = findViewById(R.id.recyclerExams)
        progressBar   = findViewById(R.id.progressBar)
        navHome       = findViewById(R.id.navHome)
        navHistory    = findViewById(R.id.navHistory)
        navProfile    = findViewById(R.id.navProfile)

        recyclerExams.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        btnMulaiUjian.setOnClickListener {
            val intent = Intent(this, DetailUjianActivity::class.java)
            startActivity(intent)
        }

        navHome.setOnClickListener {
            Toast.makeText(this, "Anda sudah berada di Beranda", Toast.LENGTH_SHORT).show()
        }

        navHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    // ─── LOAD PROFIL + UJIAN ──────────────────────────────────────────────────

    private fun loadProfileAndExams() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Ambil user id dari Supabase Auth session
                val userId = repository.getCurrentUserId()

                if (userId == null) {
                    navigateToLogin()
                    return@launch
                }

                // Ambil data profil dari tabel profiles
                val profileResult = repository.getProfile(userId)

                profileResult.fold(
                    onSuccess = { profile ->
                        currentProfile = profile
                        // Tampilkan nama dari kolom full_name
                        tvWelcome.text = "Selamat datang, ${profile.fullName}!"

                        // Load riwayat/ujian setelah profil berhasil diambil
                        loadExams(profile.id)
                    },
                    onFailure = { error ->
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@DashboardActivity,
                            "Gagal memuat profil: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@DashboardActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadExams(profileId: String) {
        lifecycleScope.launch {
            try {
                // Ambil riwayat exam_sessions milik user ini
                val result = repository.getExamHistory(profileId)

                progressBar.visibility = View.GONE

                result.fold(
                    onSuccess = { sessions ->
                        if (sessions.isNotEmpty()) {
                            // Ambil 3 sesi terbaru untuk ditampilkan di dashboard
                            val recentSessions = sessions.take(3)
                            setupRecyclerView(recentSessions)
                        } else {
                            Toast.makeText(
                                this@DashboardActivity,
                                "Belum ada ujian",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onFailure = { error ->
                        Toast.makeText(
                            this@DashboardActivity,
                            "Gagal memuat ujian: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@DashboardActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ─── RECYCLER VIEW ────────────────────────────────────────────────────────

    private fun setupRecyclerView(sessions: List<ExamSession>) {
        // Inisialisasi adapter hanya dengan lambda click listener
        val adapter = ExamHistoryAdapter { session ->
            val intent = Intent(this@DashboardActivity, DetailHasilActivity::class.java).apply {
                putExtra("SESSION_ID", session.id)
                putExtra("EXAM_TITLE", examTitle(session))
                putExtra("SCORE", session.score ?: 0.0)
                putExtra("STATUS", session.status)
            }
            startActivity(intent)
        }

        recyclerExams.adapter = adapter

        // Kirim data list menggunakan submitList (Fitur ListAdapter)
        adapter.submitList(sessions)
    }

    // Helper untuk ambil judul ujian dari session
    // Jika embed exams tersedia pakai itu, fallback ke examId
    private fun examTitle(session: ExamSession): String {
        return session.exams?.title ?: "Ujian ${session.examId.takeLast(4)}"
    }

    // ─── NAVIGASI ─────────────────────────────────────────────────────────────

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}