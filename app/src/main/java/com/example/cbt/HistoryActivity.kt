package com.example.cbt

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.slider.RangeSlider
import java.text.SimpleDateFormat
import java.util.*


class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_history)

        findViewById<ImageView>(R.id.navHome2).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }

        findViewById<ImageView>(R.id.btnFilter).setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_filter_riwayat, null)

        val btnHariIni = view.findViewById<Button>(R.id.btnHariIni)
        val btn7Hari = view.findViewById<Button>(R.id.btn7Hari)
        val btn30Hari = view.findViewById<Button>(R.id.btn30Hari)
        val btnKustom = view.findViewById<Button>(R.id.btnKustom)
        //val chipGroupMapel = view.findViewById<ChipGroup>(R.id.chipGroupMapel)
        val radioGroupStatus = view.findViewById<RadioGroup>(R.id.radioGroupStatus)
        val rangeSlider = view.findViewById<RangeSlider>(R.id.rangeSliderNilai)
        val txtRangeValue = view.findViewById<TextView>(R.id.txtRangeValue)
        val btnTerapkan = view.findViewById<Button>(R.id.btnTerapkan)
        val btnResetBawah = view.findViewById<Button>(R.id.btnResetBawah)

        // 1. Logika Klik Tanggal (Single Selection Warna)
        val dateButtons = listOf(btnHariIni, btn7Hari, btn30Hari, btnKustom)
        fun setDateActive(selected: Button) {
            dateButtons.forEach {
                it.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")))
                it.setTextColor(Color.BLACK)
            }
            selected.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0097B2")))
            selected.setTextColor(Color.WHITE)
        }

        dateButtons.forEach { btn ->
            btn.setOnClickListener {
                if (btn.id == R.id.btnKustom) {
                    val picker = MaterialDatePicker.Builder.dateRangePicker().build()
                    picker.show(supportFragmentManager, "DP")
                    picker.addOnPositiveButtonClickListener {
                        val fmt = SimpleDateFormat("dd MMM", Locale.getDefault())
                        btnKustom.text = "${fmt.format(Date(it.first))} - ${fmt.format(Date(it.second))}"
                        setDateActive(btnKustom)
                    }
                } else {
                    btnKustom.text = "Kustom"
                    setDateActive(btn)
                }
            }
        }

        // 2. Slider Update
        rangeSlider.addOnChangeListener { slider, _, _ ->
            txtRangeValue.text = "${slider.values[0].toInt()}% - ${slider.values[1].toInt()}%"
        }

        // 3. Reset Function
        btnResetBawah.setOnClickListener {
            dateButtons.forEach {
                it.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
                it.setTextColor(Color.BLACK)
            }
            btnHariIni.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0097B2")))
            btnHariIni.setTextColor(Color.WHITE)
            view.findViewById<Chip>(R.id.chipSemua).isChecked = true
            radioGroupStatus.clearCheck()
            rangeSlider.setValues(0f, 100f)
            btnKustom.text = "Kustom"
        }

        // 4. Terapkan
        //btnTerapkan.setOnClickListener {
          //  val mapel = view.findViewById<Chip>(chipGroupMapel.checkedChipId)?.text ?: "Semua"
          //  val status = when(radioGroupStatus.checkedRadioButtonId) {
            //    R.id.rbLulus -> "Lulus"
            //    R.id.rbGagal -> "Gagal"
            //    R.id.rbRemedial -> "Remedial"
            //    else -> "None"
            //}
            //Toast.makeText(this, "Filter: $mapel, Status: $status", Toast.LENGTH_SHORT).show()
            //dialog.dismiss()
        //}

        dialog.setContentView(view)
        dialog.show()
    }
}