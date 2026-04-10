package com.example.cbt.adapter

import android.R.*
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cbt.R
import com.example.cbt.model.ExamResultResponse

// ==================== EXAM HISTORY ADAPTER ====================
class ExamHistoryAdapter(
    private val onItemClick: (ExamResultResponse) -> Unit
) : ListAdapter<ExamResultResponse, ExamHistoryAdapter.ExamHistoryViewHolder>(ExamHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exam_history, parent, false)
        return ExamHistoryViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ExamHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExamHistoryViewHolder(
        itemView: View,
        private val onItemClick: (ExamResultResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvSubject = itemView.findViewById<TextView>(R.id.tvSubject)
        private val tvScore = itemView.findViewById<TextView>(R.id.tvScore)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        private val tvDuration = itemView.findViewById<TextView>(R.id.tvDuration)
        private val statusIndicator = itemView.findViewById<View>(R.id.statusIndicator)
        private val card = itemView.findViewById<LinearLayout>(R.id.cardExam)

        fun bind(exam: ExamResultResponse) {
            tvSubject.text = exam.examTitle
            tvScore.text = "${exam.scorePercentage.toInt()}%"
            tvDate.text = exam.tanggalUjian
            tvDuration.text = "${exam.waktuTempuhDetik / 60} Menit"

            // Set status color
            val statusColor = if (exam.status == "PASSED") Color.GREEN else Color.RED
            statusIndicator.setBackgroundColor(statusColor)

            // Set click listener
            card.setOnClickListener {
                onItemClick(exam)
            }
        }
    }

    private class ExamHistoryDiffCallback : DiffUtil.ItemCallback<ExamResultResponse>() {
        override fun areItemsTheSame(oldItem: ExamResultResponse, newItem: ExamResultResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExamResultResponse, newItem: ExamResultResponse): Boolean {
            return oldItem == newItem
        }
    }
}

// ==================== UPCOMING EXAM ADAPTER ====================
class UpcomingExamAdapter(
    private val exams: List<ExamResultResponse>,
    private val onItemClick: (ExamResultResponse) -> Unit
) : RecyclerView.Adapter<UpcomingExamAdapter.UpcomingExamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingExamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_upcoming_exam, parent, false)
        return UpcomingExamViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: UpcomingExamViewHolder, position: Int) {
        holder.bind(exams[position])
    }

    override fun getItemCount(): Int = exams.size

    inner class UpcomingExamViewHolder(
        itemView: View,
        private val onItemClick: (ExamResultResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle = itemView.findViewById<TextView>(R.id.tvExamTitle)
        private val tvTime = itemView.findViewById<TextView>(R.id.tvScheduledTime)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvScheduledDate)
        private val tvDuration = itemView.findViewById<TextView>(R.id.tvDuration)

        fun bind(exam: ExamResultResponse) {
            tvTitle.text = exam.examTitle
            tvTime.text = exam.waktuTempuhDetik as CharSequence?
            tvDate.text = exam.tanggalUjian
            tvDuration.text = exam.durasiMenit.toString() + " Menit"

            // Jadikan seluruh item clickable
            itemView.setOnClickListener {
                onItemClick(exam)
            }
        }
    }
}