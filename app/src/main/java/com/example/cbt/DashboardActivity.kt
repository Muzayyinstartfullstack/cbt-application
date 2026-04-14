package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.example.cbt.adapter.AvailableExamAdapter
import com.example.cbt.adapter.OngoingExamAdapter
import com.example.cbt.api.RetrofitClient
import com.example.cbt.api.TokenManager
import com.example.cbt.model.AttemptResponse
import com.example.cbt.model.ExamResponse
import com.example.cbt.repository.ExamRepository
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository
    private lateinit var tvWelcome: TextView
    private lateinit var btnMulaiUjian: Button
    private lateinit var recyclerExams: RecyclerView
    private lateinit var recyclerOngoing: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var navHome: ImageView
    private lateinit var navHistory: ImageView
    private lateinit var navProfile: ImageView
    private lateinit var sectionOngoing: LinearLayout
    private lateinit var tvNoExams: TextView

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

        // Ongoing exams section
        recyclerOngoing = findViewById(R.id.recyclerOngoing)
        sectionOngoing = findViewById(R.id.sectionOngoing)
        tvNoExams = findViewById(R.id.tvNoExams)

        // Set welcome message
        val studentName = repository.getStudentName()
        tvWelcome.text = "Selamat datang, $studentName!"

        // Setup recycler views
        recyclerExams.layoutManager = LinearLayoutManager(this)
        recyclerOngoing.layoutManager = LinearLayoutManager(this)

        // Load data
        loadAvailableExams()
        loadOngoingExams()

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

    private fun loadAvailableExams() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = repository.getAvailableExams()

                result.onSuccess { exams ->
                    progressBar.visibility = View.GONE

                    if (exams.isNotEmpty()) {
                        tvNoExams.visibility = View.GONE
                        val adapter = AvailableExamAdapter(exams) { exam ->
                            val intent = Intent(this@DashboardActivity, DetailUjianActivity::class.java)
                            startActivity(intent)
                        }
                        recyclerExams.adapter = adapter
                    } else {
                        tvNoExams.visibility = View.VISIBLE
                    }
                }

                result.onFailure { error ->
                    progressBar.visibility = View.GONE
                    tvNoExams.visibility = View.VISIBLE
                    // Jangan tampilkan toast error untuk upcoming exams, biarkan silent fail
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                tvNoExams.visibility = View.VISIBLE
            }
        }
    }

    private fun loadOngoingExams() {
        lifecycleScope.launch {
            try {
                val result = repository.getOngoingAttempts()

                result.onSuccess { ongoingAttempts ->
                    if (ongoingAttempts.isNotEmpty()) {
                        sectionOngoing.visibility = View.VISIBLE
                        val adapter = OngoingExamAdapter(ongoingAttempts) { attempt ->
                            // Resume ongoing exam
                            val intent = Intent(this@DashboardActivity, SoalUjianActivity::class.java).apply {
                                putExtra("EXAM_ID", attempt.idUjian)
                                putExtra("ATTEMPT_ID", attempt.id)
                            }
                            startActivity(intent)
                        }
                        recyclerOngoing.adapter = adapter
                    } else {
                        sectionOngoing.visibility = View.GONE
                    }
                }

                result.onFailure {
                    sectionOngoing.visibility = View.GONE
                }
            } catch (e: Exception) {
                sectionOngoing.visibility = View.GONE
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}