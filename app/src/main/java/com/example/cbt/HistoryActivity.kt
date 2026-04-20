package com.example.cbt



import android.content.Intent

import android.os.Bundle

import android.view.View

import android.widget.ImageView

import android.widget.ProgressBar

import android.widget.Toast

import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat

import androidx.core.view.WindowInsetsCompat

import androidx.lifecycle.lifecycleScope

import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView

import com.example.cbt.adapter.ExamHistoryAdapter

import com.example.cbt.data.model.ExamSessionWithStatus

import com.example.cbt.data.repository.ExamRepository

import com.google.android.material.chip.Chip

import kotlinx.coroutines.launch



class HistoryActivity : AppCompatActivity() {



    private lateinit var repository: ExamRepository

    private lateinit var recyclerHistory: RecyclerView

    private lateinit var progressBar: ProgressBar

    private lateinit var historyAdapter: ExamHistoryAdapter



    private var allExamHistory = listOf<ExamSessionWithStatus>()

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



        // Inisialisasi repository dengan context

        repository = ExamRepository(applicationContext)



        // Cek login

        if (!repository.isLoggedIn()) {

            navigateToLogin()

            return

        }



        initViews()

        setupClickListeners()

        loadExamHistory()

    }



    private fun initViews() {

        recyclerHistory = findViewById(R.id.recyclerHistory)

        progressBar = findViewById(R.id.progressBar)

        recyclerHistory.layoutManager = LinearLayoutManager(this)



        // Inisialisasi adapter (kosong dulu, nanti diisi setelah data load)

        historyAdapter = ExamHistoryAdapter { session ->

            val intent = Intent(this@HistoryActivity, HistoryDetailActivity::class.java).apply {

                putExtra("session_id", session.id)

                putExtra("exam_id", session.examId)

                putExtra("score", session.score ?: 0)

                putExtra("completed_at", session.completedAt ?: "")

                putExtra("status", session.status)

            }

            startActivity(intent)

        }

        recyclerHistory.adapter = historyAdapter

    }



    private fun setupClickListeners() {

        findViewById<ImageView>(R.id.navHome).setOnClickListener {

            startActivity(Intent(this, DashboardActivity::class.java))

            finish()

        }



        findViewById<ImageView>(R.id.navProfile).setOnClickListener {

            startActivity(Intent(this, ProfileActivity::class.java))

            finish()

        }



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

                    chips.keys.forEach { otherId ->

                        if (otherId != chipId) findViewById<Chip>(otherId).isChecked = false

                    }

                    filterExamResults(subjectName)

                } else if (subjectName != "Semua") {

                    findViewById<Chip>(R.id.chipSemua).isChecked = true

                }

                isUpdatingChip = false

            }

        }

    }



    private fun loadExamHistory() {

        progressBar.visibility = View.VISIBLE



        lifecycleScope.launch {

            val userId = repository.getCurrentUserId()

            if (userId == null) {

                navigateToLogin()

                return@launch

            }



            val result = repository.getExamHistory(userId)

            result.fold(

                onSuccess = { historyList ->

                    allExamHistory = historyList

                    progressBar.visibility = View.GONE



                    if (allExamHistory.isNotEmpty()) {

                        historyAdapter.submitList(allExamHistory)

                    } else {

                        Toast.makeText(this@HistoryActivity, "Belum ada riwayat ujian", Toast.LENGTH_SHORT).show()

                    }

                },

                onFailure = { error ->

                    progressBar.visibility = View.GONE

                    Toast.makeText(this@HistoryActivity, "Gagal memuat riwayat: ${error.message}", Toast.LENGTH_LONG).show()

                }

            )

        }

    }



    private fun filterExamResults(selectedSubject: String) {

        if (!::historyAdapter.isInitialized) return

        val filtered = if (selectedSubject == "Semua") {
            allExamHistory
        } else {
            // Filter berdasarkan title exam (karena subjectName tidak ada di model)
            allExamHistory.filter { session ->
                session.exam?.title?.contains(selectedSubject, ignoreCase = true) == true
            }
        }

        historyAdapter.submitList(filtered)

    }



    private fun navigateToLogin() {

        startActivity(Intent(this, LoginActivity::class.java))

        finish()

    }

}