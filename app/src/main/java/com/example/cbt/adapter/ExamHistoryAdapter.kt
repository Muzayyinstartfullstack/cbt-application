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
import com.example.cbt.model.ExamSession

class ExamHistoryAdapter(
    private val onItemClick: (ExamSession) -> Unit
) : ListAdapter<ExamSession, ExamHistoryAdapter.ExamHistoryViewHolder>(ExamHistoryDiffCallback()) {

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

        fun bind(session: ExamSession) {
            // Mengambil judul dari relasi table exams (jika ada) atau fallback ke ID
            tvSubject.text = session.exams?.title ?: "Ujian ${session.examId.takeLast(4)}"

            // Format Score: Sesuai properti di ExamSession
            val scoreValue = session.score ?: 0.0
            tvScore.text = "${scoreValue.toInt()}%"

            // Tanggal (Pastikan property ini ada di model ExamSession Anda)
            tvDate.text = session.createdAt ?: "-"

            // Set status color (COMPLETED vs IN_PROGRESS atau PASSED/FAILED)
            val statusColor = if (session.status == "COMPLETED") Color.GREEN else Color.YELLOW
            statusIndicator.setBackgroundColor(statusColor)

            card.setOnClickListener {
                onItemClick(session)
            }
        }
    }

    private class ExamHistoryDiffCallback : DiffUtil.ItemCallback<ExamSession>() {
        override fun areItemsTheSame(oldItem: ExamSession, newItem: ExamSession): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExamSession, newItem: ExamSession): Boolean {
            return oldItem == newItem
        }
    }
}