package com.example.cbt



import android.os.Bundle

import android.view.View

import android.widget.ImageButton

import android.widget.LinearLayout

import android.widget.ProgressBar

import android.widget.TextView

import android.widget.Toast

import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat

import androidx.core.view.WindowInsetsCompat

import androidx.lifecycle.lifecycleScope

import com.example.cbt.data.repository.ExamRepository

import kotlinx.coroutines.launch



class HistoryDetailActivity : AppCompatActivity() {



    private lateinit var repository: ExamRepository

    private lateinit var progressBar: ProgressBar

    private lateinit var tvStatus: TextView

    // topicContainer tidak ada di layout - fitur analisis topik disabled
    // private var topicContainer: LinearLayout? = null



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_history_detail)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_history_detail)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)

            insets

        }



        // Inisialisasi repository dengan context

        repository = ExamRepository(applicationContext)



        // Inisialisasi views

        progressBar = findViewById(R.id.progressBar)

        tvStatus = findViewById(R.id.tvStatus)



        // Tombol kembali

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {

            finish()

        }



        // Ambil data dari intent (bisa dari HistoryActivity)

        val sessionId = intent.getStringExtra("session_id") ?: intent.getStringExtra("exam_id") ?: ""

        val subjectFromIntent = intent.getStringExtra("subject") ?: ""

        val scoreFromIntent = intent.getStringExtra("score") ?: ""

        val dateFromIntent = intent.getStringExtra("date") ?: ""

        val isPassedFromIntent = intent.getBooleanExtra("isPassed", false)



        if (sessionId.isNotEmpty()) {

            loadExamResultFromApi(sessionId)

        } else if (subjectFromIntent.isNotEmpty()) {

            // Fallback ke data dari intent jika tidak ada sessionId

            displayExamResult(

                subject = subjectFromIntent,

                score = scoreFromIntent,

                date = dateFromIntent,

                duration = "0",

                isPassed = isPassedFromIntent,

                totalQuestions = 45, // default, bisa diambil dari intent juga

                correctCount = 0

            )

        } else {

            Toast.makeText(this, "Data tidak lengkap", Toast.LENGTH_SHORT).show()

            finish()

        }

    }



    private fun loadExamResultFromApi(sessionId: String) {

        progressBar.visibility = View.VISIBLE

        tvStatus.visibility = View.GONE



        lifecycleScope.launch {

            val result = repository.getExamResult(sessionId)

            result.fold(

                onSuccess = { examResult ->

                    progressBar.visibility = View.GONE

                    // Gunakan examId sebagai title karena relasi exam tidak tersedia

                    val examIdStr = examResult.examSession?.examId?.toString() ?: "Unknown"
                    val examTitle = "Ujian #${if (examIdStr.length > 4) examIdStr.substring(examIdStr.length - 4) else examIdStr}"

                    val scoreValue = examResult.score // misal 86.0

                    val totalQuestions = examResult.totalQuestions

                    val correctCount = examResult.correctAnswers

                    val wrongCount = totalQuestions - correctCount

                    val durationMinutes = examResult.durationMinutes ?: 0

                    val rank = calculateRank(scoreValue.toInt())

                    val isPassed = scoreValue >= 70.0 // threshold

                    val completedDate = examResult.completedAt ?: examResult.createdAt



                    displayExamResult(

                        subject = examTitle,

                        score = "${scoreValue.toInt()}%",

                        date = completedDate,

                        duration = durationMinutes.toString(),

                        isPassed = isPassed,

                        totalQuestions = totalQuestions,

                        correctCount = correctCount,

                        wrongCount = wrongCount,

                        rank = rank

                    )

                },

                onFailure = { error ->

                    progressBar.visibility = View.GONE

                    Toast.makeText(this@HistoryDetailActivity, "Gagal memuat detail: ${error.message}", Toast.LENGTH_LONG).show()

                    finish()

                }

            )

        }

    }



    private fun displayExamResult(

        subject: String,

        score: String,

        date: String,

        duration: String,

        isPassed: Boolean,

        totalQuestions: Int,

        correctCount: Int,

        wrongCount: Int = totalQuestions - correctCount,

        rank: String = "#13 / 36"

    ) {

        // Header

        findViewById<TextView>(R.id.tvSubject).text = subject



        // Progress circular

        val scoreValue = score.replace("%", "").toDoubleOrNull()?.toInt() ?: 0

        findViewById<ProgressBar>(R.id.circularProgress).progress = scoreValue

        findViewById<TextView>(R.id.tvPercent).text = scoreValue.toString()



        // Waktu (durasi)

        findViewById<TextView>(R.id.tvWaktu).text = "${duration}m"



        // Benar / Salah

        findViewById<TextView>(R.id.tvBenar).text = "$correctCount/$totalQuestions"

        findViewById<TextView>(R.id.tvSalah).text = wrongCount.toString()



        // Peringkat

        findViewById<TextView>(R.id.tvPeringkat).text = rank



        // Status Lulus

        tvStatus.visibility = View.VISIBLE

        tvStatus.text = if (isPassed) "✅ LULUS" else "❌ TIDAK LULUS"

        tvStatus.setTextColor(

            if (isPassed) android.graphics.Color.parseColor("#4CAF50")

            else android.graphics.Color.parseColor("#F44336")

        )

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



    // Data class untuk analisis topik (harus sama dengan yang dikirim dari repository)

    data class TopicAnalysis(

        val topicName: String,

        val percentage: Int

    )

}