package com.example.cbt.adapter

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
import com.example.cbt.model.AttemptResponse
import com.example.cbt.model.ExamResponse
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
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)
        private val card = itemView.findViewById<LinearLayout>(R.id.cardExam)

        fun bind(exam: ExamResultResponse) {
            tvSubject.text = exam.examTitle
            tvScore.text = "${exam.scorePercentage.toInt()}%"
            tvDate.text = exam.tanggalUjian
            if (exam.waktuTempuhDetik > 0) {
                tvDuration.text = "${exam.waktuTempuhDetik / 60} Menit"
            } else if (exam.durasiMenit != null && exam.durasiMenit > 0) {
                tvDuration.text = "${exam.durasiMenit} Menit"
            } else {
                tvDuration.text = "- Menit"
            }

            // Set score color based on status
            val isPassed = exam.status.equals("PASSED", ignoreCase = true) ||
                           exam.status.equals("submitted", ignoreCase = true) && exam.scorePercentage >= 55
            val scoreColor = if (isPassed) Color.parseColor("#4CAF50") else Color.parseColor("#FF4444")
            tvScore.setTextColor(scoreColor)

            // Set status text
            val statusText = if (isPassed) " Lulus" else " Tidak Lulus"
            tvStatus.text = statusText
            tvStatus.setTextColor(if (isPassed) Color.parseColor("#4CAF50") else Color.parseColor("#FF4444"))

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
            tvTime.text = exam.tanggalUjian
            tvDate.text = exam.tanggalUjian
            tvDuration.text = (exam.durasiMenit?.toString() ?: "-") + " Menit"

            // Jadikan seluruh item clickable
            itemView.setOnClickListener {
                onItemClick(exam)
            }
        }
    }
}

// ==================== AVAILABLE EXAM ADAPTER ====================
class AvailableExamAdapter(
    private val exams: List<ExamResponse>,
    private val onItemClick: (ExamResponse) -> Unit
) : RecyclerView.Adapter<AvailableExamAdapter.AvailableExamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableExamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_upcoming_exam, parent, false)
        return AvailableExamViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: AvailableExamViewHolder, position: Int) {
        holder.bind(exams[position])
    }

    override fun getItemCount(): Int = exams.size

    inner class AvailableExamViewHolder(
        itemView: View,
        private val onItemClick: (ExamResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle = itemView.findViewById<TextView>(R.id.tvExamTitle)
        private val tvTime = itemView.findViewById<TextView>(R.id.tvScheduledTime)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvScheduledDate)
        private val tvDuration = itemView.findViewById<TextView>(R.id.tvDuration)

        fun bind(exam: ExamResponse) {
            tvTitle.text = exam.judul
            tvTime.text = exam.startTime ?: "-"
            tvDate.text = exam.startTime ?: "-"
            tvDuration.text = "${exam.durasi} Menit"

            itemView.setOnClickListener {
                onItemClick(exam)
            }
        }
    }
}

// ==================== ONGOING EXAM ADAPTER ====================
class OngoingExamAdapter(
    private val attempts: List<AttemptResponse>,
    private val onItemClick: (AttemptResponse) -> Unit
) : RecyclerView.Adapter<OngoingExamAdapter.OngoingExamViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OngoingExamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_upcoming_exam, parent, false)
        return OngoingExamViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: OngoingExamViewHolder, position: Int) {
        holder.bind(attempts[position])
    }

    override fun getItemCount(): Int = attempts.size

    inner class OngoingExamViewHolder(
        itemView: View,
        private val onItemClick: (AttemptResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle = itemView.findViewById<TextView>(R.id.tvExamTitle)
        private val tvTime = itemView.findViewById<TextView>(R.id.tvScheduledTime)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvScheduledDate)
        private val tvDuration = itemView.findViewById<TextView>(R.id.tvDuration)

        fun bind(attempt: AttemptResponse) {
            tvTitle.text = "Ujian #${attempt.idUjian.takeLast(4)}"
            tvTime.text = attempt.waktuMulai ?: "-"
            tvDate.text = attempt.waktuMulai ?: "-"
            tvDuration.text = "Sedang berlangsung"

            itemView.setOnClickListener {
                onItemClick(attempt)
            }
        }
    }
}