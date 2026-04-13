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
import com.example.cbt.adapter.UpcomingExamAdapter
import com.example.cbt.api.RetrofitClient
import com.example.cbt.api.TokenManager
import com.example.cbt.model.ExamResultResponse
import com.example.cbt.repository.ExamRepository
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository
    private lateinit var tvWelcome: TextView
    private lateinit var btnMulaiUjian: Button
    private lateinit var recyclerExams: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var navHome: ImageView
    private lateinit var navHistory: ImageView
    private lateinit var navProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        TokenManager.init(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_dashboard)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize repository
        repository = ExamRepository(RetrofitClient.instance, this)

        // Check authentication
        if (!repository.isLoggedIn()) {
            navigateToLogin()
            return
        }

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome)
        btnMulaiUjian = findViewById(R.id.btnMulaiUjian)
        recyclerExams = findViewById(R.id.recyclerExams)
        progressBar = findViewById(R.id.progressBar)
        navHome = findViewById(R.id.navHome)
        navHistory = findViewById(R.id.navHistory)
        navProfile = findViewById(R.id.navProfile)

        // Set welcome message
        val studentName = repository.getStudentName()
        tvWelcome.text = "Selamat datang, $studentName!"

        // Setup recycler view
        recyclerExams.layoutManager = LinearLayoutManager(this)

        // Load upcoming exams
        loadUpcomingExams()

        // Setup click listeners
        btnMulaiUjian.setOnClickListener {
            val intent = Intent(this, DetailUjianActivity::class.java)
            startActivity(intent)
        }

        navHome.setOnClickListener {
            Toast.makeText(this, "Anda sudah berada di Beranda", Toast.LENGTH_SHORT).show()
        }

        navHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUpcomingExams() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = repository.getExamHistory()

                result.onSuccess { examHistory ->
                    progressBar.visibility = View.GONE
                    val exams = examHistory.data

                    if (exams.isNotEmpty()) {
                        // Filter upcoming exams (bisa disesuaikan dengan data dari server)
                        val upcomingExams = exams.take(3) // Ambil 3 ujian terbaru/mendatang

                        val adapter = UpcomingExamAdapter(upcomingExams) { exam ->
                            // Navigate to detail when clicked
                            val intent = Intent(this@DashboardActivity, HistoryDetailActivity::class.java)
                            intent.putExtra("exam_id", exam.id)
                            intent.putExtra("exam_title", exam.examTitle)
                            intent.putExtra("score", "${exam.scorePercentage.toInt()}%")
                            intent.putExtra("date", exam.tanggalUjian)
                            intent.putExtra("duration", "${exam.waktuTempuhDetik / 60} Menit")
                            intent.putExtra("isPassed", exam.status == "PASSED")
                            startActivity(intent)
                        }

                        recyclerExams.adapter = adapter
                    } else {
                        // Show empty state
                        Toast.makeText(this@DashboardActivity, "Tidak ada ujian", Toast.LENGTH_SHORT).show()
                    }
                }

                result.onFailure { error ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@DashboardActivity,
                        "Gagal memuat ujian: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}