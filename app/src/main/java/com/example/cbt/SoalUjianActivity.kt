package com.example.cbt

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.cbt.api.RetrofitClient
import com.example.cbt.model.Question
import com.example.cbt.repository.ExamRepository
import kotlinx.coroutines.launch

class SoalUjianActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository
    private lateinit var timerView: TextView
    private lateinit var questionView: TextView
    private lateinit var progressButton: TextView
    private lateinit var loadingIndicator: ProgressBar

    private var examId: String = ""
    private var examTitle: String = ""
    private var durasiMenit: Int = 60
    private var totalQuestions: Int = 45
    private var passingGrade: Int = 70

    private var selectedOption = -1
    private var currentQuestion = 0
    private var timeRemaining = 0L
    private var countDownTimer: CountDownTimer? = null
    private var isBookmarked = false

    // Data
    private lateinit var questions: List<Question>
    private var answersMap = mutableMapOf<String, String>() // question_id -> jawaban (A, B, C, D, E)
    private var bookmarkedQuestions = mutableSetOf<String>() // question_id

    private lateinit var optionA: LinearLayout
    private lateinit var optionB: LinearLayout
    private lateinit var optionC: LinearLayout
    private lateinit var optionD: LinearLayout
    private lateinit var optionE: LinearLayout

    private lateinit var btnPrevious: AppCompatButton
    private lateinit var btnNext: AppCompatButton
    private lateinit var btnBookmark: AppCompatButton
    private lateinit var btnSizeIncrease: ImageButton
    private lateinit var btnSizeDecrease: ImageButton
    private lateinit var btnNavigasi: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soal_ujian)

        // Initialize repository
        repository = ExamRepository(RetrofitClient.instance, this)

        // Get data dari intent
        examId = intent.getStringExtra("EXAM_ID") ?: ""
        examTitle = intent.getStringExtra("EXAM_TITLE") ?: "Ujian"
        durasiMenit = intent.getIntExtra("EXAM_DURATION", 60)
        totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 45)
        passingGrade = intent.getIntExtra("PASSING_GRADE", 70)

        if (examId.isEmpty()) {
            Toast.makeText(this, "Data ujian tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize views
        initializeViews()
        setupClickListeners()

        // Load questions from API
        loadQuestions()
    }

    private fun initializeViews() {
        timerView = findViewById(R.id.tv_timer)
        questionView = findViewById(R.id.tv_question)
        progressButton = findViewById(R.id.btn_question_progress)
        loadingIndicator = findViewById(R.id.progressBar)

        optionA = findViewById(R.id.option_a)
        optionB = findViewById(R.id.option_b)
        optionC = findViewById(R.id.option_c)
        optionD = findViewById(R.id.option_d)
        optionE = findViewById(R.id.option_e)

        btnPrevious = findViewById(R.id.btn_previous)
        btnNext = findViewById(R.id.btn_next)
        btnBookmark = findViewById(R.id.btn_bookmark)
        btnSizeIncrease = findViewById(R.id.btn_size_increase)
        btnSizeDecrease = findViewById(R.id.btn_size_decrease)
        btnNavigasi = findViewById(R.id.btn_navigasi)
    }

    private fun setupClickListeners() {
        optionA.setOnClickListener { selectOption(0, "A") }
        optionB.setOnClickListener { selectOption(1, "B") }
        optionC.setOnClickListener { selectOption(2, "C") }
        optionD.setOnClickListener { selectOption(3, "D") }
        optionE.setOnClickListener { selectOption(4, "E") }

        btnPrevious.setOnClickListener { goToPreviousQuestion() }
        btnNext.setOnClickListener { goToNextQuestion() }
        btnBookmark.setOnClickListener { toggleBookmark() }

        btnSizeIncrease.setOnClickListener { increaseFontSize() }
        btnSizeDecrease.setOnClickListener { decreaseFontSize() }

        btnNavigasi.setOnClickListener {
            // TODO: Buka navigation bottom sheet
            Toast.makeText(this, "Navigasi soal akan dibuka", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadQuestions() {
        loadingIndicator.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = repository.getQuestions(examId)

                result.onSuccess { questionList ->
                    questions = questionList
                    loadingIndicator.visibility = View.GONE

                    if (questions.isNotEmpty()) {
                        currentQuestion = 0
                        // Set durasi dari server atau default
                        timeRemaining = (durasiMenit * 60).toLong()
                        startTimer()
                        loadQuestion()
                    } else {
                        Toast.makeText(
                            this@SoalUjianActivity,
                            "Tidak ada soal tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                result.onFailure { error ->
                    loadingIndicator.visibility = View.GONE
                    Toast.makeText(
                        this@SoalUjianActivity,
                        "Gagal memuat soal: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                loadingIndicator.visibility = View.GONE
                Toast.makeText(
                    this@SoalUjianActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun selectOption(optionIndex: Int, optionLabel: String) {
        selectedOption = optionIndex
        updateOptionUI()

        // Simpan jawaban
        if (currentQuestion < questions.size) {
            val questionId = questions[currentQuestion].id
            answersMap[questionId] = optionLabel

            // Submit to API
            submitAnswerToApi(questionId, optionLabel)
        }
    }

    private fun submitAnswerToApi(questionId: String, jawaban: String) {
        lifecycleScope.launch {
            try {
                val nomorSoal = questions[currentQuestion].nomor
                val result = repository.submitAnswer(
                    examId = examId,
                    questionId = questionId,
                    nomorSoal = nomorSoal,
                    jawaban = jawaban,
                    isBookmarked = isBookmarked
                )

                result.onSuccess {
                    // Jawaban berhasil disimpan
                    // Optionally show toast atau update UI
                }

                result.onFailure { error ->
                    Toast.makeText(
                        this@SoalUjianActivity,
                        "Gagal menyimpan jawaban: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SoalUjianActivity,
                    "Error menyimpan jawaban: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateOptionUI() {
        val unselected = R.drawable.bg_unselected_option
        optionA.setBackgroundResource(unselected)
        optionB.setBackgroundResource(unselected)
        optionC.setBackgroundResource(unselected)
        optionD.setBackgroundResource(unselected)
        optionE.setBackgroundResource(unselected)

        val selected = R.drawable.bg_selected_option
        when (selectedOption) {
            0 -> optionA.setBackgroundResource(selected)
            1 -> optionB.setBackgroundResource(selected)
            2 -> optionC.setBackgroundResource(selected)
            3 -> optionD.setBackgroundResource(selected)
            4 -> optionE.setBackgroundResource(selected)
        }
    }

    private fun goToNextQuestion() {
        if (currentQuestion < questions.size - 1) {
            currentQuestion++
            loadQuestion()
        } else {
            Toast.makeText(this, "Ini soal terakhir", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToPreviousQuestion() {
        if (currentQuestion > 0) {
            currentQuestion--
            loadQuestion()
        } else {
            Toast.makeText(this, "Ini soal pertama", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadQuestion() {
        if (currentQuestion < questions.size) {
            val question = questions[currentQuestion]

            // Set pertanyaan
            questionView.text = "${question.nomor}. ${question.pertanyaan}"

            // Set opsi (Anda perlu update layout XML untuk menampilkan opsi ini)
            val optionAText = findViewById<TextView>(R.id.option_a_text)
            val optionBText = findViewById<TextView>(R.id.option_b_text)
            val optionCText = findViewById<TextView>(R.id.option_c_text)
            val optionDText = findViewById<TextView>(R.id.option_d_text)
            val optionEText = findViewById<TextView>(R.id.option_e_text)

            optionAText.text = question.opsiA
            optionBText.text = question.opsiB
            optionCText.text = question.opsiC
            optionDText.text = question.opsiD
            optionEText.text = question.opsiE

            // Load jawaban yang sudah ada
            val questionId = question.id
            val previousAnswer = answersMap[questionId]
            selectedOption = when (previousAnswer) {
                "A" -> 0
                "B" -> 1
                "C" -> 2
                "D" -> 3
                "E" -> 4
                else -> -1
            }

            isBookmarked = bookmarkedQuestions.contains(questionId)

            updateOptionUI()
            updateProgressButton()
        }
    }

    private fun toggleBookmark() {
        if (currentQuestion < questions.size) {
            val questionId = questions[currentQuestion].id

            isBookmarked = !isBookmarked

            if (isBookmarked) {
                bookmarkedQuestions.add(questionId)
                btnBookmark.text = "✓ Ragu-ragu"
            } else {
                bookmarkedQuestions.remove(questionId)
                btnBookmark.text = "Ragu-ragu"
            }

            btnBookmark.alpha = if (isBookmarked) 0.8f else 1.0f

            // Submit bookmark status ke API
            if (answersMap.containsKey(questionId)) {
                submitAnswerToApi(questionId, answersMap[questionId]!!)
            }
        }
    }

    private fun updateProgressButton() {
        progressButton.text = "Soal ${currentQuestion + 1} dari $totalQuestions"
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeRemaining * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished / 1000
                updateTimerDisplay()
            }

            override fun onFinish() {
                timerView.text = "00:00:00"
                onTimeUp()
            }
        }.start()
    }

    private fun updateTimerDisplay() {
        val hours = timeRemaining / 3600
        val minutes = (timeRemaining % 3600) / 60
        val seconds = timeRemaining % 60
        timerView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun onTimeUp() {
        countDownTimer?.cancel()
        Toast.makeText(this, "Waktu habis!", Toast.LENGTH_LONG).show()
        submitExamResult()
    }

    private fun submitExamResult() {
        // Hitung hasil ujian
        val jumlahTerjawab = answersMap.size
        val jumlahBenar = jumlahTerjawab // Seharusnya dari backend scoring
        val scorePercentage = (jumlahBenar.toDouble() / totalQuestions) * 100
        val waktuTempuhDetik = (durasiMenit * 60) - timeRemaining

        lifecycleScope.launch {
            try {
                val result = repository.submitExam(
                    examId = examId,
                    totalSoal = totalQuestions,
                    jumlahTerjawab = jumlahTerjawab,
                    jumlahBenar = jumlahBenar,
                    scorePercentage = scorePercentage,
                    waktuTempuhDetik = waktuTempuhDetik
                )

                result.onSuccess { examResult ->
                    // Navigate ke hasil ujian
                    val intent = Intent(this@SoalUjianActivity, HistoryDetailActivity::class.java)
                    intent.putExtra("exam_id", examResult.id)
                    intent.putExtra("subject", examResult.examTitle)
                    intent.putExtra("score", "${examResult.scorePercentage.toInt()}%")
                    intent.putExtra("date", examResult.tanggalUjian)
                    intent.putExtra("duration", "${examResult.waktuTempuhDetik / 60} Menit")
                    intent.putExtra("isPassed", examResult.status == "PASSED")
                    startActivity(intent)
                    finish()
                }

                result.onFailure { error ->
                    Toast.makeText(
                        this@SoalUjianActivity,
                        "Gagal submit ujian: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SoalUjianActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun increaseFontSize() {
        val currentSize = questionView.textSize / resources.displayMetrics.scaledDensity
        questionView.textSize = currentSize + 2
    }

    private fun decreaseFontSize() {
        val currentSize = questionView.textSize / resources.displayMetrics.scaledDensity
        if (currentSize > 12) questionView.textSize = currentSize - 2
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}