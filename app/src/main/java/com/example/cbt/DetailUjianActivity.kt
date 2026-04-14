package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cbt.repository.ExamRepository
import kotlinx.coroutines.launch

class DetailUjianActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository
    private lateinit var btnBack: ImageButton
    private lateinit var btnMulai: Button
    private lateinit var et1: EditText
    private lateinit var et2: EditText
    private lateinit var et3: EditText
    private lateinit var et4: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMapel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_ujian)

        val rootView = findViewById<View>(R.id.activity_detail_ujian)
        rootView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // PERBAIKAN: Gunakan constructor kosong sesuai error log
        repository = ExamRepository()

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        btnBack = findViewById(R.id.btn_back)
        btnMulai = findViewById(R.id.btn_submit_exam)
        et1 = findViewById(R.id.et_code_1)
        et2 = findViewById(R.id.et_code_2)
        et3 = findViewById(R.id.et_code_3)
        et4 = findViewById(R.id.et_code_4)
        progressBar = findViewById(R.id.progressBar)
        tvMapel = findViewById(R.id.tv_mapel_detail)

        setupTokenAutoMove(et1, et2, et3, et4)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }
        btnMulai.setOnClickListener { verifyExamToken() }
    }

    private fun verifyExamToken() {
        // Di sini kita asumsikan inputToken adalah ID Ujian (Exam ID)
        // karena di repository kamu tidak ada pengecekan string token khusus.
        val inputToken = "${et1.text}${et2.text}${et3.text}${et4.text}".trim()

        if (inputToken.length < 4) {
            Toast.makeText(this, "Masukkan kode ujian lengkap!", Toast.LENGTH_SHORT).show()
            return
        }

        btnMulai.isEnabled = false
        btnMulai.text = "Memverifikasi..."
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // PERBAIKAN 1: Gunakan getExamDetail (karena checkExamToken tidak ada di repository)
                val result = repository.getExamDetail(inputToken)

                result.fold(
                    onSuccess = { exam ->
                        // Ambil ID User yang sedang login
                        val profileId = repository.getCurrentUserId() ?: ""

                        // PERBAIKAN 2: Gunakan getOrCreateSession (pengganti startAttempt)
                        val sessionResult = repository.getOrCreateSession(exam.id, profileId)

                        progressBar.visibility = View.GONE
                        sessionResult.fold(
                            onSuccess = { session ->
                                Toast.makeText(this@DetailUjianActivity, "Berhasil: ${exam.title}", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@DetailUjianActivity, SoalUjianActivity::class.java).apply {
                                    putExtra("EXAM_ID", exam.id)
                                    putExtra("EXAM_TITLE", exam.title)
                                    putExtra("EXAM_DURATION", exam.durationMinutes)
                                    putExtra("SESSION_ID", session.id) // Di repository kamu pakainya session
                                }
                                startActivity(intent)
                                finish()
                            },
                            onFailure = { error ->
                                resetUIState()
                                Toast.makeText(this@DetailUjianActivity, "Gagal sesi: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onFailure = { error ->
                        resetUIState()
                        Toast.makeText(this@DetailUjianActivity, "Ujian tidak ditemukan!", Toast.LENGTH_SHORT).show()
                        clearTokenFields(et1, et2, et3, et4)
                    }
                )
            } catch (e: Exception) {
                resetUIState()
                Toast.makeText(this@DetailUjianActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun resetUIState() {
        btnMulai.isEnabled = true
        btnMulai.text = "Mulai Ujian Sekarang →"
        progressBar.visibility = View.GONE
    }

    private fun setupTokenAutoMove(vararg editTexts: EditText) {
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }
            })
        }
    }

    private fun clearTokenFields(vararg editTexts: EditText) {
        for (et in editTexts) {
            et.text.clear()
        }
        editTexts[0].requestFocus()
    }
}