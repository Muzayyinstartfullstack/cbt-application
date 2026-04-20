package com.example.cbt

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// --- PERBAIKAN IMPORT (Cek Folder Projectmu) ---
import com.example.cbt.adapter.AvailableExamAdapter
import com.example.cbt.data.repository.ExamRepository
import com.example.cbt.data.model.Profile
import com.example.cbt.data.model.ExamWithDetails

import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class DashboardActivity : AppCompatActivity() {

    // Inisialisasi repository agar tidak Unresolved Reference
    private lateinit var repository: ExamRepository

    private lateinit var tvWelcome: TextView
    private lateinit var tvUserName: TextView
    private lateinit var tvNis: TextView
    private lateinit var tvClass: TextView
    private lateinit var recyclerAvailableExams: RecyclerView
    private lateinit var recyclerUpcomingExams: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var navHome: ImageView
    private lateinit var navHistory: ImageView
    private lateinit var navProfile: ImageView
    private lateinit var tvDate: TextView

    private lateinit var availableAdapter: AvailableExamAdapter
    private lateinit var upcomingAdapter: AvailableExamAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        // Penanganan WindowInsets agar tidak error saat build
        val mainView = findViewById<View>(R.id.main_dashboard)
        mainView?.let { v ->
            ViewCompat.setOnApplyWindowInsetsListener(v) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Inisialisasi Repository
        repository = ExamRepository(this)

        if (!repository.isLoggedIn()) {
            navigateToLogin()
            return
        }

        initViews()
        setupClickListeners()
        loadProfileAndExams()
    }

    private fun initViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        tvUserName = findViewById(R.id.UserName)
        tvNis = findViewById(R.id.tvNis)
        tvClass = findViewById(R.id.tvClass)
        recyclerAvailableExams = findViewById(R.id.recyclerAvailableExams)
        recyclerUpcomingExams = findViewById(R.id.recyclerExams)
        progressBar = findViewById(R.id.progressBar)
        navHome = findViewById(R.id.navHome)
        navHistory = findViewById(R.id.navHistory)
        navProfile = findViewById(R.id.navProfile)
        tvDate = findViewById(R.id.tvDate)

        // Set Adapter
        availableAdapter = AvailableExamAdapter { exam ->
            val intent = Intent(this, DetailUjianActivity::class.java).apply {
                putExtra("EXAM_ID", exam.id)
                putExtra("EXAM_TITLE", exam.title)
            }
            startActivity(intent)
        }
        recyclerAvailableExams.adapter = availableAdapter
        recyclerAvailableExams.layoutManager = LinearLayoutManager(this)

        upcomingAdapter = AvailableExamAdapter { exam ->
            val intent = Intent(this, DetailUjianActivity::class.java).apply {
                putExtra("EXAM_ID", exam.id)
                putExtra("EXAM_TITLE", exam.title)
            }
            startActivity(intent)
        }
        recyclerUpcomingExams.adapter = upcomingAdapter
        recyclerUpcomingExams.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        navHome.setOnClickListener {
            Toast.makeText(this, "Anda sudah di halaman Beranda", Toast.LENGTH_SHORT).show()
        }
        navHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun loadProfileAndExams() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val userId = repository.getCurrentUserId()
                if (userId == null) {
                    navigateToLogin()
                    return@launch
                }

                repository.getProfile(userId).fold(
                    onSuccess = { profile ->
                        bindProfileData(profile)
                        loadActiveExams()
                        loadUpcomingExams()
                        progressBar.visibility = View.GONE
                    },
                    onFailure = { error ->
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@DashboardActivity, "Gagal profil: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@DashboardActivity, "Sesi berakhir", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Pastikan parameternya bersih (tanpa embel-embel data.model)
    private fun bindProfileData(profile: Profile) {
        val today = LocalDate.now()
        val hari = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
        val tanggal = today.dayOfMonth
        val bulanNama = today.month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
        val formattedDate = "$hari, $tanggal $bulanNama"

        // Sekarang ini HARUSNYA tidak eror karena sudah sesuai dengan Profile.kt kamu
        tvWelcome.text = "Selamat Datang,\n${profile.fullName}"
        tvUserName.text = profile.fullName
        tvNis.text = "NIS: ${profile.nis}"
        tvClass.text = "Role: ${profile.role}"
        tvDate.text = formattedDate
    }

    private fun loadActiveExams() {
        lifecycleScope.launch {
            repository.getActiveExams().onSuccess { examList ->
                // Tambahkan .map { it.exam } jika repository-mu membungkus datanya
                // Atau pastikan import model Exam sudah benar
                availableAdapter.submitList(examList)
            }
        }
    }

    private fun loadUpcomingExams() {
        lifecycleScope.launch {
            repository.getUpcomingExams().onSuccess { exams ->
                upcomingAdapter.submitList(exams)
            }.onFailure {
                upcomingAdapter.submitList(emptyList())
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}