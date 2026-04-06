package com.example.cbt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.cbt.R

class ExamSubmitDialog : DialogFragment() {

    private var isAllQuestionsAnswered: Boolean = false
    private var onCekSoal: (() -> Unit)? = null
    private var onKumpulkan: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_submit_exam, null)

        val layoutNotFinished = dialogView.findViewById<ViewGroup>(R.id.layoutNotFinished)
        val layoutReady = dialogView.findViewById<ViewGroup>(R.id.layoutReady)

        // Tampilkan layout sesuai kondisi
        if (isAllQuestionsAnswered) {
            layoutNotFinished.visibility = View.GONE
            layoutReady.visibility = View.VISIBLE

            // Semua soal sudah dijawab
            dialogView.findViewById<Button>(R.id.btnKumpulkanJawaban).setOnClickListener {
                dismiss()
                onKumpulkan?.invoke()
            }
            dialogView.findViewById<Button>(R.id.btnCekSoalReady).setOnClickListener {
                dismiss()
                onCekSoal?.invoke()
            }
        } else {
            layoutNotFinished.visibility = View.VISIBLE
            layoutReady.visibility = View.GONE

            // Ada soal yang belum dijawab
            dialogView.findViewById<Button>(R.id.btnCekSoal).setOnClickListener {
                dismiss()
                onCekSoal?.invoke()
            }
            dialogView.findViewById<Button>(R.id.btnKumpulkanSaja).setOnClickListener {
                dismiss()
                onKumpulkan?.invoke()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()
    }

    // Setter untuk parameter
    fun setIsAllQuestionsAnswered(answered: Boolean) = apply {
        isAllQuestionsAnswered = answered
    }

    fun setOnCekSoal(callback: () -> Unit) = apply {
        onCekSoal = callback
    }

    fun setOnKumpulkan(callback: () -> Unit) = apply {
        onKumpulkan = callback
    }
}