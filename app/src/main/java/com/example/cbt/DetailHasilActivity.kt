package com.example.cbt

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailHasilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hasil)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        // 5. Click Listeners
        btnBack.setOnClickListener { finish() }

    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_filter_history, null)

        // 1. Inisialisasi View dari layout dialog (Harus pakai view.findViewById)

        val btnTerapkan = view.findViewById<Button>(R.id.btnTerapkan)

    }
}