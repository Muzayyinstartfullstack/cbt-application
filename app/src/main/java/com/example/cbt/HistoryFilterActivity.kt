package com.example.cbt

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.RangeSlider
import com.google.android.material.chip.Chip

class HistoryFilterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_history)

        val btnClose = findViewById<TextView>(R.id.btnClose)
        val btnTerapkan = findViewById<TextView>(R.id.btnTerapkan)
        val btnAturUlang = findViewById<TextView>(R.id.btnAturUlang)
        val chipHariIni = findViewById<Chip>(R.id.chipHariIni)
        val chip7Hari = findViewById<Chip>(R.id.chip7Hari)
        val chip30Hari = findViewById<Chip>(R.id.chip30Hari)
        val chipKustom = findViewById<Chip>(R.id.chipKustom)
        val cbRemedial = findViewById<CheckBox>(R.id.cbRemedial)
        val rangeSlider = findViewById<RangeSlider>(R.id.rangeSlider)
        val tvRentangNilai = findViewById<TextView>(R.id.tvRentangNilai)

        btnClose.setOnClickListener { navigateBackToHistory() }

        // Function untuk reset filter
        val resetFilter = {
            chipHariIni?.isChecked = true
            chip7Hari?.isChecked = false
            chip30Hari?.isChecked = false
            chipKustom?.isChecked = false
            cbRemedial?.isChecked = false
            rangeSlider?.setValues(0f, 100f)
            tvRentangNilai?.text = "0% - 100%"
            Toast.makeText(this@HistoryFilterActivity, "Filter diatur ulang", Toast.LENGTH_SHORT).show()
        }

        btnAturUlang?.setOnClickListener { resetFilter.invoke() }

        // Set label formatter untuk range slider
        rangeSlider?.setLabelFormatter { value ->
            "${value.toInt()}%"
        }

        // Perbaikan listener RangeSlider
        rangeSlider?.addOnChangeListener { slider, _, _ ->
            val values = slider.values
            tvRentangNilai?.text = "${values[0].toInt()}% - ${values[1].toInt()}%"
        }

        btnTerapkan?.setOnClickListener {
            // Di sini Anda bisa mengambil nilai filter dan mengirimnya ke activity jika diperlukan
            navigateBackToHistory()
        }
    }

    private fun navigateBackToHistory() {
        val intent = android.content.Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        navigateBackToHistory()
    }
}