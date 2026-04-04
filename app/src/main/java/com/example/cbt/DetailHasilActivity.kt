package com.example.cbt

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailHasilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hasil)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
        findViewById<Button>(R.id.btnKembali).setOnClickListener {
            finish()
        }
    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_filter_history, null)

        view.findViewById<Button>(R.id.btnTerapkan).setOnClickListener {
            // TODO: handle filter logic di sini
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
}