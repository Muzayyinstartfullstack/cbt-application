package com.example.cbt.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cbt.R
import com.example.cbt.model.QuestionFilter
import com.example.cbt.model.QuestionStatus

class QuestionGridFilterAdapter(
    private val context: Context,
    private var totalQuestions: Int,
    private var currentQuestion: Int,
    private var questionStatus: Map<Int, QuestionStatus>,
    private val listener: OnQuestionClickListener
) : RecyclerView.Adapter<QuestionGridFilterAdapter.ViewHolder>() {

    private var currentFilter: QuestionFilter = QuestionFilter.ALL
    private var filteredList: List<Int> = (1..totalQuestions).toList()

    interface OnQuestionClickListener {
        fun onQuestionClick(questionNumber: Int)
    }

    init {
        applyFilter()
    }

    fun setFilter(filter: QuestionFilter) {
        currentFilter = filter
        applyFilter()
    }

    private fun applyFilter() {
        val allQuestions = (1..totalQuestions).toList()
        filteredList = when (currentFilter) {
            QuestionFilter.ALL -> allQuestions
            QuestionFilter.DIJAWAB -> allQuestions.filter { questionStatus[it] == QuestionStatus.DIJAWAB }
            QuestionFilter.RAGU_RAGU -> allQuestions.filter { questionStatus[it] == QuestionStatus.RAGU_RAGU }
            QuestionFilter.BELUM -> allQuestions.filter { questionStatus[it] == QuestionStatus.BELUM || !questionStatus.containsKey(it) }
        }
        notifyDataSetChanged()
    }

    fun updateQuestionStatus(questionNumber: Int, status: QuestionStatus) {
        applyFilter()
    }

    fun updateCurrentQuestion(newCurrent: Int) {
        currentQuestion = newCurrent
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_question_nav_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val qNum = filteredList[position]
        holder.tvNumber.text = qNum.toString()

        val status = questionStatus[qNum] ?: QuestionStatus.BELUM

        when (status) {
            QuestionStatus.DIJAWAB -> holder.tvNumber.setBackgroundColor(Color.parseColor("#4CAF50"))
            QuestionStatus.RAGU_RAGU -> holder.tvNumber.setBackgroundColor(Color.parseColor("#FFC107"))
            QuestionStatus.BELUM -> holder.tvNumber.setBackgroundColor(Color.parseColor("#E0E0E0"))
        }

        if (qNum == currentQuestion) {
            holder.tvNumber.setBackgroundColor(Color.parseColor("#2196F3"))
            holder.tvNumber.setTextColor(Color.WHITE)
        } else {
            holder.tvNumber.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            listener.onQuestionClick(qNum)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.tv_question_number)
    }
}
