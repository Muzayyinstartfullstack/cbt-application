package com.example.cbt

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.chip.Chip

class HistoryActivity : AppCompatActivity() {
    private var isUpdatingChip = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Card listeners - Navigasi ke detail page
        findViewById<CardView>(R.id.cardMath).setOnClickListener {
            navigateToDetail("Matematika", "86%", "15 Jan, 09:16", "90 Menit", true)
        }

        findViewById<CardView>(R.id.cardHistory).setOnClickListener {
            navigateToDetail("Sejarah", "75%", "16 Jan, 10:00", "60 Menit", true)
        }

        findViewById<CardView>(R.id.cardEnglish).setOnClickListener {
            navigateToDetail("Bahasa Inggris", "43%", "17 Jan, 11:30", "60 Menit", false)
        }

        findViewById<CardView>(R.id.cardIndo).setOnClickListener {
            navigateToDetail("Bahasa Indonesia", "92%", "18 Jan, 08:00", "90 Menit", true)
        }

        // Bottom Navigation
        findViewById<ImageView>(R.id.navHome).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<ImageView>(R.id.navHistory).setOnClickListener {
            Toast.makeText(this, "Anda sudah berada di History", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.navProfile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Filter button
        findViewById<ImageView>(R.id.btnFilter).setOnClickListener {
            val intent = Intent(this, HistoryFilterActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Chip listeners - untuk filter by subject
        setupChipListeners()
    }

    private fun setupChipListeners() {
        val chips = mapOf(
            R.id.chipSemua to "Semua",
            R.id.chipMatematika to "Matematika",
            R.id.chipSejarah to "Sejarah",
            R.id.chipBahasaInggris to "Bahasa Inggris",
            R.id.chipBahasaIndonesia to "Bahasa Indonesia",
            R.id.chipAgama to "Agama",
            R.id.chipMPP to "MPP",
            R.id.chipKK to "KK",
            R.id.chipOlahRaga to "Olah Raga",
            R.id.chipPKK to "PKK",
            R.id.chipPKN to "PKN"
        )

        chips.forEach { (chipId, subjectName) ->
            findViewById<Chip>(chipId).setOnCheckedChangeListener { _, isChecked ->
                if (isUpdatingChip) return@setOnCheckedChangeListener

                isUpdatingChip = true

                if (isChecked) {
                    // Uncheck semua chip lain
                    chips.keys.forEach { otherId ->
                        if (otherId != chipId) {
                            findViewById<Chip>(otherId).isChecked = false
                        }
                    }
                    // Filter berdasarkan subject yang dipilih
                    filterExamResults(subjectName)
                } else {
                    // Jika di-uncheck, set "Semua" sebagai default
                    if (subjectName != "Semua") {
                        findViewById<Chip>(R.id.chipSemua).isChecked = true
                    }
                }

                isUpdatingChip = false
            }
        }
    }

    private fun filterExamResults(selectedSubject: String) {
        // TODO: Implementasi filter data
        // Contoh: Tampilkan hanya hasil ujian untuk subject tertentu
        // Jika "Semua" dipilih, tampilkan semua hasil ujian
        when (selectedSubject) {
            "Semua" -> {
                // Tampilkan semua kartu
            }
            "Matematika" -> {
                // Tampilkan hanya hasil Matematika
            }
            // ... dan seterusnya untuk subject lainnya
        }
    }

    private fun navigateToDetail(
        subject: String,
        score: String,
        date: String,
        duration: String,
        isPassed: Boolean
    ) {
        val intent = Intent(this, HistoryDetailActivity::class.java)
        intent.putExtra("subject", subject)
        intent.putExtra("score", score)
        intent.putExtra("date", date)
        intent.putExtra("duration", duration)
        intent.putExtra("isPassed", isPassed)
        startActivity(intent)
    }

}