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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun main() {
    val today = LocalDate.now()

    val hari = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID")) // Senin, Selasa, dll.
    val tanggal = today.dayOfMonth
    val bulanNama = today.month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
}

class DashboardActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository

    // Views
    private lateinit var tvWelcome: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvNis: TextView
    private lateinit var tvClass: TextView
    private lateinit var recyclerAvailableExams: RecyclerView
    private lateinit var recyclerExams: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var navHome: ImageView
    private lateinit var navHistory: ImageView
    private lateinit var navProfile: ImageView
    private lateinit var tvDate: TextView

    // Data user yang sedang login
    private var currentProfile: Profile? = null

    // Adapters
    private lateinit var availableAdapter: com.example.cbt.adapter.AvailableExamAdapter
    private lateinit var upcomingAdapter: com.example.cbt.adapter.AvailableExamAdapter
    private lateinit var historyAdapter: ExamHistoryAdapter

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
        tvWelcome              = findViewById(R.id.tvWelcome)
        tvUserName             = findViewById(R.id.UserName)
        tvNis                  = findViewById(R.id.tvNis)
        tvClass                = findViewById(R.id.tvClass)
        recyclerAvailableExams = findViewById(R.id.recyclerAvailableExams)
        recyclerExams          = findViewById(R.id.recyclerExams)
        progressBar            = findViewById(R.id.progressBar)
        navHome                = findViewById(R.id.navHome)
        navHistory             = findViewById(R.id.navHistory)
        navProfile             = findViewById(R.id.navProfile)
        tvDate                 = findViewById(R.id.tvDate)

        // Init Adapters immediately to avoid "No adapter attached"
        availableAdapter = com.example.cbt.adapter.AvailableExamAdapter { exam ->
            val intent = Intent(this@DashboardActivity, DetailUjianActivity::class.java).apply {
                putExtra("EXAM_ID", exam.id)
                putExtra("EXAM_TITLE", exam.title)
            }
            startActivity(intent)
        }
        recyclerAvailableExams.adapter = availableAdapter
        recyclerAvailableExams.layoutManager = LinearLayoutManager(this)

        upcomingAdapter = com.example.cbt.adapter.AvailableExamAdapter { exam ->
            val intent = Intent(this@DashboardActivity, DetailUjianActivity::class.java).apply {
                putExtra("EXAM_ID", exam.id)
                putExtra("EXAM_TITLE", exam.title)
            }
            startActivity(intent)
        }
        recyclerExams.adapter = upcomingAdapter
        recyclerExams.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {

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

                        // Format tanggal dalam bahasa Indonesia
                        val today = LocalDate.now()
                        val hari = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
                        val tanggal = today.dayOfMonth
                        val bulanNama = today.month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
                        val formattedDate = "$hari, $tanggal $bulanNama"

                        // Tampilkan nama dari kolom full_name
                        tvWelcome.text = "Selamat Datang,\n${profile.fullName}"
                        tvUserName.text = profile.fullName
                        tvNis.text = "NIS/NIP: ${profile.nisnip}"
                        tvClass.text = profile.className
                        tvDate.text = formattedDate

                        // Load data ujian tersedia (Active/Running exams)
                        loadAvailableExams(profile.id.toString())

                        // Load data ujian yang segera dimulai (Upcoming)
                        loadUpcomingExams()

                        progressBar.visibility = View.GONE
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

    private fun loadAvailableExams(profileId: String) {
        lifecycleScope.launch {
            try {
                val result = repository.getAvailableExams(profileId)
                result.fold(
                    onSuccess = { exams ->
                        availableAdapter.submitList(exams)
                    },
                    onFailure = { error ->
                        // Jika permission denied, jangan tampilkan Toast mengganggu tapi tampilkan list kosong
                        availableAdapter.submitList(emptyList())
                        android.util.Log.e("Dashboard", "Gagal load exams: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this@DashboardActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadUpcomingExams() {
        lifecycleScope.launch {
            try {
                val result = repository.getUpcomingExams()
                result.fold(
                    onSuccess = { exams ->
                        upcomingAdapter.submitList(exams)
                    },
                    onFailure = { error ->
                        android.util.Log.e("Dashboard", "Gagal load upcoming: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun loadExams(profileId: String) {
        // Method ini mungkin tidak dipakai lagi jika kita fokus ke Upcoming
        // Tapi kita biarkan jika sewaktu-waktu ingin load riwayat di tempat lain
    }

    // ─── RECYCLER VIEW ────────────────────────────────────────────────────────

    private fun setupRecyclerView(sessions: List<ExamSession>) {
        // Jika masih butuh historyAdapter, ditaruh di tempat lain.
        // historyAdapter.submitList(sessions)
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