package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.example.cbt.api.RetrofitClient
import com.example.cbt.repository.ExamRepository
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var historyAdapter: ExamHistoryAdapter

    private var isUpdatingChip = false
    private var allExamHistory = listOf<com.example.cbt.model.ExamResultResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize repository
        repository = ExamRepository(RetrofitClient.instance, this)

        // Initialize views
        recyclerHistory = findViewById(R.id.recyclerHistory)
        progressBar = findViewById(R.id.progressBar)

        // Setup recycler view
        recyclerHistory.layoutManager = LinearLayoutManager(this)

        // Setup click listeners
        setupClickListeners()

        // Load exam history
        loadExamHistory()
    }

    private fun setupClickListeners() {
        // Card listeners - sudah tidak digunakan, diganti dengan adapter

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

    private fun loadExamHistory() {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = repository.getExamHistory()

                result.onSuccess { examHistory ->
                    progressBar.visibility = View.GONE
                    val results = examHistory.data
                    allExamHistory = results

                    // Update stats dynamically
                    updateStats(results)

                    if (results.isNotEmpty()) {
                        historyAdapter = ExamHistoryAdapter { exam ->
                            val intent = Intent(this@HistoryActivity, HistoryDetailActivity::class.java)
                            intent.putExtra("exam_id", exam.id)
                            intent.putExtra("subject", exam.examTitle)
                            intent.putExtra("score", "${exam.scorePercentage.toInt()}%")
                            intent.putExtra("date", exam.tanggalUjian)
                            intent.putExtra("duration", "${exam.waktuTempuhDetik / 60} Menit")
                            intent.putExtra("isPassed", exam.status.equals("PASSED", ignoreCase = true) || exam.scorePercentage >= 55)
                            startActivity(intent)
                        }

                        historyAdapter.submitList(results)
                        recyclerHistory.adapter = historyAdapter

                    } else {
                        Toast.makeText(this@HistoryActivity, "Tidak ada riwayat ujian", Toast.LENGTH_SHORT).show()
                    }
                }

                result.onFailure { error ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@HistoryActivity,
                        "Gagal memuat riwayat: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@HistoryActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateStats(results: List<com.example.cbt.model.ExamResultResponse>) {
        val avgScore = if (results.isNotEmpty()) {
            results.map { it.scorePercentage }.average()
        } else 0.0
        val completedCount = results.size

        findViewById<TextView>(R.id.tvRataNilai)?.text = "${avgScore.toInt()}%"
        findViewById<TextView>(R.id.tvJumlahSelesai)?.text = completedCount.toString()
    }

    private fun filterExamResults(selectedSubject: String) {
        if (selectedSubject == "Semua") {
            historyAdapter.submitList(allExamHistory)
        } else {
            val filtered = allExamHistory.filter { exam ->
                exam.examTitle.contains(selectedSubject, ignoreCase = true)
            }
            historyAdapter.submitList(filtered)
        }
    }
}