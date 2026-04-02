package com.example.cbt

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.slider.RangeSlider
import com.google.android.material.chip.Chip

class FilterHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_filter_history)  // pastikan ini layout activity utama, bukan dialog

        val btnFilter = findViewById<ImageView>(R.id.btnFilter)
        btnFilter?.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        try {
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.layout_filter_history, null)

            val btnClose = view.findViewById<TextView>(R.id.btnClose)
            val btnTerapkan = view.findViewById<TextView>(R.id.btnTerapkan)
            val btnAturUlang = view.findViewById<TextView>(R.id.btnAturUlang)
            val chipHariIni = view.findViewById<Chip>(R.id.chipHariIni)
            val chip7Hari = view.findViewById<Chip>(R.id.chip7Hari)
            val chip30Hari = view.findViewById<Chip>(R.id.chip30Hari)
            val chipKustom = view.findViewById<Chip>(R.id.chipKustom)
            val cbRemedial = view.findViewById<CheckBox>(R.id.cbRemedial)
            val rangeSlider = view.findViewById<RangeSlider>(R.id.rangeSlider)
            val tvRentangNilai = view.findViewById<TextView>(R.id.tvRentangNilai)
            val tvResetHeader = view.findViewById<TextView>(R.id.tvResetHeader)

            btnClose?.setOnClickListener { dialog.dismiss() }

            // Function untuk reset filter
            val resetFilter = {
                chipHariIni?.isChecked = true
                chip7Hari?.isChecked = false
                chip30Hari?.isChecked = false
                chipKustom?.isChecked = false
                cbRemedial?.isChecked = false
                rangeSlider?.setValues(0f, 100f)
                tvRentangNilai?.text = "0% - 100%"
                Toast.makeText(this@FilterHistoryActivity, "Filter diatur ulang", Toast.LENGTH_SHORT).show()
            }

            tvResetHeader?.setOnClickListener { resetFilter.invoke() }
            btnAturUlang?.setOnClickListener { resetFilter.invoke() }

            // Set label formatter untuk range slider
            rangeSlider?.setLabelFormatter { value ->
                "${value.toInt()}%"
            }

            // ✅ Perbaikan listener RangeSlider (tanpa error)
            rangeSlider?.addOnChangeListener { slider, _, _ ->
                val values = slider.values
                tvRentangNilai?.text = "${values[0].toInt()}% - ${values[1].toInt()}%"
            }

            btnTerapkan?.setOnClickListener {
                // Di sini Anda bisa mengambil nilai filter dan mengirimnya ke activity
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal memuat filter: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}