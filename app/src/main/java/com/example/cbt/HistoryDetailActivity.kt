package com.example.cbt

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ProgressBar

class HistoryDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_history_detail)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil data dari intent
        val subject = intent.getStringExtra("subject") ?: "Unknown"
        val score = intent.getStringExtra("score") ?: "0%"
        val date = intent.getStringExtra("date") ?: "-"
        val duration = intent.getStringExtra("duration") ?: "-"
        val isPassed = intent.getBooleanExtra("isPassed", false)

        // Set data ke UI
        findViewById<TextView>(R.id.tvSubject).text = subject

        // Parse score value
        val scoreValue = score.replace("%", "").toIntOrNull() ?: 0

        // Set progress bar dan score
        findViewById<ProgressBar>(R.id.circularProgress).progress = scoreValue
        findViewById<TextView>(R.id.tvPercent).text = scoreValue.toString()

        // Set waktu (extract angka dari duration)
        val durationValue = duration.replace(" Menit", "").replace("Menit", "").trim()
        findViewById<TextView>(R.id.tvWaktu).text = "${durationValue}m"

        // Hitung jumlah soal benar dan salah (asumsi total 45 soal)
        val totalSoal = 45
        val jumlahBenar = (scoreValue * totalSoal / 100).toInt()
        val jumlahSalah = totalSoal - jumlahBenar

        // Set statistik jawaban
        findViewById<TextView>(R.id.tvBenar).text = "$jumlahBenar/$totalSoal"
        findViewById<TextView>(R.id.tvSalah).text = jumlahSalah.toString()

        // Set peringkat (bisa disesuaikan dengan data dari backend)
        val peringkat = calculateRank(scoreValue)
        findViewById<TextView>(R.id.tvPeringkat).text = peringkat

        // Back button
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    /**
     * Menghitung peringkat berdasarkan score
     * Bisa diubah sesuai dengan logika bisnis yang sebenarnya
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
