package com.example.cbt

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class SoalUjianActivity : AppCompatActivity() {

    private lateinit var timerView: TextView
    private lateinit var questionView: TextView
    private lateinit var progressButton: TextView

    private var selectedOption = -1
    private var currentQuestion = 1
    private var totalQuestions = 45
    private var timeRemaining = 1L * 3600 + 28 * 60 + 54
    private var countDownTimer: CountDownTimer? = null
    private var isBookmarked = false

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soal_ujian)

        initializeViews()
        setupClickListeners()
        startTimer()
    }

    private fun initializeViews() {
        timerView = findViewById(R.id.tv_timer)
        questionView = findViewById(R.id.tv_question)
        progressButton = findViewById(R.id.btn_question_progress)

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

        updateProgressButton()
    }

    private fun setupClickListeners() {
        optionA.setOnClickListener { selectOption(0) }
        optionB.setOnClickListener { selectOption(1) }
        optionC.setOnClickListener { selectOption(2) }
        optionD.setOnClickListener { selectOption(3) }
        optionE.setOnClickListener { selectOption(4) }

        btnPrevious.setOnClickListener { goToPreviousQuestion() }
        btnNext.setOnClickListener { goToNextQuestion() }
        btnBookmark.setOnClickListener { toggleBookmark() }

        btnSizeIncrease.setOnClickListener { increaseFontSize() }
        btnSizeDecrease.setOnClickListener { decreaseFontSize() }
    }

    private fun selectOption(optionIndex: Int) {
        selectedOption = optionIndex
        updateOptionUI()
    }

    private fun updateOptionUI() {
        optionA.setBackgroundResource(R.drawable.bg_unselected_option)
        optionB.setBackgroundResource(R.drawable.bg_unselected_option)
        optionC.setBackgroundResource(R.drawable.bg_unselected_option)
        optionD.setBackgroundResource(R.drawable.bg_unselected_option)
        optionE.setBackgroundResource(R.drawable.bg_unselected_option)

        when (selectedOption) {
            0 -> optionA.setBackgroundResource(R.drawable.bg_selected_option)
            1 -> optionB.setBackgroundResource(R.drawable.bg_selected_option)
            2 -> optionC.setBackgroundResource(R.drawable.bg_selected_option)
            3 -> optionD.setBackgroundResource(R.drawable.bg_selected_option)
            4 -> optionE.setBackgroundResource(R.drawable.bg_selected_option)
        }
    }

    private fun goToNextQuestion() {
        if (currentQuestion < totalQuestions) {
            currentQuestion++
            updateProgressButton()
            loadQuestion()
        }
    }

    private fun goToPreviousQuestion() {
        if (currentQuestion > 1) {
            currentQuestion--
            updateProgressButton()
            loadQuestion()
        }
    }

    private fun loadQuestion() {
        selectedOption = -1
        updateOptionUI()
        questionView.text = "Pertanyaan Soal $currentQuestion: Hasil dari 2x + 5 = 15 adalah...."
    }

    private fun toggleBookmark() {
        isBookmarked = !isBookmarked
        btnBookmark.text = if (isBookmarked) "✓ Ragu-ragu" else "Ragu-ragu"
        btnBookmark.alpha = if (isBookmarked) 0.8f else 1.0f
    }

    private fun updateProgressButton() {
        progressButton.text = "Soal $currentQuestion dari $totalQuestions"
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeRemaining * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished / 1000
                updateTimerDisplay()
            }
            override fun onFinish() {
                timerView.text = "00:00:00"
                Toast.makeText(this@SoalUjianActivity, "Waktu Habis!", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun updateTimerDisplay() {
        val hours = timeRemaining / 3600
        val minutes = (timeRemaining % 3600) / 60
        val seconds = timeRemaining % 60
        timerView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
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