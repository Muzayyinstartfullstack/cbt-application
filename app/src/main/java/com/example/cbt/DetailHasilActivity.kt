package com.example.cbt

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailHasilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hasil)
        showFilterDialog()
    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_filter_history, null)

        val btnTerapkan = view.findViewById<Button>(R.id.btnTerapkan)
        dialog.setContentView(view)

        btnTerapkan.setOnClickListener {
            // TODO: Handle terapkan action
            dialog.dismiss()
        }

        dialog.show()
    }
}