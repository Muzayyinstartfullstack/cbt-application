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

        // Initialize repository (Constructor kosong sesuai file ExamRepository.kt kamu)
        repository = ExamRepository()

        progressBar = findViewById(R.id.progressBar)

        // Get data from intent
        val sessionId = intent.getStringExtra("exam_id") ?: "" // ID Session dari riwayat
        val subject = intent.getStringExtra("subject") ?: "Unknown"
        val scoreStr = intent.getStringExtra("score") ?: "0%"
        val date = intent.getStringExtra("date") ?: "-"
        val isPassed = intent.getBooleanExtra("isPassed", false)

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Jika kita punya sessionId, fetch detail hasil ujian dari repository
        if (sessionId.isNotEmpty()) {
            loadExamResultFromApi(sessionId)
        } else {
            // Jika tidak ada ID, tampilkan data mentah dari intent (dengan durasi default)
            displayExamResult(subject, scoreStr, date, "0", isPassed)
        }
    }

    private fun loadExamResultFromApi(sessionId: String) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // PERBAIKAN: Gunakan getExamResult(sessionId) sesuai fungsi di repository kamu
                val result = repository.getExamResult(sessionId)

                result.fold(
                    onSuccess = { examResult ->
                        progressBar.visibility = View.GONE

                        // PERBAIKAN: Ambil judul dari relasi examSession -> exams
                        val title = examResult.examSession?.exams?.title ?: "Ujian"
                        val scoreValue = examResult.score
                        val totalSoal = examResult.totalQuestions
                        val benar = examResult.correctAnswers
                        val tanggal = examResult.createdAt

                        displayExamResult(
                            subject = title,
                            score = "${scoreValue.toInt()}%",
                            date = tanggal,
                            duration = "0", // Kamu bisa hitung selisih start_time & end_time jika perlu
                            isPassed = scoreValue >= 70.0, // Contoh threshold lulus
                            totalQuestions = totalSoal,
                            correctCount = benar
                        )
                    },
                    onFailure = { error ->
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@HistoryDetailActivity, "Gagal: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@HistoryDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayExamResult(
        subject: String,
        score: String,
        date: String,
        duration: String,
        isPassed: Boolean,
        totalQuestions: Int = 45, // Default jika tidak dari API
        correctCount: Int = 0     // Default jika tidak dari API
    ) {
        // Set Subject
        findViewById<TextView>(R.id.tvSubject).text = subject

        // Parse Score
        val scoreValue = score.replace("%", "").toDoubleOrNull()?.toInt() ?: 0

        // Circular Progress & Score Text
        findViewById<ProgressBar>(R.id.circularProgress).progress = scoreValue
        findViewById<TextView>(R.id.tvPercent).text = scoreValue.toString()

        // Durasi (Hanya angka menit)
        findViewById<TextView>(R.id.tvWaktu).text = "${duration}m"

        // Statistik Jawaban (Benar/Salah)
        // Jika correctCount 0 (dari intent), kita hitung manual. Jika dari API, pakai correctCount.
        val finalCorrect = if (correctCount > 0) correctCount else (scoreValue * totalQuestions / 100)
        val finalWrong = totalQuestions - finalCorrect

        findViewById<TextView>(R.id.tvBenar).text = "$finalCorrect/$totalQuestions"
        findViewById<TextView>(R.id.tvSalah).text = finalWrong.toString()

        // Peringkat (Statik sesuai logic kamu)
        findViewById<TextView>(R.id.tvPeringkat).text = calculateRank(scoreValue)

        // Status Lulus
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        if (isPassed) {
            tvStatus.text = "LULUS"
            tvStatus.setTextColor(android.graphics.Color.GREEN)
        } else {
            tvStatus.text = "TIDAK LULUS"
            tvStatus.setTextColor(android.graphics.Color.RED)
        }
    }

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