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
import com.example.cbt.database.SupabaseClient
import com.example.cbt.model.Question
import com.example.cbt.model.QuestionOption
import com.example.cbt.model.ExamSession
import com.example.cbt.repository.ExamRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch

class SoalUjianActivity : AppCompatActivity() {

    private lateinit var repository: ExamRepository

    // Views
    private lateinit var timerView: TextView
    private lateinit var questionView: TextView
    private lateinit var progressButton: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var optionA: LinearLayout
    private lateinit var optionB: LinearLayout
    private lateinit var optionC: LinearLayout
    private lateinit var optionD: LinearLayout
    private lateinit var optionE: LinearLayout
    private lateinit var optionAText: TextView
    private lateinit var optionBText: TextView
    private lateinit var optionCText: TextView
    private lateinit var optionDText: TextView
    private lateinit var optionEText: TextView
    private lateinit var btnPrevious: AppCompatButton
    private lateinit var btnNext: AppCompatButton
    private lateinit var btnBookmark: AppCompatButton
    private lateinit var btnSizeIncrease: ImageButton
    private lateinit var btnSizeDecrease: ImageButton
    private lateinit var btnNavigasi: AppCompatButton

    // Data dari intent
    private var examId: String = ""
    private var sessionId: String = ""
    private var examTitle: String = ""
    private var durasiMenit: Int = 60
    private var totalQuestions: Int = 0
    private var profileId: String = ""

    // State
    private var selectedOption = -1
    private var currentQuestion = 0
    private var timeRemaining = 0L
    private var countDownTimer: CountDownTimer? = null
    private var isBookmarked = false

    // Data soal
    private var questions: List<Question> = emptyList()

    // question_id -> option_id yang dipilih
    private var answersOptionIdMap = mutableMapOf<String, String>()

    // question_id yang di-bookmark (ragu-ragu)
    private var bookmarkedQuestions = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soal_ujian)

        repository = ExamRepository()

        // Ambil data dari intent
        examId      = intent.getStringExtra("EXAM_ID") ?: ""
        sessionId   = intent.getStringExtra("SESSION_ID") ?: ""
        examTitle   = intent.getStringExtra("EXAM_TITLE") ?: "Ujian"
        durasiMenit = intent.getIntExtra("EXAM_DURATION", 60)
        profileId   = intent.getStringExtra("PROFILE_ID") ?: ""

        if (examId.isEmpty() || sessionId.isEmpty()) {
            Toast.makeText(this, "Data ujian tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initializeViews()
        setupClickListeners()
        loadQuestions()
    }

    // ─── INIT ─────────────────────────────────────────────────────────────────

    private fun initializeViews() {
        timerView        = findViewById(R.id.tv_timer)
        questionView     = findViewById(R.id.tv_question)
        progressButton   = findViewById(R.id.btn_question_progress)
        loadingIndicator = findViewById(R.id.progressBar)

        optionA = findViewById(R.id.option_a)
        optionB = findViewById(R.id.option_b)
        optionC = findViewById(R.id.option_c)
        optionD = findViewById(R.id.option_d)
        optionE = findViewById(R.id.option_e)

        optionAText = findViewById(R.id.option_a_text)
        optionBText = findViewById(R.id.option_b_text)
        optionCText = findViewById(R.id.option_c_text)
        optionDText = findViewById(R.id.option_d_text)
        optionEText = findViewById(R.id.option_e_text)

        btnPrevious    = findViewById(R.id.btn_previous)
        btnNext        = findViewById(R.id.btn_next)
        btnBookmark    = findViewById(R.id.btn_bookmark)
        btnSizeIncrease = findViewById(R.id.btn_size_increase)
        btnSizeDecrease = findViewById(R.id.btn_size_decrease)
        btnNavigasi    = findViewById(R.id.btn_navigasi)
    }

    private fun setupClickListeners() {
        // Pilih opsi berdasarkan index dan option_label dari question_options
        optionA.setOnClickListener { selectOption(0) }
        optionB.setOnClickListener { selectOption(1) }
        optionC.setOnClickListener { selectOption(2) }
        optionD.setOnClickListener { selectOption(3) }
        optionE.setOnClickListener { selectOption(4) }

        btnPrevious.setOnClickListener { goToPreviousQuestion() }
        btnNext.setOnClickListener     { goToNextQuestion() }
        btnBookmark.setOnClickListener { toggleBookmark() }

        btnSizeIncrease.setOnClickListener { increaseFontSize() }
        btnSizeDecrease.setOnClickListener { decreaseFontSize() }

        btnNavigasi.setOnClickListener {
            Toast.makeText(this, "Navigasi soal akan dibuka", Toast.LENGTH_SHORT).show()
            // TODO: buka NavigationSoalBottomSheet
        }
    }

    // ─── LOAD SOAL ────────────────────────────────────────────────────────────

    private fun loadQuestions() {
        loadingIndicator.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                // Ambil session_questions beserta relasi questions dan question_options
                val sessionQuestions = SupabaseClient.client
                    .from("session_questions")
                    .select(
                        Columns.raw(
                            "id, question_order, question_id, " +
                                    "questions(" +
                                    "  id, question_text, image_url, topic_id, subject_id, " +
                                    "  question_options(id, option_label, option_text, is_correct)" +
                                    ")"
                        )
                    ) {
                        filter { eq("session_id", sessionId) }
                        order("question_order", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
                    }
                    .decodeList<com.example.cbt.model.SessionQuestion>()

                // Ekstrak Question dari relasi
                questions = sessionQuestions.mapNotNull { it.questions }
                totalQuestions = questions.size

                loadingIndicator.visibility = View.GONE

                if (questions.isNotEmpty()) {
                    currentQuestion = 0
                    timeRemaining   = (durasiMenit * 60).toLong()
                    startTimer()
                    loadQuestion()
                } else {
                    Toast.makeText(
                        this@SoalUjianActivity,
                        "Tidak ada soal tersedia",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                loadingIndicator.visibility = View.GONE
                Toast.makeText(
                    this@SoalUjianActivity,
                    "Gagal memuat soal: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ─── TAMPIL SOAL ──────────────────────────────────────────────────────────

    private fun loadQuestion() {
        if (currentQuestion >= questions.size) return

        val question = questions[currentQuestion]

        // Tampilkan teks soal — field: question_text
        questionView.text = "${currentQuestion + 1}. ${question.questionText}"

        // Urutkan opsi berdasarkan option_label (A, B, C, D, E)
        val sortedOptions = question.options.sortedBy { it.optionLabel }

        optionAText.text = sortedOptions.getOrNull(0)?.optionText ?: ""
        optionBText.text = sortedOptions.getOrNull(1)?.optionText ?: ""
        optionCText.text = sortedOptions.getOrNull(2)?.optionText ?: ""
        optionDText.text = sortedOptions.getOrNull(3)?.optionText ?: ""
        optionEText.text = sortedOptions.getOrNull(4)?.optionText ?: ""

        // Sembunyikan opsi yang kosong
        optionE.visibility = if (sortedOptions.size >= 5) View.VISIBLE else View.GONE

        // Restore pilihan sebelumnya jika ada
        val chosenOptionId = answersOptionIdMap[question.id]
        selectedOption = if (chosenOptionId != null) {
            sortedOptions.indexOfFirst { it.id == chosenOptionId }
        } else {
            -1
        }

        // Restore status bookmark
        isBookmarked = bookmarkedQuestions.contains(question.id)
        btnBookmark.text = if (isBookmarked) "✓ Ragu-ragu" else "Ragu-ragu"
        btnBookmark.alpha = if (isBookmarked) 0.8f else 1.0f

        updateOptionUI()
        updateProgressButton()
    }

    // ─── PILIH JAWABAN ────────────────────────────────────────────────────────

    private fun selectOption(optionIndex: Int) {
        if (currentQuestion >= questions.size) return

        val question      = questions[currentQuestion]
        val sortedOptions = question.options.sortedBy { it.optionLabel }
        val chosenOption  = sortedOptions.getOrNull(optionIndex) ?: return

        selectedOption = optionIndex

        // Simpan option_id yang dipilih
        answersOptionIdMap[question.id] = chosenOption.id

        updateOptionUI()

        // Simpan jawaban ke tabel session_answers (upsert)
        saveAnswerToSupabase(
            sessionQuestionId = getSessionQuestionId(question.id),
            chosenOptionId    = chosenOption.id,
            isCorrect         = chosenOption.isCorrect
        )
    }

    private fun getSessionQuestionId(questionId: String): String {
        // Dicari dari data session_questions yang sudah diload
        // Karena kita perlu session_question.id untuk foreign key di session_answers
        // Simpan mapping saat load soal
        return sessionQuestionIdMap[questionId] ?: ""
    }

    // Mapping question_id -> session_question_id (diisi saat loadQuestions)
    private var sessionQuestionIdMap = mutableMapOf<String, String>()

    private fun saveAnswerToSupabase(
        sessionQuestionId: String,
        chosenOptionId: String,
        isCorrect: Boolean
    ) {
        if (sessionQuestionId.isEmpty()) return

        lifecycleScope.launch {
            try {
                // Upsert ke tabel session_answers
                // UNIQUE constraint: session_question_id
                SupabaseClient.client
                    .from("session_answers")
                    .upsert(
                        mapOf(
                            "session_question_id" to sessionQuestionId,
                            "chosen_option_id"    to chosenOptionId,
                            "is_correct"          to isCorrect,
                            "answered_at"         to java.time.Instant.now().toString()
                        )
                    ) {
                        onConflict = "session_question_id"
                    }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SoalUjianActivity,
                    "Gagal menyimpan jawaban: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // ─── UPDATE UI ────────────────────────────────────────────────────────────

    private fun updateOptionUI() {
        val unselected = R.drawable.bg_unselected_option
        val selected   = R.drawable.bg_selected_option

        optionA.setBackgroundResource(unselected)
        optionB.setBackgroundResource(unselected)
        optionC.setBackgroundResource(unselected)
        optionD.setBackgroundResource(unselected)
        optionE.setBackgroundResource(unselected)

        when (selectedOption) {
            0 -> optionA.setBackgroundResource(selected)
            1 -> optionB.setBackgroundResource(selected)
            2 -> optionC.setBackgroundResource(selected)
            3 -> optionD.setBackgroundResource(selected)
            4 -> optionE.setBackgroundResource(selected)
        }
    }

    private fun updateProgressButton() {
        progressButton.text = "Soal ${currentQuestion + 1} dari $totalQuestions"
    }

    // ─── NAVIGASI SOAL ────────────────────────────────────────────────────────

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

    // ─── BOOKMARK / RAGU-RAGU ────────────────────────────────────────────────

    private fun toggleBookmark() {
        if (currentQuestion >= questions.size) return

        val questionId = questions[currentQuestion].id
        isBookmarked   = !isBookmarked

        if (isBookmarked) {
            bookmarkedQuestions.add(questionId)
            btnBookmark.text  = "✓ Ragu-ragu"
            btnBookmark.alpha = 0.8f
        } else {
            bookmarkedQuestions.remove(questionId)
            btnBookmark.text  = "Ragu-ragu"
            btnBookmark.alpha = 1.0f
        }
    }

    // ─── TIMER ────────────────────────────────────────────────────────────────

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
        val hours   = timeRemaining / 3600
        val minutes = (timeRemaining % 3600) / 60
        val seconds = timeRemaining % 60
        timerView.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun onTimeUp() {
        countDownTimer?.cancel()
        Toast.makeText(this, "Waktu habis!", Toast.LENGTH_LONG).show()
        submitExam()
    }

    // ─── SUBMIT UJIAN ─────────────────────────────────────────────────────────

    private fun submitExam() {
        lifecycleScope.launch {
            try {
                // Hitung skor dari is_correct di question_options
                val totalJawaban = answersOptionIdMap.size
                val jumlahBenar  = hitungJumlahBenar()
                val score        = if (totalQuestions > 0) {
                    (jumlahBenar.toDouble() / totalQuestions) * 100
                } else 0.0

                val now = java.time.Instant.now().toString()

                // 1. Update status exam_sessions → submitted
                SupabaseClient.client
                    .from("exam_sessions")
                    .update(
                        mapOf(
                            "status"   to "submitted",
                            "end_time" to now,
                            "score"    to score
                        )
                    ) {
                        filter { eq("id", sessionId) }
                    }

                // 2. Insert ke exam_results
                SupabaseClient.client
                    .from("exam_results")
                    .insert(
                        mapOf(
                            "session_id"      to sessionId,
                            "total_questions" to totalQuestions,
                            "correct_answers" to jumlahBenar,
                            "score"           to score
                        )
                    )

                // 3. Navigasi ke halaman hasil
                val intent = Intent(this@SoalUjianActivity, DetailHasilActivity::class.java)
                intent.putExtra("SESSION_ID",  sessionId)
                intent.putExtra("EXAM_TITLE",  examTitle)
                intent.putExtra("SCORE",       score)
                intent.putExtra("TOTAL",       totalQuestions)
                intent.putExtra("BENAR",       jumlahBenar)
                startActivity(intent)
                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@SoalUjianActivity,
                    "Gagal submit ujian: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Hitung jawaban benar berdasarkan is_correct di tabel question_options.
     * Tidak perlu request ke server — data sudah ada di question.options.
     */
    private fun hitungJumlahBenar(): Int {
        var benar = 0
        for (question in questions) {
            val chosenOptionId = answersOptionIdMap[question.id] ?: continue
            val chosenOption   = question.options.find { it.id == chosenOptionId }
            if (chosenOption?.isCorrect == true) benar++
        }
        return benar
    }

    // ─── FONT SIZE ────────────────────────────────────────────────────────────

    private fun increaseFontSize() {
        val current = questionView.textSize / resources.displayMetrics.scaledDensity
        if (current < 24) questionView.textSize = current + 2
    }

    private fun decreaseFontSize() {
        val current = questionView.textSize / resources.displayMetrics.scaledDensity
        if (current > 12) questionView.textSize = current - 2
    }

    // ─── LIFECYCLE ────────────────────────────────────────────────────────────

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}