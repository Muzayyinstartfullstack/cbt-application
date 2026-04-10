package com.example.cbt.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.cbt.model.QuestionFilter
import com.example.cbt.model.QuestionStatus
import com.example.cbt.ui.adapter.QuestionGridFilterAdapter
import com.example.cbt.R

class NavigationSoalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var questionGridRecycler: RecyclerView
    private lateinit var gridAdapter: QuestionGridFilterAdapter

    private lateinit var filterDijawab: LinearLayout
    private lateinit var filterRagu: LinearLayout
    private lateinit var filterBelum: LinearLayout

    private lateinit var btnSelesai: Button
    private lateinit var btnClose: Button

    private var onQuestionClick: ((Int) -> Unit)? = null
    private var onSubmitClick: (() -> Unit)? = null

    private var totalQuestions = 45
    private var currentQuestion = 1
    private var questionStatus: Map<Int, QuestionStatus> = emptyMap()

    companion object {
        private const val ARG_TOTAL_QUESTIONS = "total_questions"
        private const val ARG_CURRENT_QUESTION = "current_question"

        fun newInstance(
            totalQuestions: Int = 45,
            currentQuestion: Int = 1,
            questionStatus: Map<Int, QuestionStatus> = emptyMap()
        ): NavigationSoalBottomSheet {
            return NavigationSoalBottomSheet().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TOTAL_QUESTIONS, totalQuestions)
                    putInt(ARG_CURRENT_QUESTION, currentQuestion)
                }
                this.questionStatus = questionStatus
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_nav_soal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    fun setOnQuestionClickListener(callback: (Int) -> Unit) {
        onQuestionClick = callback
    }

    fun setOnSubmitClickListener(callback: () -> Unit) {
        onSubmitClick = callback
    }

    fun updateQuestionStatus(questionNumber: Int, status: QuestionStatus) {
        questionStatus = questionStatus.toMutableMap().apply {
            put(questionNumber, status)
        }
        if (::gridAdapter.isInitialized) {
            gridAdapter.updateQuestionStatus(questionNumber, status)
        }
    }

    fun updateCurrentQuestion(newCurrent: Int) {
        currentQuestion = newCurrent
        if (::gridAdapter.isInitialized) {
            gridAdapter.updateCurrentQuestion(newCurrent)
        }
    }
}
