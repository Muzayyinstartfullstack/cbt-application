package com.example.examapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.examapp.data.models.QuestionFilter
import com.example.examapp.data.models.QuestionStatus
import com.example.examapp.ui.adapter.QuestionGridFilterAdapter

/**
 * Bottom Sheet Fragment untuk navigasi soal
 * Component standalone yang bisa digunakan di berbagai activity
 */
class NavigationSoalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var questionGridRecycler: RecyclerView
    private lateinit var gridAdapter: QuestionGridFilterAdapter

    private lateinit var filterDijawab: LinearLayout
    private lateinit var filterRagu: LinearLayout
    private lateinit var filterBelum: LinearLayout

    private lateinit var btnSelesai: Button
    private lateinit var btnClose: Button

    // Callback
    private var onQuestionClick: ((Int) -> Unit)? = null
    private var onSubmitClick: (() -> Unit)? = null

    // Data
    private var totalQuestions = 45
    private var currentQuestion = 1
    private var questionStatus: Map<Int, QuestionStatus> = emptyMap()

    companion object {
        private const val ARG_TOTAL_QUESTIONS = "total_questions"
        private const val ARG_CURRENT_QUESTION = "current_question"
        private const val ARG_QUESTION_STATUS = "question_status"

        fun newInstance(
            totalQuestions: Int = 45,
            currentQuestion: Int = 1,
            questionStatus: Map<Int, QuestionStatus> = emptyMap()
        ): NavigationSoalBottomSheet {
            return NavigationSoalBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TOTAL_QUESTIONS, totalQuestions)
                    putInt(ARG_CURRENT_QUESTION, currentQuestion)
                    // Note: Untuk status map, pass via setter method lebih mudah
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_nav_soal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Parse arguments
        arguments?.let {
            totalQuestions = it.getInt(ARG_TOTAL_QUESTIONS, 45)
            currentQuestion = it.getInt(ARG_CURRENT_QUESTION, 1)
        }

        initializeViews(view)
        setupRecyclerView()
        setupEventListeners()
    }

    private fun initializeViews(view: View) {
        questionGridRecycler = view.findViewById(R.id.question_grid_recycler)
        filterDijawab = view.findViewById(R.id.filter_dijawab)
        filterRagu = view.findViewById(R.id.filter_ragu)
        filterBelum = view.findViewById(R.id.filter_belum)
        btnSelesai = view.findViewById(R.id.btn_selesai)
        btnClose = view.findViewById(R.id.btn_close_nav)
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 5)
        questionGridRecycler.layoutManager = layoutManager

        gridAdapter = QuestionGridFilterAdapter(
            context = requireContext(),
            totalQuestions = totalQuestions,
            currentQuestion = currentQuestion,
            questionStatus = questionStatus,
            listener = object : QuestionGridFilterAdapter.OnQuestionClickListener {
                override fun onQuestionClick(questionNumber: Int) {
                    onQuestionClick?.invoke(questionNumber)
                    dismiss()
                }
            }
        )

        questionGridRecycler.adapter = gridAdapter
    }

    private fun setupEventListeners() {
        btnClose.setOnClickListener {
            dismiss()
        }

        btnSelesai.setOnClickListener {
            onSubmitClick?.invoke()
            dismiss()
        }

        filterDijawab.setOnClickListener {
            gridAdapter.setFilter(QuestionFilter.DIJAWAB)
        }

        filterRagu.setOnClickListener {
            gridAdapter.setFilter(QuestionFilter.RAGU_RAGU)
        }

        filterBelum.setOnClickListener {
            gridAdapter.setFilter(QuestionFilter.BELUM)
        }
    }

    /**
     * Set callback saat soal di-klik
     */
    fun setOnQuestionClickListener(callback: (Int) -> Unit) {
        onQuestionClick = callback
    }

    /**
     * Set callback saat submit di-klik
     */
    fun setOnSubmitClickListener(callback: () -> Unit) {
        onSubmitClick = callback
    }

    /**
     * Update question status
     */
    fun updateQuestionStatus(questionNumber: Int, status: QuestionStatus) {
        questionStatus = questionStatus.toMutableMap().apply {
            put(questionNumber, status)
        }
        if (::gridAdapter.isInitialized) {
            gridAdapter.updateQuestionStatus(questionNumber, status)
        }
    }

    /**
     * Update current question
     */
    fun updateCurrentQuestion(newCurrent: Int) {
        currentQuestion = newCurrent
        if (::gridAdapter.isInitialized) {
            gridAdapter.updateCurrentQuestion(newCurrent)
        }
    }

    /**
     * Set question status map
     */
    fun setQuestionStatus(statusMap: Map<Int, QuestionStatus>) {
        questionStatus = statusMap
        if (::gridAdapter.isInitialized) {
            gridAdapter.notifyDataSetChanged()
        }
    }
}