package com.example.cbt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cbt.R
import com.example.cbt.model.Exam

class AvailableExamAdapter(
    private val onExamClick: (Exam) -> Unit
) : ListAdapter<Exam, AvailableExamAdapter.AvailableExamViewHolder>(ExamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableExamViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_available_exam, parent, false)
        return AvailableExamViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableExamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AvailableExamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDetails: TextView = itemView.findViewById(R.id.tvDetails)
        private val btnMulai: Button = itemView.findViewById(R.id.btnMulai)
        private val imgSubject: ImageView = itemView.findViewById(R.id.imgSubject)

        fun bind(exam: Exam) {
            tvTitle.text = exam.title
            tvDetails.text = "${exam.durationMinutes} Menit"
            
            // Logika untuk menampilkan gambar mapel bisa di sini
            when {
                exam.title.contains("Matematika", true) -> imgSubject.setImageResource(R.drawable.img_math)
                // Tambahkan mapel lain jika ada resource-nya
            }

            btnMulai.setOnClickListener {
                onExamClick(exam)
            }
        }
    }

    private class ExamDiffCallback : DiffUtil.ItemCallback<Exam>() {
        override fun areItemsTheSame(oldItem: Exam, newItem: Exam): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Exam, newItem: Exam): Boolean {
            return oldItem == newItem
        }
    }
}
