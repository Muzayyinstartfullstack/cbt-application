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
import com.example.cbt.data.model.ExamSessionWithStatus

class ExamHistoryAdapter(
    private val onItemClick: (ExamSessionWithStatus) -> Unit
) : ListAdapter<ExamSessionWithStatus, ExamHistoryAdapter.ExamHistoryViewHolder>(ExamHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exam_history, parent, false)
        return ExamHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExamHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExamHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSubject = itemView.findViewById<TextView>(R.id.tvSubject)
        private val tvScore = itemView.findViewById<TextView>(R.id.tvScore)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        private val statusIndicator = itemView.findViewById<View>(R.id.statusIndicator)
        private val card = itemView.findViewById<LinearLayout>(R.id.cardExam)

        fun bind(session: ExamSessionWithStatus) {
            // Mengambil judul dari relasi exam (jika ada) atau fallback ke ID
            tvSubject.text = session.exam?.title ?: "Ujian ${session.examId}"

            // Format Score
            val scoreValue = session.score ?: 0
            tvScore.text = "${scoreValue}%"

            // Tanggal
            tvDate.text = session.createdAt

            // Set status color
            val statusColor = if (session.status == "COMPLETED") Color.GREEN else Color.YELLOW
            statusIndicator.setBackgroundColor(statusColor)

            card.setOnClickListener {
                onItemClick(session)
            }
        }
    }

    private class ExamHistoryDiffCallback : DiffUtil.ItemCallback<ExamSessionWithStatus>() {
        override fun areItemsTheSame(oldItem: ExamSessionWithStatus, newItem: ExamSessionWithStatus): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExamSessionWithStatus, newItem: ExamSessionWithStatus): Boolean {
            return oldItem == newItem
        }
    }
}