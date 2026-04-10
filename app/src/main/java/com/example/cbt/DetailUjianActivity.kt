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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_ujian)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_detail_ujian)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize repository
        repository = ExamRepository(RetrofitClient.instance, this)

        // Initialize views
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

        // Setup auto-move focus for token input
        setupTokenAutoMove(et1, et2, et3, et4)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener { finish() }

        btnMulai.setOnClickListener {
            verifyExamToken()
        }
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

    private fun verifyExamToken() {
        val inputToken = "${et1.text}${et2.text}${et3.text}${et4.text}"

        if (inputToken.length < 4) {
            Toast.makeText(this, "Masukkan kode ujian lengkap!", Toast.LENGTH_SHORT).show()
            return
        }

        // Disable button and show loading
        btnMulai.isEnabled = false
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = repository.checkExamToken(inputToken)

                result.onSuccess { exam ->
                    progressBar.visibility = View.GONE

                    // Navigate to soal ujian
                    val intent = Intent(this@DetailUjianActivity, SoalUjianActivity::class.java)
                    intent.putExtra("EXAM_ID", exam.id)
                    intent.putExtra("EXAM_TITLE", exam.judul)
                    intent.putExtra("EXAM_DURATION", exam.durasiMenit)
                    intent.putExtra("TOTAL_QUESTIONS", exam.totalSoal)
                    intent.putExtra("PASSING_GRADE", exam.passingGrade)
                    startActivity(intent)
                    finish()
                }

                result.onFailure { error ->
                    btnMulai.isEnabled = true
                    progressBar.visibility = View.GONE

                    Toast.makeText(
                        this@DetailUjianActivity,
                        "Verifikasi gagal: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    clearTokenFields(et1, et2, et3, et4)
                }
            } catch (e: Exception) {
                btnMulai.isEnabled = true
                progressBar.visibility = View.GONE

                Toast.makeText(
                    this@DetailUjianActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun clearTokenFields(vararg editTexts: EditText) {
        for (et in editTexts) {
            et.text.clear()
        }
        editTexts[0].requestFocus()
    }
}