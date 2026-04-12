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
import com.example.cbt.api.RetrofitClient
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

        // 1. Handling System Bars (Edge-to-Edge)
        val rootView = findViewById<View>(R.id.activity_detail_ujian)
        rootView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // 2. Initialize Repository & Views
        repository = ExamRepository(RetrofitClient.instance, this)
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

        // Setup auto-focus movement
        setupTokenAutoMove(et1, et2, et3, et4)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnMulai.setOnClickListener {
            verifyExamToken()
        }
    }

    private fun verifyExamToken() {
        val inputToken = "${et1.text}${et2.text}${et3.text}${et4.text}".trim()

        if (inputToken.length < 4) {
            Toast.makeText(this, "Masukkan kode ujian lengkap!", Toast.LENGTH_SHORT).show()
            return
        }

        // UI State: Loading
        btnMulai.isEnabled = false
        btnMulai.text = "Memverifikasi..."
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Menggunakan Repository Pattern agar kode clean
                val result = repository.checkExamToken(inputToken)

                result.onSuccess { exam ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@DetailUjianActivity, "Berhasil: ${exam.judul}", Toast.LENGTH_SHORT).show()

                    // Pindah ke SoalUjianActivity dengan data lengkap
                    val intent = Intent(this@DetailUjianActivity, SoalUjianActivity::class.java).apply {
                        putExtra("EXAM_ID", exam.id)
                        putExtra("EXAM_TITLE", exam.judul)
                        putExtra("EXAM_DURATION", exam.durasi)
                        putExtra("TOTAL_QUESTIONS", exam.totalSoal)
                    }
                    startActivity(intent)
                    finish()
                }

                result.onFailure { error ->
                    resetUIState()
                    Toast.makeText(this@DetailUjianActivity, "Kode Ujian Salah!", Toast.LENGTH_SHORT).show()
                    clearTokenFields(et1, et2, et3, et4)
                }
            } catch (e: Exception) {
                resetUIState()
                Toast.makeText(this@DetailUjianActivity, "Server Error: ${e.message}", Toast.LENGTH_LONG).show()
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
                    // Jika user input 1 karakter, pindah ke kotak selanjutnya
                    if (s?.length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                    // Opsi tambahan: Jika user hapus, balik ke kotak sebelumnya
                    if (s?.length == 0 && i > 0) {
                        editTexts[i - 1].requestFocus()
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