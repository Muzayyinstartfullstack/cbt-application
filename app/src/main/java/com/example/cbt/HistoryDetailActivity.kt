package com.example.cbt

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import android.view.View
import com.example.cbt.api.RetrofitClient
import com.example.cbt.repository.ExamRepository
import kotlinx.coroutines.launch

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_history_detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize repository
        repository = ExamRepository(RetrofitClient.instance, this)

        progressBar = findViewById(R.id.progressBar)

        // Get data from intent
        val examId = intent.getStringExtra("exam_id") ?: ""
        val subject = intent.getStringExtra("subject") ?: "Unknown"
        val score = intent.getStringExtra("score") ?: "0%"
        val date = intent.getStringExtra("date") ?: "-"
        val duration = intent.getStringExtra("duration") ?: "-"
        val isPassed = intent.getBooleanExtra("isPassed", false)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // If we have exam_id, fetch from API, otherwise use intent data
        if (examId.isNotEmpty()) {
            loadExamResultFromApi(examId)
        } else {
            displayExamResult(subject, score, date, duration, isPassed)
        }
    }

    private fun loadExamResultFromApi(examId: String) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Fetch riwayat ujian dan cari yang sesuai dengan ID
                val result = repository.getExamHistory()

                result.onSuccess { examHistory ->
                    progressBar.visibility = View.GONE

                    val exam = examHistory.find { it.id == examId }
                    if (exam != null) {
                        displayExamResult(
                            subject = exam.examTitle,
                            score = "${exam.scorePercentage.toInt()}%",
                            date = exam.tanggalUjian,
                            duration = "${exam.waktuTempuhDetik / 60} Menit",
                            isPassed = exam.status == "PASSED"
                        )
                    } else {
                        Toast.makeText(this@HistoryDetailActivity, "Data ujian tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                result.onFailure { error ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@HistoryDetailActivity,
                        "Gagal memuat detail: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@HistoryDetailActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun displayExamResult(
        subject: String,
        score: String,
        date: String,
        duration: String,
        isPassed: Boolean
    ) {
        // Set data ke UI
        findViewById<TextView>(R.id.tvSubject).text = subject

        // Parse score value
        val scoreValue = score.replace("%", "").toIntOrNull() ?: 0

        // Set progress bar dan score
        findViewById<android.widget.ProgressBar>(R.id.circularProgress).progress = scoreValue
        findViewById<TextView>(R.id.tvPercent).text = scoreValue.toString()

        // Set waktu
        val durationValue = duration.replace(" Menit", "").replace("Menit", "").trim()
        findViewById<TextView>(R.id.tvWaktu).text = "${durationValue}m"

        // Hitung jumlah soal benar dan salah (asumsi total 45 soal)
        val totalSoal = 45
        val jumlahBenar = (scoreValue * totalSoal / 100).toInt()
        val jumlahSalah = totalSoal - jumlahBenar

        // Set statistik jawaban
        findViewById<TextView>(R.id.tvBenar).text = "$jumlahBenar/$totalSoal"
        findViewById<TextView>(R.id.tvSalah).text = jumlahSalah.toString()

        // Set peringkat berdasarkan score
        val peringkat = calculateRank(scoreValue)
        findViewById<TextView>(R.id.tvPeringkat).text = peringkat

        // Set status passed/failed
        val statusColor = if (isPassed) android.graphics.Color.GREEN else android.graphics.Color.RED
        val statusText = if (isPassed) "LULUS" else "TIDAK LULUS"
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        tvStatus.text = statusText
        tvStatus.setTextColor(statusColor)
    }

    /**
     * Menghitung peringkat berdasarkan score
     */
    private fun calculateRank(score: Int): String {
        return when {
            score >= 90 -> "#1 / 36"
            score >= 80 -> "#5 / 36"
            score >= 70 -> "#13 / 36"
            score >= 60 -> "#20 / 36"
            else -> "#30 / 36"
        }
    }
}