package com.example.cbt

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class DetailHasilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_hasil)

        // Panggil fungsi klik back
        setupClickListeners()

        // Panggil filter dialog (sesuai kode dari main)
        showFilterDialog()
    }

    private fun setupClickListeners() {
        // Logika tombol back (icon)
        findViewById<ImageButton>(R.id.btnBack)?.setOnClickListener {
            finish()
        }
        // Logika tombol kembali (button teks)
        findViewById<Button>(R.id.btnKembali)?.setOnClickListener {
            finish()
        }
    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        // Gunakan layout filter_history
        val view = layoutInflater.inflate(R.layout.activity_filter_history, null)

        val btnTerapkan = view.findViewById<Button>(R.id.btnTerapkan)
        dialog.setContentView(view)

        btnTerapkan?.setOnClickListener {
            Toast.makeText(this, "Filter Diterapkan", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }
}